package fr.plum.e2e.manager.core.domain.model.aggregate.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class WorkerUnit extends Entity<WorkerUnitId> {

  @Setter @Builder.Default private WorkerUnitStatus status = WorkerUnitStatus.IN_PROGRESS;

  private WorkerUnitFilter filter;

  public boolean isFinish() {
    return status != WorkerUnitStatus.IN_PROGRESS;
  }
}
