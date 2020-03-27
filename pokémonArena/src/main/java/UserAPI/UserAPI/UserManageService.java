package UserAPI.UserAPI;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.WebApplicationException;
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
}
