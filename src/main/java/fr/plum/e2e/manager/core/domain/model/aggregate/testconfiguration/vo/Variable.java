package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record Variable(String value) {
  public Variable {
    Assert.notBlank("Variable value", value);
  }
}
