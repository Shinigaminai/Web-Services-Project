package UserAPI.UserAPI;

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
import java.util.List;


@Path("users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UserManageResource {

    private static final Logger LOGGER = Logger.getLogger(UserManageResource.class.getName());

    @Inject
    @PersistenceContext
    EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUsers() {
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
    public String getSingleUserByName(@PathParam String userName) {
        Users entity = entityManager.createNamedQuery("Users.findByName", Users.class)
                .setParameter("name",userName)
                .getSingleResult();
        if (entity == null) {
            throw new WebApplicationException("User with name '" + userName + "' does not exist.", 404);
        }
        return toJSON(entity);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teams/{userID}")
    public String getPokeTeamFromUser(@PathParam Integer userID) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PokeTeam> cq = cb.createQuery(PokeTeam.class);
        Root<PokeTeam> rootEntry = cq.from(PokeTeam.class);
        cq.select(rootEntry).where(cb.equal(rootEntry.get("user").get("userID"),userID));
        List <PokeTeam> pokeTeamList = entityManager.createQuery(cq).getResultList();
        if (pokeTeamList.size() == 0) {
            throw new WebApplicationException("PokeTeam with ForeignKey 'userID' " + userID + " does not exist.", 404);
        }
        return toJSON(pokeTeamList);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("team/{pokeTeamID}")
    public String getPokeTeam(@PathParam Integer pokeTeamID) {
        PokeTeam pokeTeam = entityManager.find(PokeTeam.class, pokeTeamID);
        if (pokeTeam == null) {
            throw new WebApplicationException("PokeTeam with'pokeTeamID' " + pokeTeamID + " does not exist.", 404);
        }

        return toJSON(pokeTeam);
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{userID}/pokemonOfTeam")
    public String getPokemonOfTeam(@PathParam Integer userID) {

        PokeTeam pokeTeam = entityManager.find(PokeTeam.class, userID);
        if (pokeTeam == null) {
            throw new WebApplicationException("PokeTeam with ForeignKey 'userID' " + userID + " does not exist.", 404);
        }
        //List <Pokemon> pokemonList = entityManager.createNamedQuery("Pokemon.findByPokeTeamID", Pokemon.class)
        //                                            .setParameter("ptID", pokeTeam.getPokeTeamID())
        //                                            .getResultList();

        return toJSON(pokeTeam.getPokemonList());
    }
        /*
         //some parameters to your method
    String param1 = "1";
    String paramNull = null;

    CriteriaBuilder cBuilder = em.getCriteriaBuilder();
    CriteriaQuery cQuery = cBuilder.createQuery();
    Root<A> pokemonRoot = cQuery.from(A.class);

    //Constructing list of parameters
    List<Predicate> predicates = new ArrayList<Predicate>();

    //Adding predicates in case of parameter not being null
    if (param1 != null) {
        predicates.add(
                cBuilder.equal(pokemonRoot.get("someAttribute"), param1));
    }
    if (paramNull != null) {
        predicates.add(
                cBuilder.equal(pokemonRoot.get("someOtherAttribute"), paramNull));
    }
    //query itself
    cQuery.select(pokemonRoot)
            .where(predicates.toArray(new Predicate[]{}));
    //execute query and do something with result
    em.createQuery(cQuery).getResultList();
         */

    @POST
    @Transactional
    public Response createUserWithTeam(Users user) {           //User name must be set... in POST-Body^^
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
        return Response.ok(pokeTeamFresh).status(201).build();
    }

    @POST
    @Path("{userID}/addTeam")
    @Transactional
    public Response createTeamForUser(@PathParam Integer userID) {

        Users user = entityManager.find(Users.class, userID);

        if(user == null){
            throw new WebApplicationException("User with 'userID' " + userID + " does not exist.", 404);
        }

        PokeTeam pokeTeamFresh = new PokeTeam();
        pokeTeamFresh.setUser(user);
        //user.getPokeTeamList().add(pokeTeamFresh);
        entityManager.persist(pokeTeamFresh);

        return Response.ok(pokeTeamFresh).status(201).build();
    }

    @POST
    @Path("addPokemonToTeam/{teamID}")
    @Transactional
    public Response addPokemonToTeam(@PathParam Integer teamID, Pokemon pokemon) {                  //Pokemon Object in POST-Body must have internID set!
        if (pokemon.getInternID() == null) {
            throw new WebApplicationException("internID of Pokemon wasn't set on request.", 422);
        }
        if (pokemon.getPokemonID() != null) {
            throw new WebApplicationException("pokemonID was invalidly set on request.", 422);
        }

        PokeTeam pokeTeam = entityManager.find(PokeTeam.class, teamID);

        if(pokeTeam == null){
            throw new WebApplicationException("PokeTeam with 'pokeTeamID' " + teamID + " does not exist.", 404);
        }

        //Users user = pokeTeam.getUser();

        Pokemon pokemonFresh = new Pokemon();
        pokemonFresh.setPokeTeam(pokeTeam);
        pokemonFresh.setInternID(pokemon.getInternID());
        //pokeTeam.getPokemonList().add(pokemonFresh);
        entityManager.persist(pokemonFresh);


        return Response.ok(pokemon).status(201).build();
    }



/*    @PUT
    @Path("/name/{name}")
    @Transactional
    public Users updateByName(@PathParam String name, Users users) {
        if (users.getName() == null) {
            throw new WebApplicationException("Users' Name was not set on request.", 422);
        }

        Users user = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.name= :username", Users.class).
                setParameter("username", name).getSingleResult();

        if (user == null) {
            throw new WebApplicationException("User with the name " + name + " does not exist.", 404);
        }

        user.setName(users.getName());

        return user;
    }*/

    @PUT
    @Path("/id/{userID}")               //Gets UserName-Update from POST-Body
    @Transactional
    public Users updateByID(@PathParam Integer userID, Users users) {
        if (users.getName() == null) {
            throw new WebApplicationException("Users' Name was not set on request.", 422);
        }

        Users entity = entityManager.find(Users.class, userID);

        if (entity == null) {
            throw new WebApplicationException("User with userID of " + userID + " does not exist.", 404);
        }

        entity.setName(users.getName());
        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam Integer id) {
        Users entity = entityManager.getReference(Users.class, id);
        if (entity == null) {
            throw new WebApplicationException("Fruit with id of " + id + " does not exist.", 404);
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
