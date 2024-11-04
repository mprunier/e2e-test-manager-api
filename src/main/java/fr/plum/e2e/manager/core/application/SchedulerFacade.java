package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerService;
import fr.plum.e2e.manager.core.domain.usecase.scheduler.UpdateSchedulerUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SchedulerFacade {

  private final UpdateSchedulerUseCase updateSchedulerUseCase;
  private final SchedulerService schedulerService;

  public SchedulerFacade(
      SchedulerRepositoryPort schedulerRepositoryPort,
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort) {

    this.updateSchedulerUseCase =
        new UpdateSchedulerUseCase(schedulerRepositoryPort, eventPublisherPort, clockPort);
    this.schedulerService = new SchedulerService(schedulerRepositoryPort);
  }

  public void updateScheduler(UpdateSchedulerCommand schedulerCommand) {
    updateSchedulerUseCase.execute(schedulerCommand);
  }

  public Scheduler getSchedulerDetails(CommonQuery query) {
    return schedulerService.getScheduler(query.environmentId());
  }
}
