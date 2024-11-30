package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.schdulerconfiguration;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter.DayOfWeekConverter;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.AbstractAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduler_configuration")
@EqualsAndHashCode(callSuper = true)
public class JpaSchedulerConfigurationEntity extends AbstractAuditableEntity {

  @Id
  @Column(name = "environment_id", nullable = false)
  private UUID environmentId;

  @Column(name = "is_enabled", nullable = false)
  private boolean enabled;

  @Column(name = "hour", nullable = false)
  private Integer hour;

  @Column(name = "minute", nullable = false)
  private Integer minute;

  @Convert(converter = DayOfWeekConverter.class)
  @Column(name = "days_of_week", nullable = false)
  private List<DayOfWeek> daysOfWeek;
}
