package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.application.command.metrics.AddMetricsCommandHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.MetricsHelper;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.WorkerNotificationHelper;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.adapter.EnvironmentNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.UpdateFinalMetricsNotificationsEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.WorkerNotificationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerCompletedEventConsumer {

  private final WorkerNotificationHelper workerNotificationHelper;
  private final AddMetricsCommandHandler addMetricsCommandHandler;
  private final EnvironmentNotifier environmentNotifier;
  private final MetricsHelper metricsHelper;

  public void workerCompleted(@ObservesAsync WorkerCompletedEvent event) {
    try {
      addMetrics(event);
      workerNotificationHelper.sendWorkerUpdatedNotification(
          event.environmentId(), event.worker(), WorkerNotificationStatus.COMPLETED);
    } catch (Exception e) {
      log.error("Error while sending worker completed notification", e);
    }
  }

  private void addMetrics(WorkerCompletedEvent event) {
    var addMetricsCommand =
        new AddMetricsCommand(
            event.environmentId(), MetricsType.fromWorkerType(event.worker().getType()));
    addMetricsCommandHandler.execute(addMetricsCommand);

    var metricsResponse = metricsHelper.getLastMetrics(event.environmentId().value());
    var updateFinalMetricsNotificationsEvent =
        UpdateFinalMetricsNotificationsEvent.builder()
            .environmentId(event.environmentId().value())
            .metrics(metricsResponse)
            .build();
    environmentNotifier.notifySubscribers(updateFinalMetricsNotificationsEvent);
  }
}
