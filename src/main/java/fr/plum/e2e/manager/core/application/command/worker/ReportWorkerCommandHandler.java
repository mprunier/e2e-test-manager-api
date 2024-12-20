package fr.plum.e2e.manager.core.application.command.worker;

import fr.plum.e2e.manager.core.application.locker.CommandLock;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.report.Report;
import fr.plum.e2e.manager.core.domain.model.aggregate.report.ReportTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
import fr.plum.e2e.manager.core.domain.model.event.WorkerUnitCompletedEvent;
import fr.plum.e2e.manager.core.domain.model.exception.ArtifactReportException;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerExtractorPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerUnitPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ReportWorkerCommandHandler implements CommandHandler<ReportWorkerCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final WorkerUnitPort workerUnitPort;
  private final TestConfigurationRepositoryPort testConfigurationRepositoryPort;
  private final WorkerRepositoryPort workerRepositoryPort;
  private final WorkerExtractorPort workerExtractorPort;
  private final TestResultRepositoryPort testResultRepositoryPort;
  private final TransactionManagerPort transactionManagerPort;

  private final EnvironmentService environmentService;

  public ReportWorkerCommandHandler(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerUnitPort workerUnitPort,
      TestConfigurationRepositoryPort testConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      WorkerExtractorPort workerExtractorPort,
      TestResultRepositoryPort testResultRepositoryPort,
      TransactionManagerPort transactionManagerPort,
      EnvironmentRepositoryPort environmentRepositoryPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.workerUnitPort = workerUnitPort;
    this.testConfigurationRepositoryPort = testConfigurationRepositoryPort;
    this.workerRepositoryPort = workerRepositoryPort;
    this.workerExtractorPort = workerExtractorPort;
    this.testResultRepositoryPort = testResultRepositoryPort;
    this.transactionManagerPort = transactionManagerPort;
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
  }

  @Override
  @CommandLock
  public void execute(ReportWorkerCommand command) {
    var worker = workerRepositoryPort.find(command.workerUnitId()).orElse(null);

    if (worker == null) {
      log.trace("Worker Unit {} not found", command.workerUnitId());
      return;
    }

    var environment = environmentService.getEnvironment(worker.getEnvironmentId());
    var workerUnitStatus =
        workerUnitPort.getWorkerStatus(
            environment.getSourceCodeInformation(), command.workerUnitId());

    if (WorkerUnitStatus.IN_PROGRESS.equals(workerUnitStatus)) {
      return;
    }

    var testConfigurationsToRun = getTestConfigurationsToRun(worker, command.workerUnitId());
    var testResults =
        processWorkerResults(
            worker, environment, command, workerUnitStatus, testConfigurationsToRun);

    handleMissingTests(testConfigurationsToRun, testResults, worker);

    var workerUnit = worker.findWorkerUnit(command.workerUnitId());
    workerUnit.setStatus(workerUnitStatus);

    transactionManagerPort.executeInTransaction(
        () -> {
          testResultRepositoryPort.saveAll(testResults);

          transactionManagerPort.registerAfterCommit(() -> publishWorkerUnitCompletedEvent(worker));

          if (worker.isCompleted()) {
            finalizeWorker(worker);
          } else {
            workerRepositoryPort.save(worker);
          }
        });
  }

  private List<TestConfigurationId> getTestConfigurationsToRun(
      Worker worker, WorkerUnitId workerUnitId) {
    var workerUnit = worker.findWorkerUnit(workerUnitId);

    return switch (worker.getType()) {
      case ALL, GROUP, FILE ->
          testConfigurationRepositoryPort.findAllIds(
              worker.getEnvironmentId(), workerUnit.getFilter().fileNames());
      case SUITE ->
          testConfigurationRepositoryPort.findAllIds(
              worker.getEnvironmentId(),
              workerUnit.getFilter().suiteFilter().suiteConfigurationId());
      case TEST -> List.of(workerUnit.getFilter().testFilter().testConfigurationId());
    };
  }

  private List<TestResult> processWorkerResults(
      Worker worker,
      Environment environment,
      ReportWorkerCommand command,
      WorkerUnitStatus workerUnitStatus,
      List<TestConfigurationId> testConfigurationsToRun) {

    try {
      if (WorkerUnitStatus.FAILED.equals(workerUnitStatus)
          || WorkerUnitStatus.SUCCESS.equals(workerUnitStatus)) {
        return processSuccessOrFailedStatus(worker, environment, command);
      }

      if (WorkerUnitStatus.CANCELED.equals(workerUnitStatus)) {
        return createCanceledResults(worker, testConfigurationsToRun);
      }

      return new ArrayList<>();
    } catch (ArtifactReportException e) {
      return createErrorResults(worker, testConfigurationsToRun, TestResultStatus.NO_REPORT_ERROR);
    } catch (Exception e) {
      log.error("Error while recording worker result", e);
      return createErrorResults(worker, testConfigurationsToRun, TestResultStatus.SYSTEM_ERROR);
    }
  }

  private List<TestResult> processSuccessOrFailedStatus(
      Worker worker, Environment environment, ReportWorkerCommand command) {

    var reportArtifacts =
        workerUnitPort.getWorkerReportArtifacts(
            environment.getSourceCodeInformation(), command.workerUnitId());

    var reports = workerExtractorPort.extractWorkerReportArtifacts(reportArtifacts);
    var results = new ArrayList<TestResult>();

    var testConfIdFilter = getTestConfigurationIdsFilter(worker);

    reports.forEach(
        report -> {
          processTestsWithoutSuite(report, worker, results, testConfIdFilter);
          processSuiteTests(report, worker, results, testConfIdFilter);
        });

    return results;
  }

  private ArrayList<TestConfigurationId> getTestConfigurationIdsFilter(Worker worker) {
    var testConfIdFilter = new ArrayList<TestConfigurationId>();
    if (worker.getWorkerUnits().getFirst().getFilter().testFilter() != null) {
      testConfIdFilter.add(
          worker.getWorkerUnits().getFirst().getFilter().testFilter().testConfigurationId());
    }
    if (worker.getWorkerUnits().getFirst().getFilter().suiteFilter() != null) {
      testConfIdFilter.addAll(
          testConfigurationRepositoryPort.findAllIds(
              worker.getEnvironmentId(),
              worker.getWorkerUnits().getFirst().getFilter().suiteFilter().suiteConfigurationId()));
    }
    return testConfIdFilter;
  }

  private void processTestsWithoutSuite(
      Report report,
      Worker worker,
      List<TestResult> results,
      ArrayList<TestConfigurationId> testConfIdFilter) {
    report
        .tests()
        .forEach(
            reportTest ->
                testConfigurationRepositoryPort
                    .findId(report.fileName(), SuiteTitle.noSuite(), reportTest.title())
                    .ifPresent(
                        configId -> {
                          if (testConfIdFilter.isEmpty() || testConfIdFilter.contains(configId)) {
                            results.add(createTestResult(worker, configId, reportTest));
                          }
                        }));
  }

  private void processSuiteTests(
      Report report,
      Worker worker,
      List<TestResult> results,
      ArrayList<TestConfigurationId> testConfIdFilter) {
    report
        .suites()
        .forEach(
            suite ->
                suite
                    .tests()
                    .forEach(
                        test ->
                            testConfigurationRepositoryPort
                                .findId(report.fileName(), suite.title(), test.title())
                                .ifPresent(
                                    configId -> {
                                      if (testConfIdFilter.isEmpty()
                                          || testConfIdFilter.contains(configId)) {
                                        results.add(createTestResult(worker, configId, test));
                                      }
                                    })));
  }

  private List<TestResult> createCanceledResults(
      Worker worker, List<TestConfigurationId> testConfigurationIds) {

    return testConfigurationIds.stream()
        .map(id -> createTestResultWithoutReport(worker, id, TestResultStatus.CANCELED))
        .toList();
  }

  private List<TestResult> createErrorResults(
      Worker worker,
      List<TestConfigurationId> testConfigurationIds,
      TestResultStatus testResultStatus) {

    return testConfigurationIds.stream()
        .map(id -> createTestResultWithoutReport(worker, id, testResultStatus))
        .toList();
  }

  private TestResult createTestResult(
      Worker worker, TestConfigurationId id, ReportTest reportTest) {
    var result = TestResult.create(worker, id, reportTest);
    result.createAuditInfo(worker.getAuditInfo().getCreatedBy(), clockPort.now());
    return result;
  }

  private TestResult createTestResultWithoutReport(
      Worker worker, TestConfigurationId id, TestResultStatus status) {
    var result = TestResult.createWithoutInformation(worker, id, status);
    result.createAuditInfo(worker.getAuditInfo().getCreatedBy(), clockPort.now());
    return result;
  }

  private void handleMissingTests(
      List<TestConfigurationId> expectedTests, List<TestResult> actualResults, Worker worker) {

    expectedTests.stream()
        .filter(
            expected ->
                actualResults.stream()
                    .noneMatch(actual -> actual.getTestConfigurationId().equals(expected)))
        .map(
            id -> createTestResultWithoutReport(worker, id, TestResultStatus.NO_CORRESPONDING_TEST))
        .forEach(actualResults::add);
  }

  private void publishWorkerUnitCompletedEvent(Worker worker) {
    eventPublisherPort.publishAsync(
        new WorkerUnitCompletedEvent(
            worker.getEnvironmentId(), worker.getAuditInfo().getCreatedBy(), worker));
  }

  private void finalizeWorker(Worker worker) {
    log.trace("Finalizing worker {}", worker.getId());
    testResultRepositoryPort.updateParentsConfigurationStatus(worker.getId());
    testResultRepositoryPort.clearAllWorkerId(worker.getId());
    workerRepositoryPort.delete(worker.getId());
    transactionManagerPort.registerAfterCommit(
        () ->
            eventPublisherPort.publishAsync(
                new WorkerCompletedEvent(
                    worker.getEnvironmentId(), worker.getAuditInfo().getCreatedBy(), worker)));
  }
}