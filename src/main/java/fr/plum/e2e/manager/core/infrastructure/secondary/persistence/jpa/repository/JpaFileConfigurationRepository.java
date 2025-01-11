package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaFileConfigurationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaFileConfigurationId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaFileConfigurationRepository
    implements PanacheRepositoryBase<JpaFileConfigurationEntity, JpaFileConfigurationId> {

  public List<JpaFileConfigurationEntity> findAll(EnvironmentId environmentId) {
    return list("environmentId = ?1", environmentId.value());
  }

  public void deleteAll(List<JpaFileConfigurationEntity> entities) {
    delete(
        "id in ?1",
        entities.stream()
            .map(e -> new JpaFileConfigurationId(e.getFileName(), e.getEnvironmentId()))
            .toList());
  }

  public List<JpaFileConfigurationEntity> findAll(
      EnvironmentId environmentId, GroupName groupName) {
    return list("environmentId = ?1 AND groupName = ?2", environmentId.value(), groupName.value());
  }

  public Optional<JpaFileConfigurationEntity> find(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    return find(
            "FROM JpaFileConfigurationEntity fc JOIN fc.suiteConfigurations sc "
                + "WHERE fc.environmentId = ?1 AND sc.id = ?2",
            environmentId.value(),
            suiteConfigurationId.value())
        .firstResultOptional();
  }

  public Optional<JpaFileConfigurationEntity> find(
      EnvironmentId environmentId, TestConfigurationId testConfigurationId) {
    return find(
            "FROM JpaFileConfigurationEntity fc JOIN fc.suiteConfigurations sc "
                + "JOIN sc.testConfigurations tc "
                + "WHERE fc.environmentId = ?1 AND tc.id = ?2",
            environmentId.value(),
            testConfigurationId.value())
        .firstResultOptional();
  }
}
