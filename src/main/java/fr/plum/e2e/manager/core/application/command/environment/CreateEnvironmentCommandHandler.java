package fr.plum.e2e.manager.core.application.command.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
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
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicReference;

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

    AtomicReference<EnvironmentId> environmentId = new AtomicReference<>();
    transactionManagerPort.executeInTransaction(
        () -> {
          var environment = createEnvironment(environmentCommand);
          createSynchronization(environmentCommand, environment);
          createScheduler(environmentCommand, environment);
          environmentId.set(environment.getId());
        });

    eventPublisherPort.publishAsync(
        new EnvironmentCreatedEvent(environmentId.get(), environmentCommand.actionUsername()));
  }

  private void createScheduler(
      CreateEnvironmentCommand environmentCommand, Environment environment) {
    var scheduler =
        SchedulerConfiguration.initialize(
            environment.getId(), clockPort.now(), environmentCommand.actionUsername());
    schedulerConfigurationRepositoryPort.save(scheduler);
  }

  private void createSynchronization(
      CreateEnvironmentCommand environmentCommand, Environment environment) {
    var synchronization =
        Synchronization.initialize(
            environment.getId(), clockPort.now(), environmentCommand.actionUsername());
    synchronizationRepositoryPort.save(synchronization);
  }

  private Environment createEnvironment(CreateEnvironmentCommand environmentCommand) {
    var environment = environmentCommand.toDomain(clockPort);
    environmentRepositoryPort.save(environment);
    return environment;
  }
}
