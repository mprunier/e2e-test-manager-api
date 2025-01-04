package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SchedulerMinute(int value) {
  public SchedulerMinute {
    Assert.notNull("SchedulerMinute", value);
  }

  public static SchedulerMinute defaultMinute() {
    return new SchedulerMinute(0);
  }
}
