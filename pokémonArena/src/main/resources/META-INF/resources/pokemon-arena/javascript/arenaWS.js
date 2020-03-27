var connectedToArena = false;
var socketArena;

var challenging = null;
var challengers = [];

jQuery(function() {
    $("#readyToFight").click(function () {
        document.getElementById("readyToFight").classList.add("loading");
        if (connectedToArena) {
            document.getElementById("readyToFight").classList.remove("loading");
            connectedToArena = false;
            socketArena = null;
            document.getElementById("challenge-list").classList.add("hidden");
            document.getElementById("readyToFight").innerHTML = "ready to fight";
            document.getElementById("challenge-list").innerHTML = "";
        } else {
            var name = $("#username-input").val();
            socketArena = new FancyWebSocket("ws://" + location.host + "/arena/" + name);

            socketArena.bind('open', function(data) {
                document.getElementById("readyToFight").classList.remove("loading");
                document.getElementById("readyToFight").innerHTML = "quit arena";
                connectedToArena = true;
                document.getElementById("challenge-list").classList.remove("hidden");
                socketArena.send('getChallengers', {});
            });
            socketArena.bind('close', function(data) {
                document.getElementById("readyToFight").innerHTML = "ready to fight";
                connectedToArena = false;
                document.getElementById("challenge-list").classList.add("hidden");
                alert("[!] Verbindung zu Arena Socket abgebrochen!");
            });
            socketArena.bind('userconnect', receivedArenaUserconnect);
            socketArena.bind('challenge', receivedArenaChallenge);
            socketArena.bind('cancelChallenge', receivedArenaChallengeCancel);
            socketArena.bind('answerChallenge', receivedArenaChallengeAnswer);

            socketArena.bind('assignedArena', function(data){setArena(data.arena);});
            socketArena.bind('assignedArena', enterArena);
            socketArena.bind('opponentInfo', receivedOpponentInfo);
            socketArena.bind('selectPokemon', receivedSelectPokemon);
        }
    });
});