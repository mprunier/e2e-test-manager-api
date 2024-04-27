package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MetricRepository implements PanacheRepositoryBase<MetricsEntity, Long> {

    public List<MetricsEntity> findAllByEnvironmentIdSince(long environmentId, LocalDate since) {
        var sinceStartOfDay = since.atStartOfDay(ZoneId.systemDefault());
        return list("environment.id = ?1 AND createdAt >= ?2 ORDER BY createdAt ASC", environmentId, sinceStartOfDay);
    }

    public Optional<MetricsEntity> findLastMetrics(long environmentId) {
        return find("environment.id = ?1 ORDER BY createdAt DESC", environmentId).firstResultOptional();
    }

    public Optional<MetricsEntity> findLastMetricsWithAllTests(long environmentId) {
        return find("environment.id = ?1 AND isAllTestsRun IS TRUE ORDER BY createdAt DESC", environmentId).firstResultOptional();
    }
}
