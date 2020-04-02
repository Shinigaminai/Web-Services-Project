package sessions;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class SessionsContainer {
    private Map<String, Session> sessions = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    public void addSession(String key, Session session) {
        this.sessions.put(key, session);
    }
    public Session getSession(String key) {
        return sessions.get(key);
    }
    public void removeSession(String key) {
        sessions.remove(key);
    }

    public void send(String username, String message) {
        if (sessions.get(username) != null) {
            send(sessions.get(username), message);
        } else {
            System.out.println("tried to send message to non existant user " + username + ": " + message);
        }
    }
    public void send(Session session, String message) {
        session.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }
    public void send(String username, String event, Object data) {
        try {
            String message = createMessage(event, data);
            send(username, message);
        } catch (Exception e) {
        }
    }

    public void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
    public void broadcast(String event, Object data) {
        broadcast(createMessage(event, data));
    }
    public String createMessage(String event, Object data) {
        try {
            return "{\"event\":\"" + event + "\",\"data\":" + mapper.writeValueAsString(data) + "}";
        } catch (Exception e) {
            return null;
        }
    }
}
