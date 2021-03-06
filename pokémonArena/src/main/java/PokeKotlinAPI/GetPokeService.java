package PokeKotlinAPI;

import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.*;

import javax.enterprise.context.ApplicationScoped;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class GetPokeService {
    private PokeApi pokeApi;
    private Map<Integer, Pokemon> savedPokemonMap;
    private Map<Integer, PokemonSpecies> savedPokemonSpeciesMap;
    private Map<Integer, Pokedex> savedPokedex;
    private Map<Integer, Type> savedTypes;
    private Map<Integer, Move> savedMoves;

    public GetPokeService() {
        pokeApi = new PokeApiClient();
        savedPokemonMap = new HashMap<>();
        savedPokemonSpeciesMap = new HashMap<>();
        savedPokedex = new HashMap<>();
        savedTypes = new HashMap<>();
        savedMoves = new HashMap<>();
    }

    public Pokedex getPokedex(int id) {
        if (savedPokedex.containsKey(id)) {
            return savedPokedex.get(id);
        } else {
            Pokedex pokedex = pokeApi.getPokedex(id);
            savedPokedex.put(id, pokedex);
            return pokedex;
        }
    }

    public Pokemon getPokemon(int id) {
        if (savedPokemonMap.containsKey(id)) {
            return savedPokemonMap.get(id);
        } else {
            Pokemon pokemon = pokeApi.getPokemon(id);
            savedPokemonMap.put(id, pokemon);
            return pokemon;
        }
    }

    public PokemonSpecies getPokemonSpecies(int id) {
        if (savedPokemonSpeciesMap.containsKey(id)) {
            return savedPokemonSpeciesMap.get(id);
        } else {
            PokemonSpecies pokemonSpecies = pokeApi.getPokemonSpecies(id);
            savedPokemonSpeciesMap.put(id, pokemonSpecies);
            return pokemonSpecies;
        }
    }

    public Type getType(int id) {
        if (savedTypes.containsKey(id)) {
            return savedTypes.get(id);
        } else {
            Type type = pokeApi.getType(id);
            savedTypes.put(id, type);
            return type;
        }
    }

    public Move getMove(int id) {
        if (savedMoves.containsKey(id)) {
            return savedMoves.get(id);
        } else {
            Move move = pokeApi.getMove(id);
            savedMoves.put(id, move);
            return move;
        }
    }
}
