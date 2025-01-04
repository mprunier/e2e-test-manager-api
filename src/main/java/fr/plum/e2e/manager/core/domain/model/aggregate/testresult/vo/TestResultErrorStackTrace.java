package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultErrorStackTrace(String value) {
  public TestResultErrorStackTrace {
    Assert.notBlank("TestResultErrorStackTrace", value);
  }
}
