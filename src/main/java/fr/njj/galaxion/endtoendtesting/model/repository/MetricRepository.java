package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
public class MetricRepository implements PanacheRepositoryBase<MetricsEntity, Long> {

    public List<MetricsEntity> findAllByEnvironmentIdSince(long environmentId, LocalDate since) {
        var sinceStartOfDay = since.atStartOfDay(ZoneId.systemDefault());
        return list("environment.id = ?1 AND createdAt >= ?2 ORDER BY createdAt DESC", environmentId, sinceStartOfDay);
    }
}
