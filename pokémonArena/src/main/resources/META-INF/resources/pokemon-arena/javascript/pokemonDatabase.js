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
    var add = document.createElement("BUTTON");
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
    entry.title = type.type.name;
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

var addToTeam = function (id) {
    //TODO
    var teamlist = [];
    if(id in teamlist) {
        alert("Dieses Pokémon ist bereits in deinem Team");
    } else {
        if(teamlist.length > 5) {
            alert("Dein Pokémon-Team ist voll");
        } else {
            getPokemon(id, function(pokemon) {
                createTeamEntry(pokemon);
            });
        }
    }
    console.log("add to team "+id);
}

var createTeamEntry = function (pokemon) {
    var entry = document.createElement("DIV");
    var name = document.createElement("DIV");
    var types = document.createElement("DIV");
    name.innerHTML = pokemon.name;
    pokemon.types.forEach( createTypeEntry, types );
    entry.appendChild(name);
    entry.appendChild(types);
    document.getElementById("pokemon-team").appendChild(entry);
}