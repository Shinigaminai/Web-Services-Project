package UserAPI.UserAPI;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "Pokemon")
@NamedQueries({
        //@NamedQuery(name = "PokeTeam.findAll",
        //     query = "SELECT p FROM PokeTeam p ORDER BY p.pokeTeamID",
        //     hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") ),
        //@NamedQuery(name = "Pokemon.findByPokeTeamID",
        //query = "SELECT p FROM Pokemon p WHERE p.pokeTeamID = :ptID")
})

@Cacheable
public class Pokemon {



    @Id
    @SequenceGenerator(
            name = "pokemonSequence",
            sequenceName = "Pokemon_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pokemonSequence")
    private Integer pokemonID;

    @JsonManagedReference
     @ManyToOne
     @JoinColumn(name = "pokeTeamID")
     private PokeTeam pokeTeam;

    @Column(length = 40)
    private Integer internID;

    public Pokemon(){

    }

    public Pokemon(Integer internID) {
        this.internID = internID;
    }

    public Integer getPokemonID() {
        return pokemonID;
    }

    public void setPokemonID(Integer pokemonID) {
        this.pokemonID = pokemonID;
    }

    public Integer getInternID() {
        return internID;
    }

    public void setInternID(Integer internID) {
        this.internID = internID;
    }

    public PokeTeam getPokeTeam() {
        return pokeTeam;
    }

    public void setPokeTeam(PokeTeam pokeTeam) {
        this.pokeTeam = pokeTeam;
    }
}
