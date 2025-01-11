package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record TestResultScreenshotId(UUID value) {
  public TestResultScreenshotId {
    Assert.notNull("TestResultScreenshotId", value);
  }

  public static TestResultScreenshotId generate() {
    return new TestResultScreenshotId(UUID.randomUUID());
  }
}
