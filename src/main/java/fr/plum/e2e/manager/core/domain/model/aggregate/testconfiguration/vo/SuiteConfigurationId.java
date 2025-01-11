package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record SuiteConfigurationId(UUID value) {
  public SuiteConfigurationId {
    Assert.notNull("SuiteConfigurationId value", value);
  }

  public static SuiteConfigurationId generate() {
    return new SuiteConfigurationId(UUID.randomUUID());
  }
}
