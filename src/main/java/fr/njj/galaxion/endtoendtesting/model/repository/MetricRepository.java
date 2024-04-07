package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MetricRepository implements PanacheRepositoryBase<MetricsEntity, Long> {

    public List<MetricsEntity> findAllByEnvironmentId(long environmentId) {
        return list("environment.id = ?1", environmentId);
    }
}
