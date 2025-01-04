package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public record SchedulerDaysOfWeek(List<DayOfWeek> list) {
  public SchedulerDaysOfWeek {
    Assert.notNull("SchedulerDaysOfWeek", list);
  }

  public static SchedulerDaysOfWeek defaultDaysOfWeek() {
    return new SchedulerDaysOfWeek(new ArrayList<>());
  }
}
