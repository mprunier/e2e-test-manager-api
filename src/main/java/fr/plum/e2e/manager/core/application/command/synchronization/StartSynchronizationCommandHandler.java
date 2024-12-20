package fr.plum.e2e.manager.core.application.command.synchronization;

import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentIsSynchronizingEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StartSynchronizationCommandHandler implements CommandHandler<CommonCommand> {

  private final EventPublisherPort eventPublisherPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;

  private final SynchronizationService synchronizationService;

  public StartSynchronizationCommandHandler(
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
