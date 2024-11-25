package fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo;

import java.util.UUID;

public record TestResultId(UUID value) {
  public static TestResultId generate() {
    return new TestResultId(UUID.randomUUID());
  }
}
