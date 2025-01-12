package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.domain.model.event.WorkerUnitCompletedEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.WorkerNotificationHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerUnitCompletedEventConsumer {

  private final WorkerNotificationHelper workerNotificationHelper;

  public void workerUnitCompleted(@ObservesAsync WorkerUnitCompletedEvent event) {
    try {
      log.info(
          "One of the worker unit completed is completed for worker id [{}]. There are still [{}] worker units in progress.",
          event.worker().getId().value(),
          event.worker().countInProgressWorkerUnits());
      workerNotificationHelper.sendWorkerUnitUpdatedNotification(
          event.environmentId(), event.worker());
    } catch (Exception e) {
      log.error("Error while sending worker unit completed notification", e);
    }
  }
}
