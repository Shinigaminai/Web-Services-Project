package challenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.ChallengeEvent;
import events.EventGetChallengers;
import events.SessionEvent;
import fight.Arena;
import fight.PokemonJson;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.EventBus;
import sessions.SessionsContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/challenge/{username}")
public class ChallengeSocket {

    //static final Map<String, Class<? extends ChallengeEvent>> EVENT_CLASSES = new HashMap<String,Class<? extends ChallengeEvent>>() {{
    //    put( "getChallengers", EventGetChallengers.class);
    //}};
    SessionsContainer sessions = new SessionsContainer();
    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    EventBus bus;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Challenge] User connect: " + username);
        sessions.add(username, session);
        sessions.broadcast("userconnect",
            new HashMap<String, String>(){{
                put("user",username);
                put("action","joined");
            }}
            //Map.of("user", username, "action", "joined")
        );
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Challenge] User disconnect: " + username);
        sessions.remove(username);
        sessions.broadcast("userconnect",
            new HashMap<String, String>(){{
                put("user",username);
                put("action","left");
            }}
            //Map.of("user", username, "action", "left")
        );
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Challenge] User error [" + username + "] : " + throwable.toString());
        sessions.remove(username);
        sessions.broadcast("userconnect",
            new HashMap<String, String>(){{
                put("user",username);
                put("action","left");
            }}
            //Map.of("user", username, "action", "left")
        );
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        System.out.println("[Challenge] message by " + username + ": " + message);
        try {
            JsonNode eventNode = mapper.readTree(message);
            String eventName = eventNode.get("event").asText();
            SessionEvent event = new SessionEvent(eventName, username, eventNode.get("data"));
            bus.send(eventName, event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void receivedEvent(EventGetChallengers event) {
        System.out.println("load challengers");
        sessions.get().keySet().forEach(user -> {
            sessions.send(user, "userconnect",
                new HashMap<String, String>(){{
                    put("user",user);
                    put("action","joined");
                }}
                //Map.of("user", user, "action", "joined")
            );
        });
    }

    @ConsumeEvent("challenge")
    private void receivedEvent(SessionEvent event) {
        System.out.println("new Challenge");
        String user = event.getData().get("to").asText();
        sessions.send(user, "challenge",
            new HashMap<String, String>(){{
                put("from", event.getUser());
            }}
            //Map.of("from", username)
        );
    }
            if (event.get("event").equals("challenge")) {
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
}
