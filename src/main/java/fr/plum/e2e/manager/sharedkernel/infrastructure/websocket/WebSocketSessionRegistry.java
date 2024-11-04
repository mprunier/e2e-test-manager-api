package fr.plum.e2e.manager.sharedkernel.infrastructure.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class WebSocketSessionRegistry {
  private final ConcurrentHashMap<String, CopyOnWriteArrayList<Session>> sessionMap =
      new ConcurrentHashMap<>();

  public void registerSession(String environmentId, Session session) {
    sessionMap.computeIfAbsent(environmentId, k -> new CopyOnWriteArrayList<>()).add(session);
  }

  public void removeSession(String environmentId, Session session) {
    var sessions = sessionMap.get(environmentId);
    if (sessions != null) {
      sessions.remove(session);
      if (sessions.isEmpty()) {
        sessionMap.remove(environmentId);
      }
    }
  }

  public List<Session> getSessionsForEnvironment(String environmentId) {
    return sessionMap.getOrDefault(environmentId, new CopyOnWriteArrayList<>());
  }

  public int getTotalSessionCount() {
    return sessionMap.values().stream().mapToInt(CopyOnWriteArrayList::size).sum();
  }
}
