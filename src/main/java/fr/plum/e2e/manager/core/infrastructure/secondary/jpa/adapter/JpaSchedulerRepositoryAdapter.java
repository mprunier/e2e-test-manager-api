package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

// TODO
@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaSchedulerRepositoryAdapter implements SchedulerRepositoryPort {

  @Override
  public Optional<Scheduler> find(EnvironmentId environmentId) {
    return Optional.empty();
  }

  @Override
  public void save(Scheduler scheduler) {}

  @Override
  public void update(Scheduler scheduler) {}
}
