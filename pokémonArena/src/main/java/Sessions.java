import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import javax.ws.rs.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Path("/session/{username}")
public class Sessions {
    //load data
    //register user
    //find match
}