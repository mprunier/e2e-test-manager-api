package fr.plum.e2e.manager.core.application.query.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class GetTypeAllWorkerQueryHandler implements QueryHandler<CommonQuery, Optional<Worker>> {

  private final WorkerRepositoryPort workerRepositoryPort;

  public GetTypeAllWorkerQueryHandler(WorkerRepositoryPort workerRepositoryPort) {
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public Optional<Worker> execute(CommonQuery query) {
    return workerRepositoryPort.find(query.environmentId(), WorkerType.ALL);
  }
}
