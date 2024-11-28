package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.metrics.JpaMetricsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaMetricsRepository implements PanacheRepositoryBase<JpaMetricsEntity, UUID> {

  public Optional<JpaMetricsEntity> findLastMetrics(
      EnvironmentId environmentId, MetricsType metricsType) {
    return find(
            "environmentId = ?1 and type = ?2 order by createdAt desc",
            environmentId.value(),
            metricsType)
        .firstResultOptional();
  }
}
