package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationErrorValue;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationIsInProgress;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationAlreadyInProgressException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SynchronizationTest {

  private EnvironmentId environmentId;
  private AuditInfo auditInfo;
  private SynchronizationIsInProgress isInProgress;
  private List<SynchronizationError> errors;
  private ActionUsername username;
  private ZonedDateTime now;

  @BeforeEach
  void setUp() {
    // GIVEN
    environmentId = new EnvironmentId(UUID.randomUUID());
    username = new ActionUsername("testUser");
    now = ZonedDateTime.now();
    auditInfo = AuditInfo.create(username, now);
    isInProgress = new SynchronizationIsInProgress(false);
    errors = new ArrayList<>();
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_synchronization_with_default_values() {
      // WHEN
      Synchronization sync = Synchronization.create(environmentId, auditInfo);

      // THEN
      assertThat(sync.getId()).isEqualTo(environmentId);
      assertThat(sync.getAuditInfo()).isEqualTo(auditInfo);
      assertThat(sync.getSynchronizationIsInProgress().value()).isFalse();
      assertThat(sync.getErrors()).isEmpty();
    }

    @Test
    void should_create_synchronization_with_custom_values() {
      // WHEN
      Synchronization sync =
          Synchronization.builder()
              .environmentId(environmentId)
              .auditInfo(auditInfo)
              .synchronizationIsInProgress(isInProgress)
              .errors(errors)
              .build();

      // THEN
      assertThat(sync.getId()).isEqualTo(environmentId);
      assertThat(sync.getAuditInfo()).isEqualTo(auditInfo);
      assertThat(sync.getSynchronizationIsInProgress()).isEqualTo(isInProgress);
      assertThat(sync.getErrors()).isEqualTo(errors);
    }

    @Test
    void should_throw_exception_when_synchronizationIsInProgress_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  Synchronization.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .synchronizationIsInProgress(null)
                      .errors(errors)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description",
              "The field synchronizationIsInProgress is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_errors_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  Synchronization.builder()
                      .environmentId(environmentId)
                      .auditInfo(auditInfo)
                      .synchronizationIsInProgress(isInProgress)
                      .errors(null)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field errors is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class SynchronizationOperationsTests {

    private Synchronization sync;
    private List<SynchronizationError> synchronizationErrors;

    @BeforeEach
    void setUp() {
      sync = Synchronization.create(environmentId, auditInfo);
      synchronizationErrors = new ArrayList<>();
      synchronizationErrors.add(
          new SynchronizationError(
              new SynchronizationFileName("test.file"),
              new SynchronizationErrorValue("error message"),
              now));
    }

    @Test
    void should_start_synchronization() {
      // WHEN
      sync.start();

      // THEN
      assertThat(sync.isInProgress()).isTrue();
    }

    @Test
    void should_finish_synchronization_with_errors() {
      // GIVEN
      sync.start();

      // WHEN
      sync.finish(synchronizationErrors, username, now);

      // THEN
      assertThat(sync.isInProgress()).isFalse();
      assertThat(sync.getErrors()).isEqualTo(synchronizationErrors);
      assertThat(sync.getAuditInfo().getUpdatedBy()).isEqualTo(username);
      assertThat(sync.getAuditInfo().getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void should_finish_synchronization_without_updating_errors() {
      // GIVEN
      sync.start();

      // WHEN
      sync.finishWithoutUpdateErrors();

      // THEN
      assertThat(sync.isInProgress()).isFalse();
      assertThat(sync.getErrors()).isEmpty();
    }

    @Test
    void should_throw_exception_when_starting_already_in_progress_synchronization() {
      // GIVEN
      sync.start();

      // WHEN/THEN
      assertThatThrownBy(() -> sync.assertIsNotInProgress())
          .isInstanceOf(SynchronizationAlreadyInProgressException.class);
    }

    @Test
    void should_keep_existing_error_date_when_same_file_and_error() {
      // GIVEN
      ZonedDateTime oldDate = now.minusDays(1);
      SynchronizationError existingError =
          new SynchronizationError(
              new SynchronizationFileName("test.file"),
              new SynchronizationErrorValue("error message"),
              oldDate);
      List<SynchronizationError> initialErrors = new ArrayList<>();
      initialErrors.add(existingError);

      Synchronization syncWithErrors =
          Synchronization.builder()
              .environmentId(environmentId)
              .auditInfo(auditInfo)
              .synchronizationIsInProgress(isInProgress)
              .errors(initialErrors)
              .build();

      // WHEN
      SynchronizationError newError =
          new SynchronizationError(
              new SynchronizationFileName("test.file"),
              new SynchronizationErrorValue("error message"),
              now);
      List<SynchronizationError> newErrors = new ArrayList<>();
      newErrors.add(newError);

      syncWithErrors.finish(newErrors, username, now);

      // THEN
      assertThat(syncWithErrors.getErrors()).hasSize(1).containsExactly(existingError);
    }

    @Test
    void should_keep_new_error_date_when_different_file_or_error() {
      // GIVEN
      ZonedDateTime oldDate = now.minusDays(1);
      SynchronizationError existingError =
          new SynchronizationError(
              new SynchronizationFileName("test.file"),
              new SynchronizationErrorValue("error message"),
              oldDate);
      List<SynchronizationError> initialErrors = new ArrayList<>();
      initialErrors.add(existingError);

      Synchronization syncWithErrors =
          Synchronization.builder()
              .environmentId(environmentId)
              .auditInfo(auditInfo)
              .synchronizationIsInProgress(isInProgress)
              .errors(initialErrors)
              .build();

      // WHEN
      SynchronizationError newError =
          new SynchronizationError(
              new SynchronizationFileName("test.file"),
              new SynchronizationErrorValue("different error message"),
              now);
      List<SynchronizationError> newErrors = new ArrayList<>();
      newErrors.add(newError);

      syncWithErrors.finish(newErrors, username, now);

      // THEN
      assertThat(syncWithErrors.getErrors())
          .hasSize(1)
          .containsExactly(newError)
          .extracting("at")
          .containsExactly(now);
    }
  }
}
