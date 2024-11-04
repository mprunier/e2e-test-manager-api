package fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo;

public record SchedulerHour(int value) {
  public static SchedulerHour defaultHour() {
    return new SchedulerHour(0);
  }
}
