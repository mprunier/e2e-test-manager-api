package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationIsInProgress;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationAlreadyInProgressException;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Synchronization extends AggregateRoot<EnvironmentId> {

  private SynchronizationIsInProgress synchronizationIsInProgress;
  private List<SynchronizationError> errors;

  @Builder
  public Synchronization(
      EnvironmentId environmentId,
      AuditInfo auditInfo,
      SynchronizationIsInProgress synchronizationIsInProgress,
      List<SynchronizationError> errors) {
    super(environmentId, auditInfo);
    Assert.notNull("synchronizationIsInProgress", synchronizationIsInProgress);
    Assert.notNull("errors", errors);
    this.synchronizationIsInProgress = synchronizationIsInProgress;
    this.errors = errors;
  }

  public static Synchronization create(EnvironmentId environmentId, AuditInfo auditInfo) {
    return builder()
        .environmentId(environmentId)
        .auditInfo(auditInfo)
        .synchronizationIsInProgress(SynchronizationIsInProgress.defaultStatus())
        .errors(new ArrayList<>())
        .build();
  }

  public void start() {
    synchronizationIsInProgress = synchronizationIsInProgress.start();
  }

  public void finish(
      List<SynchronizationError> synchronizationErrors,
      ActionUsername username,
      ZonedDateTime now) {
    this.errors =
        synchronizationErrors.stream()
            .map(
                newError ->
                    errors.stream()
                        .filter(
                            existingError ->
                                existingError.file().equals(newError.file())
                                    && existingError.error().equals(newError.error()))
                        .findFirst()
                        .orElse(newError))
            .toList();

    synchronizationIsInProgress = synchronizationIsInProgress.finish();
    getAuditInfo().update(username, now);
  }

  public void finishWithoutUpdateErrors() {
    synchronizationIsInProgress = synchronizationIsInProgress.finish();
  }

  public boolean isInProgress() {
    return synchronizationIsInProgress.value();
  }

  public void assertIsNotInProgress() {
    if (isInProgress()) {
      throw new SynchronizationAlreadyInProgressException(id);
    }
  }
}
