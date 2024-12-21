package fr.plum.e2e.manager.core.domain.port.repository;

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

  Optional<Worker> assertNotWorkerInProgressByType(
      EnvironmentId environmentId, WorkerType workerType);

  int countAll();

  List<Worker> findAll();

  List<Worker> findAll(EnvironmentId environmentId);

  Optional<Worker> find(EnvironmentId environmentId, WorkerType workerType);

  Optional<Worker> find(WorkerUnitId workerUnitId);

  Optional<Worker> find(WorkerId workerId);
}
