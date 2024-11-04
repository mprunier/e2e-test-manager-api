package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.PipelineGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PipelineGroupRepository implements PanacheRepositoryBase<PipelineGroupEntity, Long> {

  public PipelineGroupEntity findLastPipelineGroupByEnvironmentId(Long environmentId) {
    return find("environment.id = ?1 ORDER BY createdAt DESC", environmentId).firstResult();
  }
}
