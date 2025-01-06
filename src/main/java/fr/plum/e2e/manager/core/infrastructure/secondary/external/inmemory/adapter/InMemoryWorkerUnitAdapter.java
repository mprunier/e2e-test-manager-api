package fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.port.WorkerUnitPort;
import java.util.ArrayList;
import java.util.List;

public class InMemoryWorkerUnitAdapter implements WorkerUnitPort {
  
  private final List<WorkerExecution> executions = new ArrayList<>();

  @Override
  public WorkerUnitId runWorker(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {
    var execution =
        new WorkerExecution(environment, workerUnitFilter, workerVariables, workerIsRecordVideo);
    executions.add(execution);
    return new WorkerUnitId("worker-" + executions.size());
  }

  @Override
  public WorkerUnitStatus getWorkerStatus(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    return null;
  }

  @Override
  public Object getWorkerReportArtifacts(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    return null;
  }

  @Override
  public void cancel(SourceCodeInformation sourceCodeInformation, WorkerUnitId id) {
    // No-op for tests
  }

  public List<WorkerExecution> getExecutions() {
    return executions;
  }

  public record WorkerExecution(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {}
}
