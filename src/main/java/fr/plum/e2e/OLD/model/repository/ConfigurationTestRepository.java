package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import fr.plum.e2e.OLD.model.entity.ConfigurationTestEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class ConfigurationTestRepository
    implements PanacheRepositoryBase<ConfigurationTestEntity, Long> {

  public void deleteByFileAndEnv(String file, long environmentId) {
    delete("environment.id = ?1 AND file = ?2", environmentId, file);
  }

  public Optional<ConfigurationTestEntity> findBy(
      String file, long environmentId, long suiteId, String title) {
    return find(
            "environment.id = ?1 AND file = ?2 AND configurationSuite.id = ?3 AND title = ?4",
            environmentId,
            file,
            suiteId,
            title)
        .firstResultOptional();
  }

  public List<ConfigurationTestEntity> findAllBy(long environmentId) {
    return list("environment.id = ?1 ORDER BY title", environmentId);
  }

  public List<ConfigurationTestEntity> findAllNewByEnvironment(long environmentId) {
    return list("environment.id = ?1 AND status = ?2", environmentId, ConfigurationStatus.NEW);
  }

  public void deleteByEnvAndFileAndNotInTestIds(
      long environmentId, String file, List<Long> testIds) {
    delete("environment.id = ?1 AND file = ?2 AND id NOT IN ?3", environmentId, file, testIds);
  }

  public List<ConfigurationTestEntity> findAllByIds(Set<Long> configurationTestIds) {
    return list("id IN ?1", configurationTestIds);
  }

  public List<ConfigurationTestEntity> findAllByFiles(List<String> files) {
    return list("file IN ?1", files);
  }

  public List<ConfigurationTestEntity> findAllNewTests(long environmentId) {
    return list("environment.id = ?1 AND status = ?2", environmentId, ConfigurationStatus.NEW);
  }
}
