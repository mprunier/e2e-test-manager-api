package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobType;
import fr.njj.galaxion.endtoendtesting.model.entity.JobEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;
import java.util.List;

@ApplicationScoped
public class JobRepository implements PanacheRepositoryBase<JobEntity, Long> {

    public List<JobEntity> getInProgress(JobType type) {
        return list("status = ?1 AND type = ?2",
                    JobStatus.IN_PROGRESS, type);
    }

    public List<JobEntity> getOldInProgress(JobType type, long oldMinutes) {
        return list("status = ?1 AND createdAt < ?2 AND type = ?3",
                    JobStatus.IN_PROGRESS, ZonedDateTime.now().minusMinutes(oldMinutes), type);
    }

    public long countInProgress() {
        return count("status = ?1", JobStatus.IN_PROGRESS);
    }

}
