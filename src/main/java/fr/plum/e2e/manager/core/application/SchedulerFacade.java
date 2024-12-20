package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerConfigurationService;
import fr.plum.e2e.manager.core.domain.usecase.schedulerconfiguration.UpdateSchedulerConfigurationUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SchedulerFacade {

  private final UpdateSchedulerConfigurationUseCase updateSchedulerConfigurationUseCase;
  private final SchedulerConfigurationService schedulerConfigurationService;

  public SchedulerFacade(
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort,
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort) {

    this.updateSchedulerConfigurationUseCase =
        new UpdateSchedulerConfigurationUseCase(
            schedulerConfigurationRepositoryPort, eventPublisherPort, clockPort);
    this.schedulerConfigurationService =
        new SchedulerConfigurationService(schedulerConfigurationRepositoryPort);
  }

  public void updateScheduler(UpdateSchedulerCommand schedulerCommand) {
    updateSchedulerConfigurationUseCase.execute(schedulerCommand);
  }

  public SchedulerConfiguration getSchedulerDetails(CommonQuery query) {
    return schedulerConfigurationService.getScheduler(query.environmentId());
  }
}
