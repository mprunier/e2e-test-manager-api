package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.port.repository.SchedulerConfigurationRepositoryPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySchedulerConfigurationRepositoryAdapter
    implements SchedulerConfigurationRepositoryPort {

  private final Map<EnvironmentId, SchedulerConfiguration> configurations = new HashMap<>();

  @Override
  public List<SchedulerConfiguration> findAll() {
    return new ArrayList<>(configurations.values());
  }

  @Override
  public Optional<SchedulerConfiguration> find(EnvironmentId environmentId) {
    return Optional.ofNullable(configurations.get(environmentId));
  }

  @Override
  public void save(SchedulerConfiguration schedulerConfiguration) {
    configurations.put(schedulerConfiguration.getId(), schedulerConfiguration);
  }

  @Override
  public void update(SchedulerConfiguration schedulerConfiguration) {
    configurations.put(schedulerConfiguration.getId(), schedulerConfiguration);
  }
}
