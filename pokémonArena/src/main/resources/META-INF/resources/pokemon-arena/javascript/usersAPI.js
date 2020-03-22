
var getUserTeam = function(id, callback) {
    $.get("http://" + location.host + "/users/team/" + id, callback)
        .fail(function() {
            alert( "failed to load team" );
        });
}

var getUserTeams = function(id, callback) {
    $.get("http://" + location.host + "/users/teams/" + id, callback)
        .fail(function() {
            alert( "failed to load teams" );
        });
}

var getUserPokemon = function(id, callback) {
    $.get("http://" + location.host + "/users/pokemon/" + id, callback)
        .fail(function() {
            alert( "failed to load user pokemon" );
        });
}

var setUser = function(name, callback) {
    $.post("http://" + location.host + "/users/", {"name": name}, callback);
}

var addUserTeamPokemon = function(teamid, pokemonid, callback) {
    $.post("http://" + location.host + "/users/team/" + teamid, {"pokemonid": pokemonid, aktion: addPokemon}, callback);
}
var removeUserTeamPokemon = function(teamid, pokemonid, callback) {
    $.post("http://" + location.host + "/users/team/" + teamid, {"pokemonid": pokemonid, aktion: removePokemon}, callback);
}

var setUserPokemonAttacks = function(pokemonEntryId, attacks, callback) {
    $.post("http://" + location.host + "/users/pokemon/" + pokemonEntryId, {"attacks": attacks}, callback);
}