package fr.plum.e2e.manager.core.domain.usecase.scheduler;

import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.event.SchedulerUpdatedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;

public class UpdateSchedulerUseCase implements CommandUseCase<UpdateSchedulerCommand> {

  private final SchedulerRepositoryPort schedulerRepositoryPort;
  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;

  private final SchedulerService schedulerService;

  public UpdateSchedulerUseCase(
      SchedulerRepositoryPort schedulerRepositoryPort,
      EventPublisherPort eventPublisherPort,
      ClockPort clockPort) {
    this.schedulerRepositoryPort = schedulerRepositoryPort;
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.schedulerService = new SchedulerService(schedulerRepositoryPort);
  }

  @Override
  public void execute(UpdateSchedulerCommand schedulerCommand) {
    var scheduler = schedulerService.getScheduler(schedulerCommand.environmentId());

    scheduler.setIsEnabled(schedulerCommand.isEnabled());
    scheduler.setDaysOfWeek(schedulerCommand.daysOfWeek());
    scheduler.setHour(schedulerCommand.schedulerHour());
    scheduler.setMinute(schedulerCommand.schedulerMinute());

    scheduler.getAuditInfo().update(schedulerCommand.actionUsername(), clockPort.now());

    schedulerRepositoryPort.update(scheduler);

    eventPublisherPort.publishAsync(
        new SchedulerUpdatedEvent(
            schedulerCommand.environmentId(), schedulerCommand.actionUsername()));
  }
}
