package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import lombok.Builder;

@Builder
public record WorkerUnitFilterTest(TestConfigurationId testConfigurationId, TestTitle testTitle) {}
