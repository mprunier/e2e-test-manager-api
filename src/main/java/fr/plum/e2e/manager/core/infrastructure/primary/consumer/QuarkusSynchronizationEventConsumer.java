package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.application.SynchronizationFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentIsSynchronizingEvent;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentSynchronizedEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SynchronizationErrorResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.adapter.WebSocketNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.SynchronizationCompletedNotificationEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.SynchronizationIsInProgressNotificationEvent;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusSynchronizationEventConsumer {

  private final SynchronizationFacade synchronizationFacade;

  private final WebSocketNotifier webSocketNotifier;

  public void environmentIsSynchronizing(@ObservesAsync EnvironmentIsSynchronizingEvent event) {
    try {
      var externalEvent =
          SynchronizationIsInProgressNotificationEvent.builder()
              .environmentId(event.environmentId())
              .build();
      webSocketNotifier.notifySubscribers(externalEvent);

      var processCommand =
          CommonCommand.builder()
              .environmentId(event.environmentId())
              .username(event.username())
              .build();
      synchronizationFacade.processSynchronization(processCommand);
    } catch (CustomException exception) {
      logError("process", event.environmentId(), exception.getDescription());
    } catch (Exception exception) {
      logError("process", event.environmentId(), exception.getMessage());
    }
  }

  public void environmentSynchronized(@ObservesAsync EnvironmentSynchronizedEvent event) {
    try {
      var syncErrors = SynchronizationErrorResponse.fromDomain(event.synchronizationErrors());
      var externalEvent =
          SynchronizationCompletedNotificationEvent.builder()
              .environmentId(event.environmentId())
              .syncErrors(syncErrors)
              .build();
      webSocketNotifier.notifySubscribers(externalEvent);
    } catch (CustomException exception) {
      logError("finish", event.environmentId(), exception.getDescription());
    } catch (Exception exception) {
      logError("finish", event.environmentId(), exception.getMessage());
    }
  }

  private static void logError(String action, EnvironmentId environmentId, String message) {
    log.error(
        "Error during {} synchronization for Environment id [{}] : {}.",
        action,
        environmentId.value(),
        message);
  }
}
