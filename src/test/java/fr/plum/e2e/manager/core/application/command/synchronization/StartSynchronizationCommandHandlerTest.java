package fr.plum.e2e.manager.core.application.command.synchronization;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentIsSynchronizingEvent;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationAlreadyInProgressException;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySynchronizationRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartSynchronizationCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");

  private StartSynchronizationCommandHandler handler;
  private InMemorySynchronizationRepositoryAdapter synchronizationRepository;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;

  @BeforeEach
  void setUp() {
    synchronizationRepository = new InMemorySynchronizationRepositoryAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    clock = new InMemoryClockAdapter();

    handler = new StartSynchronizationCommandHandler(eventPublisher, synchronizationRepository);

    var initialSync = Synchronization.create(ENVIRONMENT_ID, createAuditInfo());
    synchronizationRepository.save(initialSync);
  }

  @Test
  void should_start_synchronization_with_valid_command() {
    // Given
    var command = new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);

    // When
    handler.execute(command);

    // Then
    var updatedSync = synchronizationRepository.find(ENVIRONMENT_ID);
    assertTrue(updatedSync.isPresent());

    var sync = updatedSync.get();
    assertTrue(sync.isInProgress());

    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (EnvironmentIsSynchronizingEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(ENVIRONMENT_ID, event.environmentId());
    assertEquals(ACTION_USERNAME, event.username());
  }

  @Test
  void should_throw_exception_when_synchronization_already_in_progress() {
    // Given
    var command = createValidCommand();
    var existingSync = synchronizationRepository.find(ENVIRONMENT_ID);
    if (existingSync.isEmpty()) {
      fail("Synchronization not found");
    }
    existingSync.get().start();
    synchronizationRepository.update(existingSync.get());

    // When/Then
    assertThrows(SynchronizationAlreadyInProgressException.class, () -> handler.execute(command));
  }

  private CommonCommand createValidCommand() {
    return new CommonCommand(ENVIRONMENT_ID, ACTION_USERNAME);
  }

  private AuditInfo createAuditInfo() {
    return AuditInfo.create(ACTION_USERNAME, clock.now());
  }
}
