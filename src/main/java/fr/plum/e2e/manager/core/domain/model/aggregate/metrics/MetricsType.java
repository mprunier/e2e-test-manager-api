package fr.plum.e2e.manager.core.domain.model.aggregate.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public enum MetricsType {
  ALL,
  GROUP,
  FILE,
  SUITE,
  TEST;

  public static MetricsType fromWorkerType(WorkerType workerType) {
    Assert.notNull("workerType", workerType);
    return switch (workerType) {
      case GROUP -> GROUP;
      case FILE -> FILE;
      case SUITE -> SUITE;
      case TEST -> TEST;
      default -> ALL;
    };
  }
}
