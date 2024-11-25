package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import java.util.UUID;

public record TestResultVideoId(UUID value) {
  public static TestResultVideoId generate() {
    return new TestResultVideoId(UUID.randomUUID());
  }
}
