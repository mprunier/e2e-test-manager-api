package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import fr.plum.e2e.manager.core.domain.model.exception.SchedulerNotFoundException;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchedulerService {

  private final SchedulerRepositoryPort schedulerRepositoryPort;

  public Scheduler getScheduler(EnvironmentId id) {
    return schedulerRepositoryPort
        .findById(id)
        .orElseThrow(() -> new SchedulerNotFoundException(id));
  }
}
