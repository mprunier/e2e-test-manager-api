package fr.plum.e2e.manager.core.domain.model.aggregate.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.model.exception.DomainAssertException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class Worker extends AggregateRoot<WorkerId> {

  private EnvironmentId environmentId;
  private WorkerType type;

  @Builder.Default private List<WorkerVariable> variables = new ArrayList<>();

  @Builder.Default private List<WorkerUnit> workerUnits = new ArrayList<>();

  public static Worker initialize(EnvironmentId environmentId, WorkerType type) {
    return builder().id(WorkerId.generate()).environmentId(environmentId).type(type).build();
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
      if (type == WorkerType.TEST && workerUnit.getFilter().testConfiguration() == null) {
        throw new DomainAssertException(
            "Worker filter must contain test configuration for worker type TEST");
      }
      if (type == WorkerType.SUITE && workerUnit.getFilter().suiteConfiguration() == null) {
        throw new DomainAssertException(
            "Worker filter must contain suite configuration for worker type SUITE");
      }
    }
  }

  public void addVariables(List<WorkerVariable> newVariables) {
    variables.addAll(newVariables);
  }

  public boolean isCompleted() {
    return workerUnits.stream().allMatch(WorkerUnit::isCompleted);
  }

  public WorkerUnit findWorkerUnit(WorkerUnitId workerUnitId) {
    return workerUnits.stream()
        .filter(workerUnit -> workerUnit.getId().equals(workerUnitId))
        .findFirst()
        .orElseThrow(() -> new DomainAssertException("Worker unit not found"));
  }
}
