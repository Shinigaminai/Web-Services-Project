package UserAPI.UserAPI;

import Fight.UserManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    static EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static String getAllUsers() {
        return toJSON(entityManager.createNamedQuery("Users.findAll", Users.class)
                .getResultList()
                .toArray(new Users[0])
        );
    }

    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userID}")
    public String getSingle(@PathParam Integer userID) {
        Users entity = entityManager.find(Users.class, userID);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + userID + " does not exist.", 404);
        }
        return toJSON(entity);
    }*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userName}")
    public static String getSingleUserByName(@PathParam String userName) {
        Users entity = UserManageService.getSingleUserByName(userName);
        return toJSON(entity);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teams/{userID}")
    public static String getPokeTeamFromUser(@PathParam Integer userID) {
        List<Integer> pokeTeamIDList = UserManageService.getPokeTeamFromUser(userID);
        return toJSON(pokeTeamIDList);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/{pokeTeamID}")
    public static String getPokeTeam(@PathParam Integer pokeTeamID) {
        List<Map<String,Integer>> outputSetList = UserManageService.getPokeTeam(pokeTeamID);
        return toJSON(outputSetList);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("attacks/{entryID}")
    public static String getAttacksFromPokemon(@PathParam Integer entryID) {

        Pokemon pokemon = entityManager.find(Pokemon.class,entryID);

        if (pokemon == null) {
            throw new WebApplicationException("Pokemon with'entryID' " + entryID + " does not exist.", 404);
        }

        List <Integer> attackNumberList = new ArrayList<>();

        attackNumberList.add(pokemon.getAttackNumber1());
        attackNumberList.add(pokemon.getAttackNumber2());
        attackNumberList.add(pokemon.getAttackNumber3());
        attackNumberList.add(pokemon.getAttackNumber4());

        return toJSON(attackNumberList);
    }

    @POST
    @Transactional
    public static Response createUserWithTeam(Users user) {           //User name must be set... in POST-Body^^
        if (user.getName() == null) {
            throw new WebApplicationException("Users' Name wasn't set on request.", 422);
        }
        if (user.getUserID() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        Users userFresh = new Users();
        userFresh.setName(user.getName());

        entityManager.persist(userFresh);
        entityManager.flush();


        user = entityManager.find(Users.class, userFresh.getUserID());
        PokeTeam pokeTeamFresh = new PokeTeam();

        pokeTeamFresh.setUser(user);
        //user.getPokeTeamList().add(pokeTeamFresh);                                        //only the owning side needs to be set !!!--> Hibernate also needs to do something ;)lul
        entityManager.persist(pokeTeamFresh);
        return Response.ok(user).status(201).build();
    }

    @POST
    @Path("{userID}/addTeam")
    @Transactional
    public static Response createTeamForUser(@PathParam Integer userID) {

        Users user = entityManager.find(Users.class, userID);

        if(user == null){
            throw new WebApplicationException("User with 'userID' " + userID + " does not exist.", 404);
        }

        PokeTeam pokeTeamFresh = new PokeTeam();
        pokeTeamFresh.setUser(user);
        entityManager.persist(pokeTeamFresh);

        return Response.ok(toJSON(pokeTeamFresh)).status(201).build();
    }

    @POST
    @Path("addPokemonToTeam/{teamID}")
    @Transactional
    public static Response addPokemonToTeam(@PathParam Integer teamID, Pokemon pokemon) {                  //Pokemon Object in POST-Body must have pokemonID set!
        if (pokemon.getPokemonID() == null) {
            throw new WebApplicationException("pokemonID of Pokemon wasn't set on request.", 422);
        }
        if (pokemon.getEntryID() != null) {
            throw new WebApplicationException("entryID was invalidly set on request.", 422);
        }

        PokeTeam pokeTeam = entityManager.find(PokeTeam.class, teamID);

        if(pokeTeam == null){
            throw new WebApplicationException("PokeTeam with 'pokeTeamID' " + teamID + " does not exist.", 404);
        }

        Pokemon pokemonFresh = new Pokemon();
        pokemonFresh.setPokemonID(pokemon.getPokemonID());
        pokemonFresh.setPokeTeam(pokeTeam);
        entityManager.persist(pokemonFresh);

        return Response.ok(toJSON(pokemonFresh)).status(201).build();
    }

    @PUT
    @Path("/id/{userID}")               //Gets UserName-Update from POST-Body
    @Transactional
    public static String updateUserNameByID(@PathParam Integer userID, Users users) {
        if (users.getName() == null) {
            throw new WebApplicationException("Users' Name was not set on request.", 422);
        }

        Users entity = entityManager.find(Users.class, userID);

        if (entity == null) {
            throw new WebApplicationException("User with userID of " + userID + " does not exist.", 404);
        }

        entity.setName(users.getName());
        return toJSON(entity);
    }

    @PUT
    @Path("/attacksToPokemon/{entryID}")              //entryID of Pokemon
    @Transactional
    public static String updatePokemonAttack(@PathParam Integer entryID, Integer[] attackArray) {         //attackArray must contain 4 entries with attackNumbers
        if (attackArray.length == 0 || attackArray.length<4) {
            throw new WebApplicationException("AttackArray was not set right on request.", 422);
        }

        Pokemon entity = entityManager.find(Pokemon.class, entryID);

        if (entity == null) {
            throw new WebApplicationException("Pokemon with entryID of " + entryID + " does not exist.", 404);
        }

        entity.setAttackNumber1(attackArray[0]);
        entity.setAttackNumber2(attackArray[1]);
        entity.setAttackNumber3(attackArray[2]);
        entity.setAttackNumber4(attackArray[3]);

        return toJSON(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public static Response deleteUser(@PathParam Integer id) {
        Users entity = entityManager.getReference(Users.class, id);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    @DELETE
    @Path("/pokemon/{entryID}")
    @Transactional
    public static Response deletePokemon(@PathParam Integer entryID) {
        Pokemon entity = entityManager.getReference(Pokemon.class, entryID);
        if (entity == null) {
            throw new WebApplicationException("Pokemon with entryID of " + entryID + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    @DELETE
    @Path("/team/{teamID}")
    @Transactional
    public static Response deletePokeTeam(@PathParam Integer teamID) {
        PokeTeam entity = entityManager.getReference(PokeTeam.class, teamID);
        if (entity == null) {
            throw new WebApplicationException("Team with teamID of " + teamID + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
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




    public static String toJSON(Object o) {
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
