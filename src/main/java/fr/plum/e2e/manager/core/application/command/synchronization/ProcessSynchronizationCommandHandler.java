package fr.plum.e2e.manager.core.application.command.synchronization;

import fr.plum.e2e.manager.core.application.command.synchronization.process.ConfigurationSynchronizer;
import fr.plum.e2e.manager.core.application.command.synchronization.process.SourceCodeSynchronizer;
import fr.plum.e2e.manager.core.application.command.synchronization.process.factory.SynchronizationErrorFactory;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentSynchronizedEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.JavascriptConverterPort;
import fr.plum.e2e.manager.core.domain.port.SourceCodePort;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.TransactionManagerPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the synchronization process between source code repositories and test configurations.
 * This handler orchestrates a multi-step synchronization workflow:
 *
 * <p>1. Source Code Synchronization:
 *
 * <p>- Clones the repository from the environment's source control
 *
 * <p>- Processes TypeScript/JavaScript files (transpilation and conversion)
 *
 * <p>2. Configuration Synchronization:
 *
 * <p>- Analyzes differences between existing and new test configurations
 *
 * <p>- Handles creation, updates, and deletion of test configurations
 *
 * <p>3. Error Management:
 *
 * <p>- Collects and tracks synchronization errors at both file and global levels
 *
 * <p>- Ensures proper error reporting even in case of partial synchronization
 *
 * <p>- Maintains synchronization state for error recovery
 *
 * <p>The handler ensures proper cleanup of temporary resources and publishes synchronization
 * completion events regardless of the synchronization outcome. All operations are performed within
 * appropriate transaction boundaries to maintain data consistency.
 */
@Slf4j
@ApplicationScoped
public class ProcessSynchronizationCommandHandler implements CommandHandler<CommonCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisher;
  private final TransactionManagerPort transactionManagerPort;
  private final SynchronizationRepositoryPort synchronizationRepositoryPort;
  private final EnvironmentService environmentService;
  private final SynchronizationService synchronizationService;
  private final SourceCodeSynchronizer sourceCodeSynchronizer;
  private final ConfigurationSynchronizer configurationSynchronizer;

  public ProcessSynchronizationCommandHandler(
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

    var environment = environmentService.getEnvironment(command.environmentId());

    try {
      sourceCodeProject = sourceCodeSynchronizer.cloneRepository(environment);
      Map<SynchronizationFileName, SynchronizationFileContent> processedFiles =
          sourceCodeSynchronizer.processFiles(sourceCodeProject, errors);

      // Specific transaction for configuration synchronization for not saving partial data
      transactionManagerPort.executeInTransaction(
          () ->
              configurationSynchronizer.synchronizeConfigurations(
                  command.environmentId(), processedFiles, errors));

    } catch (CustomException exception) {
      log.error(
          "Error during synchronization for Environment id [{}].",
          command.environmentId().value(),
          exception);
      errors.add(
          SynchronizationErrorFactory.createGlobalError(
              exception.getDescription(), clockPort.now()));
    } catch (Exception exception) {
      log.error(
          "Error during synchronization for Environment id [{}].",
          command.environmentId().value(),
          exception);
      errors.add(
          SynchronizationErrorFactory.createGlobalError(exception.getMessage(), clockPort.now()));
    } finally {
      sourceCodeSynchronizer.cleanup(command.environmentId(), sourceCodeProject);
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
