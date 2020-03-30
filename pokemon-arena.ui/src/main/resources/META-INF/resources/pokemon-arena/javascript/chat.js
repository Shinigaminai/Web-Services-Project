var connectedToChat = false;
var socketChat;

var connectToChat = function(callback) {
    if (! connectedToChat) {
        var name = $("#username-input").val();
        console.log("Username: " + name);
        socketChat = new FancyWebSocket("ws://" + location.host + "/chat/" + name);

        // bind to server events
        socketChat.bind('open', function(){
            connectedToChat = true;
            console.log("Connected to the web socket");
        });

        socketChat.bind('close', function() {
            receivedChatStatus('Connection closed by server');
            connectedToChat = false;
            socketChat = undefined;
            scrollToChatBottom();
        });
        socketChat.bind('okToConnect', callback);
        socketChat.bind('blockedConnect', function(){
            $("#login-button").removeClass("loading");
            showNotification('This user is already connected');
            connectedToChat = false;
            socketChat = undefined;
        });

        socketChat.bind('message', receivedChatMessage);
        socketChat.bind('message', scrollToChatBottom);
        socketChat.bind('userconnect', receivedChatUserconnect);
        socketChat.bind('userconnect', scrollToChatBottom);
    }
};

var receivedChatUserconnect = function(message) {
    console.log("[i] User " + message.user + " " + message.action + " [chat]");
    var entry = document.createElement("DIV");
    var inner = document.createElement("SPAN");
    inner.innerHTML = "User " + message.user + " " + message.action;
    entry.appendChild(inner);
    entry.classList.add("connect-message");
    document.getElementById("chat").appendChild(entry);
}

var receivedChatStatus = function(message) {
    var entry = document.createElement("DIV");
    var inner = document.createElement("SPAN");
    inner.innerHTML = message;
    entry.appendChild(inner);
    entry.classList.add("status-message");
    document.getElementById("chat").appendChild(entry);
}

var receivedChatMessage = function (message) {
    var entry = createMessageElement(message);
    var name = $("#username-input").val();
    if(message.sender == name) {
        entry.classList.add("out-message");
    } else {
        entry.classList.add("in-message");
    }
    document.getElementById("chat").appendChild(entry);
}

var createMessageElement = function(m) {
    var entry = document.createElement("DIV");
    var bubble = document.createElement("SPAN")
    var sender = document.createElement("SPAN");
    var message = document.createElement("SPAN");
    sender.classList.add("sender");
    message.classList.add("message");
    sender.innerHTML = m.sender + ":";
    message.innerHTML = m.message;
    bubble.appendChild(sender);
    bubble.appendChild(message);
    entry.appendChild(bubble);
    return entry;
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