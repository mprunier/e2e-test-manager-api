package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentView;
import fr.plum.e2e.manager.core.domain.port.out.query.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.environment.JpaEnvironmentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class JpaListAllEnvironmentsAdapter implements ListAllEnvironmentsPort {

  @Override
  public List<EnvironmentView> listAll() {
    return JpaEnvironmentEntity.<JpaEnvironmentEntity>findAll().stream()
        .map(entity -> new EnvironmentView(entity.getId(), entity.getDescription()))
        .toList();
  }
}
