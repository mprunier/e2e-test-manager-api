package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record VariableValue(String value) {
  public VariableValue {
    Assert.notBlank("VariableValue", value);
  }
}
