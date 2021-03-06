package PokeKotlinAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pokedata")
public class GetPokeResource {

    @Inject
    GetPokeService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pokedex/{id}")
    public String getPokedex(@PathParam int id) {
        return toJSON(service.getPokedex(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)                       //name, category and id of pokemon
    @Path("/pokemonspecies/{id}")
    public String getPokemonSpecies(@PathParam int id) {
        return toJSON(service.getPokemonSpecies(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)                   //Many information about a pokemon
    @Path("/pokemon/{id}")
    public String getPokemon(@PathParam int id) {
        return toJSON(service.getPokemon(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/type/{id}")
    public String getType(@PathParam int id) {
        return toJSON(service.getType(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/move/{id}")
    public String getMove(@PathParam int id) {
        return toJSON(service.getMove(id));
    }

    public String toJSON(Object o) {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            return null;
        }
    }
}
