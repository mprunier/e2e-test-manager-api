package fr.plum.e2e.manager.core.domain.usecase.synchronization;

import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentIsSynchronizingEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;

public class StartSynchronizationUseCase implements CommandUseCase<CommonCommand> {

  private final EventPublisherPort eventPublisherPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;

  private final SynchronizationService synchronizationService;

  public StartSynchronizationUseCase(
      EventPublisherPort eventPublisherPort,
      SynchronizationRepositoryPort synchronizationRepositoryPort) {
    this.eventPublisherPort = eventPublisherPort;
    this.synchronizationRepositoryPort = synchronizationRepositoryPort;
    this.synchronizationService = new SynchronizationService(synchronizationRepositoryPort);
  }

  @Override
  public void execute(CommonCommand command) {
    var synchronization = synchronizationService.getSynchronization(command.environmentId());
    synchronization.assertIsNotInProgress();
    synchronization.start();

    synchronizationRepositoryPort.update(synchronization);

    eventPublisherPort.publishAsync(
        new EnvironmentIsSynchronizingEvent(command.environmentId(), command.username()));
  }
}
