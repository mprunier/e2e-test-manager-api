package fr.njj.galaxion.endtoendtesting.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.njj.galaxion.endtoendtesting.domain.event.Event;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@ServerEndpoint("/events/environments/{environment_id}")
public class WebSocketEvents {

    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Session>> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("environment_id") String environmentId) {
        sessionMap.computeIfAbsent(environmentId, k -> new CopyOnWriteArrayList<>()).add(session);
        log.trace("New session opened: [{}] on Environment ID [{}]", session.getId(), environmentId);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("environment_id") String environmentId) {
        log.trace("Message from {} on Environment ID [{}] : {}", session.getId(), environmentId, message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("environment_id") String environmentId) {
        var sessions = sessionMap.get(environmentId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionMap.remove(environmentId);
            }
        }
        log.trace("Session closed: {} on environment ID [{}]", session.getId(), environmentId);
    }

    @OnError
    public void onError(Session session, Throwable throwable, @PathParam("environment_id") String environmentId) {
        onClose(session, environmentId);
        log.trace("Error on session {} on Environment ID [{}] : {}", session.getId(), environmentId, throwable.getMessage());
    }

    public static void sendEventToEnvironmentSessions(String environmentId, Event event) {
        log.trace("Send event [{}] on environment [{}].", event.getClass(), environmentId);
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String message;
        try {
            message = mapper.writeValueAsString(event);
        } catch (Exception e) {
            log.error("Error converting message to JSON", e);
            return;
        }

        if (sessionMap.containsKey(environmentId)) {
            CopyOnWriteArrayList<Session> sessions = sessionMap.get(environmentId);
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            }
        } else {
            log.trace("No session on environment [{}]", environmentId);
        }
    }

}
