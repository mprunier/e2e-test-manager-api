package fr.plum.e2e.manager.core.application.command.synchronization;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationIsInProgress;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentSynchronizedEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.JavascriptConverterPort;
import fr.plum.e2e.manager.core.domain.port.SourceCodePort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.TransactionManagerPort;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessSynchronizationCommandHandlerTest {

  @Mock private ClockPort clockPort;
  @Mock private EventPublisherPort eventPublisher;
  @Mock private EnvironmentRepositoryPort environmentRepository;
  @Mock private SourceCodePort sourceCodePort;
  @Mock private FileSynchronizationPort fileSynchronizationPort;
  @Mock private SynchronizationRepositoryPort synchronizationRepository;
  @Mock private TransactionManagerPort transactionManager;
  @Mock private FileConfigurationRepositoryPort fileConfigurationRepository;
  @Mock private JavascriptConverterPort javascriptConverter;

  private ProcessSynchronizationCommandHandler commandHandler;

  private final ZonedDateTime NOW = ZonedDateTime.now();
  private final EnvironmentId ENV_ID = EnvironmentId.generate();
  private final ActionUsername USERNAME = new ActionUsername("testUser");

  @BeforeEach
  void setUp() {
    commandHandler =
        new ProcessSynchronizationCommandHandler(
            clockPort,
            eventPublisher,
            environmentRepository,
            sourceCodePort,
            fileSynchronizationPort,
            synchronizationRepository,
            transactionManager,
            fileConfigurationRepository,
            javascriptConverter);

    when(clockPort.now()).thenReturn(NOW);
  }

  @Test
  void execute_SuccessfulSynchronization() {
    // Given
    CommonCommand command =
        CommonCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Environment environment = createTestEnvironment();
    Synchronization synchronization = createTestSynchronization();
    SourceCodeProject sourceProject = new SourceCodeProject(new File("/tmp/test"));
    Map<SynchronizationFileName, SynchronizationFileContent> processedFiles = new HashMap<>();
    processedFiles.put(
        new SynchronizationFileName("test.spec.js"), new SynchronizationFileContent("content"));

    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(synchronizationRepository.find(ENV_ID)).thenReturn(Optional.of(synchronization));
    when(sourceCodePort.cloneRepository(any())).thenReturn(sourceProject);
    when(fileSynchronizationPort.listFiles(any())).thenReturn(processedFiles);

    // When
    commandHandler.execute(command);

    // Then
    verify(sourceCodePort).cloneRepository(environment.getSourceCodeInformation());
    verify(fileSynchronizationPort).listFiles(sourceProject);
    verify(transactionManager).executeInTransaction(any());
    verify(synchronizationRepository).update(any());
    verify(eventPublisher).publishAsync(any(EnvironmentSynchronizedEvent.class));
  }

  @Test
  void execute_HandlesSynchronizationError() {
    // Given
    CommonCommand command =
        CommonCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Environment environment = createTestEnvironment();
    Synchronization synchronization = createTestSynchronization();

    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(synchronizationRepository.find(ENV_ID)).thenReturn(Optional.of(synchronization));
    when(sourceCodePort.cloneRepository(any())).thenThrow(new RuntimeException("Clone failed"));

    // When
    commandHandler.execute(command);

    // Then
    ArgumentCaptor<EnvironmentSynchronizedEvent> eventCaptor =
        ArgumentCaptor.forClass(EnvironmentSynchronizedEvent.class);
    verify(eventPublisher).publishAsync(eventCaptor.capture());

    EnvironmentSynchronizedEvent capturedEvent = eventCaptor.getValue();
    assertFalse(capturedEvent.synchronizationErrors().isEmpty());
    assertEquals("Clone failed", capturedEvent.synchronizationErrors().getFirst().error().value());
  }

  @Test
  void execute_HandlesFileProcessingError() {
    // Given
    CommonCommand command =
        CommonCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Environment environment = createTestEnvironment();
    Synchronization synchronization = createTestSynchronization();
    SourceCodeProject sourceProject = new SourceCodeProject(new File("/tmp/test"));

    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(synchronizationRepository.find(ENV_ID)).thenReturn(Optional.of(synchronization));
    when(sourceCodePort.cloneRepository(any())).thenReturn(sourceProject);
    when(fileSynchronizationPort.listFiles(any()))
        .thenThrow(new RuntimeException("File processing failed"));

    // When
    commandHandler.execute(command);

    // Then
    ArgumentCaptor<EnvironmentSynchronizedEvent> eventCaptor =
        ArgumentCaptor.forClass(EnvironmentSynchronizedEvent.class);
    verify(eventPublisher).publishAsync(eventCaptor.capture());
    verify(synchronizationRepository).update(any());

    EnvironmentSynchronizedEvent capturedEvent = eventCaptor.getValue();
    assertFalse(capturedEvent.synchronizationErrors().isEmpty());
    assertTrue(
        capturedEvent.synchronizationErrors().stream()
            .anyMatch(error -> error.error().value().contains("File processing failed")));
  }

  private Environment createTestEnvironment() {
    SourceCodeInformation sourceCodeInfo =
        SourceCodeInformation.builder()
            .projectId("testProject")
            .token("testToken")
            .branch("main")
            .build();

    return Environment.builder()
        .environmentId(ENV_ID)
        .environmentDescription(new EnvironmentDescription("Test Environment"))
        .sourceCodeInformation(sourceCodeInfo)
        .auditInfo(AuditInfo.create(USERNAME, NOW))
        .maxParallelWorkers(new MaxParallelWorkers(1))
        .isEnabled(new EnvironmentIsEnabled(true))
        .variables(new ArrayList<>())
        .build();
  }

  private Synchronization createTestSynchronization() {
    return Synchronization.builder()
        .environmentId(ENV_ID)
        .synchronizationIsInProgress(new SynchronizationIsInProgress(false))
        .auditInfo(AuditInfo.create(USERNAME, NOW))
        .errors(new ArrayList<>())
        .build();
  }

  @Test
  void execute_CleanupIsCalledEvenOnFailure() {
    // Given
    CommonCommand command =
        CommonCommand.builder().environmentId(ENV_ID).username(USERNAME).build();

    Environment environment = createTestEnvironment();
    Synchronization synchronization = createTestSynchronization();
    SourceCodeProject sourceProject = new SourceCodeProject(new File("/tmp/test"));

    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
    when(synchronizationRepository.find(ENV_ID)).thenReturn(Optional.of(synchronization));
    when(sourceCodePort.cloneRepository(any())).thenReturn(sourceProject);
    when(fileSynchronizationPort.listFiles(any())).thenThrow(new RuntimeException("Test error"));

    // When
    commandHandler.execute(command);

    // Then
    verify(synchronizationRepository).update(any());
    verify(eventPublisher).publishAsync(any(EnvironmentSynchronizedEvent.class));
    // Verify cleanup was called
    verify(sourceCodePort).cloneRepository(any());
  }
}
