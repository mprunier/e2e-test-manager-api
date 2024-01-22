package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ConfigurationTestRepository implements PanacheRepositoryBase<ConfigurationTestEntity, Long> {

    public void deleteBy(String file, long environmentId) {
        delete("environment.id = ?1 AND file = ?2", environmentId, file);
    }

    public Optional<ConfigurationTestEntity> findBy(String file, long environmentId, long suiteId, String title) {
        return find("environment.id = ?1 AND file = ?2 AND configurationSuite.id = ?3 AND title = ?4", environmentId, file, suiteId, title).firstResultOptional();
    }

    public List<ConfigurationTestEntity> findAllBy(long environmentId) {
        return find("environment.id = ?1", environmentId).stream().toList();
    }

    public void deleteBy(long environmentId, String file, List<Long> testIds) {
        delete("environment.id IN ?1 AND file = ?2 AND id NOT IN ?3", environmentId, file, testIds);
    }
}
