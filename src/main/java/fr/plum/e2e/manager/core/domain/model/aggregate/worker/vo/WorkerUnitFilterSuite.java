package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import lombok.Builder;

@Builder
public record WorkerUnitFilterSuite(
    SuiteConfigurationId suiteConfigurationId, SuiteTitle suiteTitle) {}
