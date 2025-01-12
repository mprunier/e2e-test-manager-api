package fr.plum.e2e.manager.core.application.command.synchronization;

import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentIsSynchronizingEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    log.info(
        "[{}] ran synchronization on Environment id [{}].",
        command.username().value(),
        command.environmentId().value());

    var synchronization = synchronizationService.getSynchronization(command.environmentId());
    synchronization.assertIsNotInProgress();
    synchronization.start();

    synchronizationRepositoryPort.update(synchronization);

    eventPublisherPort.publishAsync(
        new EnvironmentIsSynchronizingEvent(command.environmentId(), command.username()));
  }
}
