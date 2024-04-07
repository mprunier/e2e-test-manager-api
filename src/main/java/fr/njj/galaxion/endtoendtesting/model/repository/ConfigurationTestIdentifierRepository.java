package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestIdentifierEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ConfigurationTestIdentifierRepository implements PanacheRepositoryBase<ConfigurationTestIdentifierEntity, Long> {

    public List<ConfigurationTestIdentifierEntity> findAllByEnv(long environmentId) {
        return list("environmentId = ?1", environmentId);
    }

    public List<ConfigurationTestIdentifierEntity> findAllByEnvAndIdentifier(long environmentId, String identifier) {
        return list("environmentId = ?1 AND identifier = ?2", environmentId, identifier);
    }
}
