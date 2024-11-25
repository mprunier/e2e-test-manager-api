package fr.plum.e2e.manager.core.domain.usecase.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metric.Metrics;
import fr.plum.e2e.manager.core.domain.model.query.GetAllMetricsQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;
import java.util.List;

public class ListAllMetricsUseCase implements QueryUseCase<GetAllMetricsQuery, List<Metrics>> {

  private final MetricsRepositoryPort metricsRepositoryPort;

  public ListAllMetricsUseCase(MetricsRepositoryPort metricsRepositoryPort) {
    this.metricsRepositoryPort = metricsRepositoryPort;
  }

  @Override
  public List<Metrics> execute(GetAllMetricsQuery query) {
    return metricsRepositoryPort.findAllMetrics(query.environmentId(), query.sinceAt());
  }
}
