package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.FileGroupEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class FileGroupRepository implements PanacheRepositoryBase<FileGroupEntity, String> {

  public void deleteByFileAndEnv(String file, long environmentId) {
    delete("environment.id = ?1 AND file = ?2", environmentId, file);
  }

  public Optional<FileGroupEntity> findByFileAndEnv(String file, long environmentId) {
    return find("environment.id = ?1 AND file = ?2", environmentId, file).firstResultOptional();
  }

  public List<FileGroupEntity> findAllByEnv(long environmentId) {
    return list("environment.id", environmentId);
  }

  public Set<String> findAllFilesByGroup(long environmentId, String group) {
    return find("environment.id = ?1 AND group = ?2", environmentId, group).stream()
        .map(FileGroupEntity::getFile)
        .collect(Collectors.toSet());
  }
}
