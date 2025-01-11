package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import lombok.Builder;

@Builder
public record SearchSuiteConfigurationQuery(
    EnvironmentId environmentId,
    SuiteConfigurationId suiteConfigurationId,
    TestConfigurationId testConfigurationId,
    Tag tag,
    FileName fileName,
    ConfigurationStatus status,
    boolean allNotSuccess,
    int page,
    int size,
    String sortField,
    String sortOrder) {}
