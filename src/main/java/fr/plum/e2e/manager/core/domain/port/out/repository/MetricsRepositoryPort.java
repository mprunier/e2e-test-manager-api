package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MetricsRepositoryPort {

  Optional<Metrics> findLastMetrics(EnvironmentId environmentId, MetricsType metricsType);

  void save(Metrics metrics);

  TestCount testCount(EnvironmentId environmentId);

  SuiteCount suiteCount(EnvironmentId environmentId);

  PassCount passCount(EnvironmentId environmentId);

  FailureCount failureCount(EnvironmentId environmentId);

  SkippedCount skippedCount(EnvironmentId environmentId);

  List<Metrics> findAllMetrics(EnvironmentId environmentId, LocalDate sinceAt);
}
