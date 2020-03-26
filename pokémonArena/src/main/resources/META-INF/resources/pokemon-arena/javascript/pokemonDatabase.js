var element = $("#pokemon-list");

var loadPokemonTeam = function() {
    getUserTeams(currentUserId, function(teams) {
        getUserTeam(teams[0], function(pokemonEntries) {
            for(i in pokemonEntries) {
                loadPokemonTeamEntry(pokemonEntries[i]);
            }
        });
    });
}

var loadPokemonTeamEntry = function (entry) {
    getPokemon(entry.pokemonID, function(pokemon) {
        console.log("pokemonEntry: "+ entry.entryID);
        createTeamEntry(pokemon, entry.entryID);
    });
}

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
    var sprite = document.createElement("IMG");
    var stats = document.createElement("DIV");
    var add = document.createElement("BUTTON");
    head = createHeadEntry(pokemon);
    entry.appendChild(head);
    sprite.src = pokemon.sprites.frontDefault;  //sprite
    sprite.alt = "Sprite";
    add.setAttribute("onclick", "addToTeam("+pokemon.id+")");  //addToTeam button
    add.innerHTML = "+ add to team";
    add.title = "add this pokémon to your team";
    var spriteAddDiv = document.createElement("DIV");
    spriteAddDiv.appendChild(sprite);
    spriteAddDiv.appendChild(add);
    spriteAddDiv.classList.add("pokemonSpriteAddDiv");
    entry.appendChild(spriteAddDiv);
    stats.classList.add("stats");   //stats
    pokemon.stats.forEach ( createStatEntry, stats );
    entry.appendChild(stats);
    entry.classList.add("list-entry");   //entry
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
    entry.title = type.type.name;
    entry.style.order = type.type.id;
    this.appendChild(entry);
}

var createHeadEntry = function( pokemon ) {
    var head = document.createElement("DIV");
    head.classList.add("head");
    var name = document.createElement("DIV");
    var types = document.createElement("DIV");
    name.innerHTML = pokemon.name;
    head.appendChild(name);
    pokemon.types.forEach( createTypeEntry, types );
    types.classList.add("pokemonEntry-typesArea");
    head.appendChild(types);
    return head;
}

var addToTeam = function (id) {
    var teamnumber = 0;
    getUserTeams(currentUserId, function(userteams) {
        if (userteams == null) {
            console.log("[E] Teams konnten nicht geladen werden");
        } else {
            var teamId = userteams[teamnumber];
            getUserTeam(teamId, function(teamlist){
                if (teamlist == null) {
                    console.log("[E] Pokémon in Team konnten nicht geladen werden");
                }
                if (checkPokemonInTeamlist(id, teamlist)) {
                    alert("[!] Dieses Pokémon ist bereits in deinem Team");
                } else if (teamlist.length > 5) {
                    alert("[!] Dein Pokémon-Team ist voll");
                } else {
                    getPokemon(id, function(pokemon) {
                        addUserTeamPokemon(teamId, pokemon.id, function(m) {
                            createTeamEntry(pokemon, m.entryID);
                            console.log("[i] add "+m.entryID+" to team");
                        });
                    });
                }
            });
        }
    });
}

var createTeamEntry = function (pokemon, entryId) {
    var entry = document.createElement("DIV");
    head = createHeadEntry(pokemon);
    entry.classList.add("list-entry");
    var removeButton = document.createElement("BUTTON");
    removeButton.setAttribute("onclick", "removeFromTeam(this, "+entryId+")");  //addToTeam button
    removeButton.innerHTML = "remove";
    removeButton.title = "remove this pokémon from your team";
    removeButton.classList.add("remove-button");
    head.appendChild(removeButton);
    entry.appendChild(head);
    document.getElementById("pokemon-team-list").appendChild(entry);
}

var removeFromTeam = function ( buttonElement, pokemonEntryId ) {
    removeUserTeamPokemon(pokemonEntryId, function(m) {
        console.log("[i] remove "+pokemonEntryId+" from team");
        buttonElement.parentElement.parentElement.remove();
    });
}

var checkPokemonInTeamlist = function(pokemonId, entryIds) {
    for(i in entryIds) {
        if(entryIds[i].pokemonID == pokemonId) {
            return true;
        }
    }
    return false;
}