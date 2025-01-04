package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultUrlError(String value) {
  public TestResultUrlError {
    Assert.notBlank("TestResultUrlError", value);
  }
}
