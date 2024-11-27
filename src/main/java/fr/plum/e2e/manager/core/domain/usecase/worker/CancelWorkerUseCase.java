package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCanceledEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerUnitPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;

public class CancelWorkerUseCase implements CommandUseCase<CancelWorkerCommand> {

  private final EventPublisherPort eventPublisherPort;
  private final WorkerUnitPort workerUnitPort;
  private final WorkerRepositoryPort workerRepositoryPort;

  public CancelWorkerUseCase(
      EventPublisherPort eventPublisherPort,
      WorkerUnitPort workerUnitPort,
      WorkerRepositoryPort workerRepositoryPort) {
    this.eventPublisherPort = eventPublisherPort;
    this.workerUnitPort = workerUnitPort;
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public void execute(CancelWorkerCommand command) {
    var optionalWorker = workerRepositoryPort.find(command.workerId());
    if (optionalWorker.isPresent()) {
      var worker = optionalWorker.get();
      worker.getWorkerUnits().forEach(workerUnit -> workerUnitPort.cancel(workerUnit.getId()));
      workerRepositoryPort.delete(worker.getId());
      var workerCanceledEvent =
          new WorkerCanceledEvent(worker.getEnvironmentId(), command.actionUsername(), worker);
      eventPublisherPort.publish(workerCanceledEvent);
    }
  }
}
