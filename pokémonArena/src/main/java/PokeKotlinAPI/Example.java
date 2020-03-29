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
    }
}