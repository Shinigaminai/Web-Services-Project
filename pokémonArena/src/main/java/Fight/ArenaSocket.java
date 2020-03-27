package Fight;

import UserAPI.UserAPI.UserManageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/{username}")
public class ArenaSocket extends Arena {

    private static final Logger LOG = Logger.getLogger(ArenaSocket.class);
    //Map<String, Session> sessions = new HashMap<>();      // only temporary users necessary
    Map<String,Arena> arenas = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Fight.Arena] User connect: " + username);
        sessions.put(username, session);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "joined")));
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Fight.Arena] User disconnect: " + username);
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "left")));
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Fight.Arena] User error [" + username + "] : " + throwable.toString());
        sessions.remove(username);
        broadcast(createMessage("userconnect", Map.of("user", username, "action", "left")));
        LOG.error("onError", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        System.out.println("Fight.ArenaSocket message by " + username + ": " + message);
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
            if(event.getEvent().equals("selectPokemon")){
                arenas.get(username).sendSelectPokemon(event,username);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private void createRoom(String user1, String user2){
        Arena arena = new Arena();
        String key = user1 + "-arena-" + user2;
        arena.sessions.put(user1,sessions.get(user1));
        arena.sessions.put(user2,sessions.get(user2));
        arenas.put(key,arena);
        String message = createMessage("assignedArena", Map.of("arena",key));
        arena.send(user1, message);
        arena.send(user2, message);
        sessions.remove(user1);
        sessions.remove(user2);
        broadcast(createMessage("userconnect", Map.of("user", user1, "action", "fight")));
        broadcast(createMessage("userconnect", Map.of("user", user2, "action", "fight")));
        sendOpponentInfo(user1);
        sendOpponentInfo(user2);
        Integer userI1 = userManageService.getSingleUserByName(user1).getUserID();
        Integer pokeTeam1 = userManageService.getPokeTeamFromUser(userI1).get(0);
        Integer userI2 = userManageService.getSingleUserByName(user2).getUserID();
        Integer pokeTeam2 = userManageService.getPokeTeamFromUser(userI2).get(0);

        for(Map<String,Integer> p : userManageService.getPokeTeam(pokeTeam1)){
         arena.allPkm.put(p.get("entryID"),new Pokemon(p.get("entryID"),user1));
        }
        for(Map<String,Integer> p : userManageService.getPokeTeam(pokeTeam2)){
            arena.allPkm.put(p.get("entryID"),new Pokemon(p.get("entryID"),user2));
        }
    }
}
