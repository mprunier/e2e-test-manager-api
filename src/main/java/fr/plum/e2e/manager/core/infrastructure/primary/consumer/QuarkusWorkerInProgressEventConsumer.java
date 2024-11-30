package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.domain.model.event.WorkerInProgressEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.WorkerNotificationHelper;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerNotificationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerInProgressEventConsumer {

  private final WorkerNotificationHelper workerNotificationHelper;

  public void workerInProgress(@ObservesAsync WorkerInProgressEvent event) {
    workerNotificationHelper.sendWorkerUpdatedNotification(
        event.environmentId(), event.worker(), WorkerNotificationStatus.IN_PROGRESS);

    workerNotificationHelper.sendWorkerUnitUpdatedNotification(
        event.environmentId(), event.worker());
  }
}
