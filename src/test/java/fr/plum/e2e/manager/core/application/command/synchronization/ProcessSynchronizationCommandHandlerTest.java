package fr.plum.e2e.manager.core.application.command.synchronization;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_ES6_TRANSPILATION;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.ERROR_TYPESCRIPT_TRANSPILATION;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.GLOBAL_ENVIRONMENT_ERROR;
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
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
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
import java.util.List;
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
    synchronizationRepository = new InMemorySynchronizationRepositoryAdapter();
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

    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (EnvironmentSynchronizedEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(ENVIRONMENT_ID, event.environmentId());
    assertEquals(ACTION_USERNAME, event.username());
    assertEquals(1, event.synchronizationErrors().size());
    assertTrue(
        event.synchronizationErrors().getFirst().error().value().contains(ERROR_ES6_TRANSPILATION));
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

  @Test
  void should_handle_multiple_files_with_errors() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var tsFile = new SynchronizationFileName("test.ts");
    var tsContent = new SynchronizationFileContent(ERROR_TS);
    var jsFile = new SynchronizationFileName("test.js");
    var jsContent = new SynchronizationFileContent(ERROR_ES6);

    fileSynchronizationAdapter.addFile(project, tsFile, tsContent);
    fileSynchronizationAdapter.addFile(project, jsFile, jsContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertEquals(2, sync.get().getErrors().size());
    assertTrue(
        sync.get().getErrors().stream()
            .anyMatch(error -> error.error().value().contains(ERROR_TYPESCRIPT_TRANSPILATION)));
    assertTrue(
        sync.get().getErrors().stream()
            .anyMatch(error -> error.error().value().contains(ERROR_ES6_TRANSPILATION)));
  }

  @Test
  void should_persist_synchronization_errors_between_runs() {
    // Given
    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var fileName = new SynchronizationFileName("test.ts");
    var fileContent = new SynchronizationFileContent(ERROR_TS);
    fileSynchronizationAdapter.addFile(project, fileName, fileContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);
    handler.execute(command);

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
  void should_handle_source_code_clone_failure() {
    // Given
    sourceCodeAdapter =
        new InMemorySourceCodeAdapter() {
          @Override
          public SourceCodeProject cloneRepository(SourceCodeInformation sourceCodeInformation) {
            throw new RuntimeException("Failed to clone repository");
          }
        };

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

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertFalse(sync.get().isInProgress());
    assertEquals(1, sync.get().getErrors().size());

    var error = sync.get().getErrors().getFirst();
    assertTrue(error.error().value().contains("Failed to clone repository"));
    assertEquals(GLOBAL_ENVIRONMENT_ERROR, error.file().value());
  }

  @Test
  void should_handle_empty_file_list() {
    // Given
    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertFalse(sync.get().isInProgress());
    assertTrue(sync.get().getErrors().isEmpty());

    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertTrue(configs.isEmpty());
  }

  @Test
  void should_update_existing_file_configuration() { // TODO Update non testé pour de vrai
    // Given
    var existingConfig = createFileConfiguration("test.js", "Original content");
    fileConfigurationRepository.save(List.of(existingConfig));

    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    var fileName = new SynchronizationFileName("test.js");
    var newContent = new SynchronizationFileContent("Updated content");
    fileSynchronizationAdapter.addFile(project, fileName, newContent);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertEquals(1, configs.size());
    var updatedConfig = configs.getFirst();
    assertEquals("test.js", updatedConfig.getId().value());

    // Verify sync status
    var sync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(sync.isPresent());
    assertFalse(sync.get().isInProgress());
    assertTrue(sync.get().getErrors().isEmpty());
  }

  @Test
  void should_delete_removed_file_configuration() {
    // Given
    var existingConfig1 = createFileConfiguration("test1.js", "Content 1");
    var existingConfig2 = createFileConfiguration("test2.js", "Content 2");
    fileConfigurationRepository.save(List.of(existingConfig1, existingConfig2));

    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    // Only add one file, simulating the other was deleted
    var fileName = new SynchronizationFileName("test1.js");
    var content = new SynchronizationFileContent("Content 1");
    fileSynchronizationAdapter.addFile(project, fileName, content);

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertEquals(1, configs.size());
    assertEquals("test1.js", configs.getFirst().getId().value());
  }

  @Test
  void should_handle_multiple_changes() { // TODO Update non testé pour de vrai
    // Given - Initial state with 3 files
    var existingConfig1 = createFileConfiguration("stay.js", "Stay content");
    var existingConfig2 = createFileConfiguration("update.js", "Old content");
    var existingConfig3 = createFileConfiguration("delete.js", "Delete content");
    fileConfigurationRepository.save(List.of(existingConfig1, existingConfig2, existingConfig3));

    var project = sourceCodeAdapter.cloneRepository(SOURCE_CODE_INFO);
    fileSynchronizationAdapter.addFile(
        project,
        new SynchronizationFileName("stay.js"),
        new SynchronizationFileContent("Stay content"));
    fileSynchronizationAdapter.addFile(
        project,
        new SynchronizationFileName("update.js"),
        new SynchronizationFileContent("New content"));
    fileSynchronizationAdapter.addFile(
        project, new SynchronizationFileName("new.js"), new SynchronizationFileContent("New file"));

    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var configs = fileConfigurationRepository.findAll(ENVIRONMENT_ID);
    assertEquals(3, configs.size());

    assertTrue(configs.stream().anyMatch(c -> "stay.js".equals(c.getId().value())));
    assertTrue(configs.stream().anyMatch(c -> "update.js".equals(c.getId().value())));
    assertTrue(configs.stream().anyMatch(c -> "new.js".equals(c.getId().value())));
    assertFalse(configs.stream().anyMatch(c -> "delete.js".equals(c.getId().value())));
  }

  private FileConfiguration createFileConfiguration(String filename, String content) {
    return FileConfiguration.builder()
        .fileName(new FileName(filename))
        .environmentId(ENVIRONMENT_ID)
        .auditInfo(AuditInfo.create(ACTION_USERNAME, clock.now()))
        .suites(new ArrayList<>())
        .build();
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
