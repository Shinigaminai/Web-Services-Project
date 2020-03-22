var connectedToArena = false;
var socketArena;

jQuery(function() {
    $("#readyToFight").click(function () {
        if (connectedToArena) {
            connectedToArena = false;
            socketArena = null;
            document.getElementById("challenge-list").classList.add("hidden");
            document.getElementById("readyToFight").innerHTML = "ready to fight";
            document.getElementById("challenge-list").innerHTML = "";
        } else {
            var name = $("#username-input").val();
            socketArena = new FancyWebSocket("ws://" + location.host + "/arena/" + name);

            socketArena.bind('open', function(data) {
                document.getElementById("readyToFight").innerHTML = "cancel";
                connectedToArena = true;
                document.getElementById("challenge-list").classList.remove("hidden");
            });
            socketArena.bind('close', function(data) {
                document.getElementById("readyToFight").innerHTML = "ready to fight";
                connectedToArena = false;
                document.getElementById("challenge-list").classList.add("hidden");
                alert("[!] Verbindung zu Arena Socket abgebrochen!");
            });
            socketArena.bind('userconnect', receivedArenaUserconnect);
            socketArena.bind('challenge', receivedArenaChallenge);
        }
    });
});

var receivedArenaUserconnect = function(data) {
    console.log("[i] User " + data.user + " " + data.action + " [arena]");
    if (data.action == "joined") {
        var entry = document.createElement("DIV");
        var name = document.createElement("SPAN");
        var challengeButton = document.createElement("BUTTON");
        challengeButton.innerHTML = "challenge";
        challengeButton.setAttribute("onclick", "challenge(" + data.user +")");
        name.innerHTML = data.user;
        entry.name = "challenger-" + data.user;
        entry.appendChild(name);
        entry.appendChild(challengeButton);
        document.getElementById("challenger-list").appendChild(entry);
    } else if (data.action == "left") {
        document.getElementByName("challenger-" + data.user).remove();
    } else {
        console.log("[E] unknown userconnect action [arena]");
    }
}

var receivedArenaChallenge = function(data) {
    alert("challenged by " + data.user);
}