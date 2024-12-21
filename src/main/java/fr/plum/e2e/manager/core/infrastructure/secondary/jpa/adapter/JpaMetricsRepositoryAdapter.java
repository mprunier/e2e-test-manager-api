package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.port.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.MetricsMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.metrics.JpaMetricsEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaMetricsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaMetricsRepositoryAdapter implements MetricsRepositoryPort {

  private final EntityManager entityManager;
  private final JpaMetricsRepository repository;

  @Override
  public Optional<Metrics> findLastMetrics(EnvironmentId environmentId, MetricsType metricsType) {
    return repository.findLastMetrics(environmentId, metricsType).map(MetricsMapper::toDomain);
  }

  @Override
  public void save(Metrics metrics) {
    var entity = MetricsMapper.toEntity(metrics);
    entity.persist();
  }

  @Override
  public TestCount testCount(EnvironmentId environmentId) {
    var result =
        entityManager
            .createQuery(
                """
                   SELECT COUNT(t)
                   FROM JpaTestConfigurationEntity t
                   JOIN t.suiteConfiguration s
                   JOIN s.fileConfiguration f
                   WHERE f.environmentId = :envId
                   """,
                Long.class)
            .setParameter("envId", environmentId.value())
            .getSingleResult();
    return new TestCount(result.intValue());
  }

  @Override
  public SuiteCount suiteCount(EnvironmentId environmentId) {
    var result =
        entityManager
            .createQuery(
                """
                   SELECT COUNT(s)
                   FROM JpaSuiteConfigurationEntity s
                   JOIN s.fileConfiguration f
                   WHERE f.environmentId = :envId
                   """,
                Long.class)
            .setParameter("envId", environmentId.value())
            .getSingleResult();
    return new SuiteCount(result.intValue());
  }

  @Override
  public PassCount passCount(EnvironmentId environmentId) {
    var result =
        entityManager
            .createQuery(
                """
                   SELECT COUNT(t)
                   FROM JpaTestConfigurationEntity t
                   JOIN t.suiteConfiguration s
                   JOIN s.fileConfiguration f
                   WHERE f.environmentId = :envId
                   AND t.status = :status
                   """,
                Long.class)
            .setParameter("envId", environmentId.value())
            .setParameter("status", ConfigurationStatus.SUCCESS)
            .getSingleResult();
    return new PassCount(result.intValue());
  }

  @Override
  public FailureCount failureCount(EnvironmentId environmentId) {
    var result =
        entityManager
            .createQuery(
                """
                   SELECT COUNT(t)
                   FROM JpaTestConfigurationEntity t
                   JOIN t.suiteConfiguration s
                   JOIN s.fileConfiguration f
                   WHERE f.environmentId = :envId
                   AND (t.status = :status OR t.status = :systemErrorStatus OR t.status = :noCorrespondTestStatus OR t.status = :noReportErrorStatus OR t.status = :unknownStatus)
                   """,
                Long.class)
            .setParameter("envId", environmentId.value())
            .setParameter("status", ConfigurationStatus.FAILED)
            .setParameter("systemErrorStatus", ConfigurationStatus.SYSTEM_ERROR)
            .setParameter("noCorrespondTestStatus", ConfigurationStatus.NO_CORRESPONDING_TEST)
            .setParameter("noReportErrorStatus", ConfigurationStatus.NO_REPORT_ERROR)
            .setParameter("unknownStatus", ConfigurationStatus.UNKNOWN)
            .getSingleResult();
    return new FailureCount(result.intValue());
  }

  @Override
  public SkippedCount skippedCount(EnvironmentId environmentId) {
    var result =
        entityManager
            .createQuery(
                """
                   SELECT COUNT(t)
                   FROM JpaTestConfigurationEntity t
                   JOIN t.suiteConfiguration s
                   JOIN s.fileConfiguration f
                   WHERE f.environmentId = :envId
                   AND (t.status = :status OR t.status = :partialStatus OR t.status = :cancelStatus)
                   """,
                Long.class)
            .setParameter("envId", environmentId.value())
            .setParameter("status", ConfigurationStatus.SKIPPED)
            .setParameter("partialStatus", ConfigurationStatus.PARTIAL_SKIPPED)
            .setParameter("cancelStatus", ConfigurationStatus.CANCELED)
            .getSingleResult();
    return new SkippedCount(result.intValue());
  }

  @Override
  public List<Metrics> findAllMetrics(EnvironmentId environmentId, LocalDate sinceAt) {
    return entityManager
        .createQuery(
            """
                FROM JpaMetricsEntity m
                WHERE m.environmentId = :envId
                AND m.createdAt >= :since
                ORDER BY m.createdAt DESC
                """,
            JpaMetricsEntity.class)
        .setParameter("envId", environmentId.value())
        .setParameter("since", sinceAt.atStartOfDay(ZoneId.systemDefault()))
        .getResultList()
        .stream()
        .map(MetricsMapper::toDomain)
        .collect(Collectors.toList());
  }
}
