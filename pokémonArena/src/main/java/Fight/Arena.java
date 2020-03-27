package Fight;

import UserAPI.UserAPI.UserManageResource;
import UserAPI.UserAPI.Users;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.MapKey;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class Arena {
    protected Map<String, Session> sessions = new HashMap<>(); //user, session
    protected ObjectMapper mapper = new ObjectMapper();
    private Map<String,Pokemon> currentPkm = new HashMap<>(); //user, currentPkm
    protected Map<Integer,Pokemon> allPkm = new HashMap<>(); //entryID, Pokemon

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
    public String createMessage(String event, Map<String, String> arguments) {
        try {
            return "{\"event\":\"" + event + "\",\"data\":" + mapper.writeValueAsString(arguments) + "}";
        } catch (Exception e) {
            return null;
        }
    }

    public String createMessage(JsonEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            return null;
        }
    }
    protected void sendOpponentInfo(String username){
        String opponent ="";
        for(String u : sessions.keySet()){
            if(u!=username){
                opponent = u;
            }
        }
       Users user = UserManageService.getSingleUserByName(opponent);
        send(username, createMessage("opponentInfo", Map.of("name",opponent,"userID", user.getUserID().toString(),
                "teamID",user.getPokeTeamList().get(0).getPokeTeamID().toString())));
    }
    protected void sendSelectPokemon(JsonEvent event, String from){
        boolean accept;
        Integer pkm = Integer.parseInt(event.getData().get("entryID"));
        Pokemon pokemon = new Pokemon(pkm,from);
        if(allPkm.get(pkm) != null){

        }
        currentPkm.replace(from,pokemon);
        send(from,createMessage("selectPokemon",Map.of("entryID",pkm.toString(),"status","accept")));
    }
}
