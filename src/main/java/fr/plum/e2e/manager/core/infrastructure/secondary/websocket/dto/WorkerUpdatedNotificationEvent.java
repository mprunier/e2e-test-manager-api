package fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto;

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
@JsonTypeName("WORKER_UPDATED_EVENT")
public class WorkerUpdatedNotificationEvent extends AbstractNotificationEvent {

  private WorkerNotificationStatus status;

  private WorkerType workerType;

  private ConfigurationSuiteWithWorkerView configurationSuiteWithWorkerView;

  @Builder
  public WorkerUpdatedNotificationEvent(
      EnvironmentId environmentId,
      WorkerType workerType,
      WorkerNotificationStatus status,
      ConfigurationSuiteWithWorkerView configurationSuiteWithWorkerView) {
    super(environmentId.value());
    this.workerType = workerType;
    this.status = status;
    this.configurationSuiteWithWorkerView = configurationSuiteWithWorkerView;

    assertSuiteWithWorkerValidity();
  }

  private void assertSuiteWithWorkerValidity() {
    if ((workerType == WorkerType.SUITE || workerType == WorkerType.TEST)
        && configurationSuiteWithWorkerView == null) {
      throw new DomainAssertException(
          "suiteWithWorker must be provided when workerType is SUITE or TEST");
    }
    if (workerType != WorkerType.SUITE
        && workerType != WorkerType.TEST
        && configurationSuiteWithWorkerView != null) {
      throw new DomainAssertException(
          "suiteWithWorker must not be provided when workerType is not SUITE or TEST");
    }
  }
}
