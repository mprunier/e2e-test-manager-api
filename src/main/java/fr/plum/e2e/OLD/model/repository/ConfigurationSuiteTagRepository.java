package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.ConfigurationSuiteTagEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConfigurationSuiteTagRepository
    implements PanacheRepositoryBase<ConfigurationSuiteTagEntity, Long> {

  public List<ConfigurationSuiteTagEntity> findAllByEnv(long environmentId) {
    return list("environmentId = ?1", environmentId);
  }

  public List<ConfigurationSuiteTagEntity> findAllByEnvAndTag(long environmentId, String tag) {
    return list("environmentId = ?1 AND tag = ?2", environmentId, tag);
  }
}
