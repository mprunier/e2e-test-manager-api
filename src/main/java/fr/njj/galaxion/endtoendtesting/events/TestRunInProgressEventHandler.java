package fr.njj.galaxion.endtoendtesting.events;

import fr.njj.galaxion.endtoendtesting.domain.event.TestRunInProgressEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestRunInProgressEventHandler {

    public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) TestRunInProgressEvent event) {
        try {
            sendEventToEnvironmentSessions(event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}