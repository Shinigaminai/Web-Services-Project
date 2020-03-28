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
    //TODO load other data;
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
}