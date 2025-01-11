package fr.plum.e2e.manager.core.application.command.environment;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.command.EnvironmentVariableCommand;
import fr.plum.e2e.manager.core.domain.model.command.UpdateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentUpdatedEvent;
import fr.plum.e2e.manager.core.domain.model.exception.DuplicateEnvironmentException;
import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryEnvironmentRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateEnvironmentCommandHandlerTest {

  private static final String PROJECT_ID = "project1";
  private static final String NEW_PROJECT_ID = "project2";
  private static final String BRANCH = "main";
  private static final String TOKEN = "token123";
  private static final EnvironmentDescription ENVIRONMENT_DESCRIPTION =
      new EnvironmentDescription("Test Environment");
  private static final EnvironmentDescription NEW_ENVIRONMENT_DESCRIPTION =
      new EnvironmentDescription("Updated Environment");
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");

  private UpdateEnvironmentCommandHandler handler;
  private InMemoryEnvironmentRepositoryAdapter environmentRepository;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;

  private Environment existingEnvironment;

  @BeforeEach
  void setUp() {
    environmentRepository = new InMemoryEnvironmentRepositoryAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    clock = new InMemoryClockAdapter();

    handler = new UpdateEnvironmentCommandHandler(clock, eventPublisher, environmentRepository);

    existingEnvironment =
        Environment.create(
            ENVIRONMENT_DESCRIPTION,
            createSourceCodeInfo(),
            Collections.emptyList(),
            createAuditInfo());
    environmentRepository.save(existingEnvironment);
  }

  @Test
  void should_update_environment_with_valid_command() {
    // Given
    var command = createValidUpdateCommand(existingEnvironment.getId());

    // When
    handler.execute(command);

    // Then
    var updatedEnvironment = environmentRepository.find(existingEnvironment.getId()).orElseThrow();
    assertEquals(
        command.description().value(), updatedEnvironment.getEnvironmentDescription().value());
    assertEquals(
        command.sourceCodeInformation().projectId(),
        updatedEnvironment.getSourceCodeInformation().projectId());
    assertEquals(
        command.sourceCodeInformation().branch(),
        updatedEnvironment.getSourceCodeInformation().branch());
    assertEquals(
        command.sourceCodeInformation().token(),
        updatedEnvironment.getSourceCodeInformation().token());

    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (EnvironmentUpdatedEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(existingEnvironment.getId(), event.environmentId());
    assertEquals(command.actionUsername(), event.username());
  }

  @Test
  void should_throw_exception_when_environment_not_found() {
    // Given
    var command = createValidUpdateCommand(EnvironmentId.generate());

    // When/Then
    assertThrows(EnvironmentNotFoundException.class, () -> handler.execute(command));
  }

  @Test
  void should_throw_exception_when_new_description_exists() {
    // Given
    var anotherEnvironment =
        Environment.create(
            NEW_ENVIRONMENT_DESCRIPTION,
            createSourceCodeInfo(),
            Collections.emptyList(),
            createAuditInfo());
    environmentRepository.save(anotherEnvironment);

    var command = createValidUpdateCommand(existingEnvironment.getId());

    // When/Then
    assertThrows(DuplicateEnvironmentException.class, () -> handler.execute(command));
  }

  private UpdateEnvironmentCommand createValidUpdateCommand(EnvironmentId environmentId) {
    return new UpdateEnvironmentCommand(
        environmentId,
        NEW_ENVIRONMENT_DESCRIPTION,
        createNewSourceCodeInfo(),
        MaxParallelWorkers.defaultValue(),
        createVariables(),
        ACTION_USERNAME);
  }

  private SourceCodeInformation createSourceCodeInfo() {
    return SourceCodeInformation.builder()
        .projectId(PROJECT_ID)
        .branch(BRANCH)
        .token(TOKEN)
        .build();
  }

  private SourceCodeInformation createNewSourceCodeInfo() {
    return SourceCodeInformation.builder()
        .projectId(NEW_PROJECT_ID)
        .branch(BRANCH)
        .token(TOKEN)
        .build();
  }

  private List<EnvironmentVariableCommand> createVariables() {
    return Collections.emptyList();
  }

  private AuditInfo createAuditInfo() {
    return AuditInfo.create(ACTION_USERNAME, clock.now());
  }
}
