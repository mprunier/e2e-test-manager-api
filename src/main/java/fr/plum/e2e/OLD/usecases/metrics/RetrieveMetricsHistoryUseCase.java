package fr.plum.e2e.OLD.usecases.metrics;

import fr.plum.e2e.OLD.service.retrieval.MetricRetrievalService;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveMetricsHistoryUseCase {

  private final MetricRetrievalService metricRetrievalService;

  @Transactional
  public List<MetricsResponse> execute(long environmentId, LocalDate since) {

    var entities = metricRetrievalService.getAllByEnvironmentSince(environmentId, since);

    var metrics = new ArrayList<MetricsResponse>();
    entities.forEach(
        entity ->
            metrics.add(
                MetricsResponse.builder()
                    .at(entity.getCreatedAt())
                    .suites(entity.getSuites())
                    .tests(entity.getTests())
                    .passPercent(entity.getPassPercent())
                    .passes(entity.getPasses())
                    .failures(entity.getFailures())
                    .skipped(entity.getSkipped())
                    .isAllTestsRun(entity.isAllTestsRun())
                    .build()));

    return metrics;
  }
}
