package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.domain.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;

public enum GitlabJobStatus {
  created,
  pending,
  running,
  success,
  failed,
  canceling,
  canceled,
  skipped;

  public WorkerUnitStatus toWorkerStatus() {
    return switch (this) {
      case created, pending, running -> WorkerUnitStatus.IN_PROGRESS;
      case success -> WorkerUnitStatus.SUCCESS;
      case failed -> WorkerUnitStatus.FAILED;
      case canceled, skipped, canceling -> WorkerUnitStatus.CANCELED;
    };
  }
}
