package fr.plum.e2e.manager.core.domain.usecase.schedulerconfiguration;

import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.event.SchedulerUpdatedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerConfigurationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;

public class UpdateSchedulerConfigurationUseCase implements CommandUseCase<UpdateSchedulerCommand> {

  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;

  private final SchedulerConfigurationService schedulerConfigurationService;

  public UpdateSchedulerConfigurationUseCase(
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort,
      EventPublisherPort eventPublisherPort,
      ClockPort clockPort) {
    this.schedulerConfigurationRepositoryPort = schedulerConfigurationRepositoryPort;
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.schedulerConfigurationService =
        new SchedulerConfigurationService(schedulerConfigurationRepositoryPort);
  }

  @Override
  public void execute(UpdateSchedulerCommand schedulerCommand) {
    var scheduler = schedulerConfigurationService.getScheduler(schedulerCommand.environmentId());

    scheduler.setIsEnabled(schedulerCommand.isEnabled());
    scheduler.setDaysOfWeek(schedulerCommand.daysOfWeek());
    scheduler.setHour(schedulerCommand.schedulerHour());
    scheduler.setMinute(schedulerCommand.schedulerMinute());

    scheduler.getAuditInfo().update(schedulerCommand.actionUsername(), clockPort.now());

    schedulerConfigurationRepositoryPort.update(scheduler);

    eventPublisherPort.publishAsync(
        new SchedulerUpdatedEvent(
            schedulerCommand.environmentId(), schedulerCommand.actionUsername()));
  }
}
