package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestRepository implements PanacheRepositoryBase<TestEntity, Long> {

  public List<TestEntity> findAllByConfigurationTestId(Long configurationTestId) {
    return list("configurationTest.id = ?1 ORDER BY createdAt DESC LIMIT 10", configurationTestId);
  }

  public long countInProgressTestEntityByEnvironmentId(long environmentId) {
    return count(
        "configurationTest.environment.id = ?1 AND status = ?2",
        environmentId,
        ConfigurationStatus.IN_PROGRESS);
  }

  public List<TestEntity> findAllBy(List<Long> ids) {
    return list("id IN ?1", ids);
  }

  public List<TestEntity> findAllErrorByPipelineId(String pipelineId) {
    return list("pipelineId = ?1 AND status != 'IN_PROGRESS' AND status != 'SUCCESS'", pipelineId);
  }
}
