package fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SchedulerConfigurationTest {

  private EnvironmentId environmentId;
  private AuditInfo auditInfo;
  private SchedulerIsEnabled isEnabled;
  private SchedulerDaysOfWeek daysOfWeek;
  private SchedulerHour hour;
  private SchedulerMinute minute;

  @BeforeEach
  void setUp() {
    // GIVEN
    environmentId = new EnvironmentId(UUID.randomUUID());
    auditInfo = AuditInfo.create(new ActionUsername("testUser"), ZonedDateTime.now());
    isEnabled = new SchedulerIsEnabled(true);
    List<DayOfWeek> days = new ArrayList<>();
    days.add(DayOfWeek.MONDAY);
    days.add(DayOfWeek.WEDNESDAY);
    daysOfWeek = new SchedulerDaysOfWeek(days);
    hour = new SchedulerHour(9);
    minute = new SchedulerMinute(30);
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_scheduler_configuration_with_default_values() {
      // WHEN
      SchedulerConfiguration config = SchedulerConfiguration.create(environmentId, auditInfo);

      // THEN
      assertThat(config.getId()).isEqualTo(environmentId);
      assertThat(config.getIsEnabled().value()).isFalse();
      assertThat(config.getDaysOfWeek().list()).isEmpty();
      assertThat(config.getHour().value()).isZero();
      assertThat(config.getMinute().value()).isZero();
      assertThat(config.getAuditInfo()).isEqualTo(auditInfo);
    }

    @Test
    void should_create_scheduler_configuration_with_custom_values() {
      // WHEN
      SchedulerConfiguration config =
          SchedulerConfiguration.builder()
              .environmentId(environmentId)
              .auditInfo(auditInfo)
              .isEnabled(isEnabled)
              .daysOfWeek(daysOfWeek)
              .hour(hour)
              .minute(minute)
              .build();

      // THEN
      assertThat(config.getId()).isEqualTo(environmentId);
      assertThat(config.getIsEnabled()).isEqualTo(isEnabled);
      assertThat(config.getDaysOfWeek()).isEqualTo(daysOfWeek);
      assertThat(config.getHour()).isEqualTo(hour);
      assertThat(config.getMinute()).isEqualTo(minute);
      assertThat(config.getAuditInfo()).isEqualTo(auditInfo);
    }

    @Test
    void should_throw_exception_when_isEnabled_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SchedulerConfiguration.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .isEnabled(null)
                      .daysOfWeek(daysOfWeek)
                      .hour(hour)
                      .minute(minute)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field isEnabled is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_daysOfWeek_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SchedulerConfiguration.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .isEnabled(isEnabled)
                      .daysOfWeek(null)
                      .hour(hour)
                      .minute(minute)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field daysOfWeek is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_hour_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SchedulerConfiguration.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .isEnabled(isEnabled)
                      .daysOfWeek(daysOfWeek)
                      .hour(null)
                      .minute(minute)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field hour is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_minute_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SchedulerConfiguration.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .isEnabled(isEnabled)
                      .daysOfWeek(daysOfWeek)
                      .hour(hour)
                      .minute(null)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field minute is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    private SchedulerConfiguration config;

    @BeforeEach
    void setUp() {
      config = SchedulerConfiguration.create(environmentId, auditInfo);
    }

    @Test
    void should_update_scheduler_configuration() {
      // WHEN
      config.update(isEnabled, daysOfWeek, hour, minute);

      // THEN
      assertThat(config.getIsEnabled()).isEqualTo(isEnabled);
      assertThat(config.getDaysOfWeek()).isEqualTo(daysOfWeek);
      assertThat(config.getHour()).isEqualTo(hour);
      assertThat(config.getMinute()).isEqualTo(minute);
    }

    @Test
    void should_throw_exception_when_updating_with_null_isEnabled() {
      // WHEN/THEN
      assertThatThrownBy(() -> config.update(null, daysOfWeek, hour, minute))
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field isEnabled is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_updating_with_null_daysOfWeek() {
      // WHEN/THEN
      assertThatThrownBy(() -> config.update(isEnabled, null, hour, minute))
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field daysOfWeek is mandatory and cannot be empty or null.");
    }
  }
}
