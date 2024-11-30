package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.SchedulerConfigurationMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaSchedulerConfigurationRepository;
import io.quarkus.cache.CacheKey;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaSchedulerConfigurationRepositoryAdapter
    implements SchedulerConfigurationRepositoryPort {

  private final JpaSchedulerConfigurationRepository repository;

  @Override
  public List<SchedulerConfiguration> findAll() {
    return repository.findAll().stream().map(SchedulerConfigurationMapper::toDomain).toList();
  }

  @Override
  public Optional<SchedulerConfiguration> find(@CacheKey EnvironmentId environmentId) {
    return repository
        .findByIdOptional(environmentId.value())
        .map(SchedulerConfigurationMapper::toDomain);
  }

  @Override
  public void save(SchedulerConfiguration schedulerConfiguration) {
    var entity = SchedulerConfigurationMapper.toEntity(schedulerConfiguration);
    entity.persist();
  }

  @Override
  public void update(SchedulerConfiguration schedulerConfiguration) {
    var entity = SchedulerConfigurationMapper.toEntity(schedulerConfiguration);
    repository.getEntityManager().merge(entity);
  }
}
