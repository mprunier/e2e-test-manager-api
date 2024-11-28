package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;
import java.util.Optional;

public class GetTypeAllWorkerUseCase implements QueryUseCase<CommonQuery, Optional<Worker>> {

  private final WorkerRepositoryPort workerRepositoryPort;

  public GetTypeAllWorkerUseCase(WorkerRepositoryPort workerRepositoryPort) {
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public Optional<Worker> execute(CommonQuery query) {
    return workerRepositoryPort.find(query.environmentId(), WorkerType.ALL);
  }
}
