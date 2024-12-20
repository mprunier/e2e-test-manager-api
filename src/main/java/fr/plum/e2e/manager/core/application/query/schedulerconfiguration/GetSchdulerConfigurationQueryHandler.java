package fr.plum.e2e.manager.core.application.query.schedulerconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SchedulerConfigurationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetSchdulerConfigurationQueryHandler
    implements QueryHandler<CommonQuery, SchedulerConfiguration> {

  private final SchedulerConfigurationService schedulerConfigurationService;

  public GetSchdulerConfigurationQueryHandler(
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort) {
    this.schedulerConfigurationService =
        new SchedulerConfigurationService(schedulerConfigurationRepositoryPort);
  }

  @Override
  public SchedulerConfiguration execute(CommonQuery query) {
    return schedulerConfigurationService.getScheduler(query.environmentId());
  }
}
