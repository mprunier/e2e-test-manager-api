package fr.plum.e2e.manager.core.domain.usecase.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentSynchronizedEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.out.JavascriptConverterPort;
import fr.plum.e2e.manager.core.domain.port.out.SourceCodePort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.process.ConfigurationSynchronizer;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.process.SourceCodeSynchronizer;
import fr.plum.e2e.manager.core.domain.usecase.synchronization.process.factory.SynchronizationErrorFactory;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
import java.util.ArrayList;

public class ProcessSynchronizationUseCase implements CommandUseCase<CommonCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisher;
  private final TransactionManagerPort transactionManagerPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;
  private final EnvironmentService environmentService;
  private final SynchronizationService synchronizationService;
  private final SourceCodeSynchronizer sourceCodeSynchronizer;
  private final ConfigurationSynchronizer configurationSynchronizer;

  public ProcessSynchronizationUseCase(
      ClockPort clockPort,
      EventPublisherPort eventPublisher,
      EnvironmentRepositoryPort environmentRepository,
      SourceCodePort sourceCodePort,
      FileSynchronizationPort fileSynchronizationPort,
      SynchronizationRepositoryPort synchronizationRepositoryPort,
      TransactionManagerPort transactionManagerPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      JavascriptConverterPort javascriptConverterPort) {
    this.clockPort = clockPort;
    this.eventPublisher = eventPublisher;
    this.synchronizationRepositoryPort = synchronizationRepositoryPort;
    this.transactionManagerPort = transactionManagerPort;

    this.environmentService = new EnvironmentService(environmentRepository);
    this.synchronizationService = new SynchronizationService(synchronizationRepositoryPort);
    this.sourceCodeSynchronizer =
        new SourceCodeSynchronizer(
            clockPort, sourceCodePort, fileSynchronizationPort, javascriptConverterPort);
    this.configurationSynchronizer =
        new ConfigurationSynchronizer(
            clockPort, fileConfigurationRepositoryPort, fileSynchronizationPort);
  }

  @Override
  public void execute(CommonCommand command) {
    var errors = new ArrayList<SynchronizationError>();
    SourceCodeProject sourceCodeProject = null;

    try {
      var environment = environmentService.getEnvironment(command.environmentId());

      sourceCodeProject = sourceCodeSynchronizer.cloneRepository(environment);
      var processedFiles = sourceCodeSynchronizer.processFiles(sourceCodeProject, errors);

      // Specific transaction for configuration synchronization for not saving partial data
      transactionManagerPort.executeInTransaction(
          () ->
              configurationSynchronizer.synchronizeConfigurations(
                  command.environmentId(), processedFiles, errors));

    } catch (CustomException exception) {
      errors.add(
          SynchronizationErrorFactory.createGlobalError(
              command.environmentId(), exception.getDescription(), clockPort.now()));
    } catch (Exception exception) {
      errors.add(
          SynchronizationErrorFactory.createGlobalError(
              command.environmentId(), exception.getMessage(), clockPort.now()));
    } finally {
      sourceCodeSynchronizer.cleanup(command.environmentId(), sourceCodeProject, errors);
    }

    finishSynchronization(command.environmentId(), errors, command.username());

    eventPublisher.publishAsync(
        new EnvironmentSynchronizedEvent(command.environmentId(), command.username(), errors));
  }

  private void finishSynchronization(
      EnvironmentId environmentId,
      ArrayList<SynchronizationError> errors,
      ActionUsername username) {
    var synchronization = synchronizationService.getSynchronization(environmentId);
    synchronization.finish(errors, username, clockPort.now());

    synchronizationRepositoryPort.update(synchronization);
  }
}
