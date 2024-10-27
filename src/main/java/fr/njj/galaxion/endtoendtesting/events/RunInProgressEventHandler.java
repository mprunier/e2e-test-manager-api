package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.event.send.AllTestsPipelinesUpdatedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.send.RunInProgressEvent;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RetrieveAllTestsPipelinesUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunInProgressEventHandler {

  private final RetrieveAllTestsPipelinesUseCase retrieveAllTestsPipelinesUseCase;

  public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) RunInProgressEvent event) {
    try {
      sendEventToEnvironmentSessions(event);
      buildAndSendUpdateAllTestsPipelinesEvent(event.getEnvironmentId());
    } catch (Exception e) {
      log.error("Error while sending event", e);
    }
  }

  private void buildAndSendUpdateAllTestsPipelinesEvent(Long environmentId) {
    var pipelines = retrieveAllTestsPipelinesUseCase.execute(environmentId);
    var updateAllTestsPipelinesEvent =
        AllTestsPipelinesUpdatedEvent.builder()
            .environmentId(environmentId)
            .pipelines(pipelines)
            .build();
    sendEventToEnvironmentSessions(updateAllTestsPipelinesEvent);
  }
}
