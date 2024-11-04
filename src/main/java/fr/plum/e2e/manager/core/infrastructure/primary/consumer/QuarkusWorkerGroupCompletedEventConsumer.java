package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.domain.model.event.WorkerGroupCompletedEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.adapter.WebSocketNotifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerGroupCompletedEventConsumer {

  private final WebSocketNotifier webSocketNotifier;

  public void workerGroupCompleted(@ObservesAsync WorkerGroupCompletedEvent event) {
    // TODO
  }
}
