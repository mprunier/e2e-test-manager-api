package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_JPA_ENVIRONMENTS_BY_PROJECT_BRANCH;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_JPA_ENVIRONMENT_BY_ID;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsVariableView;
import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsView;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.cache.QuarkusCacheManager;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.EnvironmentMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.environment.JpaEnvironmentEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaEnvironmentRepository;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaEnvironmentRepositoryAdapter implements EnvironmentRepositoryPort {

  private final JpaEnvironmentRepository repository;
  private final EntityManager entityManager;
  private final QuarkusCacheManager cacheManager;

  @CacheResult(cacheName = CACHE_JPA_ENVIRONMENT_BY_ID)
  @Override
  public Optional<Environment> find(@CacheKey EnvironmentId environmentId) {
    var optionalJpaEnvironment = repository.findByIdOptional(environmentId.value());
    return optionalJpaEnvironment.map(EnvironmentMapper::toDomain);
  }

  @CacheResult(cacheName = CACHE_JPA_ENVIRONMENTS_BY_PROJECT_BRANCH)
  @Override
  public List<Environment> findAll(@CacheKey String projectId, @CacheKey String branch) {
    return repository.findByProjectIdAndBranch(projectId, branch).stream()
        .map(EnvironmentMapper::toDomain)
        .toList();
  }

  @Override
  public void save(Environment environment) {
    var entity = EnvironmentMapper.toEntity(environment);
    entity.persist();
  }

  @Override
  public void update(Environment environment) {
    var entity = EnvironmentMapper.toEntity(environment);
    repository.getEntityManager().merge(entity);
    invalidateCaches(environment);
  }

  @Override
  public boolean exist(EnvironmentDescription description) {
    return repository.count("description", description.value()) > 0;
  }

  @Override
  public EnvironmentDetailsView findDetails(EnvironmentId environmentId) {
    var query =
        entityManager.createQuery(
            """
            SELECT e, COALESCE(s.isInProgress, false) as syncInProgress
            FROM JpaEnvironmentEntity e
            LEFT JOIN JpaSynchronizationEntity s ON s.environmentId = e.id
            WHERE e.id = :environmentId
            """,
            Tuple.class);
    query.setParameter("environmentId", environmentId.value());

    try {
      var result = query.getSingleResult();
      var environment = (JpaEnvironmentEntity) result.get(0);
      var syncInProgress = (Boolean) result.get(1);

      var variablesQuery =
          entityManager.createQuery(
              """
              SELECT NEW fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsVariableView(
                  v.name,
                  v.defaultValue,
                  v.description,
                  v.isHidden)
              FROM JpaEnvironmentVariableEntity v
              WHERE v.environment.id = :environmentId
              ORDER BY v.name ASC
              """,
              EnvironmentDetailsVariableView.class);
      variablesQuery.setParameter("environmentId", environmentId.value());

      var variables = variablesQuery.getResultList();

      return new EnvironmentDetailsView(
          environment.getId(),
          environment.getDescription(),
          environment.getProjectId(),
          environment.getBranch(),
          environment.getToken(),
          environment.isEnabled(),
          environment.getMaxParallelTestNumber(),
          syncInProgress,
          variables,
          environment.getCreatedBy(),
          environment.getUpdatedBy(),
          environment.getCreatedAt(),
          environment.getUpdatedAt());
    } catch (NoResultException e) {
      throw new EnvironmentNotFoundException(environmentId);
    }
  }

  private void invalidateCaches(Environment environment) {
    cacheManager.invalidateCacheByKey(
        CACHE_JPA_ENVIRONMENT_BY_ID, environment.getId().value().toString());
    cacheManager.invalidateCacheByKey(
        CACHE_JPA_ENVIRONMENTS_BY_PROJECT_BRANCH,
        environment.getSourceCodeInformation().projectId()
            + ":"
            + environment.getSourceCodeInformation().branch());
  }
}
