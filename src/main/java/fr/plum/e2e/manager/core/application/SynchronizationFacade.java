package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.out.JavascriptConverterPort;
import fr.plum.e2e.manager.core.domain.port.out.SourceCodePort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.CleanAllSynchronizationUseCase;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.ListAllSynchronizationErrorsUseCase;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.ProcessSynchronizationUseCase;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.StartSynchronizationUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SynchronizationFacade {
  private final StartSynchronizationUseCase startSynchronizationUseCase;
  private final ProcessSynchronizationUseCase processSynchronizationUseCase;
  private final ListAllSynchronizationErrorsUseCase listAllSynchronizationErrorsUseCase;
  private final CleanAllSynchronizationUseCase cleanAllSynchronizationUseCase;

  public SynchronizationFacade(
      ClockPort clockPort,
      EventPublisherPort eventPublisher,
      EnvironmentRepositoryPort environmentRepository,
      SourceCodePort sourceCodePort,
      FileSynchronizationPort fileSynchronizationPort,
      SynchronizationRepositoryPort synchronizationRepositoryPort,
      TransactionManagerPort transactionManagerPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      JavascriptConverterPort javascriptConverterPort) {
    this.cleanAllSynchronizationUseCase =
        new CleanAllSynchronizationUseCase(synchronizationRepositoryPort);
    this.listAllSynchronizationErrorsUseCase =
        new ListAllSynchronizationErrorsUseCase(synchronizationRepositoryPort);
    this.processSynchronizationUseCase =
        new ProcessSynchronizationUseCase(
            clockPort,
            eventPublisher,
            environmentRepository,
            sourceCodePort,
            fileSynchronizationPort,
            synchronizationRepositoryPort,
            transactionManagerPort,
            fileConfigurationRepositoryPort,
            javascriptConverterPort);
    this.startSynchronizationUseCase =
        new StartSynchronizationUseCase(eventPublisher, synchronizationRepositoryPort);
  }

  public void startSynchronization(CommonCommand command) {
    startSynchronizationUseCase.execute(command);
  }

  public void processSynchronization(CommonCommand command) {
    processSynchronizationUseCase.execute(command);
  }

  public List<SynchronizationError> listErrors(CommonQuery query) {
    return listAllSynchronizationErrorsUseCase.execute(query);
  }

  public void cleanAllSynchronizations() {
    cleanAllSynchronizationUseCase.execute();
  }
}
