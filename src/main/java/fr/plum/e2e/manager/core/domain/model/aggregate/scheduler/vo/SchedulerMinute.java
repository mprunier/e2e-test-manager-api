package fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo;

public record SchedulerMinute(int value) {
  public static SchedulerMinute defaultMinute() {
    return new SchedulerMinute(0);
  }
}
