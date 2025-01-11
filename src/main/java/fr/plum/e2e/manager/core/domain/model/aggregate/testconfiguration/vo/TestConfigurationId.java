package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record TestConfigurationId(UUID value) {
  public TestConfigurationId {
    Assert.notNull("TestConfigurationId value", value);
  }

  public static TestConfigurationId generate() {
    return new TestConfigurationId(UUID.randomUUID());
  }
}
