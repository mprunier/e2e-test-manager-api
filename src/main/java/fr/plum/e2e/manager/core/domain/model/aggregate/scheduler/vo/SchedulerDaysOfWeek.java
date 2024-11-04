package fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo;

import java.time.DayOfWeek;
import java.util.List;

public record SchedulerDaysOfWeek(List<DayOfWeek> daysOfWeek) {
  public static SchedulerDaysOfWeek defaultDaysOfWeek() {
    return new SchedulerDaysOfWeek(List.of());
  }
}
