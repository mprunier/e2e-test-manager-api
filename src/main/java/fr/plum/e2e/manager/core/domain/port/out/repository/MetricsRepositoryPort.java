package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo.TestCount;

public interface MetricsRepositoryPort {

  void save(Metrics metrics);

  TestCount testCount(EnvironmentId environmentId);

  SuiteCount suiteCount(EnvironmentId environmentId);

  PassCount passCount(EnvironmentId environmentId);

  FailureCount failureCount(EnvironmentId environmentId);

  SkippedCount skippedCount(EnvironmentId environmentId);
}
