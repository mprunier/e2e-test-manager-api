package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteView;
import fr.plum.e2e.manager.core.domain.model.view.CriteriaOptionView;
import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;
import fr.plum.e2e.manager.core.domain.port.out.query.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.SuiteMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class JpaSearchSuiteConfigurationAdapter implements SearchSuiteConfigurationPort {

  private final EntityManager entityManager;

  @Override
  public PaginatedView<ConfigurationSuiteView> search(SearchSuiteConfigurationQuery query) {

    StringBuilder queryStr =
        new StringBuilder(
            "SELECT DISTINCT s FROM JpaSuiteConfigurationEntity s "
                + "WHERE s.fileConfiguration.environmentId = :environmentId");

    Parameters params = new Parameters();
    params.and("environmentId", query.environmentId().value());

    if (query.suiteConfigurationId() != null) {
      queryStr.append(" AND s.id = :suiteId");
      params.and("suiteId", query.suiteConfigurationId().value());
    }

    if (query.testConfigurationId() != null) {
      queryStr.append(" AND EXISTS (SELECT 1 FROM s.testConfigurations t WHERE t.id = :testId)");
      params.and("testId", query.testConfigurationId().value());
    }

    if (query.tag() != null) {
      queryStr.append(
          " AND (:tag MEMBER OF s.tags OR EXISTS (SELECT 1 FROM s.testConfigurations t WHERE :tag MEMBER OF t.tags))");
      params.and("tag", query.tag().value());
    }

    if (query.fileName() != null) {
      queryStr.append(" AND s.fileConfiguration.value = :value");
      params.and("value", query.fileName().value());
    }

    if (query.status() != null) {
      queryStr.append(" AND s.status = :status");
      params.and("status", query.status());
    }

    if (Boolean.TRUE.equals(query.allNotSuccess())) {
      queryStr.append(" AND s.status != :successStatus");
      params.and("successStatus", ConfigurationStatus.SUCCESS);
    }

    queryStr.append(" ORDER BY ");

    String orderByClause =
        switch (query.sortField()) {
          case "file" -> "s.fileConfiguration.fileName";
          case "lastPlayedAt" -> "s.lastPlayedAt";
          case "title" -> "s.title";
          case "status" -> "s.status";
          default -> "s.id";
        };

    queryStr
        .append(orderByClause)
        .append(" ")
        .append("asc".equalsIgnoreCase(query.sortOrder()) ? "ASC" : "DESC");

    PanacheQuery<JpaSuiteConfigurationEntity> panacheQuery =
        JpaSuiteConfigurationEntity.find(queryStr.toString(), params.map());

    panacheQuery.page(Page.of(query.page(), query.size()));

    List<JpaSuiteConfigurationEntity> results = panacheQuery.list();

    if (!results.isEmpty()) {
      entityManager
          .createQuery(
              """
                SELECT DISTINCT s
                FROM JpaSuiteConfigurationEntity s
                LEFT JOIN FETCH s.testConfigurations
                LEFT JOIN FETCH s.fileConfiguration
                WHERE s IN :suites
                """,
              JpaSuiteConfigurationEntity.class)
          .setParameter("suites", results)
          .getResultList();
    }

    List<ConfigurationSuiteView> content =
        results.stream().map(SuiteMapper::toSuiteResponse).toList();

    return new PaginatedView<>(
        content, query.page(), panacheQuery.pageCount(), query.size(), panacheQuery.count());
  }

  @Override
  public SearchCriteriaView findAllCriteria(EnvironmentId environmentId) {
    List<CriteriaOptionView> suites =
        entityManager
            .createQuery(
                "SELECT id, title FROM JpaSuiteConfigurationEntity WHERE fileConfiguration.environmentId = :envId ORDER BY LOWER(title)",
                Object[].class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(result -> new CriteriaOptionView(result[0].toString(), (String) result[1]))
            .toList();

    List<CriteriaOptionView> tests =
        entityManager
            .createQuery(
                "SELECT id, title FROM JpaTestConfigurationEntity WHERE suiteConfiguration.fileConfiguration.environmentId = :envId ORDER BY LOWER(title)",
                Object[].class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(result -> new CriteriaOptionView(result[0].toString(), (String) result[1]))
            .toList();

    List<CriteriaOptionView> files =
        entityManager
            .createQuery(
                "SELECT fileName FROM JpaFileConfigurationEntity WHERE environmentId = :envId ORDER BY LOWER(fileName)",
                String.class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(fileName -> new CriteriaOptionView(fileName, fileName))
            .toList();

    var tags = mergeTags(environmentId);

    return new SearchCriteriaView(suites, tests, files, tags);
  }

  private List<CriteriaOptionView> mergeTags(EnvironmentId environmentId) {
    @SuppressWarnings("unchecked")
    List<String> suiteTags =
        entityManager
            .createNativeQuery(
                """
                SELECT DISTINCT unnest(s.tags)
                FROM suite_configuration s
                JOIN file_configuration f ON s.file_configuration_name = f.file_name
                AND s.file_configuration_environment_id = f.environment_id
                WHERE f.environment_id = :envId
                """)
            .setParameter("envId", environmentId.value())
            .getResultList();

    @SuppressWarnings("unchecked")
    List<String> testTags =
        entityManager
            .createNativeQuery(
                """
            SELECT DISTINCT unnest(t.tags)
            FROM test_configuration t
            JOIN suite_configuration s ON t.suite_id = s.id
            JOIN file_configuration f ON s.file_configuration_name = f.file_name
            AND s.file_configuration_environment_id = f.environment_id
            WHERE f.environment_id = :envId
            """)
            .setParameter("envId", environmentId.value())
            .getResultList();

    List<String> groupNames =
        entityManager
            .createQuery(
                """
            SELECT DISTINCT e.groupName
            FROM JpaFileConfigurationEntity e
            WHERE e.environmentId = :envId
            AND e.groupName IS NOT NULL
            """,
                String.class)
            .setParameter("envId", environmentId.value())
            .getResultList();

    return Stream.of(suiteTags, testTags, groupNames)
        .flatMap(List::stream)
        .filter(tag -> tag != null && !tag.isEmpty())
        .distinct()
        .sorted(String.CASE_INSENSITIVE_ORDER)
        .map(tag -> new CriteriaOptionView(tag, tag))
        .toList();
  }
}
