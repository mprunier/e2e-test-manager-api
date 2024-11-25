package fr.plum.e2e.manager.core.domain.usecase.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metric.Metrics;
import fr.plum.e2e.manager.core.domain.model.query.GetMetricsQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.MetricsService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetLastMetricsUseCase implements QueryUseCase<GetMetricsQuery, Metrics> {

  private final MetricsService metricsService;

  public GetLastMetricsUseCase(MetricsRepositoryPort metricsRepositoryPort) {
    this.metricsService = new MetricsService(metricsRepositoryPort);
  }

  @Override
  public Metrics execute(GetMetricsQuery query) {
    return metricsService.getLastMetrics(query.environmentId(), query.metricsType());
  }
}
