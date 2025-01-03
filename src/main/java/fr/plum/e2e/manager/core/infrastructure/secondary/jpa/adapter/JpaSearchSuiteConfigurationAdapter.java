package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteProjection;
import fr.plum.e2e.manager.core.domain.model.projection.CriteriaOptionProjection;
import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import fr.plum.e2e.manager.core.domain.model.projection.SearchCriteriaProjection;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.port.view.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.SuiteMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
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

    StringBuilder queryStr =
        new StringBuilder(
            "SELECT DISTINCT s FROM JpaSuiteConfigurationEntity s "
                + "WHERE s.fileConfiguration.environmentId = :environmentId");

    Map<String, Object> params = new HashMap<>();
    params.put("environmentId", query.environmentId().value());

    if (query.suiteConfigurationId() != null) {
      queryStr.append(" AND s.id = :suiteId");
      params.put("suiteId", query.suiteConfigurationId().value());
    }

    if (query.testConfigurationId() != null) {
      queryStr.append(" AND EXISTS (SELECT 1 FROM s.testConfigurations t WHERE t.id = :testId)");
      params.put("testId", query.testConfigurationId().value());
    }

    //    if (query.tag() != null) { TODO
    //      var tagIds = findIdsWithTag(query.tag().value());
    //      if (tagIds != null && !tagIds.isEmpty()) {
    //        queryStr.append(" AND s.id IN :tagIds");
    //        params.put("tagIds", tagIds);
    //      }
    //    }

    if (query.fileName() != null) {
      queryStr.append(" AND s.fileConfiguration.value = :value");
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

    String orderByClause =
        switch (query.sortField()) {
          case "file" -> "s.fileConfiguration.fileName";
          case "lastPlayedAt" -> "s.lastPlayedAt";
          case "title" -> "s.title";
          case "status" -> "s.status";
          default -> "s.id";
        };

    queryStr
        .append(" ORDER BY ")
        .append(orderByClause)
        .append(" ")
        .append("asc".equalsIgnoreCase(query.sortOrder()) ? "ASC" : "DESC");

    TypedQuery<JpaSuiteConfigurationEntity> queryTyped =
        entityManager.createQuery(queryStr.toString(), JpaSuiteConfigurationEntity.class);

    params.forEach(queryTyped::setParameter);

    int pageSize = query.size();
    int pageNumber = query.page();

    queryTyped.setFirstResult(pageNumber * pageSize);
    queryTyped.setMaxResults(pageSize);

    List<JpaSuiteConfigurationEntity> results = queryTyped.getResultList();

    List<ConfigurationSuiteProjection> content =
        results.stream().map(SuiteMapper::toSuiteResponse).toList();

    long totalItems =
        entityManager
            .createQuery(
                """
                      SELECT COUNT(DISTINCT s.id)
                      FROM JpaSuiteConfigurationEntity s
                      WHERE s.fileConfiguration.environmentId = :environmentId
                      """,
                Long.class)
            .setParameter("environmentId", query.environmentId().value())
            .getSingleResult();

    return new PaginatedProjection<>(
        content,
        query.page(),
        (int) Math.ceil((double) totalItems / query.size()),
        query.size(),
        totalItems);
  }

  @SuppressWarnings("unchecked")
  public List<UUID> findIdsWithTag(String tag, String environmentId) {
    return entityManager
        .createNativeQuery(
            """
       SELECT DISTINCT sc.id
       FROM suite_configuration sc
       FULL OUTER JOIN test_configuration tc ON tc.suite_id = sc.id
       WHERE :tag = ANY(tc.tags)
       OR :tag = ANY(sc.tags)
       """,
            UUID.class)
        .setParameter("tag", tag)
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
