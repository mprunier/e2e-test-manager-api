package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.exception.ConcurrentWorkersReachedException;
import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkerService {

  private final WorkerRepositoryPort workerRepositoryPort;
  private final ConfigurationPort configurationPort;

  public void assertWorkerNotReached() {
    var inProgressWorker = workerRepositoryPort.countAll();
    if (inProgressWorker >= configurationPort.getMaxJobInParallel()) {
      throw new ConcurrentWorkersReachedException();
    }
  }
}
