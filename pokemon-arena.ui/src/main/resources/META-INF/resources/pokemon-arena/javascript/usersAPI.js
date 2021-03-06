
var getUserId = function(name, callbackSuccess, callbackFailure) {
    $.get("http://" + location.host + "/users/" + name)
        .done(callbackSuccess)
        .fail(callbackFailure);
}

var registerUser = function(name, callbackSuccess, callbackFailure) {
    $.ajax({
        url: "http://" + location.host + "/users/",
        type: "POST",
        data: JSON.stringify({"name": name}),
        contentType:"application/json; charset=utf-8",
        dataType:"json",
    }).done(callbackSuccess)
      .fail(callbackFailure);
}

var getUserTeam = function(id, callback) {
    $.get("http://" + location.host + "/users/team/" + id, callback)
        .fail(function() {
            alert( "[E] failed to load team" );
        });
}

var getUserTeams = function(id, callback) {
    $.get("http://" + location.host + "/users/teams/" + id, callback)
        .fail(function() {
            alert( "[E] failed to load teams" );
        });
}

var getUserPokemonAttacks = function(id, callback) {
    $.get("http://" + location.host + "/users/attacks/" + id, callback)
        .fail(function() {
            alert( "[E] failed to load user pokemon moves" );
        });
}

var getUserPokemon = function(id, callback) {
    $.get("http://" + location.host + "/users/pokemon/" + id, callback)
        .fail(function() {
            alert( "[E] failed to load user pokemon" );
        });
}

var addUserTeamPokemon = function(teamid, pokemonid, callback) {
    $.ajax({
        url: "http://" + location.host + "/users/addPokemonToTeam/" + teamid,
        type: "POST",
        data: JSON.stringify({"pokemonID": pokemonid}),
        contentType:"application/json; charset=utf-8",
        dataType:"json",
    }).done(callback)
}

var removeUserTeamPokemon = function(entryID, callback) {
    $.ajax({
        url: "http://" + location.host + "/users/pokemon/" + entryID,
        type: "DELETE",
        contentType:"application/json; charset=utf-8",
        dataType:"json",
    }).done(callback)
}

var setUserPokemonAttacks = function(pokemonEntryId, attacks, callback) {
    console.log("PUT moves: " + attacks);
    $.ajax({
        url: "http://" + location.host + "/users/attacksToPokemon/" + pokemonEntryId,
        type: "PUT",
        data: JSON.stringify(attacks),
        contentType:"application/json; charset=utf-8",
        dataType:"json",
    }).done(callback)
}