package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteProjection;
import fr.plum.e2e.manager.core.domain.model.projection.CriteriaOptionProjection;
import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import fr.plum.e2e.manager.core.domain.model.projection.SearchCriteriaProjection;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.port.projection.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper.SuiteMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class JpaSearchSuiteConfigurationAdapter implements SearchSuiteConfigurationPort {

  private final EntityManager entityManager;

  @Override
  public PaginatedProjection<ConfigurationSuiteProjection> search(
      SearchSuiteConfigurationQuery query) {

    var queryComponents = buildQueryComponents(query);

    var results = executeMainQuery(queryComponents.queryStr(), queryComponents.params(), query);
    var content = results.stream().map(SuiteMapper::toSuiteResponse).toList();

    long totalItems =
        executeCountQuery(buildCountQuery(queryComponents.queryStr()), queryComponents.params());

    return new PaginatedProjection<>(
        content,
        query.page(),
        (int) Math.ceil((double) totalItems / query.size()),
        query.size(),
        totalItems);
  }

  private record QueryComponents(StringBuilder queryStr, Map<String, Object> params) {}

  private QueryComponents buildQueryComponents(SearchSuiteConfigurationQuery query) {
    StringBuilder queryStr =
        new StringBuilder(
            "SELECT DISTINCT s FROM JpaSuiteConfigurationEntity s WHERE s.fileConfiguration.environmentId = :environmentId");

    Map<String, Object> params = new HashMap<>();
    params.put("environmentId", query.environmentId().value());

    addFilterConditions(queryStr, params, query);
    addOrderByClause(queryStr, query);

    return new QueryComponents(queryStr, params);
  }

  private void addFilterConditions(
      StringBuilder queryStr, Map<String, Object> params, SearchSuiteConfigurationQuery query) {

    if (query.suiteConfigurationId() != null) {
      queryStr.append(" AND s.id = :suiteId");
      params.put("suiteId", query.suiteConfigurationId().value());
    }

    if (query.testConfigurationId() != null) {
      queryStr.append(" AND EXISTS (SELECT 1 FROM s.testConfigurations t WHERE t.id = :testId)");
      params.put("testId", query.testConfigurationId().value());
    }

    if (query.tag() != null) {
      var tagIds = findIdsWithTag(query.tag().value(), query.environmentId().value());
      queryStr.append(" AND (s.id IN :tagIds OR s.fileConfiguration.groupName = :tag)");
      params.put("tagIds", tagIds);
      params.put("tag", query.tag().value());
    }

    if (query.fileName() != null) {
      queryStr.append(" AND s.fileConfiguration.fileName = :value");
      params.put("value", query.fileName().value());
    }

    if (query.status() != null) {
      queryStr.append(" AND s.status = :status");
      params.put("status", query.status());
    }

    if (Boolean.TRUE.equals(query.allNotSuccess())) {
      queryStr.append(" AND s.status != :successStatus");
      params.put("successStatus", ConfigurationStatus.SUCCESS);
    }
  }

  private void addOrderByClause(StringBuilder queryStr, SearchSuiteConfigurationQuery query) {
    String orderByField =
        switch (query.sortField()) {
          case "file" -> "s.fileConfiguration.fileName";
          case "lastPlayedAt" -> "s.lastPlayedAt";
          case "title" -> "s.title";
          case "status" -> "s.status";
          default -> "s.id";
        };

    queryStr
        .append(" ORDER BY ")
        .append(orderByField)
        .append(" ")
        .append("asc".equalsIgnoreCase(query.sortOrder()) ? "ASC" : "DESC");
  }

  private String buildCountQuery(StringBuilder baseQuery) {
    return "SELECT COUNT(DISTINCT s.id) "
        + baseQuery.substring(baseQuery.indexOf("FROM"), baseQuery.indexOf("ORDER BY"));
  }

  private List<JpaSuiteConfigurationEntity> executeMainQuery(
      StringBuilder queryStr, Map<String, Object> params, SearchSuiteConfigurationQuery query) {
    TypedQuery<JpaSuiteConfigurationEntity> typedQuery =
        entityManager.createQuery(queryStr.toString(), JpaSuiteConfigurationEntity.class);

    params.forEach(typedQuery::setParameter);
    typedQuery.setFirstResult(query.page() * query.size());
    typedQuery.setMaxResults(query.size());

    return typedQuery.getResultList();
  }

  private long executeCountQuery(String countQuery, Map<String, Object> params) {
    TypedQuery<Long> query = entityManager.createQuery(countQuery, Long.class);
    params.forEach(query::setParameter);
    return query.getSingleResult();
  }

  @SuppressWarnings("unchecked")
  public List<UUID> findIdsWithTag(String tag, UUID environmentId) {
    return entityManager
        .createNativeQuery(
            """
                SELECT DISTINCT sc.id
                FROM suite_configuration sc
                FULL OUTER JOIN test_configuration tc ON tc.suite_id = sc.id
                WHERE sc.file_configuration_environment_id = :environmentId
                AND (:tag = ANY(tc.tags) OR :tag = ANY(sc.tags))
                """,
            UUID.class)
        .setParameter("tag", tag)
        .setParameter("environmentId", environmentId)
        .getResultList();
  }

  @Override
  public SearchCriteriaProjection findAllCriteria(EnvironmentId environmentId) {
    List<CriteriaOptionProjection> suites =
        entityManager
            .createQuery(
                "SELECT id, title FROM JpaSuiteConfigurationEntity WHERE fileConfiguration.environmentId = :envId ORDER BY LOWER(title)",
                Object[].class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(result -> new CriteriaOptionProjection(result[0].toString(), (String) result[1]))
            .toList();

    List<CriteriaOptionProjection> tests =
        entityManager
            .createQuery(
                "SELECT id, title FROM JpaTestConfigurationEntity WHERE suiteConfiguration.fileConfiguration.environmentId = :envId ORDER BY LOWER(title)",
                Object[].class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(result -> new CriteriaOptionProjection(result[0].toString(), (String) result[1]))
            .toList();

    List<CriteriaOptionProjection> files =
        entityManager
            .createQuery(
                "SELECT fileName FROM JpaFileConfigurationEntity WHERE environmentId = :envId ORDER BY LOWER(fileName)",
                String.class)
            .setParameter("envId", environmentId.value())
            .getResultList()
            .stream()
            .map(fileName -> new CriteriaOptionProjection(fileName, fileName))
            .toList();

    var tags = mergeTags(environmentId);

    return new SearchCriteriaProjection(suites, tests, files, tags);
  }

  private List<CriteriaOptionProjection> mergeTags(EnvironmentId environmentId) {
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
        .map(tag -> new CriteriaOptionProjection(tag, tag))
        .toList();
  }
}
