package Fight;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
    public Pokemon(Integer entryID,String owner){
        this.entryID=entryID;
        this.owner = owner;
    }
    private Integer entryID;
    private String owner;
    private int hp;
    private String state;
    private int currentHp;
    List<Integer> attackIDList = new ArrayList<>();

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getEntryID() {
        return entryID;
    }

    public void setAttackIDList(List<Integer> attackIDList) {
        this.attackIDList = attackIDList;
    }
}
