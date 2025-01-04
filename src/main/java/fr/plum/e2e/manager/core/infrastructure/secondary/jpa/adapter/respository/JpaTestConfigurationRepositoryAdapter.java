package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.respository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.port.repository.TestConfigurationRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaTestConfigurationRepositoryAdapter implements TestConfigurationRepositoryPort {

  private final EntityManager entityManager;

  @Override
  public Optional<TestConfigurationId> findId(
      FileName fileName, SuiteTitle suiteTitle, TestTitle testTitle) {
    var query =
        entityManager.createQuery(
            """
            SELECT t.id
            FROM JpaTestConfigurationEntity t
            JOIN t.suiteConfiguration s
            JOIN s.fileConfiguration f
            WHERE f.fileName = :fileName
            AND s.title = :suiteTitle
            AND t.title = :testTitle
            """,
            UUID.class);

    query.setParameter("fileName", fileName.value());
    query.setParameter("suiteTitle", suiteTitle.value());
    query.setParameter("testTitle", testTitle.value());

    return query.getResultStream().findFirst().map(TestConfigurationId::new);
  }

  @Override
  public List<TestConfigurationId> findAllIds(
      EnvironmentId environmentId, List<FileName> fileNames) {
    var query =
        entityManager.createQuery(
            """
            SELECT t.id
            FROM JpaTestConfigurationEntity t
            JOIN t.suiteConfiguration s
            JOIN s.fileConfiguration f
            WHERE f.environmentId = :environmentId
            AND f.fileName IN :fileNames
            """,
            UUID.class);

    query.setParameter("environmentId", environmentId.value());
    query.setParameter(
        "fileNames", fileNames.stream().map(FileName::value).collect(Collectors.toList()));

    return query.getResultList().stream()
        .map(TestConfigurationId::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<TestConfigurationId> findAllIds(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    var query =
        entityManager.createQuery(
            """
            SELECT t.id
            FROM JpaTestConfigurationEntity t
            JOIN t.suiteConfiguration s
            JOIN s.fileConfiguration f
            WHERE f.environmentId = :environmentId
            AND s.id = :suiteId
            """,
            UUID.class);

    query.setParameter("environmentId", environmentId.value());
    query.setParameter("suiteId", suiteConfigurationId.value());

    return query.getResultList().stream()
        .map(TestConfigurationId::new)
        .collect(Collectors.toList());
  }
}
