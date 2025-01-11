package fr.plum.e2e.manager.core.domain.model.aggregate.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkerUnit extends Entity<WorkerUnitId> {

  private WorkerUnitStatus status;
  private WorkerUnitFilter filter;

  @Builder
  public WorkerUnit(WorkerUnitId workerUnitId, WorkerUnitStatus status, WorkerUnitFilter filter) {
    super(workerUnitId);
    Assert.notNull("status", status);
    this.status = status;
    this.filter = filter;
  }

  public static WorkerUnit create(
      WorkerUnitId workerUnitId, WorkerUnitStatus status, WorkerUnitFilter filter) {
    return builder()
        .workerUnitId(workerUnitId)
        .status(status != null ? status : WorkerUnitStatus.IN_PROGRESS)
        .filter(filter)
        .build();
  }

  public boolean isFinish() {
    return status != WorkerUnitStatus.IN_PROGRESS;
  }

  public void updateStatus(WorkerUnitStatus status) {
    this.status = status;
  }
}
