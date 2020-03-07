var connected = false;
var socket;

var connectToChat = function() {
    if (! connected) {
        var name = $("#username-input").val();
        console.log("Val: " + name);
        socket = new WebSocket("ws://" + location.host + "/chat/" + name);
        //TODO loading animation
        socket.onopen = function() {
            //TODO end loading animation
            connected = true;
            console.log("Connected to the web socket");
            document.getElementById("login-area").classList.add("slideOutDown");  // hide / remove login area
            document.getElementById("subpage-area").classList.add("animated", "forward", "fadeInBig");
            document.getElementById("subpage-area").style.visibility = 'visible';
            document.getElementById("tabs-menu").classList.add("animated", "forward", "slideInDown");
            document.getElementById("tabs-menu").style.visibility = 'visible';
            $("#send").attr("disabled", false);
            $("#connect-button").attr("disabled", true);
            $("#username-input").attr("disabled", true);
            $("#msg").focus();
            $("#login-menu").attr("hidden", true);
        };
        socket.onmessage = function(m) {
            console.log("Got message: " + m.data);
            $("#chat").append(m.data + "\n");
            scrollToChatBottom();
        };
    }
};

var sendMessage = function() {
    if (connected) {
        var value = $("#msg").val();
        console.log("Sending " + value);
        socket.send(value);
        $("#msg").val("");
    }
};

var scrollToChatBottom = function () {
    $('#chat').scrollTop($('#chat')[0].scrollHeight);
};

$("#msg").keypress(function(event) {
    if(event.keyCode == 13 || event.which == 13) {
        sendMessage();
    }
});

$("#chat").change(function() {
    scrollToChatBottom();
});