package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SchedulerHour(int value) {
  public SchedulerHour {
    Assert.notNull("SchedulerHour", value);
  }

  public static SchedulerHour defaultHour() {
    return new SchedulerHour(0);
  }
}
