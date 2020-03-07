var initLogin = function() {
    $("#connect-button").click(connectToChat);
    $("#send").click(sendMessage);

    $("#username-input").keypress(function(event){
        if(event.keyCode == 13 || event.which == 13) {
            connectToChat();
        }
    });

    $("#username-input").focus();
};
