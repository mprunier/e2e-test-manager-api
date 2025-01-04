package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record MaxParallelWorkers(int value) {
  public MaxParallelWorkers {
    Assert.field("MaxParallelWorkers value", value).positive();
  }

  public static MaxParallelWorkers defaultValue() {
    return new MaxParallelWorkers(1);
  }
}
