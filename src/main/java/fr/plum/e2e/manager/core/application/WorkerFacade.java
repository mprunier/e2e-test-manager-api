package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.worker.CancelWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.GetTypeAllWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.ReportWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.RunWorkerUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class WorkerFacade {

  private final RunWorkerUseCase runWorkerUseCase;
  private final CancelWorkerUseCase cancelWorkerUseCase;
  private final GetTypeAllWorkerUseCase getTypeAllWorkerUseCase;
  private final ReportWorkerUseCase reportWorkerUseCase;

  public WorkerFacade(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerPort workerPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      ConfigurationPort configurationPort,
      ReportWorkerUseCase reportWorkerUseCase) {
    this.runWorkerUseCase =
        new RunWorkerUseCase(
            clockPort,
            eventPublisherPort,
            workerPort,
            environmentRepositoryPort,
            fileConfigurationRepositoryPort,
            workerRepositoryPort,
            configurationPort);
    this.cancelWorkerUseCase = new CancelWorkerUseCase();
    this.getTypeAllWorkerUseCase = new GetTypeAllWorkerUseCase(workerRepositoryPort);
    this.reportWorkerUseCase = reportWorkerUseCase; // TODO with new
  }

  public void run(RunWorkerCommand command) {
    runWorkerUseCase.execute(command);
  }

  public void cancel(CancelWorkerCommand command) {
    cancelWorkerUseCase.execute(command);
  }

  public Optional<Worker> get(CommonQuery query) {
    return getTypeAllWorkerUseCase.execute(query);
  }

  public void report(ReportWorkerCommand command) {
    reportWorkerUseCase.execute(command);
  }
}
