package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.application.MetricsFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.consumer.shared.WorkerNotificationHelper;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.MetricsHelper;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.adapter.WebSocketNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.UpdateFinalMetricsNotificationsEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerNotificationStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerCompletedEventConsumer {

  private final WorkerNotificationHelper workerNotificationHelper;
  private final MetricsFacade metricsFacade;
  private final WebSocketNotifier webSocketNotifier;
  private final MetricsHelper metricsHelper;

  public void workerCompleted(@ObservesAsync WorkerCompletedEvent event) {
    addMetrics(event);
    workerNotificationHelper.sendWorkerUpdatedNotification(
        event.environmentId(), event.worker(), WorkerNotificationStatus.COMPLETED);
  }

  private void addMetrics(WorkerCompletedEvent event) {
    var addMetricsCommand =
        new AddMetricsCommand(
            event.environmentId(), MetricsType.fromWorkerType(event.worker().getType()));
    metricsFacade.addMetrics(addMetricsCommand);

    var metricsResponse = metricsHelper.getLastMetrics(event.environmentId().value());
    var updateFinalMetricsNotificationsEvent =
        UpdateFinalMetricsNotificationsEvent.builder()
            .environmentId(event.environmentId())
            .metrics(metricsResponse)
            .build();
    webSocketNotifier.notifySubscribers(updateFinalMetricsNotificationsEvent);
  }
}
