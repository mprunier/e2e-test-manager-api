package fr.plum.e2e.manager.core.application.command.synchronization;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_ES6_TRANSPILATION;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_TYPESCRIPT_TRANSPILATION;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryJavascriptConverterAdapter.ERROR_ES6;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryJavascriptConverterAdapter.ERROR_TS;
import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentSynchronizedEvent;
import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationNotFoundException;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryFileSynchronizationAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemoryJavascriptConverterAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter.InMemorySourceCodeAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryEnvironmentRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryFileConfigurationRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySynchronizationRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.transaction.inmemory.adapter.InMemoryTransactionManagerAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessSynchronizationCommandHandlerTest {

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

  private ProcessSynchronizationCommandHandler handler;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;
  private InMemoryEnvironmentRepositoryAdapter environmentRepository;
  private InMemorySynchronizationRepositoryAdapter synchronizationRepository;
  private InMemoryFileConfigurationRepositoryAdapter fileConfigurationRepository;
  private InMemorySourceCodeAdapter sourceCodeAdapter;
  private InMemoryFileSynchronizationAdapter fileSynchronizationAdapter;
  private InMemoryJavascriptConverterAdapter javascriptConverterAdapter;
  private InMemoryTransactionManagerAdapter transactionManager;

  @BeforeEach
  void setUp() {
    clock = new InMemoryClockAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    environmentRepository = new InMemoryEnvironmentRepositoryAdapter();
    synchronizationRepository = new InMemorySynchronizationRepositoryAdapter();
    fileConfigurationRepository = new InMemoryFileConfigurationRepositoryAdapter();
    sourceCodeAdapter = new InMemorySourceCodeAdapter();
    fileSynchronizationAdapter = new InMemoryFileSynchronizationAdapter();
    javascriptConverterAdapter = new InMemoryJavascriptConverterAdapter();
    transactionManager = new InMemoryTransactionManagerAdapter();

    handler =
        new ProcessSynchronizationCommandHandler(
            clock,
            eventPublisher,
            environmentRepository,
            sourceCodeAdapter,
            fileSynchronizationAdapter,
            synchronizationRepository,
            transactionManager,
            fileConfigurationRepository,
            javascriptConverterAdapter);

    setupInitialData();
  }

  @Test
  void should_process_synchronization_successfully() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var fileName = new SynchronizationFileName("test.js");
    var fileContent = new SynchronizationFileContent("...");
    fileSynchronizationAdapter.addFile(project, fileName, fileContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertFalse(sync.get().isInProgress());
    assertTrue(sync.get().getErrors().isEmpty());

    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertEquals(1, configs.size());

    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (EnvironmentSynchronizedEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(ENVIRONMENT_ID, event.environmentId());
    assertEquals(ACTION_USERNAME, event.username());
    assertTrue(event.synchronizationErrors().isEmpty());
  }

  @Test
  void should_handle_environment_not_found() {
    // Given
    var nonExistentId = new EnvironmentId(UUID.randomUUID());
    var command = new CommonCommand(nonExistentId, ACTION_USERNAME);

    // When/Then
    assertThrows(EnvironmentNotFoundException.class, () -> handler.execute(command));
  }

  @Test
  void should_handle_synchronization_not_found() {
    // Given
    synchronizationRepository = new InMemorySynchronizationRepositoryAdapter(); // Reset repository
    handler =
        new ProcessSynchronizationCommandHandler(
            clock,
            eventPublisher,
            environmentRepository,
            sourceCodeAdapter,
            fileSynchronizationAdapter,
            synchronizationRepository,
            transactionManager,
            fileConfigurationRepository,
            javascriptConverterAdapter);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When/Then
    assertThrows(SynchronizationNotFoundException.class, () -> handler.execute(command));
  }

  @Test
  void should_handle_typescript_transpilation_error() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var fileName = new SynchronizationFileName("test.ts");
    var fileContent = new SynchronizationFileContent("..." + ERROR_TS);
    fileSynchronizationAdapter.addFile(project, fileName, fileContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertEquals(1, sync.get().getErrors().size());
    assertTrue(
        sync.get().getErrors().getFirst().error().value().contains(ERROR_TYPESCRIPT_TRANSPILATION));
  }

  @Test
  void should_handle_es6_transpilation_error() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var fileName = new SynchronizationFileName("test.js");
    var fileContent = new SynchronizationFileContent("..." + ERROR_ES6);
    fileSynchronizationAdapter.addFile(project, fileName, fileContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertEquals(1, sync.get().getErrors().size());
    assertTrue(sync.get().getErrors().getFirst().error().value().contains(ERROR_ES6_TRANSPILATION));
  }

  @Test
  void should_process_multiple_files() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var jsFile = new SynchronizationFileName("test1.js");
    var jsContent = new SynchronizationFileContent("...");
    var tsFile = new SynchronizationFileName("test2.ts");
    var tsContent = new SynchronizationFileContent("...");

    fileSynchronizationAdapter.addFile(project, jsFile, jsContent);
    fileSynchronizationAdapter.addFile(project, tsFile, tsContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertEquals(2, configs.size());

    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertTrue(sync.get().getErrors().isEmpty());
  }

  private void setupInitialData() {
    var environment =
        Environment.builder()
            .environmentId(ENVIRONMENT_ID)
            .environmentDescription(ENVIRONMENT_DESCRIPTION)
            .sourceCodeInformation(SOURCE_CODE_INFO)
            .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
            .maxParallelWorkers(new MaxParallelWorkers(1))
            .isEnabled(new EnvironmentIsEnabled(true))
            .variables(new ArrayList<>())
            .build();

    environmentRepository.save(environment);

    var sync = Synchronization.create(ENVIRONMENT_ID, AuditInfo.create(clock.now()));
    synchronizationRepository.save(sync);
  }
}
