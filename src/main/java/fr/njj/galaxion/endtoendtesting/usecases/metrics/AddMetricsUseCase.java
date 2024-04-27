package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.MetricRepository;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AddMetricsUseCase {

    public static final long MINUTES = 30L;

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final MetricRepository metricRepository;

    @Transactional
    public void execute(
            long environmentId,
            Metrics metrics,
            boolean isAllTestsRun) {

        //        var now = ZonedDateTime.now();
        //        metricRepository.findLastMetrics(environmentId).ifPresentOrElse(lastMetrics -> {
        //            if (areMetricsSame(lastMetrics, metrics) && lastMetrics.getCreatedAt().isAfter(now.minusMinutes(MINUTES))) {
        //                lastMetrics.setCreatedAt(now);
        //            } else {
        //                saveMetrics(environmentId, metrics);
        //            }
        //        }, () -> saveMetrics(environmentId, metrics));

        saveMetrics(environmentId, metrics, isAllTestsRun);
    }

    private boolean areMetricsSame(MetricsEntity lastMetrics, Metrics metrics) {
        return lastMetrics.getSuites() == metrics.suites() &&
               lastMetrics.getTests() == metrics.tests() &&
               lastMetrics.getPassPercent() == metrics.passPercent();
    }

    private void saveMetrics(
            long environmentId,
            Metrics metrics,
            boolean isAllTestsRun) {
        var environment = environmentRetrievalService.getEnvironment(environmentId);
        MetricsEntity
                .builder()
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