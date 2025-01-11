package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultReference(String value) {
  public TestResultReference {
    Assert.notBlank("TestResultReference", value);
  }
}
