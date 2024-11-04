package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import java.time.DayOfWeek;
import java.util.List;
import lombok.Builder;

@Builder
public record SchedulerResponse(
    Boolean isEnabled, Integer hour, Integer minute, List<DayOfWeek> daysOfWeek) {

  public static SchedulerResponse fromDomain(Scheduler scheduler) {
    return builder()
        .isEnabled(scheduler.getIsEnabled().value())
        .hour(scheduler.getHour().value())
        .minute(scheduler.getMinute().value())
        .daysOfWeek(scheduler.getDaysOfWeek().daysOfWeek())
        .build();
  }
}
