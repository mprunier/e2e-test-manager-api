package fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo;

public record SchedulerIsEnabled(boolean value) {
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
