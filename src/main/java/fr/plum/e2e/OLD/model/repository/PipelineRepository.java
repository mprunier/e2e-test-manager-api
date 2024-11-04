package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.domain.enumeration.PipelineType;
import fr.plum.e2e.OLD.model.entity.PipelineEntity;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.WorkerStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import java.util.List;

@ApplicationScoped
public class PipelineRepository implements PanacheRepositoryBase<PipelineEntity, String> {

  public List<PipelineEntity> getOldInProgress(long oldMinutes) {
    return list(
        "status = ?1 AND createdAt < ?2",
        WorkerStatus.IN_PROGRESS,
        ZonedDateTime.now().minusMinutes(oldMinutes));
  }

  public long countInProgress() {
    return count("status = ?1", WorkerStatus.IN_PROGRESS);
  }

  public boolean isAllTestRunning(long environmentId) {
    return count(
            "environment.id = ?1 AND status = ?2 AND (type = ?3 or type = ?4)",
            environmentId,
            WorkerStatus.IN_PROGRESS,
            PipelineType.ALL,
            PipelineType.ALL_IN_PARALLEL)
        > 0;
  }

  public List<PipelineEntity> getAllInProgress(long environmentId) {
    return list("environment.id = ?1 AND status = ?2", environmentId, WorkerStatus.IN_PROGRESS);
  }
}
