package fr.plum.e2e.manager.core.domain.port.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import java.util.List;
import java.util.Optional;

public interface SchedulerConfigurationRepositoryPort {
  List<SchedulerConfiguration> findAll();

  Optional<SchedulerConfiguration> find(EnvironmentId environmentId);

  void save(SchedulerConfiguration schedulerConfiguration);

  void update(SchedulerConfiguration schedulerConfiguration);
}
