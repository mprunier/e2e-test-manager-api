package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import java.util.UUID;

public record TestResultScreenshotId(UUID value) {
  public static TestResultScreenshotId generate() {
    return new TestResultScreenshotId(UUID.randomUUID());
  }
}
