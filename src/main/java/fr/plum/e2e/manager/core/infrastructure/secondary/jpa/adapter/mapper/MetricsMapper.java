package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassPercentage;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AuditInfo;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.metrics.JpaMetricsEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetricsMapper {

  public static JpaMetricsEntity toEntity(Metrics domain) {
    return JpaMetricsEntity.builder()
        .environmentId(domain.getEnvironmentId().value())
        .type(domain.getType())
        .suites(domain.getSuiteCount().value())
        .tests(domain.getTestCount().value())
        .passes(domain.getPassCount().value())
        .failures(domain.getFailureCount().value())
        .skipped(domain.getSkippedCount().value())
        .passPercent(domain.getPassPercentage().value())
        .createdAt(domain.getAuditInfo().getCreatedAt())
        .build();
  }

  public static Metrics toDomain(JpaMetricsEntity entity) {
    return Metrics.builder()
        .environmentId(new EnvironmentId(entity.getEnvironmentId()))
        .type(entity.getType())
        .suiteCount(new SuiteCount(entity.getSuites()))
        .testCount(new TestCount(entity.getTests()))
        .passCount(new PassCount(entity.getPasses()))
        .failureCount(new FailureCount(entity.getFailures()))
        .skippedCount(new SkippedCount(entity.getSkipped()))
        .passPercentage(new PassPercentage(entity.getPassPercent()))
        .auditInfo(AuditInfo.create(entity.getCreatedAt()))
        .build();
  }

  public static List<Metrics> toDomains(List<JpaMetricsEntity> entities) {
    return entities.stream().map(MetricsMapper::toDomain).toList();
  }
}
