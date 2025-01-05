package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySynchronizationRepositoryAdapter implements SynchronizationRepositoryPort {
  private final Map<EnvironmentId, Synchronization> synchronizations = new HashMap<>();

  @Override
  public Optional<Synchronization> find(EnvironmentId environmentId) {
    return Optional.ofNullable(synchronizations.get(environmentId));
  }

  @Override
  public List<Synchronization> findAll() {
    return new ArrayList<>(synchronizations.values());
  }

  @Override
  public void save(Synchronization synchronization) {
    synchronizations.put(synchronization.getId(), synchronization);
  }

  @Override
  public void update(Synchronization synchronization) {
    synchronizations.put(synchronization.getId(), synchronization);
  }

  @Override
  public void updateAll(List<Synchronization> syncs) {
    syncs.forEach(sync -> synchronizations.put(sync.getId(), sync));
  }
}
