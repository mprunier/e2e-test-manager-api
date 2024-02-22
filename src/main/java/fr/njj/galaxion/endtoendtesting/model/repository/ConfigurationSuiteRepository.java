package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ConfigurationSuiteRepository implements PanacheRepositoryBase<ConfigurationSuiteEntity, Long> {

    public List<ConfigurationSuiteEntity> findAllBy(String file, long environmentId) {
        return find("environment.id = ?1 AND file = ?2", environmentId, file).stream().toList();
    }

    public Optional<ConfigurationSuiteEntity> findBy(String file, long environmentId, String title, Long suiteParentId) {
        if (suiteParentId == null) {
            return find("environment.id = ?1 AND file = ?2 AND title = ?3 AND parentSuite IS NULL", environmentId, file, title).firstResultOptional();
        } else {
            return find("environment.id = ?1 AND file = ?2 AND title = ?3 AND parentSuite.id = ?4", environmentId, file, title, suiteParentId).firstResultOptional();
        }
    }

    public List<ConfigurationSuiteEntity> findByParentId(Long suiteParentId) {
        return find("parentSuite.id = ?1", suiteParentId).stream().toList();
    }

    public List<ConfigurationSuiteEntity> findAllBy(long environmentId) {
        return find("environment.id = ?1", environmentId).stream().toList();
    }

    public void deleteBy(String file, long environmentId) {
        delete("environment.id = ?1 AND file = ?2", environmentId, file);
    }

    public void deleteByEnvAndFileAndNotInSuiteIds(long environmentId, String file, List<Long> suiteIds) {
        delete("environment.id IN ?1 AND file = ?2 AND id NOT IN ?3", environmentId, file, suiteIds);
    }

    public List<String> findAllFilesBy(long environmentId) {
        return find("SELECT DISTINCT file FROM ConfigurationSuiteEntity WHERE environment.id = ?1", environmentId).project(String.class).list();
    }
}
