package Fight;

public class Pokemon {
    public Pokemon(Integer entryID,String owner){
        this.entryID=entryID;
        this.owner = owner;
    }
    private Integer entryID;
    private String owner;
    private int hp;
    private String state;

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setState(String state) {
        this.state = state;
    }
}
