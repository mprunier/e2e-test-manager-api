package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;
import java.util.List;

@ApplicationScoped
public class PipelineRepository implements PanacheRepositoryBase<PipelineEntity, String> {

    public List<PipelineEntity> getOldInProgress(long oldMinutes) {
        return list("status = ?1 AND createdAt < ?2",
                    PipelineStatus.IN_PROGRESS, ZonedDateTime.now().minusMinutes(oldMinutes));
    }

    public long countInProgress() {
        return count("status = ?1", PipelineStatus.IN_PROGRESS);
    }

}
