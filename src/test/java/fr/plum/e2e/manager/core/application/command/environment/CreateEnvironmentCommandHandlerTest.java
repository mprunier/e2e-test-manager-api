package fr.plum.e2e.manager.core.application.command.environment;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.command.CreateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.command.EnvironmentVariableCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentCreatedEvent;
import fr.plum.e2e.manager.core.domain.model.exception.DuplicateEnvironmentException;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryEnvironmentRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySchedulerConfigurationRepositoryAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySynchronizationAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.transaction.inmemory.adapter.InMemoryTransactionManagerAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateEnvironmentCommandHandlerTest {

  private static final String PROJECT_ID = "project1";
  private static final String BRANCH = "main";
  private static final String TOKEN = "token123";
  private static final EnvironmentDescription ENVIRONMENT_DESCRIPTION =
      new EnvironmentDescription("Test Environment");
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");

  private CreateEnvironmentCommandHandler handler;

  private InMemoryEnvironmentRepositoryAdapter environmentRepository;
  private InMemorySynchronizationAdapter synchronizationRepository;
  private InMemorySchedulerConfigurationRepositoryAdapter schedulerRepository;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;

  @BeforeEach
  void setUp() {
    environmentRepository = new InMemoryEnvironmentRepositoryAdapter();
    synchronizationRepository = new InMemorySynchronizationAdapter();
    schedulerRepository = new InMemorySchedulerConfigurationRepositoryAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    clock = new InMemoryClockAdapter();
    InMemoryTransactionManagerAdapter transactionManager = new InMemoryTransactionManagerAdapter();

    handler =
        new CreateEnvironmentCommandHandler(
            clock,
            eventPublisher,
            environmentRepository,
            synchronizationRepository,
            schedulerRepository,
            transactionManager);
  }

  @Test
  void should_create_environment_with_valid_command() {
    // Given
    var command = createValidCommand();

    // When
    handler.execute(command);

    // Then
    var environments = environmentRepository.findAll(PROJECT_ID, BRANCH);
    assertEquals(1, environments.size());

    var environment = environments.getFirst();
    assertEquals(command.description().value(), environment.getEnvironmentDescription().value());
    assertEquals(
        command.sourceCodeInformation().projectId(),
        environment.getSourceCodeInformation().projectId());
    assertEquals(
        command.sourceCodeInformation().branch(), environment.getSourceCodeInformation().branch());
    assertEquals(
        command.sourceCodeInformation().token(), environment.getSourceCodeInformation().token());

    var scheduler = schedulerRepository.find(environment.getId());
    assertTrue(scheduler.isPresent());

    var sync = synchronizationRepository.find(environment.getId());
    assertTrue(sync.isPresent());

    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (EnvironmentCreatedEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(environment.getId(), event.environmentId());
    assertEquals(command.actionUsername(), event.username());
  }

  @Test
  void should_throw_exception_when_environment_description_exists() {
    // Given
    var existingEnvironment =
        Environment.create(
            ENVIRONMENT_DESCRIPTION,
            createSourceCodeInfo(),
            Collections.emptyList(),
            createAuditInfo());
    environmentRepository.save(existingEnvironment);

    var command = createValidCommand();

    // When/Then
    assertThrows(DuplicateEnvironmentException.class, () -> handler.execute(command));
  }

  private SourceCodeInformation createSourceCodeInfo() {
    return SourceCodeInformation.builder()
        .projectId(PROJECT_ID)
        .branch(BRANCH)
        .token(TOKEN)
        .build();
  }

  private CreateEnvironmentCommand createValidCommand() {
    return new CreateEnvironmentCommand(
        ENVIRONMENT_DESCRIPTION,
        createSourceCodeInfo(),
        MaxParallelWorkers.defaultValue(),
        createVariables(),
        ACTION_USERNAME);
  }

  private List<EnvironmentVariableCommand> createVariables() {
    return Collections.emptyList();
  }

  private AuditInfo createAuditInfo() {
    return AuditInfo.create(ACTION_USERNAME, clock.now());
  }
}
