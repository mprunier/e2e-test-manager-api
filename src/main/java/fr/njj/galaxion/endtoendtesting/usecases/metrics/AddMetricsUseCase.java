package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
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
