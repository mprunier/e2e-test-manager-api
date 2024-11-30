package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import java.time.DayOfWeek;
import java.util.List;
import lombok.Builder;

@Builder
public record SchedulerResponse(
    Boolean isEnabled, Integer hour, Integer minute, List<DayOfWeek> daysOfWeek) {

  public static SchedulerResponse fromDomain(SchedulerConfiguration schedulerConfiguration) {
    return builder()
        .isEnabled(schedulerConfiguration.getIsEnabled().value())
        .hour(schedulerConfiguration.getHour().value())
        .minute(schedulerConfiguration.getMinute().value())
        .daysOfWeek(schedulerConfiguration.getDaysOfWeek().list())
        .build();
  }
}
