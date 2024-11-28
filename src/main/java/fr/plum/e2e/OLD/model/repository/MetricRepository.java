package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.metrics.JpaMetricsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetricRepository implements PanacheRepositoryBase<JpaMetricsEntity, Long> {

  public List<JpaMetricsEntity> findAllByEnvironmentIdSince(long environmentId, LocalDate since) {
    var sinceStartOfDay = since.atStartOfDay(ZoneId.systemDefault());
    return list(
        "environment.id = ?1 AND createdAt >= ?2 ORDER BY createdAt ASC",
        environmentId,
        sinceStartOfDay);
  }

  public Optional<JpaMetricsEntity> findLastMetrics(long environmentId) {
    return find("environment.id = ?1 ORDER BY createdAt DESC", environmentId).firstResultOptional();
  }

  public Optional<JpaMetricsEntity> findLastMetricsWithAllTests(long environmentId) {
    return find(
            "environment.id = ?1 AND isAllTestsRun IS TRUE ORDER BY createdAt DESC", environmentId)
        .firstResultOptional();
  }
}
