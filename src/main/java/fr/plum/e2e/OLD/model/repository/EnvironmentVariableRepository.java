package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.EnvironmentVariableEntity;
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
