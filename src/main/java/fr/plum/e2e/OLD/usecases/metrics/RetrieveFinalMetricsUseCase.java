package fr.plum.e2e.OLD.usecases.metrics;

import fr.plum.e2e.OLD.domain.response.MetricsResponse;
import fr.plum.e2e.OLD.service.retrieval.MetricRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveFinalMetricsUseCase {

  private final MetricRetrievalService metricRetrievalService;

  @Transactional
  public MetricsResponse execute(long environmentId) {
    var metricsResponseBuilder = MetricsResponse.builder();

    var lastMetrics = metricRetrievalService.getOptionalLastMetrics(environmentId);
    lastMetrics.ifPresent(
        metricsEntity ->
            metricsResponseBuilder
                .at(metricsEntity.getCreatedAt())
                .suites(metricsEntity.getSuites())
                .tests(metricsEntity.getTests())
                .passPercent(metricsEntity.getPassPercent())
                .passes(metricsEntity.getPasses())
                .failures(metricsEntity.getFailures())
                .skipped(metricsEntity.getSkipped())
                .isAllTestsRun(metricsEntity.isAllTestsRun())
                .build());
    var lastMetricsWithAllTests =
        metricRetrievalService.getOptionalLastMetricsWithAllTestsRun(environmentId);
    lastMetricsWithAllTests.ifPresent(
        metricsEntity ->
            metricsResponseBuilder.lastAllTestsRunAt(metricsEntity.getCreatedAt()).build());

    return metricsResponseBuilder.build();
  }
}
