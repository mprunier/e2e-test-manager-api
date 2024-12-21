package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.exception.FileNotFoundException;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileConfigurationService {

  private final FileConfigurationRepositoryPort fileConfigurationRepositoryPort;

  public FileConfiguration getFileConfigurationBySuiteId(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId) {
    return fileConfigurationRepositoryPort
        .find(environmentId, suiteConfigurationId)
        .orElseThrow(() -> new FileNotFoundException(suiteConfigurationId));
  }

  public FileConfiguration getFileConfigurationByTestId(
      EnvironmentId environmentId, TestConfigurationId testConfigurationId) {
    return fileConfigurationRepositoryPort
        .find(environmentId, testConfigurationId)
        .orElseThrow(() -> new FileNotFoundException(testConfigurationId));
  }
}
