package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.schdulerconfiguration.JpaSchedulerConfigurationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchedulerConfigurationMapper {

  public static JpaSchedulerConfigurationEntity toEntity(SchedulerConfiguration domain) {
    var entity =
        JpaSchedulerConfigurationEntity.builder()
            .environmentId(domain.getId().value())
            .enabled(domain.getIsEnabled().value())
            .hour(domain.getHour().value())
            .minute(domain.getMinute().value())
            .daysOfWeek(domain.getDaysOfWeek().list())
            .build();

    entity.setAuditFields(domain.getAuditInfo());
    return entity;
  }

  public static SchedulerConfiguration toDomain(JpaSchedulerConfigurationEntity entity) {
    return SchedulerConfiguration.builder()
        .id(new EnvironmentId(entity.getEnvironmentId()))
        .isEnabled(new SchedulerIsEnabled(entity.isEnabled()))
        .hour(new SchedulerHour(entity.getHour()))
        .minute(new SchedulerMinute(entity.getMinute()))
        .daysOfWeek(new SchedulerDaysOfWeek(entity.getDaysOfWeek()))
        .auditInfo(AuditInfoMapper.toDomain(entity))
        .build();
  }
}
