package PokeKotlinAPI;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pokeData")
public class GetPokeResource {

    @Inject
    GetPokeService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pokedex/{id}")
    public String getPokedex(@PathParam int id) { return service.getPokedex(id).toString();}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pokemonList")
    public String getPokemonList() { return service.getPokemonList().toString();}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pokemonSpecies/{id}")
    public String getPokemonSpecies(@PathParam int id) {
        return service.getPokemonSpecies(id).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pokemon/{id}")
    public String getPokemon(@PathParam int id) {
        return service.getPokemon(id).toString();
    }
}
