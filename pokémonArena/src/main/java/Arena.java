import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

public class Arena {
    Map<String, Session> sessions = new HashMap<>(); //user, session
}
