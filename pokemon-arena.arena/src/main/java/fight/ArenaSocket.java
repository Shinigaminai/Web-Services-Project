package fight;

import challenge.ChallengeSocket;
import events.ArenaEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.ChallengeEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/{arenaID}")
public class ArenaSocket {

    private static final Logger LOG = Logger.getLogger(ArenaSocket.class);
    Map<String,Arena> arenas = new HashMap<>();
    ChallengeSocket challengeSocket = new ChallengeSocket();

    static final Map<String, Class<? extends ArenaEvent>> EVENT_CLASSES_ARENA = new HashMap<String,Class<? extends ArenaEvent>>() {{
        put( "getChallengers", events.irgendwas.class);
    }};

    @OnOpen
    public void onOpen(Session session, @PathParam("arenaID") String arenaID) {
        System.out.println("[i][Fight.fight.Arena] User connect: " + arenaID);
        sessions.put(arenaID, session);
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
            put("user",arenaID);
            put("action","joined");
                }}
                //Map.of("user", arenaID, "action", "joined")
        ));
    }

    @OnClose
    public void onClose(Session session, @PathParam("arenaID") String arenaID) {
        System.out.println("[i][Fight.fight.Arena] User disconnect: " + arenaID);
        sessions.remove(arenaID);
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
                    put("user",username);
                    put("action","left");
                }}
                //Map.of("user", username, "action", "left")
                ));
    }

    @OnError
    public void onError(Session session, @PathParam("arenaID") String arenaID, Throwable throwable) {
        System.out.println("[E][Fight.fight.Arena] User error [" + arenaID + "] : " + throwable.toString());
        sessions.remove(arenaID);
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
                    put("user",username);
                    put("action","left");
                }}
                //Map.of("user", username, "action", "left")
        ));
        LOG.error("onError", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("arenaID") String arenaID) {
        System.out.println("Fight.fight.ArenaSocket message by " + arenaID + ": " + message);
        try {
            JsonNode eventNode = mapper.readTree(message);
            String event = eventNode.get("event").asText();

            challengeSocket.receivedEvent(eventNode);
            receivedEvent(mapper.treeToValue(eventNode, EVENT_CLASSES_CHALLENGE.get(event)));

            if (event.get("event").equals("getChallengers")) {
                System.out.println("load challengers");
                sessions.keySet().forEach(user -> {
                    send(arenaID, createMessage("userconnect",
                            new HashMap<String, String>(){{
                                put("user",user);
                                put("action","joined");
                            }}
                            //Map.of("user", user, "action", "joined")
                    ));
                });
            }
            if (event.get("event").equals("challenge")) {
                System.out.println("new Challenge");
                String user = event.get("data").get("to").asText();
                send(user, createMessage("challenge",
                        new HashMap<String, String>(){{
                            put("from",username);
                        }}
                        //Map.of("from", username)
                ));
            }
            if (event.get("event").equals("answerChallenge")) {
                System.out.println("Challenge answered");
                String user = event.get("data").get("to").asText();
                String answer = event.get("data").get("value").asText();
                send(user, createMessage("answerChallenge",
                        new HashMap<String, String>(){{
                            put("from",username);
                            put("value",answer);
                        }}
                       // Map.of("from", username, "value", answer)
                ));
                if(answer.equals("accept")){
                    createRoom(user, username);
                }
            }
            if (event.get("event").equals("cancelChallenge")) {
                System.out.println("Challenge canceled");
                String user = event.get("data").get("to").asText();
                send(user, createMessage("cancelChallenge",
                        new HashMap<String, String>(){{
                            put("from",username);
                        }}
                        //Map.of("from", username)
                ));
            }
            if(event.get("event").equals("selectPokemon")){
                arenas.get(username).sendSelectPokemon(event,username);
            }
            if(event.get("event").equals("selectMove")) {
                arenas.get(username).setCurrentMove(event.get("data").get("moveID"), username);
            }
            if(event.get("event").equals("userInfo")){
                user.put(username,Integer.valueOf(event.get("data").get("userID")));;

                ObjectMapper objectMapper = new ObjectMapper();
                String json = event.get("data").get("team");
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

                String arenaKey = event.get("data").get("arena");
                setUserInformation(arenaKey,username);
                Arena arena = arenas.get(arenaKey);
                String opponent = arena.setOpponentInfo(username);
                arena.send(opponent,createMessage("opponentInfo",event.get("data")));
            }
            if(event.get("event").equals("surrender")){
                arenas.get(username).sendSurrender(username);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void receivedEvent(ArenaEvent event) {

    }

    private void receivedEvent(ChallengeEvent event) {
        challengeSocket.receivedEvent(event);
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
        String message = createMessage("assignedArena",
                new HashMap<String, String>(){{
                    put("arena",key);
                }}
               // Map.of("arena", key)
        );
        arena.send(user1, message);
        arena.send(user2, message);
        sessions.remove(user1);
        sessions.remove(user2);
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
                    put("user",user1);
                    put("action","fight");
                }}
               // Map.of("user", user1, "action", "fight")
        ));
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
                    put("user",user2);
                    put("action","fight");
                }}
               // Map.of("user", user2, "action", "fight")
        ));
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
