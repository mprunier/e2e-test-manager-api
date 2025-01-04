package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultCode(String value) {
  public TestResultCode {
    Assert.notBlank("TestResultCode", value);
  }
}
