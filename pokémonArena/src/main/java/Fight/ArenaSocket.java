package Fight;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Message;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/Arena/{username}")

public class ArenaSocket {
    private static final Logger LOG = Logger.getLogger(ArenaSocket.class);

    Map<String, Session> sessions = new HashMap<>();      // only temporary users necessary

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        broadcast();
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast();
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        LOG.error("onError", throwable);
        broadcast();
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        broadcast();
    }

    private void broadcast() {
        sessions.forEach((k, v) -> {
            v.getAsyncRemote().sendObject(k, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
