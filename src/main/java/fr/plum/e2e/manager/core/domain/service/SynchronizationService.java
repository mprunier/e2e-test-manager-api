package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationNotFoundException;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SynchronizationService {

  private final SynchronizationRepositoryPort synchronizationRepositoryPort;

  public Synchronization getSynchronization(EnvironmentId id) {
    return synchronizationRepositoryPort
        .find(id)
        .orElseThrow(() -> new SynchronizationNotFoundException(id));
  }
}
