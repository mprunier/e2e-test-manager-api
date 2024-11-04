package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import java.util.UUID;

public record SuiteConfigurationId(UUID value) {
  public static SuiteConfigurationId generate() {
    return new SuiteConfigurationId(UUID.randomUUID());
  }
}
