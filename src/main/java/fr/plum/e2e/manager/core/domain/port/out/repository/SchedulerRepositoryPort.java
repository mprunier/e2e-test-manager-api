package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import java.util.List;
import java.util.Optional;

public interface SchedulerRepositoryPort {
  List<Scheduler> findAll();

  Optional<Scheduler> find(EnvironmentId environmentId);

  void save(Scheduler scheduler);

  void update(Scheduler scheduler);
}
