package fr.plum.e2e.OLD.usecases.metrics;

import fr.plum.e2e.OLD.domain.record.Metrics;
import fr.plum.e2e.OLD.model.entity.MetricsEntity;
import fr.plum.e2e.OLD.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AddMetricsUseCase {

  private final EnvironmentRetrievalService environmentRetrievalService;

  @Transactional
  public void execute(long environmentId, Metrics metrics, boolean isAllTestsRun) {

    var environment = environmentRetrievalService.get(environmentId);
    MetricsEntity.builder()
        .environment(environment)
        .suites(metrics.suites())
        .tests(metrics.tests())
        .passes(metrics.passes())
        .failures(metrics.failures())
        .skipped(metrics.skipped())
        .passPercent(metrics.passPercent())
        .isAllTestsRun(isAllTestsRun)
        .build()
        .persist();
  }
}
