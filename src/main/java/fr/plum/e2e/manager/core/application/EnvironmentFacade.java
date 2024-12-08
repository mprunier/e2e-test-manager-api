package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.command.CreateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.command.UpdateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsView;
import fr.plum.e2e.manager.core.domain.model.view.EnvironmentView;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.query.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.environment.CreateEnvironmentUseCase;
import fr.plum.e2e.manager.core.domain.usecase.environment.GetEnvironmentDetailsUseCase;
import fr.plum.e2e.manager.core.domain.usecase.environment.ListAllEnvironmentsUseCase;
import fr.plum.e2e.manager.core.domain.usecase.environment.UpdateEnvironmentUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EnvironmentFacade {
  private final CreateEnvironmentUseCase createEnvironmentUseCase;
  private final UpdateEnvironmentUseCase updateEnvironmentUseCase;
  private final GetEnvironmentDetailsUseCase getEnvironmentDetailsUseCase;
  private final ListAllEnvironmentsUseCase listAllEnvironmentsUseCase;

  public EnvironmentFacade(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      SynchronizationRepositoryPort synchronizationRepositoryPort,
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort,
      TransactionManagerPort transactionManagerPort,
      ListAllEnvironmentsPort listAllEnvironmentsPort) {
    this.createEnvironmentUseCase =
        new CreateEnvironmentUseCase(
            clockPort,
            eventPublisherPort,
            environmentRepositoryPort,
            synchronizationRepositoryPort,
            schedulerConfigurationRepositoryPort,
            transactionManagerPort);
    this.updateEnvironmentUseCase =
        new UpdateEnvironmentUseCase(clockPort, eventPublisherPort, environmentRepositoryPort);
    this.getEnvironmentDetailsUseCase = new GetEnvironmentDetailsUseCase(environmentRepositoryPort);
    this.listAllEnvironmentsUseCase = new ListAllEnvironmentsUseCase(listAllEnvironmentsPort);
  }

  public void createEnvironment(CreateEnvironmentCommand command) {
    createEnvironmentUseCase.execute(command);
  }

  public void updateEnvironment(UpdateEnvironmentCommand command) {
    updateEnvironmentUseCase.execute(command);
  }

  public EnvironmentDetailsView getEnvironmentDetails(CommonQuery query) {
    return getEnvironmentDetailsUseCase.execute(query);
  }

  public List<EnvironmentView> listAllEnvironments() {
    return listAllEnvironmentsUseCase.execute();
  }
}
