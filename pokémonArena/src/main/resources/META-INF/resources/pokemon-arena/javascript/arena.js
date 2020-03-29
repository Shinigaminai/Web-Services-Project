var opponentName;
var opponentId;
var opponentTeamId;
var opponentTeam = [];
var arenaKey;
var extraPokemonChooseAction = false;
var myTeam = [];

var setArena = function(key) {
    arenaKey = key;
}

var openArenaOptionTab = function(element, tabId) {
    $(".tabcontent").css("display", "none");
    $(".tablink").removeClass("active");
    document.getElementById(tabId).style.display = "flex";
    element.classList.add("active");
}

var enterArena = function() {
    $("#arena-area").removeClass("fadeOut").addClass("fadeIn").css("display", "flex");
    $("#tabs-menu").removeClass("slideInUp").addClass("slideOutDown");
    $("header").removeClass("slideInDown").addClass("slideOutUp");

    $("#defaultOption").click();

    getUserTeams(currentUserId, function(teams) {
        loadPokemonOptions(teams[0]);
    });

    extraPokemonChooseAction = true;
}

var leaveArena = function() {
    $("#arena-area").removeClass("fadeIn").addClass("fadeOut");
    setTimeout(function(){
        $('#arena-area').css("display", "none");
    }, 1000);
    $("#tabs-menu").removeClass("slideOutDown").addClass("slideInUp");
    $("header").removeClass("slideOutUp").addClass("slideInDown");
}

var loadPokemonOptions = function(teamID) {
    getUserTeam(teamID, function(pokemonEntries) {
        let team = [];
        for(i in pokemonEntries) {
            createPokemonOptionField(pokemonEntries[i]);
            console.log("create pokemon option " + pokemonEntries[i].pokemonID);
        }
    });
}

var loadMovesOptions = function(entryID) {
    for (i in myTeam) {
        if (myTeam[i].entryID == entryID) {
            for(j in myTeam[i].attacks) {
                createMoveOptionField(opponentTeam[i].attacks[j]);
            }
            return;
        }
    }
}

var receivedOpponentInfo = function(data) {
    opponentName = data.name;
    opponentId = data.userID;
    opponentTeam = JSON.parse(data.team);
    console.log("[i] loaded opponent info");
}

var receivedSelectPokemon = function(data) {
    if (data.status == "request") {
        console.log("[E] received select pokemon request");
    } else if (data.status == "info") {
        console.log("[i] opponent switched pokemon to " + data.entryID);
        selectOpponentPokemon(data.pokemonID, data.entryID);
    } else if (data.status == "accept") {
        acceptSelectPokemon(data.pokemonID, data.entryID);
    } else if (data.status == "reject") {
        rejectSelectPokemon();
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
    console.log("try send my info to arena");
    getUserTeams(currentUserId, function(teams) {
        getUserTeam(teams[0], function(pokemonEntries) {
            myTeam = [];
            console.log("create team for info to arena");
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
        cover.classList.add("optionCover", "optionCoverPokemon");
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

var createMoveOptionField = function(moveID) {
    getMove(moveID, function(pokemon) {
        let option = createMoveOption(pokemon, userPokemon.entryID);
        let cover = document.createElement("DIV");
        cover.classList.add("optionCover", "optionCoverMove");
        cover.setAttribute("onclick", "selectMove("+moveID+")");
        cover.innerHTML = "choose";
        let entry = document.createElement("DIV");
        entry.appendChild(option);
        entry.appendChild(cover);
        entry.style.position = "relative";
        $("#arenaPokemonTab").append(entry);
    });
}

var createMoveOption = function(move, moveID) {
    let option = document.createElement("DIV");
    option.classList.add = "moveOption";
    let name = document.createElement("DIV");
    name.innerHTML = move.name;
    option.appendChild(name);
    return option;
}

var selectPokemon = function(pokemonID, entryID) {
    socketArena.send("selectPokemon",{"pokemonID": pokemonID,
                                      "entryID": entryID,
                                      "arena": arenaKey,
                                      "status": "request"});
    deactivateOptions();
}

var activateOptions = function() {
    $(".optionCover").removeClass('disabled').html("choose");
}
var deactivateOptions = function() {
    $(".optionCover").addClass('disabled').html("waiting...");
}
var activateMoveOptions = function() {
    $(".optionCoverMove").removeClass('disabled').html("choose");
}
var deactivateMoveOptions = function() {
    $(".optionCoverMove").addClass('disabled').html("");
}
var deactivatePokemonOptions = function() {
    $(".optionCoverPokemon").addClass('disabled').html("");
}

var selectOpponentPokemon = function(pokemonID, entryID) {
    getPokemon(pokemonID, function(pokemon) {
        switchPokemon("pokemon-opponent", pokemon.sprites.frontDefault, pokemon.name);
    });
}

var acceptSelectPokemon = function(pokemonID, entryID) {
    activateOptions();
    deactivatePokemonOptions();
    getPokemon(pokemonID, function(pokemon) {
        switchPokemon("pokemon-me", pokemon.sprites.backDefault, pokemon.name);
    });
    if(extraPokemonChooseAction == true) {
        extraPokemonChooseAction = false;
        activateMoveOptions();
    } else {
        deactivateMoveOptions();
    }
    loadMovesOptions(entryID);
}

var switchPokemon = function(elementID, newSource, alt) {
    $("#"+elementID).removeClass("zoomIn");
    $("#"+elementID).addClass("zoomOut");
    setTimeout(function(){
        $("#"+elementID).prop("src", newSource).prop("alt", alt);
        $("#"+elementID).removeClass("zoomOut");
        $("#"+elementID).addClass("zoomIn");
    }, 1000);
}

var rejectSelectPokemon = function() {
    showNotification("Du kannst dieses Pokémon nicht auswählen", 2500);
    activateOptions();
}