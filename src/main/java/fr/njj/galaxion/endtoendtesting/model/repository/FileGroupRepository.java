package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.FileGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class FileGroupRepository implements PanacheRepositoryBase<FileGroupEntity, String> {

  public void deleteByFileAndEnv(String file, long environmentId) {
    delete("environment.id = ?1 AND file = ?2", environmentId, file);
  }

  public Optional<FileGroupEntity> findByFileAndEnv(String file, long environmentId) {
    return find("environment.id = ?1 AND file = ?2", environmentId, file).firstResultOptional();
  }
}
