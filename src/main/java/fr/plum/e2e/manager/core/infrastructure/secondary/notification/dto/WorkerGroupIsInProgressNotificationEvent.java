package fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.exception.DomainAssertException;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonTypeName("WORKER_GROUP_IS_IN_PROGRESS_EVENT")
public class WorkerGroupIsInProgressNotificationEvent extends AbstractNotificationEvent {

  private WorkerType workerType;

  private ConfigurationSuiteWithWorkerView suiteWithWorker;

  @Builder
  public WorkerGroupIsInProgressNotificationEvent(
      EnvironmentId environmentId,
      WorkerType workerType,
      ConfigurationSuiteWithWorkerView suiteWithWorker) {
    super(environmentId);
    this.workerType = workerType;
    this.suiteWithWorker = suiteWithWorker;

    assertSuiteWithWorkerValidity();
  }

  private void assertSuiteWithWorkerValidity() {
    if ((workerType == WorkerType.SUITE || workerType == WorkerType.TEST)
        && suiteWithWorker == null) {
      throw new DomainAssertException(
          "suiteWithWorker must be provided when workerType is SUITE or TEST");
    }
    if (workerType != WorkerType.SUITE
        && workerType != WorkerType.TEST
        && suiteWithWorker != null) {
      throw new DomainAssertException(
          "suiteWithWorker must not be provided when workerType is not SUITE or TEST");
    }
  }
}
