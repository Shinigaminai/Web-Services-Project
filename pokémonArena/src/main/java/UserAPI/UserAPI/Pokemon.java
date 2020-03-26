package UserAPI.UserAPI;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

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
            sequenceName = "Entry_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pokemonSequence")
    private Integer entryID;

    @JsonManagedReference
     @ManyToOne
     @JoinColumn(name = "pokeTeamID")
     private PokeTeam pokeTeam;


    @Column(length = 40)
    private Integer pokemonID;

    @Column(length = 40)
    private Integer attackNumber1;

    @Column(length = 40)
    private Integer attackNumber2;

    @Column(length = 40)
    private Integer attackNumber3;

    @Column(length = 40)
    private Integer attackNumber4;

    public Pokemon(){

    }

    public Pokemon(Integer pokemonID) {
        this.pokemonID = pokemonID;
    }

    public Integer getEntryID() {
        return entryID;
    }

    public void setEntryID(Integer entryID) {
        this.entryID = entryID;
    }

    public Integer getPokemonID() {
        return pokemonID;
    }

    public void setPokemonID(Integer pokemonID) {
        this.pokemonID = pokemonID;
    }

    public PokeTeam getPokeTeam() {
        return pokeTeam;
    }

    public void setPokeTeam(PokeTeam pokeTeam) {
        this.pokeTeam = pokeTeam;
    }

    public Integer getAttackNumber1() {
        return attackNumber1;
    }

    public void setAttackNumber1(Integer attackNumber1) {
        this.attackNumber1 = attackNumber1;
    }

    public Integer getAttackNumber2() {
        return attackNumber2;
    }

    public void setAttackNumber2(Integer attackNumber2) {
        this.attackNumber2 = attackNumber2;
    }

    public Integer getAttackNumber3() {
        return attackNumber3;
    }

    public void setAttackNumber3(Integer attackNumber3) {
        this.attackNumber3 = attackNumber3;
    }

    public Integer getAttackNumber4() {
        return attackNumber4;
    }

    public void setAttackNumber4(Integer attackNumber4) {
        this.attackNumber4 = attackNumber4;
    }
}
