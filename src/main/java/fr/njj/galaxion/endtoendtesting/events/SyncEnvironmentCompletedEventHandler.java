package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.event.send.SyncEnvironmentCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentErrorUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.UnLockEnvironmentSynchronizationUseCase;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SyncEnvironmentCompletedEventHandler {

  private final RetrieveEnvironmentErrorUseCase retrieveEnvironmentErrorUseCase;
  private final EnvironmentRetrievalService environmentRetrievalService;
  private final UnLockEnvironmentSynchronizationUseCase unLockEnvironmentSynchronizationUseCase;
  private final CacheManager cacheManager;

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) SyncEnvironmentCompletedEvent event) {
    try {
      var allErrors = retrieveEnvironmentErrorUseCase.execute(event.getEnvironmentId());
      event.setSyncErrors(allErrors);

      unLockEnvironmentSynchronizationUseCase.execute(event.getEnvironmentId());

      var environmentResponse = environmentRetrievalService.getResponse(event.getEnvironmentId());
      event.setEnvironment(environmentResponse);

      cacheManager
          .getCache("suites")
          .ifPresent(cache -> cache.invalidate(event.getEnvironmentId()).await().indefinitely());
      cacheManager
          .getCache("tests")
          .ifPresent(cache -> cache.invalidate(event.getEnvironmentId()).await().indefinitely());
      cacheManager
          .getCache("files")
          .ifPresent(cache -> cache.invalidate(event.getEnvironmentId()).await().indefinitely());
      cacheManager
          .getCache("tags")
          .ifPresent(cache -> cache.invalidate(event.getEnvironmentId()).await().indefinitely());

      sendEventToEnvironmentSessions(event);
    } catch (Exception e) {
      log.error("Error while sending event", e);
    }
  }
}
