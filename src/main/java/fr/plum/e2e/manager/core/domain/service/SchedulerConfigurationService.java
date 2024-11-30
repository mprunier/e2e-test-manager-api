package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.exception.SchedulerNotFoundException;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchedulerConfigurationService {

  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;

  public SchedulerConfiguration getScheduler(EnvironmentId id) {
    return schedulerConfigurationRepositoryPort
        .find(id)
        .orElseThrow(() -> new SchedulerNotFoundException(id));
  }
}
