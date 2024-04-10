package fr.njj.galaxion.endtoendtesting.websocket.events;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncErrorsEvent;
import fr.njj.galaxion.endtoendtesting.usecases.error.RetrieveErrorUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEvents.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SyncErrorEventService {

    private final RetrieveErrorUseCase retrieveErrorUseCase;

    @Transactional
    public void send(Long environmentId) {
        try {
            var allErrors = retrieveErrorUseCase.execute(environmentId);
            var event = SyncErrorsEvent
                    .builder()
                    .syncErrors(allErrors)
                    .build();
            sendEventToEnvironmentSessions(environmentId.toString(), event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}

