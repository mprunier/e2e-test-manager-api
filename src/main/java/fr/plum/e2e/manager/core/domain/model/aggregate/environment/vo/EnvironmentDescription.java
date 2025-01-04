package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record EnvironmentDescription(String value) {
  public EnvironmentDescription {
    Assert.notBlank("EnvironmentDescription value", value);
  }
}
