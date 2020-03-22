import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/{username}")

public class ArenaSocket {
    private static final Logger LOG = Logger.getLogger(ArenaSocket.class);
    private ObjectMapper mapper = new ObjectMapper();

    Map<String, Session> sessions = new HashMap<>();      // only temporary users necessary

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Arena] User connect: " + username);
        sessions.put(username, session);
        JsonEvent event = new JsonEvent("userconnect", Map.of("user",username,"action","joined"));
        try {
            broadcast(mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Arena] User disconnect: " + username);
        sessions.remove(username);
        JsonEvent event = new JsonEvent("userconnect", Map.of("user",username,"action","left"));
        try {
            broadcast(mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Arena] User error: " + username);
        sessions.remove(username);
        LOG.error("onError", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        System.out.println("ArenaSocket message by " + username + ": " + message);
        try {
            JsonEvent event = mapper.readValue(message, JsonEvent.class);
            System.out.println("event: " + event.event);
            System.out.println("data:  " + event.data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
