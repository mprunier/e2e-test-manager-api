package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record GroupName(String value) {
  public GroupName {
    Assert.notBlank("GroupName value", value);
  }
}
