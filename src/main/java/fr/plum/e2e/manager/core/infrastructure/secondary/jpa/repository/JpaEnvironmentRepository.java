package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.environment.JpaEnvironmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JpaEnvironmentRepository implements PanacheRepositoryBase<JpaEnvironmentEntity, UUID> {

  public List<JpaEnvironmentEntity> findByProjectIdAndBranch(String projectId, String branch) {
    return find("projectId = ?1 and branch = ?2", projectId, branch).list();
  }
}
