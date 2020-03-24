package UserAPI.OldStuff;

import com.fasterxml.jackson.databind.util.JSONPObject;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetUserService {

    public GetUserService() {
    }

    public JSONPObject getUser(String name) {
        //TODO: load UserData of specific user from persistence if already exists
        //TODO: User Stats, user's pokémon team, etc..
        return null;
    }
}
