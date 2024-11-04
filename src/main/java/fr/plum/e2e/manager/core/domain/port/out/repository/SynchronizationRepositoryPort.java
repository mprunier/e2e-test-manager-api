package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import java.util.List;
import java.util.Optional;

public interface SynchronizationRepositoryPort {
  Optional<Synchronization> findById(EnvironmentId environmentId);

  List<Synchronization> findAll();

  void save(Synchronization synchronization);

  void update(Synchronization synchronization);

  void updateAll(List<Synchronization> synchronizations);
}
