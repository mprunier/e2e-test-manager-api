package fr.plum.e2e.manager.core.domain.model.aggregate.scheduler;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.vo.SchedulerMinute;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AuditInfo;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
@Getter
public class Scheduler extends AggregateRoot<EnvironmentId> {

  @Builder.Default private SchedulerIsEnabled isEnabled = SchedulerIsEnabled.defaultStatus();

  @Builder.Default private SchedulerDaysOfWeek daysOfWeek = SchedulerDaysOfWeek.defaultDaysOfWeek();

  @Builder.Default private SchedulerHour hour = SchedulerHour.defaultHour();

  @Builder.Default private SchedulerMinute minute = SchedulerMinute.defaultMinute();

  public static Scheduler initialize(
      EnvironmentId environmentId, ZonedDateTime now, ActionUsername username) {
    return builder().id(environmentId).auditInfo(AuditInfo.create(username, now)).build();
  }
}
