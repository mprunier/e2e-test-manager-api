package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EnvironmentRepository implements PanacheRepositoryBase<EnvironmentEntity, Long> {

    public List<EnvironmentEntity> findAllEnvironmentsEnabled() {
        return list("isEnabled = ?1", true);
    }

    public Optional<EnvironmentEntity> findByBranchAndProjectId(String branch, String projectId) {
        return find("branch = ?1 AND projectId = ?2", branch, projectId).firstResultOptional();
    }
}
