package fr.plum.e2e.manager.core.application.command.environment;

import fr.plum.e2e.manager.core.domain.model.command.UpdateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentUpdatedEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UpdateEnvironmentCommandHandler implements CommandHandler<UpdateEnvironmentCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final EnvironmentRepositoryPort environmentRepositoryPort;

  private final EnvironmentService environmentService;

  public UpdateEnvironmentCommandHandler(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      EnvironmentRepositoryPort environmentRepositoryPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.environmentRepositoryPort = environmentRepositoryPort;
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
  }

  @Override
  public void execute(UpdateEnvironmentCommand environmentCommand) {
    var environment = environmentService.getEnvironment(environmentCommand.environmentId());

    if (!environment.getEnvironmentDescription().equals(environmentCommand.description())) {
      environmentService.assertEnvironmentDescriptionNotExist(environmentCommand.description());
    }

    environment.updateGlobalInfo(
        environmentCommand.description(),
        environmentCommand.sourceCodeInformation(),
        environmentCommand.maxParallelWorkers());
    environment.updateVariables(environmentCommand.toDomainVariables());
    environment.updateAuditInfo(environmentCommand.actionUsername(), clockPort.now());

    environmentRepositoryPort.update(environment);

    eventPublisherPort.publishAsync(
        new EnvironmentUpdatedEvent(
            environmentCommand.environmentId(), environmentCommand.actionUsername()));
  }
}
