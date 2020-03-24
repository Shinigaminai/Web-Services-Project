package UserAPI.UserAPI;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@NamedQuery(name = "Users.findAll",
      query = "SELECT u FROM Users u ORDER BY u.name")
@Cacheable
public class Users {

    @Id
    @SequenceGenerator(
            name = "UsersSequence",
            sequenceName = "User_Sequence_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersSequence")
    private Integer userID;

     //@OneToOne(mappedBy = "User", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
     //private PokeTeam pokeTeam;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<PokeTeam> pokeTeamList = new ArrayList<>();

    @Column(length = 40, unique = true)
    private String name;

    public Users(){

    }

    public Users(String name) {
        this.name = name;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PokeTeam> getPokeTeamList() {
        return pokeTeamList;
    }

    public void setPokeTeamList(List<PokeTeam> pokeTeamList) {
        this.pokeTeamList = pokeTeamList;
    }
}
