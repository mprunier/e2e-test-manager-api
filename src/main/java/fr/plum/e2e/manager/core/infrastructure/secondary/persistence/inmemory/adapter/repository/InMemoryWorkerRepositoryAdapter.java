package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryWorkerRepositoryAdapter implements WorkerRepositoryPort {
  private final Map<WorkerId, Worker> workers = new HashMap<>();

  @Override
  public void save(Worker worker) {
    workers.put(worker.getId(), worker);
  }

  @Override
  public void delete(WorkerId workerId) {
    workers.remove(workerId);
  }

  @Override
  public Optional<Worker> assertNotWorkerInProgressByType(
      EnvironmentId environmentId, WorkerType workerType) {
    return workers.values().stream()
        .filter(worker -> worker.getEnvironmentId().equals(environmentId))
        .filter(worker -> worker.getType().equals(workerType))
        .findFirst();
  }

  @Override
  public int countAll() {
    return workers.size();
  }

  @Override
  public List<Worker> findAll() {
    return new ArrayList<>(workers.values());
  }

  @Override
  public List<Worker> findAll(EnvironmentId environmentId) {
    return workers.values().stream()
        .filter(worker -> worker.getEnvironmentId().equals(environmentId))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Worker> find(EnvironmentId environmentId, WorkerType workerType) {
    return workers.values().stream()
        .filter(worker -> worker.getEnvironmentId().equals(environmentId))
        .filter(worker -> worker.getType().equals(workerType))
        .findFirst();
  }

  @Override
  public Optional<Worker> find(WorkerUnitId workerUnitId) {
    return workers.values().stream()
        .filter(
            worker ->
                worker.getWorkerUnits().stream()
                    .anyMatch(unit -> unit.getId().equals(workerUnitId)))
        .findFirst();
  }

  @Override
  public Optional<Worker> find(WorkerId workerId) {
    return Optional.ofNullable(workers.get(workerId));
  }
}
