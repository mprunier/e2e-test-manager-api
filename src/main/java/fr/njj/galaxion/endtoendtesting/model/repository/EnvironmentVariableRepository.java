package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class EnvironmentVariableRepository
    implements PanacheRepositoryBase<EnvironmentVariableEntity, Long> {

  public Optional<EnvironmentVariableEntity> findByEnvironmentAndName(
      Long environmentId, String name) {
    return find("environment.id = ?1 AND name = ?2", environmentId, name).firstResultOptional();
  }
}
