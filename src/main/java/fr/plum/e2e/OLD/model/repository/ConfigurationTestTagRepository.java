package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.ConfigurationTestTagEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConfigurationTestTagRepository
    implements PanacheRepositoryBase<ConfigurationTestTagEntity, Long> {

  public List<ConfigurationTestTagEntity> findAllByEnv(long environmentId) {
    return list("environmentId = ?1", environmentId);
  }

  public List<ConfigurationTestTagEntity> findAllByEnvAndTag(long environmentId, String tag) {
    return list("environmentId = ?1 AND tag = ?2", environmentId, tag);
  }
}
