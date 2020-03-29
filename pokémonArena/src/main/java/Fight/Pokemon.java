package Fight;

import PokeKotlinAPI.GetPokeService;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.PokemonType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pokemon {
    GetPokeService getPokeService = new GetPokeService();
    public Pokemon(Integer entryID, String owner, Integer pokemonID){
        this.entryID=entryID;
        this.owner = owner;
        this.pokemonID = pokemonID;
    }
    private Integer entryID;
    private Integer pokemonID;
    private String owner;

    private String state;

    private Integer hp;
    private int currentHp;
    private int attk;
    private int SpAttk;
    private int def;
    private int SpDef;
    private int init;
    private List<PokemonType> type = new ArrayList<>();

    List<Integer> attackIDList = new ArrayList<>();
    Map<Integer, Move> moves = new HashMap<>();

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

    public Integer getPokemonID() {
        return pokemonID;
    }

    public void setAttk(int attk) {
        this.attk = attk;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setSpAttk(int spAttk) {
        SpAttk = spAttk;
    }

    public void setSpDef(int spDef) {
        SpDef = spDef;
    }

    public void setInit(int init) {
        this.init = init;
    }

    public void setType(List<PokemonType> type) {
        this.type = type;
    }
    public void setMoves(){
        for(Integer i : attackIDList){
            moves.put(i,getPokeService.getMove(i));
        }
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public int getInit() {
        return init;
    }

    public List<PokemonType> getType() {
        return type;
    }

    public int getAttk() {
        return attk;
    }

    public int getSpAttk() {
        return SpAttk;
    }

    public int getDef() {
        return def;
    }

    public int getSpDef() {
        return SpDef;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getHp() {
        return hp;
    }
}
