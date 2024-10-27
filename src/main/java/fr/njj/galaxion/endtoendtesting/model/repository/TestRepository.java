package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestRepository implements PanacheRepositoryBase<TestEntity, Long> {

  public List<TestEntity> findAllByConfigurationTestId(Long configurationTestId) {
    return list(
        "configurationTest.id = ?1 AND isWaiting is FALSE ORDER BY createdAt DESC LIMIT 20",
        configurationTestId);
  }

  public List<TestEntity> findAllBy(List<Long> ids) {
    return list("id IN ?1", ids);
  }

  public List<TestEntity> findAllByPipelineId(String pipelineId) {
    return list("pipelineId = ?1", pipelineId);
  }
}
