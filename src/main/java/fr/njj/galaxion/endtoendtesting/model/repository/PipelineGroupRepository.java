package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.PipelineGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PipelineGroupRepository implements PanacheRepositoryBase<PipelineGroupEntity, Long> {

  public PipelineGroupEntity findLastPipelineGroupByEnvironmentId(Long environmentId) {
    return find("environment.id = ?1 ORDER BY created_at DESC", environmentId).firstResult();
  }
}
