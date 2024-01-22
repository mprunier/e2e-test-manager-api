package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunInProgressEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AllTestsRunInProgressEventHandler {

  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) AllTestsRunInProgressEvent event) {
    try {
      sendEventToEnvironmentSessions(event);
    } catch (Exception e) {
      log.error("Error while sending event", e);
    }
  }
}
