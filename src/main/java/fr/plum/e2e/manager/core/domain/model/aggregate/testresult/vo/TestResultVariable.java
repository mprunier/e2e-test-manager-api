package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultVariable(String name, String value) {
  public TestResultVariable {
    Assert.notBlank("TestResultVariable name", name);
    Assert.notNull("TestResultVariable value", value);
  }
}
