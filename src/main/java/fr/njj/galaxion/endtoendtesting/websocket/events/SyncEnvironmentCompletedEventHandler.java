package fr.njj.galaxion.endtoendtesting.websocket.events;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncEnvironmentCompletedEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.cache.CleanCacheAfterSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.error.RetrieveErrorUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SyncEnvironmentCompletedEventHandler {

    private final RetrieveErrorUseCase retrieveErrorUseCase;
    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CleanCacheAfterSynchronizationUseCase cleanCacheAfterSynchronizationUseCase;

    @Transactional
    @Monitored
    public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) SyncEnvironmentCompletedEvent event) {
        try {
            var allErrors = retrieveErrorUseCase.execute(event.getEnvironmentId());
            event.setSyncErrors(allErrors);

            var environmentResponse = environmentRetrievalService.getEnvironmentResponse(event.getEnvironmentId());
            event.setEnvironment(environmentResponse);

            cleanCacheAfterSynchronizationUseCase.execute(event.getEnvironmentId());

            sendEventToEnvironmentSessions(event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}

