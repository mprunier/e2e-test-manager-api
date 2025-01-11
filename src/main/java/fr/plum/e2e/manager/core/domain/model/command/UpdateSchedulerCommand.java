package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import lombok.Builder;

@Builder
public record UpdateSchedulerCommand(
    EnvironmentId environmentId,
    SchedulerIsEnabled isEnabled,
    SchedulerDaysOfWeek daysOfWeek,
    SchedulerHour schedulerHour,
    SchedulerMinute schedulerMinute,
    ActionUsername actionUsername) {}
