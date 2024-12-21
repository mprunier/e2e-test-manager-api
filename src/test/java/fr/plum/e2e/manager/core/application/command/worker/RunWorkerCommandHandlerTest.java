package fr.plum.e2e.manager.core.application.command.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerInProgressEvent;
import fr.plum.e2e.manager.core.domain.model.exception.WorkerInTypeAllAlreadyInProgressException;
import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.WorkerUnitPort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunWorkerCommandHandlerTest {

  @Mock private ClockPort clockPort;
  @Mock private EventPublisherPort eventPublisher;
  @Mock private WorkerUnitPort workerUnitPort;
  @Mock private EnvironmentRepositoryPort environmentRepository;
  @Mock private FileConfigurationRepositoryPort fileConfigurationRepository;
  @Mock private WorkerRepositoryPort workerRepository;
  @Mock private ConfigurationPort configurationPort;

  private RunWorkerCommandHandler commandHandler;

  private final ZonedDateTime NOW = ZonedDateTime.now();
  private final EnvironmentId ENV_ID = EnvironmentId.generate();
  private final ActionUsername USERNAME = new ActionUsername("testUser");

  @BeforeEach
  void setUp() {
    commandHandler =
        new RunWorkerCommandHandler(
            clockPort,
            eventPublisher,
            workerUnitPort,
            environmentRepository,
            fileConfigurationRepository,
            workerRepository,
            configurationPort);
  }

  @Test
  void execute_SingleWorker_Success() {
    // Given
    RunWorkerCommand command =
        RunWorkerCommand.builder()
            .environmentId(ENV_ID)
            .username(USERNAME)
            .fileName(new FileName("test.spec.js"))
            .build();

    Environment environment = createTestEnvironment(1);
    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(clockPort.now()).thenReturn(NOW);
    when(workerUnitPort.runWorker(any(), any(), any(), any()))
        .thenReturn(new WorkerUnitId("worker-1"));
    when(configurationPort.getMaxJobInParallel()).thenReturn(5);

    // When
    commandHandler.execute(command);

    // Then
    verify(workerRepository).save(any(Worker.class));
    verify(eventPublisher).publishAsync(any(WorkerInProgressEvent.class));
  }

  @Test
  void execute_ParallelWorkers_Success() {
    // Given
    RunWorkerCommand command =
        RunWorkerCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Environment environment = createTestEnvironment(3);
    Map<GroupName, List<FileName>> fileMap = new HashMap<>();
    fileMap.put(new GroupName("group1"), List.of(new FileName("test1.spec.js")));
    fileMap.put(new GroupName("group2"), List.of(new FileName("test2.spec.js")));
    fileMap.put(null, Collections.emptyList());

    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(fileConfigurationRepository.findAllFileNamesMapByGroupName(ENV_ID)).thenReturn(fileMap);
    when(workerUnitPort.runWorker(any(), any(), any(), any()))
        .thenReturn(new WorkerUnitId("worker-1"));
    when(clockPort.now()).thenReturn(NOW);
    when(workerRepository.assertNotWorkerInProgressByType(any(), any()))
        .thenReturn(Optional.empty());
    when(configurationPort.getMaxJobInParallel()).thenReturn(5);

    // When
    commandHandler.execute(command);

    // Then
    verify(workerRepository).save(any(Worker.class));
    verify(eventPublisher).publishAsync(any(WorkerInProgressEvent.class));
  }

  @Test
  void execute_WorkerTypeAll_AlreadyInProgress_ThrowsException() {
    // Given
    RunWorkerCommand command =
        RunWorkerCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Worker existingWorker = Worker.initialize(ENV_ID, WorkerType.ALL);
    when(workerRepository.assertNotWorkerInProgressByType(ENV_ID, WorkerType.ALL))
        .thenReturn(Optional.of(existingWorker));

    // Then
    assertThrows(
        WorkerInTypeAllAlreadyInProgressException.class,
        () -> {
          commandHandler.execute(command);
        });
  }

  @Test
  void execute_WithTestFilter_Success() {
    // Given
    TestConfigurationId testId = TestConfigurationId.generate();
    RunWorkerCommand command =
        RunWorkerCommand.builder()
            .environmentId(ENV_ID)
            .username(USERNAME)
            .testConfigurationId(testId)
            .build();

    Environment environment = createTestEnvironment(1);
    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(workerUnitPort.runWorker(any(), any(), any(), any()))
        .thenReturn(new WorkerUnitId("worker-1"));
    when(clockPort.now()).thenReturn(NOW);
    when(configurationPort.getMaxJobInParallel()).thenReturn(5);

    var testConfiguration =
        TestConfiguration.builder().id(testId).title(new TestTitle("title 1")).build();
    var suiteConfiguration = SuiteConfiguration.builder().tests(List.of(testConfiguration)).build();
    FileConfiguration fileConfiguration =
        FileConfiguration.builder()
            .id(new FileName("test.spec.js"))
            .suites(List.of(suiteConfiguration))
            .build();
    when(fileConfigurationRepository.find(ENV_ID, testId))
        .thenReturn(Optional.of(fileConfiguration));

    // When
    commandHandler.execute(command);

    // Then
    ArgumentCaptor<Worker> workerCaptor = ArgumentCaptor.forClass(Worker.class);
    verify(workerRepository).save(workerCaptor.capture());

    Worker savedWorker = workerCaptor.getValue();
    assertEquals(WorkerType.TEST, savedWorker.getType());
    assertEquals(ENV_ID, savedWorker.getEnvironmentId());
  }

  private Environment createTestEnvironment(int maxParallelWorkers) {
    SourceCodeInformation sourceCodeInfo =
        SourceCodeInformation.builder()
            .projectId("testProject")
            .token("testToken")
            .branch("main")
            .build();

    return Environment.builder()
        .id(ENV_ID)
        .environmentDescription(new EnvironmentDescription("Test Environment"))
        .sourceCodeInformation(sourceCodeInfo)
        .maxParallelWorkers(new MaxParallelWorkers(maxParallelWorkers))
        .auditInfo(AuditInfo.create(USERNAME, NOW))
        .build();
  }
}
