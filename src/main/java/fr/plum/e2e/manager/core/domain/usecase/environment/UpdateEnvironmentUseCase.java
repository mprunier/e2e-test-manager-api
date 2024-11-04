package fr.plum.e2e.manager.core.domain.usecase.environment;

import fr.plum.e2e.manager.core.domain.model.command.CreateUpdateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentUpdatedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;

public class UpdateEnvironmentUseCase implements CommandUseCase<CreateUpdateEnvironmentCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final EnvironmentRepositoryPort environmentRepositoryPort;

  private final EnvironmentService environmentService;

  public UpdateEnvironmentUseCase(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      EnvironmentRepositoryPort environmentRepositoryPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.environmentRepositoryPort = environmentRepositoryPort;
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
  }

  @Override
  public void execute(CreateUpdateEnvironmentCommand environmentCommand) {
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
