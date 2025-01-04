package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record EnvironmentVariableId(String name) {
  public EnvironmentVariableId {
    Assert.notBlank("EnvironmentVariableId name", name);
  }
}
