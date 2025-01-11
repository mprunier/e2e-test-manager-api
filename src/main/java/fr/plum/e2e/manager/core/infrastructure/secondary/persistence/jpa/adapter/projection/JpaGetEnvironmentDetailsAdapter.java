package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsProjection;
import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsVariableProjection;
import fr.plum.e2e.manager.core.domain.port.projection.GetEnvironmentDetailsPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment.JpaEnvironmentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaGetEnvironmentDetailsAdapter implements GetEnvironmentDetailsPort {

  private final EntityManager entityManager;

  @Override
  public EnvironmentDetailsProjection find(EnvironmentId environmentId) {
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
                      SELECT NEW fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsVariableProjection(
                          v.name,
                          v.defaultValue,
                          v.description,
                          v.isHidden)
                      FROM JpaEnvironmentVariableEntity v
                      WHERE v.environment.id = :environmentId
                      ORDER BY v.name ASC
                      """,
              EnvironmentDetailsVariableProjection.class);
      variablesQuery.setParameter("environmentId", environmentId.value());

      var variables = variablesQuery.getResultList();

      return new EnvironmentDetailsProjection(
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
      return null;
    }
  }
}
