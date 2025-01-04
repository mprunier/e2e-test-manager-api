package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record FileName(String value) {
  public FileName {
    Assert.notBlank("FileName value", value);
  }
}
