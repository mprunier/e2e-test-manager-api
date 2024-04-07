package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveFinalMetricsUseCase {

    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;

    @Transactional
    public MetricsResponse execute(
            long environmentId) {

        var finalMetrics = calculateFinalMetricsUseCase.execute(environmentId);

        return MetricsResponse
                .builder()
                .at(finalMetrics.at())
                .suites(finalMetrics.suites())
                .tests(finalMetrics.tests())
                .passPercent(finalMetrics.passPercent())
                .passes(finalMetrics.passes())
                .failures(finalMetrics.failures())
                .skipped(finalMetrics.skipped())
                .build();

    }

}

