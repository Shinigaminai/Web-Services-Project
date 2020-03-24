package UserAPI.UserAPI;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PokeTeam")
//@NamedQuery(name = "PokeTeam.findAll",
   //     query = "SELECT p FROM PokeTeam p ORDER BY p.pokeTeamID",
   //     hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class PokeTeam {

    @Id
    @SequenceGenerator(
            name = "pokeTeamSequence",
            sequenceName = "PokeTeam_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pokeTeamSequence")
    private Integer pokeTeamID;

     @ManyToOne
     @JoinColumn(name = "userID")
     private Users user;

     @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pokeTeam")
     private List<Pokemon> pokemonList = new ArrayList<>();

    public PokeTeam(){

    }

    public Integer getPokeTeamID() {
        return pokeTeamID;
    }

    public void setPokeTeamID(Integer pokeTeamID) {
        this.pokeTeamID = pokeTeamID;
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
    }

    public List<Pokemon> getPokemonList() {
        return pokemonList;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
