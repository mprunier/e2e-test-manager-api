package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.MetricsType;
import fr.plum.e2e.manager.core.domain.model.exception.MetricsNotFoundException;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MetricsService {

  private final MetricsRepositoryPort metricsRepositoryPort;

  public Metrics getLastMetrics(EnvironmentId environmentId, MetricsType metricsType) {
    return metricsRepositoryPort
        .findLastMetrics(environmentId, metricsType)
        .orElseThrow(() -> new MetricsNotFoundException(environmentId));
  }
}
