package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentSynchronizationErrorEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EnvironmentSynchronizationErrorRepository
    implements PanacheRepositoryBase<EnvironmentSynchronizationErrorEntity, Long> {

  public Optional<EnvironmentSynchronizationErrorEntity> findByEnvironmentIdAndFile(
      long environmentId, String file) {
    return find("environment.id = ?1 AND file = ?2", environmentId, file).stream().findFirst();
  }

  public void deleteByEnvironmentIdAndFile(long environmentId, String file) {
    delete("environment.id = ?1 AND file = ?2", environmentId, file);
  }

  public void deleteByEnvironmentId(long environmentId) {
    delete("environment.id = ?1", environmentId);
  }

  public List<EnvironmentSynchronizationErrorEntity> findByEnvironmentId(long environmentId) {
    return find("environment.id = ?1", environmentId).stream().toList();
  }
}
