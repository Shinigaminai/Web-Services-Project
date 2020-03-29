package Fight;

import PokeKotlinAPI.GetPokeService;
import UserAPI.UserAPI.UserManageService;
import UserAPI.UserAPI.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.ext.auth.User;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;
import me.sargunvohra.lib.pokekotlin.model.PokemonType;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Arena {
    protected Map<String, Session> sessions = new HashMap<>(); //user, session
    protected ObjectMapper mapper = new ObjectMapper();
    private Map<String, Pokemon> currentPkm = new HashMap<>(); //user, currentPkm
    protected Map<Integer, Pokemon> allPkm = new HashMap<>(); //entryID, Pokemon
    protected GetPokeService getPokeService = new GetPokeService();
    private Map<String,String> opponent = new HashMap<>();

    private Map<String,Integer> roundMoves = new HashMap<>(); //User, Move
    private Map<String,String> FirstLast = new HashMap<>(); //user, FirstOrLast

    private int roundState = 0;
    private String Fighter1;
    private String Fighter2;

    public void setFighter1(String fighter1) {
        Fighter1 = fighter1;
    }

    public void setFighter2(String fighter2) {
        Fighter2 = fighter2;
    }

    protected void send(String username, String message) {
        if (sessions.get(username) != null) {
            sessions.get(username).getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        } else {
            System.out.println("tried to send message to non existant user " + username + ": " + message);
        }
    }

    protected void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
    public String createMessage(String event, Map<String, String> arguments) {
        try {
            return "{\"event\":\"" + event + "\",\"data\":" + mapper.writeValueAsString(arguments) + "}";
        } catch (Exception e) {
            return null;
        }
    }

    public String createMessage(JsonEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            return null;
        }
    }
    protected String setOpponentInfo(String username){
        String opponent ="";
        for(String u : sessions.keySet()){
            if(!u.equals(username)){
                opponent = u;
                this.opponent.put(username,opponent);
            }
        }
     return opponent;
    }

    protected void sendSurrender(String user){
        String UserOpponent = opponent.get(user);
        send(UserOpponent,createMessage("surrender",Map.of("name",user)));
    }

    protected void sendSelectPokemon(JsonEvent event, String from){
        Integer pkm = Integer.parseInt(event.getData().get("entryID"));
        Pokemon pokemon = allPkm.get(pkm);
        System.out.println(pokemon.getHp()); //1
        Pokemon currentpkm = currentPkm.get(from);
        if(currentpkm==null){
            currentPkm.put(from,pokemon);
            send(from, createMessage("selectPokemon", Map.of("entryID", pkm.toString(), "pokemonID",pokemon.getPokemonID().toString(),"maxhp",pokemon.getHp().toString(),"hp",pokemon.getCurrentHp().toString(),"status", "accept")));
            send(opponent.get(from), createMessage("selectPokemon", Map.of("entryID", pkm.toString(), "pokemonID",pokemon.getPokemonID().toString(),"maxhp",pokemon.getHp().toString(),"hp",pokemon.getCurrentHp().toString(),"status", "info")));
        }else {
            if (pokemon.getCurrentHp() > 0) {
                allPkm.replace(currentPkm.get(from).getEntryID(), currentPkm.get(from));
                currentPkm.replace(from, pokemon);
                send(from, createMessage("selectPokemon", Map.of("entryID", pkm.toString(),"pokemonID",pokemon.getPokemonID().toString(),"maxhp",pokemon.getHp().toString(),"hp",pokemon.getCurrentHp().toString(), "status", "accept")));
                if (currentpkm.getCurrentHp() <= 0) {
                    send(opponent.get(from), createMessage("selectPokemon", Map.of("entryID", pkm.toString(),"pokemonID",pokemon.getPokemonID().toString(),"maxhp",pokemon.getHp().toString(),"hp",pokemon.getCurrentHp().toString(), "status", "info")));
                }
            } else {
                send(from, createMessage("selectPokemon", Map.of("entryID", pkm.toString(),"pokemonID",pokemon.getPokemonID().toString(),"maxhp",pokemon.getHp().toString(),"hp",pokemon.getCurrentHp().toString() ,"status", "reject")));
            }
        }
    }
    protected void loadAllPokemonData(){
        allPkm.forEach((k,v)->{
            v.setType(getPokeService.getPokemon(v.getPokemonID()).component15());
            v.setAttackIDList(ArenaSocket.moveIDList.get(k));
            List<PokemonStat> allStats = getPokeService.getPokemon(v.getPokemonID()).component14();
            for(PokemonStat s : allStats){
               if(s.component1().component1().equals("hp")){
                   v.setHp(s.component3());
                   v.setCurrentHp(s.component3());
               }
               if(s.component1().component1().equals("attack")){
                   v.setAttk(s.component3());
               }
               if(s.component1().component1().equals("special-attack")){
                   v.setSpAttk(s.component3());
               }
               if(s.component1().component1().equals("defense")){
                   v.setDef(s.component3());
               }
               if(s.component1().component1().equals("special-defense")){
                   v.setSpDef(s.component3());
               }
               if(s.component1().component1().equals("speed")){
                   v.setInit(s.component3());
               }
            }
            v.setMoves();
        });
    }
    protected void setCurrentMove(String moveID,String user){
        System.out.println(moveID);
        roundMoves.put(user,Integer.valueOf(moveID));
        roundState++;
        if(roundState==2){
            executeFight();
        }
    }
    private void executeFight(){
        System.out.println(currentPkm);
        if(currentPkm.get(Fighter1).getInit()<currentPkm.get(Fighter2).getInit()){
            FirstLast.put(Fighter2,"First");
            FirstLast.put(Fighter1,"Last");
            executeMove(Fighter2);
            if(currentPkm.get(Fighter1).getCurrentHp()<=0){
                checkIfBeaten(Fighter1);
                sendResult();
            } else {
                executeMove(Fighter1);
                checkIfBeaten(Fighter2);
                sendResult();
            }

        } else {
            FirstLast.put(Fighter1,"First");
            FirstLast.put(Fighter2,"Last");
            executeMove(Fighter1);
            if(currentPkm.get(Fighter2).getCurrentHp()<=0){
                checkIfBeaten(Fighter2);
                sendResult();
            } else {
                executeMove(Fighter2);
                checkIfBeaten(Fighter1);
                sendResult();
            }
        }
        FirstLast = null;
        roundState=0;
    }

    private void executeMove(String user){
        Pokemon attacker = currentPkm.get(user);
        Pokemon defender = currentPkm.get(opponent.get(user));
        Integer moveID = roundMoves.get(user);
        System.out.println(moveID);
        Move move = attacker.moves.get(moveID);

        Random random = new Random();

        int damage = 0;
        double multiplicator = 1;
        double randomDmg = (random.nextInt(16)+85)/100;
        System.out.println(move);
        System.out.println(attacker.getType());
        for(PokemonType p : attacker.getType()) {
            if (move.component21().component1().equals(p.component2().component1())){
                multiplicator=multiplicator*2;
            }
        }
        for(PokemonType p : defender.getType()) {
            if (move.component21().component1().equals(p.component2().component1())){
                multiplicator=multiplicator*0.5;
            }
            if(p.component2().component1().equals("normal")){
                if(move.component21().component1().equals("fighting")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("ghost")){
                    multiplicator=multiplicator*0;
                }
            }
            if(p.component2().component1().equals("fire")){
                if(move.component21().component1().equals("fire")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("water")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("grass")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("ice")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("ground")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("bug")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("rock")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("steel")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("fairy")){
                    multiplicator*=0.5;
                }
            }
            if(p.component2().component1().equals("water")){
                if(move.component21().component1().equals("fire")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("water")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("grass")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("electric")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("ice")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("steel")){
                    multiplicator*=0.5;
                }
            }
            if(p.component2().component1().equals("grass")){
                if(move.component21().component1().equals("fire")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("water")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("grass")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("electric")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("ice")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("poison")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("ground")){
                    multiplicator*=0.5;
                }
                if(move.component21().component1().equals("flying")){
                    multiplicator*=2;
                }
                if(move.component21().component1().equals("bug")){
                    multiplicator*=2;
                }
            }
            if(p.component2().component1().equals("fairy")){
                if(move.component21().component1().equals("fighting")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("poison")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("bug")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("dragon")){
                    multiplicator=multiplicator*0;
                }
                if(move.component21().component1().equals("dark")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("steel")){
                    multiplicator=multiplicator*2;
                }
            }

            if(p.component2().component1().equals("steel")){
                if(move.component21().component1().equals("normal")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("fire")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("grass")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("ice")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("fighting")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("poison")){
                    multiplicator=multiplicator*0;
                }
                if(move.component21().component1().equals("ground")){
                    multiplicator=multiplicator*2;
                }
                if(move.component21().component1().equals("flying")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("psychic")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("bug")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("rock")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("dragon")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("steel")){
                    multiplicator=multiplicator*0.5;
                }
                if(move.component21().component1().equals("fairy")){
                    multiplicator=multiplicator*0.5;
                }
            }

            if(p.component2().component1().equals("dark")) {
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("psychic")) {
                    multiplicator = multiplicator * 0;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("ghost")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("dark")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fairy")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("dragon")) {
                if (move.component21().component1().equals("fire")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("water")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("electric")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ice")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("dragon")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("fairy")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("ghost")) {
                if (move.component21().component1().equals("normal")) {
                    multiplicator = multiplicator * 0;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 0;
                }
                if (move.component21().component1().equals("poison")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ghost")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("dark")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("rock")) {
                if (move.component21().component1().equals("normal")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fire")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("water")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("poison")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ground")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("flying")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("steel")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("bug")) {
                if (move.component21().component1().equals("fire")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ground")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("flying")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("rock")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("psychic")) {
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("psychic")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("ghost")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("dark")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("flying")) {
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("electric")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("ice")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ground")) {
                    multiplicator = multiplicator * 0;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("rock")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("ground")) {
                if (move.component21().component1().equals("water")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("electric")) {
                    multiplicator = multiplicator * 0;
                }
                if (move.component21().component1().equals("ice")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("poison")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("rock")) {
                    multiplicator = multiplicator * 0.5;
                }
            }

            if(p.component2().component1().equals("poison")) {
                if (move.component21().component1().equals("grass")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("poison")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ground")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("psychic")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fairy")) {
                    multiplicator = multiplicator * 0.5;
                }
            }

            if(p.component2().component1().equals("fighting")) {
                if (move.component21().component1().equals("flying")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("psychic")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("bug")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("rock")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("dark")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fairy")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("ice")) {
                if (move.component21().component1().equals("fire")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("ice")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("fighting")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("rock")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("steel")) {
                    multiplicator = multiplicator * 2;
                }
            }

            if(p.component2().component1().equals("electric")) {
                if (move.component21().component1().equals("electric")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("ground")) {
                    multiplicator = multiplicator * 2;
                }
                if (move.component21().component1().equals("flying")) {
                    multiplicator = multiplicator * 0.5;
                }
                if (move.component21().component1().equals("steel")) {
                    multiplicator = multiplicator * 0.5;
                }
            }
        }

        multiplicator = multiplicator * randomDmg;

        int result = random.nextInt(100)+1;
System.out.println(move.component3());
System.out.println(result);
if(move.component3()!=null) {
    if (result <= move.component3()) { //Hit
        if (move.component12().component1().equals("physical")) {
            damage = (int) Math.round((((12 * move.component7() * (attacker.getAttk() / defender.getDef())) / 50) + 2) * multiplicator);
        } else {
            damage = (int) Math.round((((12 * move.component7() * (attacker.getSpAttk() / defender.getSpDef())) / 50) + 2) * multiplicator);
        }
    } else {

    }
}
        defender.setCurrentHp(defender.getCurrentHp()-damage);
        currentPkm.replace(user,attacker);
        currentPkm.replace(opponent.get(user),defender);
    }

    private void checkIfBeaten(String user){
        boolean beaten = true;
        for(Pokemon p : allPkm.values()){
            if(p.getOwner().equals(user)){
                if(p.getCurrentHp()>0){
                    beaten=false;
                }
            }
        }
        if(beaten) {
            send(Fighter1, createMessage("arenaResult", Map.of("winner", opponent.get(user))));
            send(Fighter2, createMessage("arenaResult", Map.of("winner", opponent.get(user))));
        }
    }

    private void sendResult(){
        System.out.println("----------------------");
        System.out.println(currentPkm.get(Fighter1).getEntryID().toString());
        System.out.println(currentPkm.get(Fighter1).getHp().toString());
        System.out.println(currentPkm.get(Fighter1).getCurrentHp().toString());
        System.out.println(FirstLast.get(Fighter1));
        send(Fighter1,createMessage("fightResult",Map.of("entryID",currentPkm.get(Fighter1).getEntryID().toString(),
                "hp",currentPkm.get(Fighter1).getCurrentHp().toString(),"maxhp",currentPkm.get(Fighter1).getHp().toString(),"fightOrder",FirstLast.get(Fighter1))));
        send(Fighter2,createMessage("fightResult",Map.of("entryID",currentPkm.get(Fighter1).getEntryID().toString(),
                "hp",currentPkm.get(Fighter1).getCurrentHp().toString(),"maxhp",currentPkm.get(Fighter1).getHp().toString(),"fightOrder",FirstLast.get(Fighter1))));
        send(Fighter1,createMessage("fightResult",Map.of("entryID",currentPkm.get(Fighter2).getEntryID().toString(),
                "hp",currentPkm.get(Fighter2).getCurrentHp().toString(),"maxhp",currentPkm.get(Fighter1).getHp().toString(),"fightOrder",FirstLast.get(Fighter2))));
        send(Fighter2,createMessage("fightResult",Map.of("entryID",currentPkm.get(Fighter2).getEntryID().toString(),
                "hp",currentPkm.get(Fighter2).getCurrentHp().toString(),"maxhp",currentPkm.get(Fighter1).getHp().toString(),"fightOrder",FirstLast.get(Fighter2))));
    }
}