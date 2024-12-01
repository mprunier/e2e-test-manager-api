package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CheckWorkerScheduler {

  private final WorkerFacade workerFacade;

  @Getter
  @ConfigProperty(name = "business.scheduler.worker.report.verification.interval-minutes")
  Integer workerVerificationInterval;

  @Getter
  @ConfigProperty(name = "business.scheduler.worker.report.cancel-timeout.interval-minutes")
  Integer workerCancelTimeoutInterval;

  private final AtomicBoolean inVerifyProgress = new AtomicBoolean(false);

  @Scheduled(cron = "0 0/1 * * * ?")
  @ActivateRequestContext
  public void schedule() {
    if (inVerifyProgress.compareAndSet(false, true)) {
      log.trace("In progress pipelines verification scheduler started.");
      try {
        var workers = workerFacade.getAll();
        for (var worker : workers) {
          if (worker
              .getAuditInfo()
              .getCreatedAt()
              .isBefore(ZonedDateTime.now().minusMinutes(workerVerificationInterval))) {
            workerFacade.cancel(
                new CancelWorkerCommand(
                    worker.getEnvironmentId(),
                    new ActionUsername("System Timeout Checker"),
                    worker.getId()));
          } else {
            for (WorkerUnit workerUnit : worker.getWorkerUnits()) {
              workerFacade.report(new ReportWorkerCommand(workerUnit.getId()));
            }
          }
        }
      } catch (Exception e) {
        log.error("Error during the verification of the in progress pipelines.", e);
      } finally {
        inVerifyProgress.set(false);
      }
    }
  }
}