package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record TestResultVideoId(UUID value) {
  public TestResultVideoId {
    Assert.notNull("TestResultVideoId", value);
  }

  public static TestResultVideoId generate() {
    return new TestResultVideoId(UUID.randomUUID());
  }
}
