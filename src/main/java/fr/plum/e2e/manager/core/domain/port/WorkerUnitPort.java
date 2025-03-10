package fr.plum.e2e.manager.core.domain.port;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import java.util.List;

public interface WorkerUnitPort {

  WorkerUnitId runWorker(
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo);

  WorkerUnitStatus getWorkerStatus(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId);

  Object getWorkerReportArtifacts(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId);

  void cancel(SourceCodeInformation sourceCodeInformation, WorkerUnitId id);
}
