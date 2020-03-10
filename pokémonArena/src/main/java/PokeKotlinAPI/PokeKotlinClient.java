package PokeKotlinAPI;


import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;





@Path("/pokeData")
public class PokeKotlinClient {

    @Inject
    GetPokeService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/pokemonSpecies/{id}")
    public String getPokemonSpecies(@PathParam int id) {
        return service.getPokemonSpecies(id).toString();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/pokemon/{id}")
    public String getPokemon(@PathParam int id) {
        return service.getPokemon(id).toString();
    }



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

}
