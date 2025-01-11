package fr.plum.e2e.manager.core.application.command.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerInProgressEvent;
import fr.plum.e2e.manager.core.domain.model.exception.ConcurrentWorkersReachedException;
import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.FileNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.WorkerInTypeAllAlreadyInProgressException;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.configuration.inmemory.adapter.InMemoryConfigurationAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryWorkerUnitAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryEnvironmentRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryFileConfigurationRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryWorkerRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RunWorkerCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID = new EnvironmentId(UUID.randomUUID());
  private static final EnvironmentId OTHER_ENVIRONMENT_ID = new EnvironmentId(UUID.randomUUID());
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");
  private static final EnvironmentDescription ENVIRONMENT_DESCRIPTION =
      new EnvironmentDescription("Test Environment");
  private static final SourceCodeInformation SOURCE_CODE_INFO =
      SourceCodeInformation.builder()
          .projectId("project1")
          .branch("main")
          .token("token123")
          .build();
  public static final MaxParallelWorkers MAX_PARALLEL_WORKERS = new MaxParallelWorkers(2);

  private RunWorkerCommandHandler handler;
  private InMemoryWorkerRepositoryAdapter workerRepository;
  private InMemoryFileConfigurationRepositoryAdapter fileConfigurationRepository;
  private InMemoryEnvironmentRepositoryAdapter environmentRepository;
  private InMemoryWorkerUnitAdapter workerUnitAdapter;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;
  private InMemoryConfigurationAdapter configuration;

  @BeforeEach
  void setUp() {
    workerRepository = new InMemoryWorkerRepositoryAdapter();
    fileConfigurationRepository = new InMemoryFileConfigurationRepositoryAdapter();
    environmentRepository = new InMemoryEnvironmentRepositoryAdapter();
    workerUnitAdapter = new InMemoryWorkerUnitAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    clock = new InMemoryClockAdapter();
    configuration = new InMemoryConfigurationAdapter();

    handler =
        new RunWorkerCommandHandler(
            clock,
            eventPublisher,
            workerUnitAdapter,
            environmentRepository,
            fileConfigurationRepository,
            workerRepository,
            configuration);

    setupEnvironmentData();
  }

  @Test
  void should_run_single_worker_for_file() {
    // Given
    var fileName = new FileName("test.spec.js");
    var fileConfig = setUpFileConfiguration(fileName, null);
    fileConfigurationRepository.save(List.of(fileConfig));

    var command =
        RunWorkerCommand.builder()
            .environmentId(ENVIRONMENT_ID)
            .username(ACTION_USERNAME)
            .fileName(fileName)
            .variables(Collections.emptyList())
            .build();

    // When
    handler.execute(command);

    // Then
    var executions = workerUnitAdapter.getExecutions();
    assertEquals(1, executions.size());
    var execution = executions.getFirst();
    assertEquals(List.of(fileName), execution.workerUnitFilter().fileNames());
    assertFalse(execution.workerIsRecordVideo().value());

    var events = eventPublisher.getPublishedEvents();
    assertEquals(1, events.size());
    var event = (WorkerInProgressEvent) events.getFirst();
    assertEquals(ENVIRONMENT_ID, event.environmentId());
    assertEquals(ACTION_USERNAME, event.username());
  }

  @Test
  void should_run_single_worker_for_suite() {
    // Given
    var suiteId = SuiteConfigurationId.generate();
    var suiteTitle = new SuiteTitle("Suite Title");
    var fileName = new FileName("test.spec.js");
    var fileConfig =
        FileConfiguration.builder()
            .fileName(fileName)
            .environmentId(ENVIRONMENT_ID)
            .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
            .suites(
                List.of(
                    setUpSuiteConfiguration(
                        suiteId,
                        suiteTitle,
                        setUpTestConfiguration(
                            TestConfigurationId.generate(), new TestTitle("Test Title")))))
            .build();
    fileConfigurationRepository.save(List.of(fileConfig));

    var command =
        RunWorkerCommand.builder()
            .environmentId(ENVIRONMENT_ID)
            .username(ACTION_USERNAME)
            .suiteConfigurationId(suiteId)
            .variables(Collections.emptyList())
            .build();

    // When
    handler.execute(command);

    // Then
    var executions = workerUnitAdapter.getExecutions();
    assertEquals(1, executions.size());
    var execution = executions.getFirst();
    assertEquals(List.of(fileName), execution.workerUnitFilter().fileNames());
    assertNotNull(execution.workerUnitFilter().suiteFilter());
    assertEquals(suiteId, execution.workerUnitFilter().suiteFilter().suiteConfigurationId());
    assertEquals(suiteTitle, execution.workerUnitFilter().suiteFilter().suiteTitle());
    assertFalse(execution.workerIsRecordVideo().value());
  }

  @Test
  void should_run_single_worker_for_test() {
    // Given

    var testId = TestConfigurationId.generate();
    var testTitle = new TestTitle("Test Case");
    var fileName = new FileName("test.spec.js");
    var fileConfig =
        FileConfiguration.builder()
            .fileName(fileName)
            .environmentId(ENVIRONMENT_ID)
            .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
            .suites(
                List.of(
                    setUpSuiteConfiguration(
                        SuiteConfigurationId.generate(),
                        new SuiteTitle("Suite"),
                        setUpTestConfiguration(testId, testTitle))))
            .build();
    fileConfigurationRepository.save(List.of(fileConfig));

    var command =
        RunWorkerCommand.builder()
            .environmentId(ENVIRONMENT_ID)
            .username(ACTION_USERNAME)
            .testConfigurationId(testId)
            .build();

    // When
    handler.execute(command);

    // Then
    var executions = workerUnitAdapter.getExecutions();
    assertEquals(1, executions.size());
    var execution = executions.getFirst();
    assertEquals(List.of(fileName), execution.workerUnitFilter().fileNames());
    assertNotNull(execution.workerUnitFilter().testFilter());
    assertEquals(testId, execution.workerUnitFilter().testFilter().testConfigurationId());
    assertEquals(testTitle, execution.workerUnitFilter().testFilter().testTitle());
    assertTrue(execution.workerIsRecordVideo().value());
  }

  @Test
  void should_run_parallel_workers_when_max_parallel_greater_than_one() {
    // Given
    var fileName1 = new FileName("test1.spec.js");
    var fileName2 = new FileName("test2.spec.js");
    var fileName3 = new FileName("test3.spec.js");

    var fileConfigs =
        List.of(
            setUpFileConfiguration(fileName1, new GroupName("group1")),
            setUpFileConfiguration(fileName2, new GroupName("group1")),
            setUpFileConfiguration(fileName3, null));
    fileConfigurationRepository.save(fileConfigs);

    var command =
        RunWorkerCommand.builder().environmentId(ENVIRONMENT_ID).username(ACTION_USERNAME).build();

    // When
    handler.execute(command);

    // Then
    var executions = workerUnitAdapter.getExecutions();
    assertTrue(executions.size() > 1);

    var allFiles =
        executions.stream().flatMap(exec -> exec.workerUnitFilter().fileNames().stream()).toList();
    assertTrue(allFiles.containsAll(List.of(fileName1, fileName2, fileName3)));

    executions.stream()
        .filter(exec -> exec.workerUnitFilter().fileNames().contains(fileName1))
        .forEach(exec -> assertTrue(exec.workerUnitFilter().fileNames().contains(fileName2)));

    assertTrue(executions.stream().noneMatch(exec -> exec.workerIsRecordVideo().value()));
  }

  @Test
  void should_throw_exception_when_max_concurrent_workers_reached() {
    // Given
    configuration.updateMaxJobInParallel(1);
    workerRepository.save(
        Worker.create(
            ENVIRONMENT_ID,
            AuditInfo.create(ACTION_USERNAME, clock.now()),
            WorkerType.FILE,
            Collections.emptyList()));

    var command =
        RunWorkerCommand.builder().environmentId(ENVIRONMENT_ID).username(ACTION_USERNAME).build();

    // When & Then
    assertThrows(ConcurrentWorkersReachedException.class, () -> handler.execute(command));
  }

  @Test
  void should_throw_exception_when_all_type_worker_already_in_progress() {
    // Given
    workerRepository.save(
        Worker.create(
            ENVIRONMENT_ID,
            AuditInfo.create(ACTION_USERNAME, clock.now()),
            WorkerType.ALL,
            Collections.emptyList()));

    var command =
        RunWorkerCommand.builder().environmentId(ENVIRONMENT_ID).username(ACTION_USERNAME).build();

    // When & Then
    assertThrows(WorkerInTypeAllAlreadyInProgressException.class, () -> handler.execute(command));
  }

  @Test
  void should_throw_exception_when_environment_not_found() {
    // Given
    var command =
        RunWorkerCommand.builder()
            .environmentId(OTHER_ENVIRONMENT_ID)
            .username(ACTION_USERNAME)
            .build();

    // When & Then
    assertThrows(EnvironmentNotFoundException.class, () -> handler.execute(command));
  }

  @Test
  void should_throw_exception_when_file_not_found() {
    // Given
    var command =
        RunWorkerCommand.builder()
            .environmentId(ENVIRONMENT_ID)
            .username(ACTION_USERNAME)
            .suiteConfigurationId(new SuiteConfigurationId(UUID.randomUUID()))
            .build();

    // When & Then
    assertThrows(FileNotFoundException.class, () -> handler.execute(command));
  }

  private void setupEnvironmentData() {
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

  private FileConfiguration setUpFileConfiguration(FileName fileName1, GroupName groupName) {
    return FileConfiguration.builder()
        .fileName(fileName1)
        .environmentId(ENVIRONMENT_ID)
        .group(groupName)
        .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
        .suites(
            List.of(
                setUpSuiteConfiguration(
                    SuiteConfigurationId.generate(),
                    new SuiteTitle("Suite"),
                    setUpTestConfiguration(TestConfigurationId.generate(), new TestTitle("Test")))))
        .build();
  }

  private static SuiteConfiguration setUpSuiteConfiguration(
      SuiteConfigurationId suiteConfigurationId, SuiteTitle suiteTitle, TestConfiguration test) {
    return SuiteConfiguration.builder()
        .suiteConfigurationId(suiteConfigurationId)
        .title(suiteTitle)
        .status(ConfigurationStatus.defaultStatus())
        .tags(new ArrayList<>())
        .variables(new ArrayList<>())
        .tests(List.of(test))
        .build();
  }

  private static TestConfiguration setUpTestConfiguration(
      TestConfigurationId testConfigurationId, TestTitle title) {
    return TestConfiguration.builder()
        .testConfigurationId(testConfigurationId)
        .title(title)
        .position(new Position(1))
        .status(ConfigurationStatus.defaultStatus())
        .tags(new ArrayList<>())
        .variables(new ArrayList<>())
        .build();
  }
}
