package fr.plum.e2e.manager.core.domain.model.aggregate.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.MetricsId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassPercentage;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AuditInfo;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class Metrics extends AggregateRoot<MetricsId> {
  private EnvironmentId environmentId;
  private MetricsType type;
  private TestCount testCount;
  private SuiteCount suiteCount;
  private PassCount passCount;
  private FailureCount failureCount;
  private SkippedCount skippedCount;
  private PassPercentage passPercentage;

  public static Metrics initialize(
      EnvironmentId environmentId, ZonedDateTime now, MetricsType type) {
    return builder()
        .environmentId(environmentId)
        .auditInfo(AuditInfo.create(now))
        .type(type)
        .build();
  }

  public void addCounts(
      TestCount testCount,
      SuiteCount suiteCount,
      PassCount passCount,
      FailureCount failureCount,
      SkippedCount skippedCount) {
    this.testCount = testCount;
    this.suiteCount = suiteCount;
    this.passCount = passCount;
    this.failureCount = failureCount;
    this.skippedCount = skippedCount;
  }

  public void calculatePassPercentage() {
    int total = testCount.value();
    if (total > 0) {
      this.passPercentage = new PassPercentage((passCount.value() * 100) / total);
    }
  }
}
