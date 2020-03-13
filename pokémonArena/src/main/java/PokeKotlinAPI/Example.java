package PokeKotlinAPI;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

public class Example {
    public static void main(String[] args) {
        PokeApi pokeApi = new PokeApiClient();
        Pokemon bulbasaur = pokeApi.getPokemon(5);
        Move move= pokeApi.getMove(5);
        PokemonSpecies species = pokeApi.getPokemonSpecies(5);
        System.out.println(bulbasaur);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(move);
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(species);
    }
}