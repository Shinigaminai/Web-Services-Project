import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/{username}")

public class ArenaSocket {
    private static final Logger LOG = Logger.getLogger(ArenaSocket.class);
    Map<String, Session> sessions = new HashMap<>();      // only temporary users necessary
    private ObjectMapper mapper = new ObjectMapper();
    Map<Integer,Arena> arenas = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Arena] User connect: " + username);
        sessions.put(username, session);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "joined")));
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Arena] User disconnect: " + username);
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "left")));
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Arena] User error [" + username + "] : " + throwable.toString());
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "left")));
        LOG.error("onError", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        System.out.println("ArenaSocket message by " + username + ": " + message);
        try {
            JsonEvent event = mapper.readValue(message, JsonEvent.class);
            //System.out.println("event: " + event.getEvent());
            //System.out.println("data:  " + event.getData());
            if (event.getEvent().equals("getChallengers")) {
                System.out.println("load challengers");
                sessions.keySet().forEach(user -> {
                    send(username, createMessage("userconnect", Map.of("user", user, "action", "joined")));
                });
            }
            if (event.getEvent().equals("challenge")) {
                System.out.println("new Challenge");
                String user = event.getData().get("to");
                send(user, createMessage("challenge", Map.of("from", username)));
            }
            if (event.getEvent().equals("answerChallenge")) {
                System.out.println("Challenge answered");
                String user = event.getData().get("to");
                String answer = event.getData().get("value");
                send(user, createMessage("answerChallenge", Map.of("from", username, "value", answer)));
                if(answer.equals("accept")){
                    createRoom(user, username);
                }
            }
            if (event.getEvent().equals("cancelChallenge")) {
                System.out.println("Challenge canceled");
                String user = event.getData().get("to");
                send(user, createMessage("cancelChallenge", Map.of("from", username)));
            }
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

    private void send(String username, String message) {
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

    private String createMessage(String event, Map<String, String> arguments) {
        try {
            return "{\"event\":\"" + event + "\",\"data\":" + mapper.writeValueAsString(arguments) + "}";
        } catch (Exception e) {
            return null;
        }
    }

    private String createMessage(JsonEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            return null;
        }
    }

    private void createRoom(String user1, String user2){
        Integer n = 1;
        for(Map.Entry<Integer,Arena> a : arenas.entrySet()){
            n++;
        }
        Arena arena = new Arena();
        arena.sessions.put(user1,sessions.get(user1));
        arena.sessions.put(user2,sessions.get(user2));
        arenas.put(n,arena);
    }
}
