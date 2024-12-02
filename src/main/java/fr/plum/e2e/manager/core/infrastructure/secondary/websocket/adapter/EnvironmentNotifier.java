package fr.plum.e2e.manager.core.infrastructure.secondary.websocket.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.AbstractNotificationEvent;
import fr.plum.e2e.manager.sharedkernel.infrastructure.websocket.WebSocketSessionRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentNotifier {
  private static final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private final WebSocketSessionRegistry registry;

  public void notifySubscribers(AbstractNotificationEvent event) {
    log.debug(
        "Send consumer [{}] on environment [{}].",
        event.getClass().getSimpleName(),
        event.getEnvironmentId().value());

    String message;
    try {
      message = objectMapper.writeValueAsString(event);
    } catch (Exception e) {
      log.error("Error converting message to JSON", e);
      return;
    }

    var envId = event.getEnvironmentId().value().toString();
    var sessions = registry.getSessionsForEnvironment(envId);

    sessions.stream()
        .filter(Session::isOpen)
        .forEach(session -> session.getAsyncRemote().sendText(message));
  }
}
