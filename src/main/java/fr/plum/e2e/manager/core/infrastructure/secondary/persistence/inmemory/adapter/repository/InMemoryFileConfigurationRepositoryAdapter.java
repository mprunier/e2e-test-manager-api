package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_GROUP_NAME;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryFileConfigurationRepositoryAdapter implements FileConfigurationRepositoryPort {
  private final Map<FileName, FileConfiguration> fileConfigurations = new HashMap<>();

  @Override
  public List<FileConfiguration> findAll(EnvironmentId environmentId) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .collect(Collectors.toList());
  }

  @Override
  public Map<GroupName, List<FileName>> findAllFileNamesMapByGroupName(
      EnvironmentId environmentId) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .collect(
            Collectors.groupingBy(
                entity ->
                    entity.getGroup() != null
                        ? new GroupName(entity.getGroup().value())
                        : new GroupName(NO_GROUP_NAME),
                Collectors.mapping(
                    fileConfiguration -> new FileName(fileConfiguration.getId().value()),
                    Collectors.toList())));
  }

  @Override
  public List<FileName> findAllFileNames(EnvironmentId environmentId, GroupName groupName) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .filter(config -> config.getGroup().equals(groupName))
        .map(FileConfiguration::getId)
        .collect(Collectors.toList());
  }

  @Override
  public List<FileName> findAllFileNames(EnvironmentId environmentId) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .map(FileConfiguration::getId)
        .collect(Collectors.toList());
  }

  @Override
  public void save(List<FileConfiguration> configs) {
    configs.forEach(config -> fileConfigurations.put(config.getId(), config));
  }

  @Override
  public void update(List<FileConfiguration> configs) {
    configs.forEach(config -> fileConfigurations.put(config.getId(), config));
  }

  @Override
  public void delete(List<FileConfiguration> configs) {
    configs.forEach(config -> fileConfigurations.remove(config.getId()));
  }

  @Override
  public Optional<FileConfiguration> find(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .filter(
            config ->
                config.getSuites().stream()
                    .anyMatch(suite -> suite.getId().equals(suiteConfigurationId)))
        .findFirst();
  }

  @Override
  public Optional<FileConfiguration> find(
      EnvironmentId environmentId, TestConfigurationId testConfigurationId) {
    return fileConfigurations.values().stream()
        .filter(config -> config.getEnvironmentId().equals(environmentId))
        .filter(
            config ->
                config.getSuites().stream()
                    .anyMatch(
                        suite ->
                            suite.getTests().stream()
                                .anyMatch(test -> test.getId().equals(testConfigurationId))))
        .findFirst();
  }
}
