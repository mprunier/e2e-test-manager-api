package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSynchronizationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigurationSynchronizationRepository implements PanacheRepositoryBase<ConfigurationSynchronizationEntity, Long> {

    public ConfigurationSynchronizationEntity findBy(long environmentId) {
        return find("environment.id = ?1", environmentId).stream().findFirst().get();
    }
}
