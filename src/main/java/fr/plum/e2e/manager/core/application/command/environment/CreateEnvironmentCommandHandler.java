package fr.plum.e2e.manager.core.application.command.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.command.CreateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentCreatedEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateEnvironmentCommandHandler implements CommandHandler<CreateEnvironmentCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final EnvironmentRepositoryPort environmentRepositoryPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;
  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final TransactionManagerPort transactionManagerPort;

  private final EnvironmentService environmentService;

  public CreateEnvironmentCommandHandler(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      SynchronizationRepositoryPort synchronizationRepositoryPort,
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort,
      TransactionManagerPort transactionManagerPort) {

    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.environmentRepositoryPort = environmentRepositoryPort;
    this.synchronizationRepositoryPort = synchronizationRepositoryPort;
    this.schedulerConfigurationRepositoryPort = schedulerConfigurationRepositoryPort;
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
    this.transactionManagerPort = transactionManagerPort;
  }

  @Override
  public void execute(CreateEnvironmentCommand environmentCommand) {
    environmentService.assertEnvironmentDescriptionNotExist(environmentCommand.description());

    transactionManagerPort.executeInTransaction(
        () -> {
          var environment = createEnvironment(environmentCommand);
          createSynchronization(environmentCommand, environment);
          createScheduler(environmentCommand, environment);
          transactionManagerPort.registerAfterCommit(
              () ->
                  eventPublisherPort.publishAsync(
                      new EnvironmentCreatedEvent(
                          environment.getId(), environmentCommand.actionUsername())));
        });
  }

  private void createScheduler(
      CreateEnvironmentCommand environmentCommand, Environment environment) {
    var scheduler =
        SchedulerConfiguration.create(
            environment.getId(),
            AuditInfo.create(environmentCommand.actionUsername(), clockPort.now()));
    schedulerConfigurationRepositoryPort.save(scheduler);
  }

  private void createSynchronization(
      CreateEnvironmentCommand environmentCommand, Environment environment) {
    var synchronization =
        Synchronization.create(
            environment.getId(),
            AuditInfo.create(environmentCommand.actionUsername(), clockPort.now()));
    synchronizationRepositoryPort.save(synchronization);
  }

  private Environment createEnvironment(CreateEnvironmentCommand environmentCommand) {
    var environment =
        Environment.create(
            environmentCommand.description(),
            environmentCommand.sourceCodeInformation(),
            environmentCommand.variables().stream()
                .map(
                    commandVariable ->
                        EnvironmentVariable.create(
                            commandVariable.name(),
                            commandVariable.value(),
                            commandVariable.description(),
                            commandVariable.isHidden()))
                .toList(),
            AuditInfo.create(environmentCommand.actionUsername(), clockPort.now()));
    environmentRepositoryPort.save(environment);
    return environment;
  }
}
