package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.respository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.WorkerMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository.JpaWorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaWorkerRepositoryAdapter implements WorkerRepositoryPort {

  private final JpaWorkerRepository repository;

  @Override
  public void save(Worker worker) {
    var entity = WorkerMapper.toEntity(worker);
    repository.persist(entity);
  }

  @Override
  public void delete(WorkerId workerId) {
    repository.deleteById(workerId.value());
  }

  @Override
  public Optional<Worker> find(WorkerId workerId) {
    return repository.findByIdOptional(workerId.value()).map(WorkerMapper::toDomain);
  }

  @Override
  public Optional<Worker> find(WorkerUnitId workerUnitId) {
    return repository.findByUnitId(workerUnitId.value()).map(WorkerMapper::toDomain);
  }

  @Override
  public Optional<Worker> find(EnvironmentId environmentId, WorkerType workerType) {
    return repository
        .findByEnvironmentIdAndType(environmentId.value(), workerType)
        .map(WorkerMapper::toDomain);
  }

  @Override
  public Optional<Worker> assertNotWorkerInProgressByType(
      EnvironmentId environmentId, WorkerType workerType) {
    return repository
        .findInProgressByEnvironmentAndType(environmentId.value(), workerType)
        .map(WorkerMapper::toDomain);
  }

  @Override
  public List<Worker> findAll(EnvironmentId environmentId) {
    return repository.findByEnvironmentId(environmentId.value()).stream()
        .map(WorkerMapper::toDomain)
        .toList();
  }

  @Override
  public int countAll() {
    return (int) repository.count();
  }

  @Override
  public List<Worker> findAll() {
    return repository.findAll().stream().map(WorkerMapper::toDomain).toList();
  }
}
