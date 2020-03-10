var root = "pokeData";
var element = $("#pokemon-list");

var loadAllPokemon = function() {
    //TODO ask for Pokedex
    //TODO filter pokemon
    //TODO for each pokemon create entry
};

var getPokemonSpecies = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemonSpecies/" + id, callback);
}

var getPokemon = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemon/" + id, callback);
}