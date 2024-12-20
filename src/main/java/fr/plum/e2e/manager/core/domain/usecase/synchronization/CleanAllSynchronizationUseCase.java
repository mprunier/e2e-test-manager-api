package fr.plum.e2e.manager.core.domain.usecase.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamCommandUseCase;

public class CleanAllSynchronizationUseCase implements NoParamCommandUseCase {

  private final SynchronizationRepositoryPort synchronizationRepositoryPort;

  public CleanAllSynchronizationUseCase(
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
