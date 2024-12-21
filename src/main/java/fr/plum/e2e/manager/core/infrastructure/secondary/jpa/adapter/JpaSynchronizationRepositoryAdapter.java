package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.SynchronizationMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaSynchronizationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaSynchronizationRepositoryAdapter implements SynchronizationRepositoryPort {

  private final JpaSynchronizationRepository repository;

  @Override
  public Optional<Synchronization> find(EnvironmentId environmentId) {
    var optionalJpaEnvironment = repository.findByIdOptional(environmentId.value());
    return optionalJpaEnvironment.map(SynchronizationMapper::toDomain);
  }

  @Override
  public List<Synchronization> findAll() {
    return repository.findAll().stream().map(SynchronizationMapper::toDomain).toList();
  }

  @Override
  public void save(Synchronization environment) {
    var entity = SynchronizationMapper.toEntity(environment);
    entity.persist();
  }

  @Override
  public void update(Synchronization environment) {
    var entity = SynchronizationMapper.toEntity(environment);
    repository.getEntityManager().merge(entity);
  }

  @Override
  public void updateAll(List<Synchronization> synchronizations) {
    synchronizations.forEach(this::update);
  }
}
