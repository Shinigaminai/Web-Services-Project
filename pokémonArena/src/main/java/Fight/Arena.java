package Fight;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class Arena {
    protected Map<String, Session> sessions = new HashMap<>(); //user, session

    protected void send(String username, String message) {
        if (sessions.get(username) != null) {
            sessions.get(username).getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        } else {
            System.out.println("tried to send message to non existant user " + username + ": " + message);
        }
    }

    protected void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
