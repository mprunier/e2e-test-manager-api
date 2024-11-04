package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
import fr.plum.e2e.manager.core.domain.model.event.WorkerGroupCompletedEvent;
import fr.plum.e2e.manager.core.domain.port.out.ConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerExtractorPort;
import fr.plum.e2e.manager.core.domain.port.out.WorkerPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.core.domain.service.FileConfigurationService;
import fr.plum.e2e.manager.core.domain.service.WorkerService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;

public class ReportWorkerUseCase implements CommandUseCase<ReportWorkerCommand> {

  private final ClockPort clockPort;
  private final EventPublisherPort eventPublisherPort;
  private final WorkerPort workerPort;
  private final FileConfigurationRepositoryPort fileConfigurationRepositoryPort;
  private final WorkerRepositoryPort workerRepositoryPort;
  private final WorkerExtractorPort workerExtractorPort;

  private final WorkerService workerService;
  private final EnvironmentService environmentService;
  private final FileConfigurationService fileConfigurationService;

  public ReportWorkerUseCase(
      ClockPort clockPort,
      EventPublisherPort eventPublisherPort,
      WorkerPort workerPort,
      WorkerExtractorPort workerExtractorPort,
      EnvironmentRepositoryPort environmentRepositoryPort,
      FileConfigurationRepositoryPort fileConfigurationRepositoryPort,
      WorkerRepositoryPort workerRepositoryPort,
      ConfigurationPort configurationPort) {
    this.clockPort = clockPort;
    this.eventPublisherPort = eventPublisherPort;
    this.workerPort = workerPort;
    this.workerExtractorPort = workerExtractorPort;
    this.fileConfigurationRepositoryPort = fileConfigurationRepositoryPort;
    this.workerRepositoryPort = workerRepositoryPort;
    this.workerService = new WorkerService(workerRepositoryPort, configurationPort);
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
    this.fileConfigurationService = new FileConfigurationService(fileConfigurationRepositoryPort);
  }

  @Override
  public void execute(ReportWorkerCommand command) {

    var optionalWorkerGroup = workerRepositoryPort.findByWorkerId(command.workerUnitId());
    if (optionalWorkerGroup.isEmpty()) {
      return;
    }
    var workerGroup = optionalWorkerGroup.get();

    var environment = environmentService.getEnvironment(workerGroup.getEnvironmentId());

    var workerStatus =
        workerPort.getWorkerStatus(environment.getSourceCodeInformation(), command.workerUnitId());
    if (WorkerUnitStatus.IN_PROGRESS.equals(workerStatus)) {
      return;
    }

    if (WorkerUnitStatus.FAILED.equals(workerStatus)
        || WorkerUnitStatus.SUCCESS.equals(workerStatus)) {
      {
        var reportArtifacts =
            workerPort.getWorkerReportArtifacts(
                environment.getSourceCodeInformation(), command.workerUnitId());
        var workerReport = workerExtractorPort.extractWorkerReportArtifacts(reportArtifacts);
        // TODO
      }

      if (WorkerUnitStatus.CANCELED.equals(workerStatus)) {
        // TODO
      }

      eventPublisherPort.publishAsync(
          new WorkerCompletedEvent(
              workerGroup.getEnvironmentId(), workerGroup.getAuditInfo().getCreatedBy()));

      if (workerGroup.isCompleted()) {
        // TODO Passer les tests en mode isWaiting à false (renommer en isHidden)
        //  ou plutot ajouté une colonne workerId, tant que c'est pas null ça veut dire que c'est
        // hidden
        workerRepositoryPort.delete(workerGroup.getId());
        // TODO create metrics --> in async : peut être dans le consumer de
        // WorkerGroupCompletedEvent
        // pour que ça se fasse avant
        eventPublisherPort.publishAsync(
            new WorkerGroupCompletedEvent(
                workerGroup.getEnvironmentId(), workerGroup.getAuditInfo().getCreatedBy()));
      }
    }
  }
}
