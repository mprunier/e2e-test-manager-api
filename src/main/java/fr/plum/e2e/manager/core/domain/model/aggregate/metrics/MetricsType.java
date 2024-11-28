package fr.plum.e2e.manager.core.domain.model.aggregate.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;

public enum MetricsType {
  ALL,
  GROUP,
  FILE,
  SUITE,
  TEST;

  public static MetricsType fromWorkerType(WorkerType workerType) {
    return switch (workerType) {
      case GROUP -> GROUP;
      case FILE -> FILE;
      case SUITE -> SUITE;
      case TEST -> TEST;
      default -> ALL;
    };
  }
}
