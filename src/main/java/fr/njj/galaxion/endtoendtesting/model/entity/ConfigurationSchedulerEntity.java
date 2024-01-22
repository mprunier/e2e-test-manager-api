package fr.njj.galaxion.endtoendtesting.model.entity;

import fr.njj.galaxion.endtoendtesting.model.converter.DayOfWeekConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "configuration_scheduler")
@EqualsAndHashCode(callSuper = true)
public class ConfigurationSchedulerEntity extends PanacheEntityBase {

  @Id
  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__configuration_suite__environment_id"),
      nullable = false)
  private EnvironmentEntity environment;

  @Setter
  @Builder.Default
  @Column(name = "is_enabled", nullable = false)
  private boolean enabled = true;

  @Builder.Default
  @Setter
  @Column(name = "scheduled_time", nullable = false)
  private ZonedDateTime scheduledTime =
      ZonedDateTime.of(2023, 05, 23, 23, 0, 0, 0, ZoneId.systemDefault());

  @Setter
  @Convert(converter = DayOfWeekConverter.class)
  @Column(name = "days_of_week", nullable = false)
  private List<DayOfWeek> daysOfWeek;
}
