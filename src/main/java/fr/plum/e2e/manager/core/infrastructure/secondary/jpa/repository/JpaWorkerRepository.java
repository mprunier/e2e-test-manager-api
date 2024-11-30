package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.worker.JpaWorkerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JpaWorkerRepository implements PanacheRepositoryBase<JpaWorkerEntity, UUID> {

  public List<JpaWorkerEntity> findByEnvironmentId(UUID environmentId) {
    return list("environmentId", environmentId);
  }

  public Optional<JpaWorkerEntity> findByEnvironmentIdAndType(UUID environmentId, WorkerType type) {
    return find("environmentId = ?1 and type = ?2", environmentId, type).firstResultOptional();
  }

  public Optional<JpaWorkerEntity> findByUnitId(String workerUnitId) {
    return find(
            "SELECT DISTINCT w FROM JpaWorkerEntity w JOIN w.units u WHERE u.id = ?1", workerUnitId)
        .firstResultOptional();
  }

  public Optional<JpaWorkerEntity> findInProgressByEnvironmentAndType(
      UUID environmentId, WorkerType type) {
    return find(
            """
            SELECT DISTINCT w
            FROM JpaWorkerEntity w
            JOIN w.units u
            WHERE w.environmentId = ?1
              AND w.type = ?2
              AND u.status = ?3
            """,
            environmentId,
            type,
            WorkerUnitStatus.IN_PROGRESS)
        .firstResultOptional();
  }
}
