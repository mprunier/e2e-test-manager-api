package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.MetricsId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.port.repository.MetricsRepositoryPort;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryMetricsRepositoryAdapter implements MetricsRepositoryPort {
  private final Map<MetricsId, Metrics> metrics = new HashMap<>();

  @Override
  public Optional<Metrics> findLastMetrics(EnvironmentId environmentId, MetricsType metricsType) {
    return metrics.values().stream()
        .filter(m -> m.getEnvironmentId().equals(environmentId))
        .filter(m -> m.getType().equals(metricsType))
        .max(Comparator.comparing(m -> m.getAuditInfo().getCreatedAt()));
  }

  @Override
  public void save(Metrics metric) {
    metrics.put(metric.getId(), metric);
  }

  @Override
  public TestCount testCount(EnvironmentId environmentId) {
    return new TestCount(10);
  }

  @Override
  public SuiteCount suiteCount(EnvironmentId environmentId) {
    return new SuiteCount(2);
  }

  @Override
  public PassCount passCount(EnvironmentId environmentId) {
    return new PassCount(7);
  }

  @Override
  public FailureCount failureCount(EnvironmentId environmentId) {
    return new FailureCount(2);
  }

  @Override
  public SkippedCount skippedCount(EnvironmentId environmentId) {
    return new SkippedCount(1);
  }

  @Override
  public List<Metrics> findAllMetrics(EnvironmentId environmentId, LocalDate sinceAt) {
    return metrics.values().stream()
        .filter(m -> m.getEnvironmentId().equals(environmentId))
        .filter(
            m ->
                m.getAuditInfo().getCreatedAt().toLocalDate().isAfter(sinceAt)
                    || m.getAuditInfo().getCreatedAt().toLocalDate().isEqual(sinceAt))
        .collect(Collectors.toList());
  }
}
