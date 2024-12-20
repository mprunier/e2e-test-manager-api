package fr.plum.e2e.manager.core.application.command.schedulerconfiguration;

import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.event.SchedulerUpdatedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerConfigurationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateSchedulerConfigurationCommandHandler
    implements CommandHandler<UpdateSchedulerCommand> {

  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;

  private final SchedulerConfigurationService schedulerConfigurationService;

  public UpdateSchedulerConfigurationCommandHandler(
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
