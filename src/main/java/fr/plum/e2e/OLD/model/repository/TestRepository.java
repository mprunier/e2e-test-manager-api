package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TestRepository implements PanacheRepositoryBase<JpaTestResultEntity, Long> {

  public List<JpaTestResultEntity> findAllByConfigurationTestId(Long configurationTestId) {
    return list(
        "configurationTest.id = ?1 AND isWaiting is FALSE ORDER BY createdAt DESC LIMIT 20",
        configurationTestId);
  }

  public List<JpaTestResultEntity> findAllBy(List<Long> ids) {
    return list("id IN ?1", ids);
  }

  public List<JpaTestResultEntity> findAllByPipelineId(String pipelineId) {
    return list("pipelineId = ?1", pipelineId);
  }
}
