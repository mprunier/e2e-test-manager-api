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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class InMemoryWorkerUnitAdapter implements WorkerUnitPort {

  @Getter private final List<WorkerExecution> executions = new ArrayList<>();
  private final Map<WorkerUnitId, WorkerUnitStatus> statuses = new HashMap<>();
  private final Map<WorkerUnitId, Object> reportArtifacts = new HashMap<>();

  @Override
  public WorkerUnitId runWorker(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {
    var execution =
        new WorkerExecution(environment, workerUnitFilter, workerVariables, workerIsRecordVideo);
    executions.add(execution);
    var id = new WorkerUnitId("worker-" + executions.size());
    statuses.put(id, WorkerUnitStatus.IN_PROGRESS);
    return id;
  }

  @Override
  public WorkerUnitStatus getWorkerStatus(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    return statuses.getOrDefault(workerUnitId, WorkerUnitStatus.IN_PROGRESS);
  }

  @Override
  public Object getWorkerReportArtifacts(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    return reportArtifacts.get(workerUnitId);
  }

  @Override
  public void cancel(SourceCodeInformation sourceCodeInformation, WorkerUnitId id) {
    statuses.put(id, WorkerUnitStatus.CANCELED);
  }

  public void setWorkerStatus(WorkerUnitId id, WorkerUnitStatus status) {
    statuses.put(id, status);
  }

  public void clear() {
    executions.clear();
    statuses.clear();
    reportArtifacts.clear();
  }

  public record WorkerExecution(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {}
}
