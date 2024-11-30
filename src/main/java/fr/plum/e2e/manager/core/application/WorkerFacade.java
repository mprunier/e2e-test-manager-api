package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.application.locker.CommandLock;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerExtractorPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerUnitPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.worker.CancelWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.GetAllWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.GetTypeAllWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.ReportWorkerUseCase;
import fr.plum.e2e.manager.core.domain.usecase.worker.RunWorkerUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WorkerFacade {

  private final RunWorkerUseCase runWorkerUseCase;
  private final CancelWorkerUseCase cancelWorkerUseCase;
  private final GetTypeAllWorkerUseCase getTypeAllWorkerUseCase;
  private final GetAllWorkerUseCase getAllWorkerUseCase;
  private final ReportWorkerUseCase reportWorkerUseCase;

  public WorkerFacade(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerUnitPort workerUnitPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      ConfigurationPort configurationPort,
      TestConfigurationRepositoryPort testConfigurationRepositoryPort,
      WorkerExtractorPort workerExtractorPort,
      TestResultRepositoryPort testResultRepositoryPort,
      TransactionManagerPort transactionManagerPort) {
    this.runWorkerUseCase =
        new RunWorkerUseCase(
            clockPort,
            eventPublisherPort,
            workerUnitPort,
            environmentRepositoryPort,
            fileConfigurationRepositoryPort,
            workerRepositoryPort,
            configurationPort);
    this.cancelWorkerUseCase =
        new CancelWorkerUseCase(
            eventPublisherPort, workerUnitPort, workerRepositoryPort, environmentRepositoryPort);
    this.getTypeAllWorkerUseCase = new GetTypeAllWorkerUseCase(workerRepositoryPort);
    this.reportWorkerUseCase =
        new ReportWorkerUseCase(
            clockPort,
            eventPublisherPort,
            workerUnitPort,
            testConfigurationRepositoryPort,
            workerRepositoryPort,
            workerExtractorPort,
            testResultRepositoryPort,
            transactionManagerPort,
            environmentRepositoryPort);
    this.getAllWorkerUseCase = new GetAllWorkerUseCase(workerRepositoryPort);
  }

  public void run(RunWorkerCommand command) {
    runWorkerUseCase.execute(command);
  }

  @CommandLock
  public void cancel(CancelWorkerCommand command) {
    cancelWorkerUseCase.execute(command);
  }

  public Optional<Worker> get(CommonQuery query) {
    return getTypeAllWorkerUseCase.execute(query);
  }

  @CommandLock
  public void report(ReportWorkerCommand command) {
    reportWorkerUseCase.execute(command);
  }

  public List<Worker> getAll() {
    return getAllWorkerUseCase.execute();
  }
}
