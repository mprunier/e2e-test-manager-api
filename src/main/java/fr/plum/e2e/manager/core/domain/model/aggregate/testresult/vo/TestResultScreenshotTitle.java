package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultScreenshotTitle(String value) {
  public TestResultScreenshotTitle {
    Assert.notBlank("TestResultScreenshotTitle", value);
  }
}
