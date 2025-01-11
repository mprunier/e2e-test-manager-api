package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import lombok.Builder;

@Builder
public record GetAllTestResultQuery(TestConfigurationId testConfigurationId) {}
