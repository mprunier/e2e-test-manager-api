package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record TestResultDuration(Integer value) {
  public TestResultDuration {
    Assert.notNull("TestResultDuration", value);
  }
}
