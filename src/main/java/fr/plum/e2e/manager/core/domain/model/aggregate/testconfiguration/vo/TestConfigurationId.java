package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import java.util.UUID;

public record TestConfigurationId(UUID value) {
  public static TestConfigurationId generate() {
    return new TestConfigurationId(UUID.randomUUID());
  }
}
