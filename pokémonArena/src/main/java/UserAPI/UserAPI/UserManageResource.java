package UserAPI.UserAPI;

import PokeKotlinAPI.GetPokeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.*;


@Path("users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UserManageResource {

    private static final Logger LOGGER = Logger.getLogger(UserManageResource.class.getName());

    @Inject
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserManageService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok(toJSON(service.getAllUsers())).status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userName}")
    public Response getSingleUserByName(@PathParam String userName) {
        return Response.ok(toJSON(service.getSingleUserByName(userName))).status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teams/{userID}")
    public Response getPokeTeamFromUser(@PathParam Integer userID) {
        return Response.ok(toJSON(service.getPokeTeamFromUser(userID))).status(200).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/{pokeTeamID}")
    public Response getPokeTeam(@PathParam Integer pokeTeamID) {
        return Response.ok(toJSON(service.getPokeTeam(pokeTeamID))).status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("pokemon/{entryID}")
    public Response getPokemonInfos(@PathParam Integer entryID) {
        return Response.ok(toJSON(service.getPokemonInfos(entryID))).status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("attacks/{entryID}")
    public Response getAttacksFromPokemon(@PathParam Integer entryID) {
        return Response.ok(toJSON(service.getAttacksFromPokemon(entryID))).status(200).build();
    }

    @POST
    @Transactional
    public Response createUserWithTeam(Users user) {           //User name must be set... in POST-Body^^
        return Response.ok(toJSON(service.createUserWithTeam(user))).status(201).build();
    }

    @POST
    @Path("{userID}/addTeam")
    @Transactional
    public Response createTeamForUser(@PathParam Integer userID) {
        return Response.ok(toJSON(service.createTeamForUser(userID))).status(201).build();
    }

    @POST
    @Path("addPokemonToTeam/{teamID}")
    @Transactional
    public Response addPokemonToTeam(@PathParam Integer teamID, Pokemon pokemon) {                  //Pokemon Object in POST-Body must have pokemonID set!
        return Response.ok(toJSON(service.addPokemonToTeam(teamID,pokemon))).status(201).build();
    }

    @PUT
    @Path("/id/{userID}")               //Gets UserName-Update from POST-Body
    @Transactional
    public Response updateUserNameByID(@PathParam Integer userID, Users users) {
        return Response.ok(toJSON(service.updateUserNameByID(userID, users))).status(200).build();
    }

    @PUT
    @Path("/attacksToPokemon/{entryID}")              //entryID of Pokemon
    @Transactional
    public Response updatePokemonAttack(@PathParam Integer entryID, Integer[] attackArray) {         //attackArray must contain 4 entries with attackNumbers
        return Response.ok(toJSON(service.updatePokemonAttack(entryID, attackArray))).status(200).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam Integer id) {
        return service.deleteUser(id);
    }

    @DELETE
    @Path("/pokemon/{entryID}")
    @Transactional
    public Response deletePokemon(@PathParam Integer entryID) {
        return service.deletePokemon(entryID);
    }

    @DELETE
    @Path("/team/{teamID}")
    @Transactional
    public Response deletePokeTeam(@PathParam Integer teamID) {
        return service.deletePokeTeam(teamID);
    }


    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("exceptionType", exception.getClass().getName())
                    .add("code", code);

            if (exception.getMessage() != null) {
                entityBuilder.add("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(entityBuilder.build())
                    .build();
        }
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
