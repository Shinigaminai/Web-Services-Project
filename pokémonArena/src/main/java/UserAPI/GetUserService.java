package UserAPI;

import com.fasterxml.jackson.databind.util.JSONPObject;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokedex;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

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
