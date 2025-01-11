package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MetricsResponse {

  @NotNull private ZonedDateTime at;

  @NotNull private MetricsType type;

  @NotNull private Integer suites;

  @NotNull private Integer tests;

  @NotNull private Integer passes;

  @NotNull private Integer failures;

  @NotNull private Integer skipped;

  @NotNull private Integer passPercent;

  @NotNull private ZonedDateTime lastAllTestsRunAt;

  public static MetricsResponse fromDomain(Metrics metrics) {
    return builder()
        .at(metrics.getAuditInfo().getCreatedAt())
        .type(metrics.getType())
        .suites(metrics.getSuiteCount().value())
        .tests(metrics.getTestCount().value())
        .passes(metrics.getPassCount().value())
        .failures(metrics.getFailureCount().value())
        .skipped(metrics.getSkippedCount().value())
        .passPercent(metrics.getPassPercentage().value())
        .lastAllTestsRunAt(
            MetricsType.ALL.equals(metrics.getType())
                ? metrics.getAuditInfo().getCreatedAt()
                : null)
        .build();
  }

  public static List<MetricsResponse> fromDomain(List<Metrics> metricsList) {
    return metricsList.stream().map(MetricsResponse::fromDomain).toList();
  }

  public void addLastAllTestsRunAt(ZonedDateTime lastAllTestsRunAt) {
    this.lastAllTestsRunAt = lastAllTestsRunAt;
  }
}
