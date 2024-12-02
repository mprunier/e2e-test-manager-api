package fr.plum.e2e.manager.core.infrastructure.primary.websocket;

import fr.plum.e2e.manager.sharedkernel.infrastructure.websocket.WebSocketSessionRegistry;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServerEndpoint("/consumer/environments/{environment_id}")
public class EnvironmentSubscriptionEndpoint {
  private final WebSocketSessionRegistry sessionRegistry;

  public EnvironmentSubscriptionEndpoint(WebSocketSessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  @OnOpen
  public void onSubscribe(Session session, @PathParam("environment_id") String environmentId) {
    sessionRegistry.registerSession(environmentId, session);
    log.debug(
        "New subscriber for Environment ID [{}]. Total subscribers: {}",
        environmentId,
        sessionRegistry.getTotalSessionCount());
  }

  @OnClose
  public void onUnsubscribe(Session session, @PathParam("environment_id") String environmentId) {
    sessionRegistry.removeSession(environmentId, session);
    log.debug(
        "Subscriber left for Environment ID [{}]. Total subscribers: {}",
        environmentId,
        sessionRegistry.getTotalSessionCount());
  }

  @OnError
  public void onError(
      Session session, Throwable throwable, @PathParam("environment_id") String environmentId) {
    log.error(
        "Error on session {} for Environment ID [{}] : {}",
        session.getId(),
        environmentId,
        throwable.getMessage());
    onUnsubscribe(session, environmentId);
  }
}
