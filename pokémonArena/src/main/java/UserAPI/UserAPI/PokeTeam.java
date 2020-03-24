package UserAPI.UserAPI;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PokeTeam")
@NamedQuery(name = "PokeTeam.findAll",
        query = "SELECT p FROM PokeTeam p ORDER BY p.pokeTeamID")
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

    @JsonIgnore
     @ManyToOne
     @JoinColumn(name = "userID")

     private Users user;


     @OneToMany(cascade = CascadeType.ALL, mappedBy = "pokeTeam")
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
