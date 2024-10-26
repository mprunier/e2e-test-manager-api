package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.TemporaryTestEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TemporaryTestRepository implements PanacheRepositoryBase<TemporaryTestEntity, Long> {

  public List<TemporaryTestEntity> findAllByPipelineId(String pipelineId) {
    return list("pipelineId = ?1", pipelineId);
  }
}
