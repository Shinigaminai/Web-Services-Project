var root = "pokedata";

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