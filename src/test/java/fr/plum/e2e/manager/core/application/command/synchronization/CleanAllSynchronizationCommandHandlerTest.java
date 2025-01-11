package fr.plum.e2e.manager.core.application.command.synchronization;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySynchronizationRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CleanAllSynchronizationCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID_1 =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final EnvironmentId ENVIRONMENT_ID_2 =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");

  private CleanAllSynchronizationCommandHandler handler;
  private InMemorySynchronizationRepositoryAdapter synchronizationRepository;
  private InMemoryClockAdapter clock;

  @BeforeEach
  void setUp() {
    synchronizationRepository = new InMemorySynchronizationRepositoryAdapter();
    clock = new InMemoryClockAdapter();

    handler = new CleanAllSynchronizationCommandHandler(synchronizationRepository);
  }

  @Test
  void should_clean_all_synchronizations() {
    // Given
    var sync1 = createSynchronization(ENVIRONMENT_ID_1);
    var sync2 = createSynchronization(ENVIRONMENT_ID_2);

    sync1.start();
    sync2.start();

    synchronizationRepository.save(sync1);
    synchronizationRepository.save(sync2);

    assertTrue(sync1.isInProgress());
    assertTrue(sync2.isInProgress());

    // When
    handler.execute();

    // Then
    var cleanedSyncs = synchronizationRepository.findAll();
    assertEquals(2, cleanedSyncs.size());

    cleanedSyncs.forEach(sync -> assertFalse(sync.isInProgress()));
  }

  @Test
  void should_do_nothing_when_no_synchronizations() {
    // When
    handler.execute();

    // Then
    var syncs = synchronizationRepository.findAll();
    assertTrue(syncs.isEmpty());
  }

  private Synchronization createSynchronization(EnvironmentId environmentId) {
    return Synchronization.create(environmentId, createAuditInfo());
  }

  private AuditInfo createAuditInfo() {
    return AuditInfo.create(ACTION_USERNAME, clock.now());
  }
}
