package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.MetricRepository;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AddMetricsUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final MetricRepository metricRepository;

    @Transactional
    public void execute(
            long environmentId,
            Metrics metrics) {
        var now = ZonedDateTime.now();
        
        metricRepository.findLastMetrics(environmentId).ifPresentOrElse(lastMetrics -> {
            if (areMetricsSame(lastMetrics, metrics) && lastMetrics.getCreatedAt().isAfter(now.minusHours(1))) {
                lastMetrics.setCreatedAt(now);
            } else {
                saveMetrics(environmentId, metrics);
            }
        }, () -> saveMetrics(environmentId, metrics));
    }

    private boolean areMetricsSame(MetricsEntity lastMetrics, Metrics metrics) {
        return lastMetrics.getSuites() == metrics.suites() &&
               lastMetrics.getTests() == metrics.tests() &&
               lastMetrics.getPassPercent() == metrics.passPercent();
    }

    private void saveMetrics(long environmentId, Metrics metrics) {
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
                .build()
                .persist();
    }
}