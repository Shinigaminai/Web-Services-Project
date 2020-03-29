package Fight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            if(event.getEvent().equals("selectMove")){
                arenas.get(username).setCurrentMove(event.getData().get("move"),username);
            }
            if(event.getEvent().equals("userInfo")){
                user.put(username,Integer.valueOf(event.getData().get("userID")));;

                ObjectMapper objectMapper = new ObjectMapper();
                String json = event.getData().get("team");
                PokemonJson[] pokemonList = objectMapper.readValue(json,PokemonJson[].class);
                List<Integer> entryList = new ArrayList<>();
                for(PokemonJson p : pokemonList){
                    entryList.add(p.entryID);
                    pokeID.put(p.entryID,p.pokemonID);
                    List<Integer> moveID = new ArrayList<>();
                    for(Integer i : p.attacks){
                        moveID.add(i);
                    }
                    moveIDList.put(p.entryID,moveID);
                }
                entryIDMap.put(username,entryList);

                String arenaKey = event.getData().get("arena");
                setUserInformation(arenaKey,username);
                Arena arena = arenas.get(arenaKey);
                String opponent = arena.setOpponentInfo(username);
                arena.send(opponent,createMessage("opponentInfo",event.getData()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static Map<String,Integer> user = new HashMap<>(); //username,UserID
    public static Map<String,List<Integer>> entryIDMap = new HashMap<>(); //username, PkmEntryID
    public static Map<Integer,Integer> pokeID = new HashMap<>(); //PkmEntryID, PokeID
    public static Map<Integer, List<Integer>> moveIDList = new HashMap<>(); //entryID, MoveIDList
    private static Map<String,String> userInformationSet = new HashMap<>(); //user, ArenaKey

    private void createRoom(String user1, String user2) {
        Arena arena = new Arena();
        String key = user1 + "-arena-" + user2;
        arena.sessions.put(user1, sessions.get(user1));
        arena.sessions.put(user2, sessions.get(user2));
        arenas.put(key, arena);
        arenas.put(user1,arena);
        arenas.put(user2,arena);
        String message = createMessage("assignedArena", Map.of("arena", key));
        arena.send(user1, message);
        arena.send(user2, message);
        sessions.remove(user1);
        sessions.remove(user2);
        broadcast(createMessage("userconnect", Map.of("user", user1, "action", "fight")));
        broadcast(createMessage("userconnect", Map.of("user", user2, "action", "fight")));
    }

    private void setUserInformation(String arenaKey,String user) {
        Arena arena = arenas.get(arenaKey);
        userInformationSet.put(user,arenaKey);
        Integer numberOfInformationSet = 0;
        for(String users : userInformationSet.keySet()){
            if(userInformationSet.get(users).equals(arenaKey)){
                numberOfInformationSet++;
            }
        }
        for (Integer p : pokeID.keySet()) {
            for (Integer i : entryIDMap.get(user)) {
                if (p.equals(i)) {
                    arena.allPkm.put(p, new Pokemon(p, user, pokeID.get(p)));
                }
            }
        }

        if(numberOfInformationSet==2){
            arena.loadAllPokemonData();
            arena.setFighter2(user);
        }
        if(numberOfInformationSet==1) {
            arena.setFighter1(user);
        }
    }
    }
