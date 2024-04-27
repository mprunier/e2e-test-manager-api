package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import fr.njj.galaxion.endtoendtesting.model.repository.MetricRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveFinalMetricsUseCase {

    private final MetricRepository metricRepository;

    @Transactional
    public MetricsResponse execute(
            long environmentId) {
        var metricsResponseBuilder = MetricsResponse.builder();

        var lastMetrics = metricRepository.findLastMetrics(environmentId);
        lastMetrics.ifPresent(metricsEntity -> metricsResponseBuilder
                .at(metricsEntity.getCreatedAt())
                .suites(metricsEntity.getSuites())
                .tests(metricsEntity.getTests())
                .passPercent(metricsEntity.getPassPercent())
                .passes(metricsEntity.getPasses())
                .failures(metricsEntity.getFailures())
                .skipped(metricsEntity.getSkipped())
                .isAllTestsRun(metricsEntity.isAllTestsRun())
                .build());
        var lastMetricsWithAllTests = metricRepository.findLastMetricsWithAllTests(environmentId);
        lastMetricsWithAllTests.ifPresent(metricsEntity -> metricsResponseBuilder
                .lastAllTestsRunAt(metricsEntity.getCreatedAt())
                .build());

        return metricsResponseBuilder.build();
    }

}

