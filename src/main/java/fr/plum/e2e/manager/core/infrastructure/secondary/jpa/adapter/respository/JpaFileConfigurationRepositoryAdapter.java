package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.respository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.FileConfigurationMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaFileConfigurationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaFileConfigurationRepositoryAdapter implements FileConfigurationRepositoryPort {

  private final JpaFileConfigurationRepository repository;

  @Override
  public List<FileConfiguration> findAll(EnvironmentId environmentId) {
    return FileConfigurationMapper.toDomainList(repository.findAll(environmentId));
  }

  @Override
  public Map<GroupName, List<FileName>> findAllFileNamesMapByGroupName(
      EnvironmentId environmentId) {
    var fileConfigurationEntities = repository.findAll(environmentId);
    return FileConfigurationMapper.toDomainFileNamesMapByGroupName(fileConfigurationEntities);
  }

  @Override
  public List<FileName> findAllFileNames(EnvironmentId environmentId, GroupName groupName) {
    return repository.findAll(environmentId, groupName).stream()
        .map(FileConfigurationMapper::toDomainFileName)
        .toList();
  }

  @Override
  public List<FileName> findAllFileNames(EnvironmentId environmentId) {
    return repository.findAll(environmentId).stream()
        .map(FileConfigurationMapper::toDomainFileName)
        .toList();
  }

  @Override
  public void save(List<FileConfiguration> fileConfigurations) {
    fileConfigurations.stream()
        .map(FileConfigurationMapper::toEntity)
        .forEach(entity -> entity.persist());
  }

  @Override
  public void update(List<FileConfiguration> fileConfigurations) {
    fileConfigurations.stream()
        .map(FileConfigurationMapper::toEntity)
        .forEach(entity -> repository.getEntityManager().merge(entity));
  }

  @Override
  public void delete(List<FileConfiguration> fileConfigurations) {
    var entities = fileConfigurations.stream().map(FileConfigurationMapper::toEntity).toList();
    repository.deleteAll(entities);
  }

  @Override
  public Optional<FileConfiguration> find(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    return repository
        .find(environmentId, suiteConfigurationId)
        .map(FileConfigurationMapper::toDomain);
  }

  @Override
  public Optional<FileConfiguration> find(
      EnvironmentId environmentId, TestConfigurationId testConfigurationId) {
    return repository
        .find(environmentId, testConfigurationId)
        .map(FileConfigurationMapper::toDomain);
  }
}
