var root = "pokedata";
var element = $("#pokemon-list");

var loadAllPokemon = function() {
    console.log("load pokemon list");
    getPokedex(2, function(pokedex) {
        fillPokemonList(pokedex.pokemonEntries);
    });
}

var fillPokemonList = function( pokemonList ) {
    console.log(pokemonList);
    pokemonList.forEach ( function(value) {
        console.log(value);
        getPokemon(value.pokemonSpecies.id, function(pokemon) {
            createPokemonEntry(pokemon);
        });
    });
}

var createPokemonEntry = function( pokemon ) {
    var entry = document.createElement("DIV");
    var name = document.createElement("DIV");
    name.innerHTML = pokemon.name;
    entry.appendChild(name);
    entry.classList.add("entry");
    console.log(pokemon);
    document.getElementById("pokemon-list").appendChild(entry);
}

var getPokedex = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokedex/" + id, callback);
}

var getPokemonSpecies = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemonSpecies/" + id, callback);
}

var getPokemon = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemon/" + id, callback);
}