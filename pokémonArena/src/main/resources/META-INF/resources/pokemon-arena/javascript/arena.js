var connectedToArena = false;
var socketArena;

var initArena = function() {
    $("#readyToFight").click(function () {
        if (! connectedToArena) {
            var name = $("#username-input").val();
            console.log("Val: " + name);
            socketArena = new WebSocket("ws://" + location.host + "/arena/" + name);
            //TODO loading animation
            socketArena.onopen = function() {
                //TODO end loading animation
                connectedToArena = true;
                alert("connected to arena socket");
            };
            socketArena.onmessage = function(m) {
                console.log("Got message: " + m.data);
                $("#chat").append(m.data + "\n");
                scrollToChatBottom();
            };
        }
    });
}