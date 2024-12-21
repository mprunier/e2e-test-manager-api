package fr.plum.e2e.manager.core.application.query.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.query.GetAllMetricsQuery;
import fr.plum.e2e.manager.core.domain.port.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ListAllMetricsQueryHandler implements QueryHandler<GetAllMetricsQuery, List<Metrics>> {

  private final MetricsRepositoryPort metricsRepositoryPort;

  public ListAllMetricsQueryHandler(MetricsRepositoryPort metricsRepositoryPort) {
    this.metricsRepositoryPort = metricsRepositoryPort;
  }

  @Override
  public List<Metrics> execute(GetAllMetricsQuery query) {
    return metricsRepositoryPort.findAllMetrics(query.environmentId(), query.sinceAt());
  }
}
