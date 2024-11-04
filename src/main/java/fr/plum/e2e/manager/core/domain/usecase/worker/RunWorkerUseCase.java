package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerGroupInProgressEvent;
import fr.plum.e2e.manager.core.domain.model.exception.WorkerInTypeAllAlreadyInProgressException;
import fr.plum.e2e.manager.core.domain.port.out.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.core.domain.service.FileConfigurationService;
import fr.plum.e2e.manager.core.domain.service.WorkerService;
import fr.plum.e2e.manager.core.domain.usecase.worker.dto.WorkerFiles;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class RunWorkerUseCase implements CommandUseCase<RunWorkerCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final WorkerPort workerPort;
  private final FileConfigurationRepositoryPort fileConfigurationRepositoryPort;
  private final WorkerRepositoryPort workerRepositoryPort;

  private final WorkerService workerService;
  private final EnvironmentService environmentService;
  private final FileConfigurationService fileConfigurationService;

  public RunWorkerUseCase(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerPort workerPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      ConfigurationPort configurationPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.workerPort = workerPort;
    this.fileConfigurationRepositoryPort = fileConfigurationRepositoryPort;
    this.workerRepositoryPort = workerRepositoryPort;
    this.workerService = new WorkerService(workerRepositoryPort, configurationPort);
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
    this.fileConfigurationService = new FileConfigurationService(fileConfigurationRepositoryPort);
  }

  @Override
  public void execute(RunWorkerCommand command) {
    assertWorkerInTypeAll(command);
    workerService.assertWorkerNotReached();

    var environment = environmentService.getEnvironment(command.environmentId());

    var workerGroup = Worker.initialize(command.environmentId(), command.getWorkerType());
    workerGroup.updateAuditInfo(command.username(), clockPort.now());
    workerGroup.addVariables(command.variables());

    if (command.getWorkerType() == WorkerType.ALL
        && environment.getMaxParallelWorkers().value() > 1) {
      executeParallelWorkers(command, environment, workerGroup);
    } else {
      executeSingleWorker(command, environment, workerGroup);
    }

    workerRepositoryPort.save(workerGroup);

    eventPublisherPort.publishAsync(
        new WorkerGroupInProgressEvent(command.environmentId(), command.username(), workerGroup));
  }

  private void assertWorkerInTypeAll(RunWorkerCommand command) {
    if (command.getWorkerType() == WorkerType.ALL
        && workerRepositoryPort
            .assertNotWorkerGroupInProgressByType(command.environmentId(), command.getWorkerType())
            .isPresent()) {
      throw new WorkerInTypeAllAlreadyInProgressException();
    }
  }

  private void executeParallelWorkers(
      RunWorkerCommand command, Environment environment, Worker worker) {
    var fileNamesMapByGroupName =
        fileConfigurationRepositoryPort.findAllFileNamesMapByGroupName(environment.getId());
    var workers = initializeWorkerLists(environment.getMaxParallelWorkers().value());

    distributeGroupedFiles(fileNamesMapByGroupName, workers);
    distributeUngroupedFiles(fileNamesMapByGroupName, workers);

    workers.stream()
        .filter(workerFiles -> !workerFiles.isEmpty())
        .map(WorkerFiles::toWorkerFilter)
        .forEach(
            workerFilter ->
                runWorker(
                    environment.getSourceCodeInformation(),
                    workerFilter,
                    command.variables(),
                    new WorkerIsRecordVideo(false),
                    worker));
  }

  private void runWorker(
      SourceCodeInformation sourceCodeInformation,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> variables,
      WorkerIsRecordVideo workerIsRecordVideo,
      Worker workerGroup) {
    var workerId =
        workerPort.runWorker(
            sourceCodeInformation, workerUnitFilter, variables, workerIsRecordVideo);
    var worker = WorkerUnit.builder().id(workerId).filter(workerUnitFilter).build();
    workerGroup.addWorkerUnit(worker);
  }

  private void distributeGroupedFiles(
      Map<GroupName, List<FileName>> fileNamesMapByGroupName, List<WorkerFiles> workers) {
    fileNamesMapByGroupName
        .values()
        .forEach(fileNames -> getSmallestBuildWorker(workers).addAll(fileNames));
  }

  private void distributeUngroupedFiles(
      Map<GroupName, List<FileName>> fileNamesMapByGroupName, List<WorkerFiles> workers) {
    fileNamesMapByGroupName
        .get(null)
        .forEach(fileName -> getSmallestBuildWorker(workers).add(fileName));
  }

  private void executeSingleWorker(
      RunWorkerCommand command, Environment environment, Worker worker) {
    var fileNamesFilter = new ArrayList<FileName>();
    SuiteConfiguration suiteFiler = null;
    TestConfiguration testFilter = null;

    switch (command.getWorkerType()) {
      case GROUP -> filterByGroup(command, environment, fileNamesFilter);
      case FILE -> fileNamesFilter.add(command.fileName());
      case SUITE -> suiteFiler = filterBySuite(command, environment, fileNamesFilter);
      case TEST -> testFilter = filterByTest(command, environment, fileNamesFilter);
    }

    var workerFilter = new WorkerUnitFilter(fileNamesFilter, command.tag(), suiteFiler, testFilter);
    var workerIsRecordVideo = getWorkerIsRecordVideo(workerFilter);
    runWorker(
        environment.getSourceCodeInformation(),
        workerFilter,
        command.variables(),
        workerIsRecordVideo,
        worker);
  }

  private void filterByGroup(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileNames =
        fileConfigurationRepositoryPort.findAllFileNames(environment.getId(), command.groupName());
    fileNamesFilter.addAll(fileNames);
  }

  private SuiteConfiguration filterBySuite(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileConfiguration =
        fileConfigurationService.getFileConfigurationBySuiteId(
            environment.getId(), command.suiteConfigurationId());
    var suiteConfiguration =
        fileConfiguration.getSuiteConfiguration(command.suiteConfigurationId());
    fileNamesFilter.add(fileConfiguration.getId());
    return suiteConfiguration;
  }

  private TestConfiguration filterByTest(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileConfiguration =
        fileConfigurationService.getFileConfigurationByTestId(
            environment.getId(), command.testConfigurationId());
    var testConfiguration = fileConfiguration.getTestConfiguration(command.testConfigurationId());
    fileNamesFilter.add(fileConfiguration.getId());
    return testConfiguration;
  }

  private static WorkerIsRecordVideo getWorkerIsRecordVideo(WorkerUnitFilter workerUnitFilter) {
    if (workerUnitFilter.canRecordVideo()) {
      return new WorkerIsRecordVideo(true);
    } else {
      return new WorkerIsRecordVideo(false);
    }
  }

  private List<WorkerFiles> initializeWorkerLists(int workerCount) {
    return IntStream.range(0, workerCount).mapToObj(i -> new WorkerFiles()).toList();
  }

  private WorkerFiles getSmallestBuildWorker(List<WorkerFiles> workers) {
    return workers.stream()
        .min(Comparator.comparingInt(WorkerFiles::size))
        .orElseThrow(() -> new IllegalStateException("No workers available"));
  }
}
