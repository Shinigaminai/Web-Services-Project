package PokeKotlinAPI;

import Fight.PokemonJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.*;

public class
Example {
    protected static GetPokeService getPokeService = new GetPokeService();
    public static void main(String[] args) {
        PokeApi pokeApi = new PokeApiClient();
        Pokemon bulbasaur = pokeApi.getPokemon(5);
        Move move = pokeApi.getMove(5);
        PokemonSpecies species = pokeApi.getPokemonSpecies(5);
        System.out.println(bulbasaur);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(move);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(species);
        System.out.println("----------------------------------------------------------------------------------------");
  /*      try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = "[{\"pokemonID\":2,\"entryID\":20,\"attacks\":[14,14,14,14]},{\"pokemonID\":1,\"entryID\":11,\"attacks\":[33,73,13,22]},{\"pokemonID\":2,\"entryID\":21,\"attacks\":[14,14,45,14]},{\"pokemonID\":3,\"entryID\":15,\"attacks\":[14,14,14,14]},{\"pokemonID\":2,\"entryID\":19,\"attacks\":[14,14,14,14]},{\"pokemonID\":24,\"entryID\":17,\"attacks\":[20,20,20,20]}]";
            PokemonJson[] pokemonList = objectMapper.readValue(jsonString, PokemonJson[].class);
            for (PokemonJson p : pokemonList) {
                for(Integer i: p.attacks){
                    System.out.println(i);
                }
            }
        }catch (Exception e){
            System.out.println(e);
        } */
    }
}