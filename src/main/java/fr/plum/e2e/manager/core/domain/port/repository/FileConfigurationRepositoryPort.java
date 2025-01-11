package fr.plum.e2e.manager.core.domain.port.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FileConfigurationRepositoryPort {

  List<FileConfiguration> findAll(EnvironmentId environmentId);

  Map<GroupName, List<FileName>> findAllFileNamesMapByGroupName(EnvironmentId environmentId);

  List<FileName> findAllFileNames(EnvironmentId environmentId, GroupName groupName);

  List<FileName> findAllFileNames(EnvironmentId environmentId);

  void save(List<FileConfiguration> fileConfigurations);

  void update(List<FileConfiguration> fileConfigurations);

  void delete(List<FileConfiguration> fileConfigurations);

  Optional<FileConfiguration> find(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId);

  Optional<FileConfiguration> find(
      EnvironmentId environmentId, TestConfigurationId testConfigurationId);
}
