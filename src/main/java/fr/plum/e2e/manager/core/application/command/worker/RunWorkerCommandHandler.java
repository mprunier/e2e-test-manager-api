package fr.plum.e2e.manager.core.application.command.worker;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_GROUP_NAME;

import fr.plum.e2e.manager.core.application.command.worker.dto.WorkerFiles;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterSuite;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerInProgressEvent;
import fr.plum.e2e.manager.core.domain.model.exception.WorkerInTypeAllAlreadyInProgressException;
import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.WorkerUnitPort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.core.domain.service.FileConfigurationService;
import fr.plum.e2e.manager.core.domain.service.WorkerService;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@ApplicationScoped
public class RunWorkerCommandHandler implements CommandHandler<RunWorkerCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final WorkerUnitPort workerUnitPort;
  private final FileConfigurationRepositoryPort fileConfigurationRepositoryPort;
  private final WorkerRepositoryPort workerRepositoryPort;

  private final WorkerService workerService;
  private final EnvironmentService environmentService;
  private final FileConfigurationService fileConfigurationService;

  public RunWorkerCommandHandler(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerUnitPort workerUnitPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      ConfigurationPort configurationPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.workerUnitPort = workerUnitPort;
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

    var worker =
        Worker.create(
            command.environmentId(),
            AuditInfo.create(command.username(), clockPort.now()),
            command.getWorkerType(),
            command.variables());

    if (command.getWorkerType() == WorkerType.ALL
        && environment.getMaxParallelWorkers().value() > 1) {
      executeParallelWorkers(command, environment, worker);
    } else {
      executeSingleWorker(command, environment, worker);
    }

    workerRepositoryPort.save(worker);

    eventPublisherPort.publishAsync(
        new WorkerInProgressEvent(command.environmentId(), command.username(), worker));
  }

  private void assertWorkerInTypeAll(RunWorkerCommand command) {
    if (command.getWorkerType() == WorkerType.ALL
        && workerRepositoryPort
            .assertNotWorkerInProgressByType(command.environmentId(), command.getWorkerType())
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
                    environment,
                    workerFilter,
                    command.variables(),
                    new WorkerIsRecordVideo(false),
                    worker));
  }

  private void runWorker(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> variables,
      WorkerIsRecordVideo workerIsRecordVideo,
      Worker worker) {
    var workerUnitId =
        workerUnitPort.runWorker(environment, workerUnitFilter, variables, workerIsRecordVideo);
    var workerUnit = WorkerUnit.create(workerUnitId, null, workerUnitFilter);
    worker.addWorkerUnit(workerUnit);
  }

  private void distributeGroupedFiles(
      Map<GroupName, List<FileName>> fileNamesMapByGroupName, List<WorkerFiles> workers) {
    fileNamesMapByGroupName.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(new GroupName(NO_GROUP_NAME)))
        .forEach(entry -> getSmallestBuildWorker(workers).addAll(entry.getValue()));
  }

  private void distributeUngroupedFiles(
      Map<GroupName, List<FileName>> fileNamesMapByGroupName, List<WorkerFiles> workers) {
    fileNamesMapByGroupName
        .get(new GroupName(NO_GROUP_NAME))
        .forEach(fileName -> getSmallestBuildWorker(workers).add(fileName));
  }

  private void executeSingleWorker(
      RunWorkerCommand command, Environment environment, Worker worker) {
    var fileNamesFilter = new ArrayList<FileName>();
    WorkerUnitFilterSuite suiteFilter = null;
    WorkerUnitFilterTest testFilter = null;

    switch (command.getWorkerType()) {
      case ALL -> filterByAll(environment, fileNamesFilter);
      case GROUP -> filterByGroup(command, environment, fileNamesFilter);
      case FILE -> fileNamesFilter.add(command.fileName());
      case SUITE -> suiteFilter = filterBySuite(command, environment, fileNamesFilter);
      case TEST -> testFilter = filterByTest(command, environment, fileNamesFilter);
    }

    var workerFilter =
        new WorkerUnitFilter(fileNamesFilter, command.tag(), suiteFilter, testFilter);
    var workerIsRecordVideo = getWorkerIsRecordVideo(workerFilter);
    runWorker(environment, workerFilter, command.variables(), workerIsRecordVideo, worker);
  }

  private void filterByAll(Environment environment, ArrayList<FileName> fileNamesFilter) {
    fileNamesFilter.addAll(fileConfigurationRepositoryPort.findAllFileNames(environment.getId()));
  }

  private void filterByGroup(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileNames =
        fileConfigurationRepositoryPort.findAllFileNames(environment.getId(), command.groupName());
    fileNamesFilter.addAll(fileNames);
  }

  private WorkerUnitFilterSuite filterBySuite(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileConfiguration =
        fileConfigurationService.getFileConfigurationBySuiteId(
            environment.getId(), command.suiteConfigurationId());
    var suiteConfiguration =
        fileConfiguration.getSuiteConfiguration(command.suiteConfigurationId());
    fileNamesFilter.add(fileConfiguration.getId());
    return new WorkerUnitFilterSuite(suiteConfiguration.getId(), suiteConfiguration.getTitle());
  }

  private WorkerUnitFilterTest filterByTest(
      RunWorkerCommand command, Environment environment, ArrayList<FileName> fileNamesFilter) {
    var fileConfiguration =
        fileConfigurationService.getFileConfigurationByTestId(
            environment.getId(), command.testConfigurationId());
    var testConfiguration = fileConfiguration.getTestConfiguration(command.testConfigurationId());
    fileNamesFilter.add(fileConfiguration.getId());
    return new WorkerUnitFilterTest(testConfiguration.getId(), testConfiguration.getTitle());
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
