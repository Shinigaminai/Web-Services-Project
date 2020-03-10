package PokeKotlinAPI;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

public class Example {
    public static void main(String[] args) {
        PokeApi pokeApi = new PokeApiClient();
        Pokemon bulbasaur = pokeApi.getPokemon(1);
        PokemonSpecies species = pokeApi.getPokemonSpecies(1);
        System.out.println(bulbasaur);
        System.out.println(species);
    }
}