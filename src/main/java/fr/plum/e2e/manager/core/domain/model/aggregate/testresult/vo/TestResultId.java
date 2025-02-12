package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record TestResultId(UUID value) {
  public TestResultId {
    Assert.notNull("TestResultId", value);
  }

  public static TestResultId generate() {
    return new TestResultId(UUID.randomUUID());
  }
}
