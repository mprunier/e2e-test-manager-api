package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.projection;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentProjection;
import fr.plum.e2e.manager.core.domain.port.view.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment.JpaEnvironmentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class JpaListAllEnvironmentsAdapter implements ListAllEnvironmentsPort {

  @Override
  public List<EnvironmentProjection> listAll() {
    return JpaEnvironmentEntity.<JpaEnvironmentEntity>findAll().stream()
        .map(entity -> new EnvironmentProjection(entity.getId(), entity.getDescription()))
        .toList();
  }
}
