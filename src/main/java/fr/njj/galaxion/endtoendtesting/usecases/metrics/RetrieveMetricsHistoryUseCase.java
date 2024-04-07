package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import fr.njj.galaxion.endtoendtesting.model.repository.MetricRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveMetricsHistoryUseCase {

    private final MetricRepository metricRepository;

    @Transactional
    public List<MetricsResponse> execute(
            long environmentId) {

        var entities = metricRepository.findAllByEnvironmentId(environmentId);

        var metrics = new ArrayList<MetricsResponse>();
        entities.forEach(entity -> metrics.add(
                MetricsResponse
                        .builder()
                        .at(entity.getCreatedAt())
                        .suites(entity.getSuites())
                        .tests(entity.getTests())
                        .passPercent(entity.getPassPercent())
                        .passes(entity.getPasses())
                        .failures(entity.getFailures())
                        .skipped(entity.getSkipped())
                        .build()));

        return metrics;
    }

}

