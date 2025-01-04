package fr.plum.e2e.manager.core.domain.model.aggregate.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.MetricsId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassPercentage;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import lombok.Builder;
import lombok.Getter;

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

  @Builder
  public Metrics(
      MetricsId metricsId,
      AuditInfo auditInfo,
      EnvironmentId environmentId,
      MetricsType type,
      TestCount testCount,
      SuiteCount suiteCount,
      PassCount passCount,
      FailureCount failureCount,
      SkippedCount skippedCount,
      PassPercentage passPercentage) {
    super(metricsId, auditInfo);
    Assert.notNull("EnvironmentId", environmentId);
    Assert.notNull("MetricsType", type);
    Assert.notNull("TestCount", testCount);
    Assert.notNull("SuiteCount", suiteCount);
    Assert.notNull("PassCount", passCount);
    Assert.notNull("FailureCount", failureCount);
    Assert.notNull("SkippedCount", skippedCount);
    this.environmentId = environmentId;
    this.type = type;
    this.testCount = testCount;
    this.suiteCount = suiteCount;
    this.passCount = passCount;
    this.failureCount = failureCount;
    this.skippedCount = skippedCount;
    this.passPercentage = passPercentage;
  }

  public static Metrics create(
      EnvironmentId environmentId,
      AuditInfo auditInfo,
      MetricsType type,
      TestCount testCount,
      SuiteCount suiteCount,
      PassCount passCount,
      FailureCount failureCount,
      SkippedCount skippedCount) {
    return builder()
        .metricsId(MetricsId.generate())
        .environmentId(environmentId)
        .auditInfo(auditInfo)
        .type(type)
        .testCount(testCount)
        .suiteCount(suiteCount)
        .passCount(passCount)
        .failureCount(failureCount)
        .skippedCount(skippedCount)
        .build();
  }

  public void calculatePassPercentage() {
    int total = testCount.value();
    if (total > 0) {
      this.passPercentage = new PassPercentage((passCount.value() * 100) / total);
    }
  }
}
