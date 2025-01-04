package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SchedulerConfiguration extends AggregateRoot<EnvironmentId> {

  private SchedulerIsEnabled isEnabled;
  private SchedulerDaysOfWeek daysOfWeek;
  private SchedulerHour hour;
  private SchedulerMinute minute;

  @Builder
  public SchedulerConfiguration(
      EnvironmentId environmentId,
      AuditInfo auditInfo,
      SchedulerIsEnabled isEnabled,
      SchedulerDaysOfWeek daysOfWeek,
      SchedulerHour hour,
      SchedulerMinute minute) {
    super(environmentId, auditInfo);
    Assert.notNull("isEnabled", isEnabled);
    Assert.notNull("daysOfWeek", daysOfWeek);
    Assert.notNull("hour", hour);
    Assert.notNull("minute", minute);
    this.isEnabled = isEnabled;
    this.daysOfWeek = daysOfWeek;
    this.hour = hour;
    this.minute = minute;
  }

  public static SchedulerConfiguration create(EnvironmentId environmentId, AuditInfo auditInfo) {
    return builder()
        .environmentId(environmentId)
        .auditInfo(auditInfo)
        .isEnabled(SchedulerIsEnabled.defaultStatus())
        .daysOfWeek(SchedulerDaysOfWeek.defaultDaysOfWeek())
        .hour(SchedulerHour.defaultHour())
        .minute(SchedulerMinute.defaultMinute())
        .build();
  }

  public void update(
      SchedulerIsEnabled isEnabled,
      SchedulerDaysOfWeek daysOfWeek,
      SchedulerHour hour,
      SchedulerMinute minute) {
    Assert.notNull("isEnabled", isEnabled);
    Assert.notNull("daysOfWeek", daysOfWeek);
    Assert.notNull("hour", hour);
    Assert.notNull("minute", minute);
    this.isEnabled = isEnabled;
    this.daysOfWeek = daysOfWeek;
    this.hour = hour;
    this.minute = minute;
  }
}
