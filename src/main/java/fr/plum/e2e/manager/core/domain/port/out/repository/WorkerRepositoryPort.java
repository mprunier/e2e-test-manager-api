package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import java.util.List;
import java.util.Optional;

public interface WorkerRepositoryPort {

  void save(Worker worker);

  void delete(WorkerId workerId);

  Optional<Worker> assertNotWorkerGroupInProgressByType(
      EnvironmentId environmentId, WorkerType workerType);

  int countWorkerInProgress();

  List<Worker> findAllByEnvironmentId(EnvironmentId environmentId);

  Optional<Worker> findTypeAllByEnvironmentId(EnvironmentId environmentId);

  Optional<Worker> findByWorkerId(WorkerUnitId workerUnitId);
}
