package fr.plum.e2e.manager.core.domain.model.aggregate.worker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkerUnitStatus {
  IN_PROGRESS(null),
  SUCCESS(null),
  FAILED(null),
  CANCELED("This worker was cancelled."),
  SYSTEM_ERROR(
      "This worker has failed due to an publisher error in the tool. Contact an administrator."),
  NO_REPORT_ERROR(
      "The worker has failed because there was no testFilter report. Check the worker with logs/artefacts on your gitlab project.");

  private final String errorMessage;
}
