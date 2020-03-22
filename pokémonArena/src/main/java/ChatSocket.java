import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/chat/{username}")
public class ChatSocket {
    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = Logger.getLogger(ChatSocket.class);

    Map<String, Session> sessions = new ConcurrentHashMap<>();      // only temporary users necessary

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Chat] User connect: " + username);
        sessions.put(username, session);
        broadcast(createMessage("userconnect", Map.of("user",username, "action", "joined")));
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Arena] User disconnect: " + username);
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user",username, "action", "left")));
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Arena] User error: " + username);
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user",username, "action", "left")));
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        if (message.equalsIgnoreCase("_ready_")) {
            //broadcast("User " + username + " joined");
        } else {
            // no interpretation necessary
            //TODO sender could be fake
            broadcast(message);
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

    private String createMessage(String event, Map<String, String> arguments) {
        try {
            return "{\"event\":\""+ event +"\",\"data\":" + mapper.writeValueAsString(arguments) + "}";
        } catch (Exception e) {
            return null;
        }
    }
}
