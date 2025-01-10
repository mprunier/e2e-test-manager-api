package fr.plum.e2e.manager.core.application.command.worker;

import static org.assertj.core.api.Assertions.assertThat;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterSuite;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.dto.report.Report;
import fr.plum.e2e.manager.core.domain.model.dto.report.ReportTest;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryWorkerExtractorAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryWorkerUnitAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryEnvironmentRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryTestConfigurationRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryTestResultRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryWorkerRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.transaction.inmemory.adapter.InMemoryTransactionManagerAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportWorkerCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID = new EnvironmentId(UUID.randomUUID());
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");
  private static final EnvironmentDescription ENVIRONMENT_DESCRIPTION =
      new EnvironmentDescription("Test Environment");
  private static final SourceCodeInformation SOURCE_CODE_INFO =
      SourceCodeInformation.builder()
          .projectId("project1")
          .branch("main")
          .token("token123")
          .build();
  private static final MaxParallelWorkers MAX_PARALLEL_WORKERS = new MaxParallelWorkers(2);
  private static final FileName TEST_FILE_NAME = new FileName("test.spec.js");
  private static final TestTitle TEST_TITLE = new TestTitle("Test 1");
  private static final SuiteTitle SUITE_TITLE = new SuiteTitle("Suite 1");

  private ReportWorkerCommandHandler handler;
  private InMemoryWorkerRepositoryAdapter workerRepository;
  private InMemoryEnvironmentRepositoryAdapter environmentRepository;
  private InMemoryTestConfigurationRepositoryAdapter testConfigurationRepository;
  private InMemoryTestResultRepositoryAdapter testResultRepository;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryWorkerUnitAdapter workerUnitPort;
  private InMemoryWorkerExtractorAdapter workerExtractor;
  private InMemoryClockAdapter clock;
  private TestConfigurationId sharedTestConfigId;
  private SuiteConfigurationId sharedSuiteConfigId;

  @BeforeEach
  void setUp() {
    workerRepository = new InMemoryWorkerRepositoryAdapter();
    environmentRepository = new InMemoryEnvironmentRepositoryAdapter();
    testConfigurationRepository = new InMemoryTestConfigurationRepositoryAdapter();
    testResultRepository = new InMemoryTestResultRepositoryAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    workerUnitPort = new InMemoryWorkerUnitAdapter();
    workerExtractor = new InMemoryWorkerExtractorAdapter();
    InMemoryTransactionManagerAdapter transactionManager = new InMemoryTransactionManagerAdapter();
    clock = new InMemoryClockAdapter();
    handler =
        new ReportWorkerCommandHandler(
            clock,
            eventPublisher,
            workerUnitPort,
            testConfigurationRepository,
            workerRepository,
            workerExtractor,
            testResultRepository,
            transactionManager,
            environmentRepository);
    setupEnvironment();
    sharedTestConfigId = new TestConfigurationId(UUID.randomUUID());
    sharedSuiteConfigId = new SuiteConfigurationId(UUID.randomUUID());
    setupTestConfiguration(sharedTestConfigId);
    workerUnitPort.clear();
    workerExtractor.clear();
  }

  @Test
  void should_do_nothing_when_worker_not_found() {
    handler.execute(new ReportWorkerCommand(new WorkerUnitId("non-existent")));
    assertThat(testResultRepository.getResults()).isEmpty();
    assertThat(eventPublisher.getPublishedEvents()).isEmpty();
  }

  @Test
  void should_do_nothing_when_worker_status_in_progress() {
    var workerId = setupWorker(WorkerType.TEST);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.IN_PROGRESS);

    handler.execute(new ReportWorkerCommand(workerId));

    assertThat(testResultRepository.getResults()).isEmpty();
    assertThat(eventPublisher.getPublishedEvents()).isEmpty();
  }

  @Test
  void should_handle_single_test_success() {
    var workerId = setupWorker(WorkerType.TEST);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.SUCCESS);
    setupSuccessReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.SUCCESS);
  }

  @Test
  void should_handle_single_test_canceled() {
    var workerId = setupWorker(WorkerType.TEST);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.CANCELED);

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.CANCELED);
  }

  @Test
  void should_handle_single_test_failed() {
    var workerId = setupWorker(WorkerType.TEST);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.FAILED);
    setupFailedReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.FAILED);
  }

  @Test
  void should_handle_suite_success() {
    var workerId = setupWorker(WorkerType.SUITE);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.SUCCESS);
    setupSuccessReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.SUCCESS);
  }

  @Test
  void should_handle_suite_canceled() {
    var workerId = setupWorker(WorkerType.SUITE);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.CANCELED);

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.CANCELED);
  }

  @Test
  void should_handle_suite_failed() {
    var workerId = setupWorker(WorkerType.SUITE);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.FAILED);
    setupFailedReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.FAILED);
  }

  @Test
  void should_handle_all_type_success() {
    var workerId = setupWorker(WorkerType.ALL);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.SUCCESS);
    setupSuccessReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.SUCCESS);
  }

  @Test
  void should_handle_all_type_canceled() {
    var workerId = setupWorker(WorkerType.ALL);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.CANCELED);

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.CANCELED);
  }

  @Test
  void should_handle_all_type_failed() {
    var workerId = setupWorker(WorkerType.ALL);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.FAILED);
    setupFailedReport();

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.FAILED);
  }

  @Test
  void should_finalize_worker_on_last_unit_completion() {
    var worker =
        Worker.create(
            ENVIRONMENT_ID,
            AuditInfo.create(ACTION_USERNAME, clock.now()),
            WorkerType.ALL,
            Collections.emptyList());

    var unit1Id = new WorkerUnitId("unit-1");
    var unit2Id = new WorkerUnitId("unit-2");
    worker.addWorkerUnit(
        WorkerUnit.create(unit1Id, WorkerUnitStatus.SUCCESS, createTestFilter(sharedTestConfigId)));
    worker.addWorkerUnit(WorkerUnit.create(unit2Id, null, createTestFilter(sharedTestConfigId)));
    workerRepository.save(worker);

    workerUnitPort.setWorkerStatus(unit2Id, WorkerUnitStatus.SUCCESS);
    setupSuccessReport();

    handler.execute(new ReportWorkerCommand(unit2Id));

    assertThat(testResultRepository.getResults()).hasSize(1);
    assertThat(eventPublisher.getPublishedEvents())
        .hasSize(2)
        .anyMatch(event -> event instanceof WorkerCompletedEvent);
    assertThat(workerRepository.countAll()).isZero();
  }

  @Test
  void should_handle_report_error() {
    var workerId = setupWorker(WorkerType.TEST);
    workerUnitPort.setWorkerStatus(workerId, WorkerUnitStatus.FAILED);
    workerExtractor.setThrowError(true);

    handler.execute(new ReportWorkerCommand(workerId));

    assertResult(TestResultStatus.NO_REPORT_ERROR);
  }

  private void setupEnvironment() {
    var environment =
        Environment.builder()
            .environmentId(ENVIRONMENT_ID)
            .environmentDescription(ENVIRONMENT_DESCRIPTION)
            .sourceCodeInformation(SOURCE_CODE_INFO)
            .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
            .maxParallelWorkers(MAX_PARALLEL_WORKERS)
            .isEnabled(new EnvironmentIsEnabled(true))
            .variables(new ArrayList<>())
            .build();

    environmentRepository.save(environment);
  }

  private WorkerUnitId setupWorker(WorkerType type) {
    var worker =
        Worker.create(
            ENVIRONMENT_ID,
            AuditInfo.create(ACTION_USERNAME, clock.now()),
            type,
            Collections.emptyList());

    var unitId = new WorkerUnitId("test-1");
    WorkerUnitFilter filter;
    if (type == WorkerType.TEST) {
      filter = createTestFilter(sharedTestConfigId);
    } else if (type == WorkerType.SUITE) {
      filter = createSuiteFilter(sharedSuiteConfigId);
    } else {
      filter = WorkerUnitFilter.builder().fileNames(List.of(TEST_FILE_NAME)).build();
    }
    worker.addWorkerUnit(WorkerUnit.create(unitId, null, filter));
    workerRepository.save(worker);

    return unitId;
  }

  private void setupTestConfiguration(TestConfigurationId testConfigId) {
    testConfigurationRepository.save(
        ENVIRONMENT_ID, TEST_FILE_NAME, SuiteTitle.noSuite(), TEST_TITLE, testConfigId);
    testConfigurationRepository.saveSuiteConfiguration(
        ENVIRONMENT_ID, sharedSuiteConfigId, testConfigId);
  }

  private WorkerUnitFilter createSuiteFilter(SuiteConfigurationId suiteConfigurationId) {
    return WorkerUnitFilter.builder()
        .fileNames(List.of(TEST_FILE_NAME))
        .suiteFilter(new WorkerUnitFilterSuite(suiteConfigurationId, SUITE_TITLE))
        .build();
  }

  private WorkerUnitFilter createTestFilter(TestConfigurationId testConfigId) {
    return WorkerUnitFilter.builder()
        .fileNames(List.of(TEST_FILE_NAME))
        .testFilter(new WorkerUnitFilterTest(testConfigId, TEST_TITLE))
        .build();
  }

  private void setupSuccessReport() {
    var test = ReportTest.builder().title(TEST_TITLE).status(TestResultStatus.SUCCESS).build();
    var report = new Report(TEST_FILE_NAME, List.of(test), Collections.emptyList());
    workerExtractor.setReports(List.of(report));
  }

  private void setupFailedReport() {
    var test = ReportTest.builder().title(TEST_TITLE).status(TestResultStatus.FAILED).build();
    var report = new Report(TEST_FILE_NAME, List.of(test), Collections.emptyList());
    workerExtractor.setReports(List.of(report));
  }

  private void assertResult(TestResultStatus expectedStatus) {
    var results = testResultRepository.getResults();
    assertThat(results)
        .hasSize(1)
        .first()
        .matches(result -> result.getStatus().equals(expectedStatus));
  }
}
