package fr.plum.e2e.manager.core.application.command.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CleanAllSynchronizationCommandHandler implements NoParamCommandHandler {

  private final SynchronizationRepositoryPort synchronizationRepositoryPort;

  public CleanAllSynchronizationCommandHandler(
      SynchronizationRepositoryPort synchronizationRepositoryPort) {
    this.synchronizationRepositoryPort = synchronizationRepositoryPort;
  }

  @Override
  public void execute() {
    var synchronizations = synchronizationRepositoryPort.findAll();
    synchronizations.forEach(Synchronization::finishWithoutUpdateErrors);

    synchronizationRepositoryPort.updateAll(synchronizations);
  }
}
