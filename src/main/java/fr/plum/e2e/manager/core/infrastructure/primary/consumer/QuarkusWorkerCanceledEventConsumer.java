package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.domain.model.event.WorkerCanceledEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.WorkerNotificationHelper;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.WorkerNotificationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerCanceledEventConsumer {

  private final WorkerNotificationHelper workerNotificationHelper;

  public void workerCompleted(@ObservesAsync WorkerCanceledEvent event) {
    try {
      workerNotificationHelper.sendWorkerUpdatedNotification(
          event.environmentId(), event.worker(), WorkerNotificationStatus.COMPLETED);
    } catch (Exception e) {
      log.error("Error while sending worker completed notification", e);
    }
  }
}
