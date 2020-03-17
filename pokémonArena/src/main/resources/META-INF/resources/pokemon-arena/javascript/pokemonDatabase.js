var root = "pokedata";
var element = $("#pokemon-list");

var loadAllPokemon = function() {
    console.log("load pokemon list");
    getPokedex(2, function(pokedex) {
        fillPokemonList(pokedex.pokemonEntries);
    });
}

var fillPokemonList = function( pokemonList ) {
    pokemonList.forEach ( function(value) {
        getPokemon(value.pokemonSpecies.id, function(pokemon) {
            createPokemonEntry(pokemon);
        });
    });
}

var createPokemonEntry = function( pokemon ) {
    var entry = document.createElement("DIV");
    var head = document.createElement("DIV");
    var sprite = document.createElement("IMG");
    var stats = document.createElement("DIV");
    head.classList.add("head");     //head
    var name = document.createElement("DIV");
    var types = document.createElement("DIV");
    name.innerHTML = pokemon.name;
    head.appendChild(name);
    pokemon.types.forEach( createTypeEntry, types );
    types.classList.add("pokemonEntry-typesArea");
    head.appendChild(types);
    entry.appendChild(head);
    sprite.src = pokemon.sprites.frontDefault;  //sprite
    sprite.alt = "Sprite";
    entry.appendChild(sprite);
    stats.classList.add("stats");   //stats
    pokemon.stats.forEach ( createStatEntry, stats );
    entry.appendChild(stats);
    entry.classList.add("entry");   //entry
    entry.style.order = pokemon.order;
    document.getElementById("pokemon-list").appendChild(entry);
}

var createStatEntry = function( stat ) {
    var entry = document.createElement("DIV");
    var statName = document.createElement("DIV");
    var statValue = document.createElement("DIV");
    statName.innerHTML = stat.stat.name;
    statValue.innerHTML = stat.baseStat;
    entry.style.order = stat.stat.id;
    entry.appendChild(statName);
    entry.appendChild(statValue);
    entry.classList.add("statEntry");
    this.appendChild(entry);
}

var createTypeEntry = function( type ) {
    var entry = document.createElement("IMG");
    entry.alt = type.type.name;
    entry.src = "svgs/types/" + type.type.name +".svg";
    entry.style.order = type.type.id;
    this.appendChild(entry);
}

var getPokedex = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokedex/" + id, callback)
      .done(function() {
        console.log( "loaded pokedex" );
      })
      .fail(function() {
        alert( "failed to load pokedex" );
      });
}

var getPokemonSpecies = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemonSpecies/" + id, callback);
}

var getPokemon = function ( id, callback) {
    $.get("http://" + location.host + "/" + root + "/pokemon/" + id, callback)
      .fail(function() {
        alert( "failed to load pokemon" );
      });
}