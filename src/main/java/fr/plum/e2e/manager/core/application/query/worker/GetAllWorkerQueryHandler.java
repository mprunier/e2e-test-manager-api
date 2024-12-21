package fr.plum.e2e.manager.core.application.query.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.application.query.NoParamQueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GetAllWorkerQueryHandler implements NoParamQueryHandler<List<Worker>> {

  private final WorkerRepositoryPort workerRepositoryPort;

  public GetAllWorkerQueryHandler(WorkerRepositoryPort workerRepositoryPort) {
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public List<Worker> execute() {
    return workerRepositoryPort.findAll();
  }
}
