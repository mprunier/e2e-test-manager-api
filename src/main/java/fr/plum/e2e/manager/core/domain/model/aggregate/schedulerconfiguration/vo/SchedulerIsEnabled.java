package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SchedulerIsEnabled(boolean value) {

  public SchedulerIsEnabled {
    Assert.notNull("SchedulerIsEnabled", value);
  }

  public static SchedulerIsEnabled defaultStatus() {
    return new SchedulerIsEnabled(false);
  }

  public SchedulerIsEnabled activate() {
    return new SchedulerIsEnabled(true);
  }

  public SchedulerIsEnabled deactivate() {
    return new SchedulerIsEnabled(false);
  }
}
