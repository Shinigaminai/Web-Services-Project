package UserAPI.UserAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserManageService {

    private static final Logger LOGGER = Logger.getLogger(UserManageService.class.getName());

    @Inject
    @PersistenceContext
    EntityManager entityManager;

    public Users[] getAllUsers(){
        return entityManager.createNamedQuery("Users.findAll", Users.class)
                .getResultList()
                .toArray(new Users[0]);
    }

    public Users getSingleUserByName(String userName) {
        System.out.println("[i] loading id for user " + userName);
        System.out.println(entityManager);
        Users entity = entityManager.createNamedQuery("Users.findByName", Users.class)
                .setParameter("name",userName)
                .getSingleResult();
        if (entity == null) {
            throw new WebApplicationException("User with name '" + userName + "' does not exist.", 404);
        }
        return entity;
    }

    public List<Integer> getPokeTeamFromUser(Integer userID) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PokeTeam> cq = cb.createQuery(PokeTeam.class);
        Root<PokeTeam> rootEntry = cq.from(PokeTeam.class);
        cq.select(rootEntry).where(cb.equal(rootEntry.get("user").get("userID"),userID));
        List<PokeTeam> pokeTeamList = entityManager.createQuery(cq).getResultList();
        if (pokeTeamList.size() == 0) {
            throw new WebApplicationException("PokeTeam with ForeignKey 'userID' " + userID + " does not exist.", 404);
        }
        List <Integer> pokeTeamIDList = new ArrayList<>();
        for (PokeTeam pt :
                pokeTeamList) {
            pokeTeamIDList.add(pt.getPokeTeamID());
        }
        return pokeTeamIDList;
    }

    public List<Map<String, Integer>> getPokeTeam(Integer pokeTeamID) {
        PokeTeam pokeTeam = entityManager.find(PokeTeam.class, pokeTeamID);
        if (pokeTeam == null) {
            throw new WebApplicationException("PokeTeam with'pokeTeamID' " + pokeTeamID + " does not exist.", 404);
        }

        List<UserAPI.UserAPI.Pokemon> pokemonList = pokeTeam.getPokemonList();
        List<Map<String,Integer>> outputSetList = new ArrayList<>();

        for (Pokemon pokemon:pokemonList) {
            Map <String, Integer> idMap = new HashMap<>();
            idMap.put("pokemonID", pokemon.getPokemonID());
            idMap.put("entryID", pokemon.getEntryID());
            outputSetList.add(idMap);
        }
        return outputSetList;
    }

    public Pokemon getPokemonInfos(Integer entryID) {
        Pokemon pokemon = entityManager.find(Pokemon.class,entryID);
        if (pokemon == null) {
            throw new WebApplicationException("Pokemon with'entryID' " + entryID + " does not exist.", 404);
        }
        return pokemon;
    }

    public List<Integer> getAttacksFromPokemon(Integer entryID) {

        Pokemon pokemon = entityManager.find(Pokemon.class,entryID);

        if (pokemon == null) {
            throw new WebApplicationException("Pokemon with'entryID' " + entryID + " does not exist.", 404);
        }

        List <Integer> attackNumberList = new ArrayList<>();

        attackNumberList.add(pokemon.getAttackNumber1());
        attackNumberList.add(pokemon.getAttackNumber2());
        attackNumberList.add(pokemon.getAttackNumber3());
        attackNumberList.add(pokemon.getAttackNumber4());

        return attackNumberList;
    }

    public Users createUserWithTeam(Users user){
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
        return user;
    }

    public PokeTeam createTeamForUser(Integer userID) {

        Users user = entityManager.find(Users.class, userID);

        if(user == null){
            throw new WebApplicationException("User with 'userID' " + userID + " does not exist.", 404);
        }

        PokeTeam pokeTeamFresh = new PokeTeam();
        pokeTeamFresh.setUser(user);
        entityManager.persist(pokeTeamFresh);

        return pokeTeamFresh;
    }

    public Pokemon addPokemonToTeam(Integer teamID, Pokemon pokemon) {                  //Pokemon Object in POST-Body must have pokemonID set!
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

        return pokemonFresh;
    }

    public Users updateUserNameByID(Integer userID, Users users) {
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

    public Pokemon updatePokemonAttack(Integer entryID, Integer[] attackArray) {         //attackArray must contain 4 entries with attackNumbers
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

        return entity;
    }

    public Response deleteUser(Integer id) {
        Users entity = entityManager.getReference(Users.class, id);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    public Response deletePokemon(Integer entryID) {
        Pokemon entity = entityManager.getReference(Pokemon.class, entryID);
        if (entity == null) {
            throw new WebApplicationException("Pokemon with entryID of " + entryID + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    public Response deletePokeTeam(Integer teamID) {
        PokeTeam entity = entityManager.getReference(PokeTeam.class, teamID);
        if (entity == null) {
            throw new WebApplicationException("Team with teamID of " + teamID + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }
}
