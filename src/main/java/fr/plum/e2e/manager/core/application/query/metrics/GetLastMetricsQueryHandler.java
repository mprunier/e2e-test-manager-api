package fr.plum.e2e.manager.core.application.query.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.query.GetMetricsQuery;
import fr.plum.e2e.manager.core.domain.port.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.MetricsService;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetLastMetricsQueryHandler implements QueryHandler<GetMetricsQuery, Metrics> {

  private final MetricsService metricsService;

  public GetLastMetricsQueryHandler(MetricsRepositoryPort metricsRepositoryPort) {
    this.metricsService = new MetricsService(metricsRepositoryPort);
  }

  @Override
  public Metrics execute(GetMetricsQuery query) {
    return metricsService.getLastMetrics(query.environmentId(), query.metricsType());
  }
}
