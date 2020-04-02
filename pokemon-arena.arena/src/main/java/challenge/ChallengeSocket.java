package challenge;

import events.ChallengeEvent;
import sessions.SessionsContainer;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@ServerEndpoint("/arena/challenge/{username}")
public class ChallengeSocket {
    static final Map<String, Class<? extends ChallengeEvent>> EVENT_CLASSES = new HashMap<String,Class<? extends ChallengeEvent>>() {{
        put( "getChallengers", events.getChallengersEvent.class);
    }};
    SessionsContainer sessions = new SessionsContainer();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("[i][Challenge] User connect: " + username);
        sessions.addSession(username, session);
        sessions.broadcast(username,
                new HashMap<String, String>(){{
                    put("user",username);
                    put("action","joined");
                }}
                //Map.of("user", username, "action", "joined")
        );
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[i][Fight.fight.Arena] User disconnect: " + username);
        sessions.remove(username);
        broadcast(createMessage("userconnect",
                new HashMap<String, String>(){{
                    put("user",username);
                    put("action","left");
                }}
                //Map.of("user", username, "action", "left")
        ));
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("[E][Fight.fight.Arena] User error [" + username + "] : " + throwable.toString());
        sessions.remove(username);
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
    public void onMessage(String message, @PathParam("username") String username) {
}
