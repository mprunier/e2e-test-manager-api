package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import java.util.List;

@ApplicationScoped
public class PipelineRepository implements PanacheRepositoryBase<PipelineEntity, String> {

  public List<PipelineEntity> getOldInProgress(long oldMinutes) {
    return list(
        "status = ?1 AND createdAt < ?2",
        PipelineStatus.IN_PROGRESS,
        ZonedDateTime.now().minusMinutes(oldMinutes));
  }

  public long countInProgress() {
    return count("status = ?1", PipelineStatus.IN_PROGRESS);
  }

  public boolean isAllTestRunning(long environmentId) {
    return count(
            "environment.id = ?1 AND status = ?2 AND (type = ?3 or type = ?4)",
            environmentId,
            PipelineStatus.IN_PROGRESS,
            PipelineType.ALL,
            PipelineType.ALL_IN_PARALLEL)
        > 0;
  }

  public List<PipelineEntity> getAllInProgress(long environmentId) {
    return list("environment.id = ?1 AND status = ?2", environmentId, PipelineStatus.IN_PROGRESS);
  }
}
