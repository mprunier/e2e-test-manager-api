package fr.plum.e2e.manager.core.domain.model.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WorkerProjection(
    UUID id, String createdBy, ZonedDateTime createdAt, WorkerType type) {

  public static WorkerProjection from(Worker worker) {
    return builder()
        .id(worker.getId().value())
        .createdBy(worker.getAuditInfo().getCreatedBy().value())
        .createdAt(worker.getAuditInfo().getCreatedAt())
        .type(worker.getType())
        .build();
  }

  public static List<WorkerProjection> findForSuite(
      String fileName, UUID suiteId, List<Worker> workers) {
    return workers.stream()
        .filter(worker -> hasWorkerForSuite(worker, fileName, suiteId))
        .map(WorkerProjection::from)
        .toList();
  }

  public static List<WorkerProjection> findForTest(UUID testId, List<Worker> workers) {
    return workers.stream()
        .filter(worker -> hasWorkerForTest(worker, testId))
        .map(WorkerProjection::from)
        .toList();
  }

  private static boolean hasWorkerForSuite(Worker worker, String fileName, UUID suiteId) {
    return worker.getType() == WorkerType.ALL
        || worker.getWorkerUnits().stream()
            .anyMatch(workerUnit -> isWorkerMatchingSuite(workerUnit, fileName, suiteId));
  }

  private static boolean hasWorkerForTest(Worker worker, UUID testId) {
    return worker.getType() == WorkerType.ALL
        || worker.getWorkerUnits().stream()
            .anyMatch(workerUnit -> isWorkerMatchingTest(workerUnit, testId));
  }

  private static boolean isWorkerMatchingSuite(
      WorkerUnit workerUnit, String fileName, UUID suiteId) {
    if (workerUnit.getFilter() == null) {
      return true;
    }

    boolean matchesFile =
        workerUnit.getFilter().fileNames().stream().anyMatch(name -> name.value().equals(fileName));

    boolean matchesSuite =
        workerUnit.getFilter().suiteFilter() != null
            && workerUnit.getFilter().suiteFilter().suiteConfigurationId().value().equals(suiteId);

    return matchesFile && matchesSuite;
  }

  private static boolean isWorkerMatchingTest(WorkerUnit workerUnit, UUID testId) {
    if (workerUnit.getFilter() == null) {
      return true;
    }

    return workerUnit.getFilter().testFilter() != null
        && workerUnit.getFilter().testFilter().testConfigurationId().value().equals(testId);
  }
}
