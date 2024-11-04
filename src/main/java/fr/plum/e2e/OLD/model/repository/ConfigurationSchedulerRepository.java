package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.ConfigurationSchedulerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConfigurationSchedulerRepository
    implements PanacheRepositoryBase<ConfigurationSchedulerEntity, Long> {

  public ConfigurationSchedulerEntity findBy(long environmentId) {
    return find("environment.id = ?1", environmentId).stream().findFirst().get();
  }

  public List<ConfigurationSchedulerEntity> findAllEnabled() {
    return list("isEnabled = true");
  }
}
