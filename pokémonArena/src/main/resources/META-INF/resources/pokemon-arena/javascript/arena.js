var opponentName;
var opponentId;
var opponentTeamId;
var opponentTeam = [];
var arenaKey;

var setArena = function(key) {
    arenaKey = key;
}

var openArenaOptionTab = function(evt, tabId) {
    $(".tabcontent").css("display", "none");
    $(".tablink").removeClass("active");
    document.getElementById(tabId).style.display = "block";
    evt.currentTarget.classList.add("active");
}

var enterArena = function() {
    $("#arena-area").removeClass("fadeOut").addClass("fadeIn").css("display", "flex");
    $("#tabs-menu").removeClass("slideInUp").addClass("slideOutDown");
    $("header").removeClass("slideInDown").addClass("slideOutUp");

    $("#defaultOption").addClass("active");
    $("#arenaPokemonTab").css("display", "block");
}

var leaveArena = function() {
    $("#arena-area").removeClass("fadeIn").addClass("fadeOut");
    setTimeout(function(){
        $('#arena-area').css("display", "none");
    }, 1000);
    $("#tabs-menu").removeClass("slideOutDown").addClass("slideInUp");
    $("header").removeClass("slideOutUp").addClass("slideInDown");
}

var receivedOpponentInfo = function(data) {
    opponentName = data.name;
    opponentId = data.userID;
    opponentTeamId = data.teamID;
    getUserTeam(opponentTeamId, function(team) {
        opponentTeam = team;
        console.log("Opponent " + data.name + " with team " + team);
    });
}

var receivedSelectPokemon = function(data) {
    if (data.status == "request") {
        console.log("[E] received select pokemon request");
    } else if (data.status == "info") {
        console.log("[i] opponent switched pokemon to " + data.entryID);
    } else if (data.status == "accept") {
        acceptSelectPokemon(data.entryID);
    } else if (data.status == "reject") {
        rejectSelectPokemon(data.entryID);
    } else {
        console.log("[E] unknown selectPokemon status");
    }
}

var surrender = function() {
    socketArena.send("surrender", {"name": currentUserName});
    leaveArena();
    showNotification("Du hast aufgegeben");
}

var sendMyInfoToArena = function() {
    getUserTeams(currentUserId, function(teams) {
        getUserTeam(teams[0], function(pokemonEntries) {
            socketArena.send("userInfo", {  "name": currentUserName, "userID": currentUserId,
                "teamID": teams[0], "team": pokemonEntries});
            for(i in pokemonEntries) {
                getUserPokemon(pokemonEntries[i].entryID, function(pokemon){
                    sendPokemonInfoToArena(pokemon);
                });
            }
        });
    });
}

var sendPokemonInfoToArena = function(pokemon) {
    let attacks = [];
    attacks.push(pokemon.attackNumber1);
    attacks.push(pokemon.attackNumber2);
    attacks.push(pokemon.attackNumber3);
    attacks.push(pokemon.attackNumber4);
    socketArena.send("pokemonInfo", {"pokemonID": pokemon.pokemonID,
                                     "entryID": pokemon.entryID,
                                     "attacks": attacks
    });
}