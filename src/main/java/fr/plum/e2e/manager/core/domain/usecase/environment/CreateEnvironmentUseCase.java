package fr.plum.e2e.manager.core.domain.usecase.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.command.CreateUpdateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentCreatedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import java.util.concurrent.atomic.AtomicReference;

public class CreateEnvironmentUseCase implements CommandUseCase<CreateUpdateEnvironmentCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final EnvironmentRepositoryPort environmentRepositoryPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;
  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final TransactionManagerPort transactionManagerPort;

  private final EnvironmentService environmentService;

  public CreateEnvironmentUseCase(
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
  public void execute(CreateUpdateEnvironmentCommand environmentCommand) {
    environmentService.assertEnvironmentDescriptionNotExist(environmentCommand.description());

    AtomicReference<EnvironmentId> environmentId = new AtomicReference<>();
    transactionManagerPort.executeInTransaction(
        () -> {
          var environment = createEnvironment(environmentCommand);
          createSynchronization(environmentCommand, environment);
          createScheduler(environmentCommand);
          environmentId.set(environment.getId());
        });

    eventPublisherPort.publishAsync(
        new EnvironmentCreatedEvent(environmentId.get(), environmentCommand.actionUsername()));
  }

  private void createScheduler(CreateUpdateEnvironmentCommand environmentCommand) {
    var scheduler =
        SchedulerConfiguration.initialize(
            environmentCommand.environmentId(),
            clockPort.now(),
            environmentCommand.actionUsername());
    schedulerConfigurationRepositoryPort.save(scheduler);
  }

  private void createSynchronization(
      CreateUpdateEnvironmentCommand environmentCommand, Environment environment) {
    var synchronization =
        Synchronization.initialize(
            environment.getId(), clockPort.now(), environmentCommand.actionUsername());
    synchronizationRepositoryPort.save(synchronization);
  }

  private Environment createEnvironment(CreateUpdateEnvironmentCommand environmentCommand) {
    var environment = environmentCommand.toDomain(clockPort);
    environmentRepositoryPort.save(environment);
    return environment;
  }
}
