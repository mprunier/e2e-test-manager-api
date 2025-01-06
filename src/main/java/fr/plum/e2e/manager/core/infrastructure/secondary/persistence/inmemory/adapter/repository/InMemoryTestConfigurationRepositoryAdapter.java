package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.port.repository.TestConfigurationRepositoryPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTestConfigurationRepositoryAdapter implements TestConfigurationRepositoryPort {

  private final Map<TestConfigKey, TestConfigurationId> configurations = new HashMap<>();
  private final Map<SuiteKey, List<TestConfigurationId>> suiteConfigurations = new HashMap<>();
  private final Map<EnvironmentFileKey, List<TestConfigurationId>> environmentFileConfigurations =
      new HashMap<>();

  record TestConfigKey(FileName fileName, SuiteTitle suiteTitle, TestTitle testTitle) {}

  record SuiteKey(EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {}

  record EnvironmentFileKey(EnvironmentId environmentId, List<FileName> fileNames) {}

  public void save(
      EnvironmentId environmentId,
      FileName fileName,
      SuiteTitle suiteTitle,
      TestTitle testTitle,
      TestConfigurationId configId) {
    var key = new TestConfigKey(fileName, suiteTitle, testTitle);
    configurations.put(key, configId);

    var fileKey = new EnvironmentFileKey(environmentId, List.of(fileName));
    environmentFileConfigurations.computeIfAbsent(fileKey, k -> new ArrayList<>()).add(configId);
  }

  public void saveSuiteConfiguration(
      EnvironmentId environmentId,
      SuiteConfigurationId suiteConfigId,
      TestConfigurationId configId) {
    var key = new SuiteKey(environmentId, suiteConfigId);
    suiteConfigurations.computeIfAbsent(key, k -> new ArrayList<>()).add(configId);
  }

  @Override
  public Optional<TestConfigurationId> findId(
      FileName fileName, SuiteTitle suiteTitle, TestTitle testTitle) {
    return Optional.ofNullable(
        configurations.get(new TestConfigKey(fileName, suiteTitle, testTitle)));
  }

  @Override
  public List<TestConfigurationId> findAllIds(
      EnvironmentId environmentId, List<FileName> fileNames) {
    return environmentFileConfigurations.getOrDefault(
        new EnvironmentFileKey(environmentId, fileNames), Collections.emptyList());
  }

  @Override
  public List<TestConfigurationId> findAllIds(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    return suiteConfigurations.getOrDefault(
        new SuiteKey(environmentId, suiteConfigurationId), Collections.emptyList());
  }
}
