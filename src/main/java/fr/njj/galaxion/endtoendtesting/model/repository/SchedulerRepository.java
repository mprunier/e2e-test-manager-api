package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.SchedulerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SchedulerRepository implements PanacheRepositoryBase<SchedulerEntity, Long> {

    public List<SchedulerEntity> findAllByEnvironment(long environmentId) {
        return find("environment.id = ?1 ORDER BY createdAt DESC LIMIT 10", environmentId).stream().toList();
    }

    public boolean assertExistInProgressByEnvironment(Long environmentId) {
        return count("environment.id = ?1 AND status = 'IN_PROGRESS'", environmentId) > 0;
    }

    public Optional<SchedulerEntity> findByPipelineId(String pipelineId) {
        return find("pipelineId = ?1", pipelineId).firstResultOptional();
    }
}
