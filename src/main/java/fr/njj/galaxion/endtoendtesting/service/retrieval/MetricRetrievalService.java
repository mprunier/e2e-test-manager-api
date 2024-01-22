package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.MetricRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MetricRetrievalService {

  private final MetricRepository metricRepository;

  @Transactional
  public List<MetricsEntity> getAllByEnvironmentSince(long environmentId, LocalDate since) {
    return metricRepository.findAllByEnvironmentIdSince(environmentId, since);
  }

  @Transactional
  public Optional<MetricsEntity> getOptionalLastMetrics(long environmentId) {
    return metricRepository.findLastMetrics(environmentId);
  }

  @Transactional
  public Optional<MetricsEntity> getOptionalLastMetricsWithAllTestsRun(long environmentId) {
    return metricRepository.findLastMetricsWithAllTests(environmentId);
  }
}
