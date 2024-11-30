package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamQueryUseCase;
import java.util.List;

public class GetAllWorkerUseCase implements NoParamQueryUseCase<List<Worker>> {

  private final WorkerRepositoryPort workerRepositoryPort;

  public GetAllWorkerUseCase(WorkerRepositoryPort workerRepositoryPort) {
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public List<Worker> execute() {
    return workerRepositoryPort.findAll();
  }
}
