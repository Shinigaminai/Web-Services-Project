
var receivedArenaUserconnect = function(data) {
    console.log("[i] User " + data.user + " " + data.action + " [arena]");
    if (data.user != currentUserName) {
        if (data.action == "joined") {
            addChallenger(data.user);
        } else if (data.action == "left") {
            removeChallenger(data.user);
        } else if (data.action == "fight") {
            removeChallenger(data.user);
        } else {
            console.log("[E] unknown userconnect action [arena]");
        }
    }
    if (data.user == challenging) {
        challenging = null;
        document.getElementById('challenging-overlay').classList.add('hidden');
        showNotification("Challenged user left");
    }
    if (data.user in challengers) {
        const index = challengers.indexOf(user);
        if (index > -1) {
            challengers.splice(index, 1);
        }
    }
}

var receivedArenaChallenge = function(data) {
    challengers.push(data.from);
    document.getElementsByName("challenger-"+data.from)[0].childNodes[1].classList.add("hidden");
    document.getElementsByName("challenger-"+data.from)[0].childNodes[2].classList.remove("hidden");
}

var receivedArenaChallengeCancel = function(data) {
    const index = challengers.indexOf(data.from);
    if (index > -1) {
        challengers.splice(index, 1);
    }
    document.getElementsByName("challenger-"+data.from)[0].childNodes[1].classList.remove("hidden");
    document.getElementsByName("challenger-"+data.from)[0].childNodes[2].classList.add("hidden");
}

var receivedArenaChallengeAnswer = function(data) {
    if(data.from == challenging) {
        console.log("challenge answer: " + data.value);
        challenging = null;
        document.getElementById('challenging-overlay').classList.add('hidden');
        if(data.value == 'decline') {
            showNotification("Challenge declined", 2000);
        } else {
            showNotification("Challenge accepted", 2000);
            //TODO start fight
        }
    } else {
        console.log("Challenge answer from user who wasn't challenged: " + data.from);
    }
}

var addChallenger = function(challenger) {
    var entry = document.createElement("DIV");
    var name = document.createElement("SPAN");
    var challengeButton = document.createElement("BUTTON");
    var acceptButton = document.createElement("BUTTON");
    var declineButton = document.createElement("BUTTON");
    var answerChallengeArea = document.createElement("DIV");
    acceptButton.innerHTML = "accept";
    declineButton.innerHTML = "decline";
    acceptButton.setAttribute("onclick", "answerChallenge('" + challenger + "', 'accept')");
    declineButton.setAttribute("onclick", "answerChallenge('" + challenger + "', 'decline')");
    answerChallengeArea.appendChild(acceptButton);
    answerChallengeArea.appendChild(declineButton);
    answerChallengeArea.classList.add("answer-challenge-area", "hidden");
    challengeButton.innerHTML = "challenge";
    challengeButton.setAttribute("onclick", "challenge('" + challenger +"')");
    challengeButton.classList.add("challenge-button");
    name.innerHTML = challenger;
    entry.setAttribute("name", "challenger-" + challenger);
    entry.appendChild(name);
    entry.appendChild(challengeButton);
    entry.appendChild(answerChallengeArea);
    entry.classList.add("challenge-entry");
    document.getElementById("challenge-list").appendChild(entry);
}


var removeChallenger = function(challenger) {
    document.getElementsByName("challenger-" + challenger).forEach( function (element) {
        element.remove();
    });
}

var challenge = function(challenged) {
    console.log("challenging " + challenged);
    socketArena.send('challenge', {"to": challenged});
    challenging = challenged;
    document.getElementById('challenging-cancel').setAttribute('onclick', 'cancelChallenge("'+challenged+'")');
    document.getElementById('challenging-title').innerHTML = challenged;
    document.getElementById('challenging-overlay').classList.remove('hidden');
}

var answerChallenge = function(user, value) {
    socketArena.send('answerChallenge', {"to": user, "value": value});
    document.getElementsByName("challenger-"+user)[0].childNodes[1].classList.remove("hidden");
    document.getElementsByName("challenger-"+user)[0].childNodes[2].classList.add("hidden");
    const index = challengers.indexOf(user);
    if (index > -1) {
        challengers.splice(index, 1);
    }
    if(value == 'accept') {
        for(challenger in challengers) {
            socketArena.send('answerChallenge', {"to": challenger, "value": "decline"});
        }
        challengers = [];
    }
}

var cancelChallenge = function(user) {
    console.log("cancel Challenge to " + user);
    socketArena.send('cancelChallenge', {"to": user});
    challenging = null;
    document.getElementById('challenging-overlay').classList.add('hidden');
}