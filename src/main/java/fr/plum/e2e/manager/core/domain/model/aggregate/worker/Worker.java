package fr.plum.e2e.manager.core.domain.model.aggregate.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.model.exception.DomainAssertException;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Worker extends AggregateRoot<WorkerId> {

  private EnvironmentId environmentId;
  private WorkerType type;
  private List<WorkerVariable> variables;
  private List<WorkerUnit> workerUnits;

  @Builder
  public Worker(
      WorkerId workerId,
      AuditInfo auditInfo,
      EnvironmentId environmentId,
      WorkerType type,
      List<WorkerVariable> variables,
      List<WorkerUnit> workerUnits) {
    super(workerId, auditInfo);
    Assert.notNull("environmentId", environmentId);
    Assert.notNull("type", type);
    Assert.notNull("variables", variables);
    Assert.notNull("workerUnits", workerUnits);
    this.environmentId = environmentId;
    this.type = type;
    this.variables = variables;
    this.workerUnits = workerUnits;
  }

  public static Worker create(
      EnvironmentId environmentId,
      AuditInfo auditInfo,
      WorkerType type,
      List<WorkerVariable> variables) {
    return builder()
        .workerId(WorkerId.generate())
        .environmentId(environmentId)
        .auditInfo(auditInfo)
        .type(type)
        .variables(variables)
        .workerUnits(new ArrayList<>())
        .build();
  }

  public void addWorkerUnit(WorkerUnit workerUnit) {
    validateWorkerUnitFilter(workerUnit);
    workerUnits.add(workerUnit);
  }

  private void validateWorkerUnitFilter(WorkerUnit workerUnit) {
    if (type == WorkerType.TEST || type == WorkerType.SUITE) {
      if (workerUnit.getFilter() == null) {
        throw new DomainAssertException(
            String.format("Worker filter is required for worker type %s", type));
      }
      if (type == WorkerType.TEST && workerUnit.getFilter().testFilter() == null) {
        throw new DomainAssertException(
            "Worker filter must contain testFilter configuration for worker type TEST");
      }
      if (type == WorkerType.SUITE && workerUnit.getFilter().suiteFilter() == null) {
        throw new DomainAssertException(
            "Worker filter must contain suiteFilter configuration for worker type SUITE");
      }
    }
  }

  public boolean isCompleted() {
    return workerUnits.stream().allMatch(WorkerUnit::isFinish);
  }

  public long countInProgressWorkerUnits() {
    return workerUnits.stream().filter(WorkerUnit::isInProgress).count();
  }

  public WorkerUnit findWorkerUnit(WorkerUnitId workerUnitId) {
    return workerUnits.stream()
        .filter(workerUnit -> workerUnit.getId().equals(workerUnitId))
        .findFirst()
        .orElseThrow(() -> new DomainAssertException("Worker unit not found"));
  }
}
