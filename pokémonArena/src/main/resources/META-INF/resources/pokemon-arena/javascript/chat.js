var connectedToChat = false;
var socketChat;

var connectToChat = function() {
    if (! connectedToChat) {
        var name = $("#username-input").val();
        console.log("Username: " + name);
        socketChat = new FancyWebSocket("ws://" + location.host + "/chat/" + name);
        //TODO start connnection animation

        // bind to server events
        socketChat.bind('open', function(data){
            //TODO end connection animation
            connectedToChat = true;
            console.log("Connected to the web socket");
            document.getElementById("login-area").classList.add("slideOutDown");  // hide / remove login area
            document.getElementById("tabs-area").classList.add("animated", "forward", "fadeIn", "delay-2s");
            document.getElementById("tabs-area").style.visibility = 'visible';
            document.getElementById("tabs-menu").classList.add("animated", "forward", "slideInUp", "delay-1s");
            document.getElementById("tabs-menu").style.visibility = 'visible';
            $("#send").attr("disabled", false);
            $("#connect-button").attr("disabled", true);
            $("#username-input").attr("disabled", true);
            $("#msg").focus();
            $("#login-menu").attr("hidden", true);
        });

        socketChat.bind('close', function() {
            recievedChatStatus('Connection closed by server');
        });

        socketChat.bind('message', recievedChatMessage);
        socketChat.bind('userconnect', recievedChatUserconnect);
    }
};

var recievedChatUserconnect = function(message) {
    var entry = document.createElement("DIV");
    entry.innerHTML = "User " + message.user + " " + message.action;
    console.log("User " + message.user + " " + message.action);
    entry.classList.add("connect-message");
    document.getElementById("chat").appendChild(entry);
}

var recievedChatStatus = function(message) {
    var entry = document.createElement("DIV");
    entry.innerHTML = message;
    entry.classList.add("status-message");
    document.getElementById("chat").appendChild(entry);
}

var recievedChatMessage = function (message) {
    var entry = document.createElement("DIV");
    var name = $("#username-input").val();
    if(message.sender == name) {
        entry.classList.add("my-message");
        entry.innerHTML = message.message;
    } else {
        entry.innerHTML = message.sender + ": " + message.message;
    }
    document.getElementById("chat").appendChild(entry);
}

var sendMessage = function() {
    if (connectedToChat) {
        var value = $("#msg").val();
        console.log("Sending " + value);
        var name = $("#username-input").val();
        socketChat.send('message', {sender: name, message: value});
        $("#msg").val("");
    }
};

var scrollToChatBottom = function () {
    $('#chat').scrollTop($('#chat')[0].scrollHeight);
};

jQuery(function () {
    $("#msg").keypress(function(event) {
        if(event.keyCode == 13 || event.which == 13) {
            sendMessage();
        }
    });

    $("#chat").change(function() {
        scrollToChatBottom();
    });
});