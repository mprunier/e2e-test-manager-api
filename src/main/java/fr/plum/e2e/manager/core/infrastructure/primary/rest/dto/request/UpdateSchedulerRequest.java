package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateSchedulerRequest(
    @NotNull Boolean isEnabled,
    @NotNull Integer hour,
    @NotNull Integer minute,
    List<DayOfWeek> daysOfWeek) {

  public UpdateSchedulerCommand toCommand(UUID environmentId) {
    return UpdateSchedulerCommand.builder()
        .environmentId(new EnvironmentId(environmentId))
        .isEnabled(new SchedulerIsEnabled(isEnabled))
        .schedulerHour(new SchedulerHour(hour))
        .schedulerMinute(new SchedulerMinute(minute))
        .daysOfWeek(daysOfWeek != null ? new SchedulerDaysOfWeek(daysOfWeek) : null)
        .build();
  }
}
