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
    document.getElementById(tabId).style.display = "flex";
    evt.currentTarget.classList.add("active");
}

var enterArena = function() {
    $("#arena-area").removeClass("fadeOut").addClass("fadeIn").css("display", "flex");
    $("#tabs-menu").removeClass("slideInUp").addClass("slideOutDown");
    $("header").removeClass("slideInDown").addClass("slideOutUp");

    $("#defaultOption").addClass("active");
    $("#arenaPokemonTab").css("display", "flex");

    getUserTeams(currentUserId, function(teams) {
        getUserTeam(teams[0], function(pokemonEntries) {
            let team = [];
            for(i in pokemonEntries) {
                createPokemonOptionField(pokemonEntries[i]);
                console.log("create pokemon option " + pokemonEntries[i].pokemonID);
            }
        });
    });
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
    opponentTeam = data.team;
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
            var myTeam = [];
            for(i in pokemonEntries) {
                getUserPokemon(pokemonEntries[i].entryID, function(pokemon){
                    let attacks = [];
                    attacks.push(pokemon.attackNumber1);
                    attacks.push(pokemon.attackNumber2);
                    attacks.push(pokemon.attackNumber3);
                    attacks.push(pokemon.attackNumber4);
                    pokeInfos = {"pokemonID": pokemon.pokemonID, "entryID": pokemon.entryID, "attacks": attacks};
                    myTeam.push(pokeInfos);
                    if(myTeam.length >= pokemonEntries.length) {
                        socketArena.send("userInfo", {  "name": currentUserName, "userID": currentUserId, "arena": arenaKey, "team": JSON.stringify(myTeam) });
                    }
                });
            }
            if(pokemonEntries.length == 0) {
                socketArena.send("userInfo", {  "name": currentUserName, "userID": currentUserId, "arena": arenaKey, "team": "[]" });
            }
        });
    });
}

var createPokemonOptionField = function(userPokemon) {
    getPokemon(userPokemon.pokemonID, function(pokemon) {
        let option = createPokemonOption(pokemon, userPokemon.entryID);
        let cover = document.createElement("DIV");
        cover.classList.add("optionCover");
        cover.setAttribute("onclick", "selectPokemon("+userPokemon.pokemonID+","+userPokemon.entryID+")");
        cover.innerHTML = "choose";
        let entry = document.createElement("DIV");
        entry.appendChild(option);
        entry.appendChild(cover);
        entry.style.position = "relative";
        $("#arenaPokemonTab").append(entry);
    });
}

var createPokemonOption = function(pokemon, entryID) {
    let option = document.createElement("DIV");
    option.classList.add = "pokemonOption";
    let sprite = document.createElement("IMG");
    sprite.src = pokemon.sprites.frontDefault;
    sprite.alt = "Sprite";
    let health = document.createElement("DIV");
    health.name = "pokemon-"+entryID+"-health";
    let name = document.createElement("DIV");
    name.innerHTML = pokemon.name;
    option.appendChild(sprite);
    option.appendChild(health);
    option.appendChild(name);
    return option;
}