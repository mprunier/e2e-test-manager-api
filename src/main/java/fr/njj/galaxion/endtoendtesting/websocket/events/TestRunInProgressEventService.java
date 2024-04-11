package fr.njj.galaxion.endtoendtesting.websocket.events;

import fr.njj.galaxion.endtoendtesting.domain.event.TestRunInProgressEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEvents.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestRunInProgressEventService {

    @Transactional
    public void send(Long environmentId,
                     Long testId,
                     Long suiteId) {
        try {
            var event = TestRunInProgressEvent
                    .builder()
                    .testId(testId)
                    .suiteId(suiteId)
                    .build();
            sendEventToEnvironmentSessions(environmentId.toString(), event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}