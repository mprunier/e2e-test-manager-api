package fr.plum.e2e.manager.core.domain.port.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import java.util.List;
import java.util.Optional;

public interface TestConfigurationRepositoryPort {

  Optional<TestConfigurationId> findId(
      EnvironmentId environmentId, FileName fileName, SuiteTitle suiteTitle, TestTitle testTitle);

  List<TestConfigurationId> findAllIds(EnvironmentId environmentId, List<FileName> fileNames);

  List<TestConfigurationId> findAllIds(
      EnvironmentId environmentId, SuiteConfigurationId suiteConfigurationId);
}
