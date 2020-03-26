var currentUserName;
var currentUserId;

jQuery(function() {
    $("#login-button").click(function () {
        login();
    });

    $("#username-input").keypress(function(event){
        if(event.keyCode == 13 || event.which == 13) {
            login();
        }
    });

    $("#send").click(sendMessage);

    $('.tab-button').click(function(e){
        e.preventDefault();

        var index = $(this).index();

        $('#tabs-area').attr('data-tab', index);
        $('#tabs-menu').attr('data-tab', index);
        console.log("set data-tab = ", index);
    });

    $("#username-input").focus();
});

var login = function() {
    currentUserName = $("#username-input").val();
    if(currentUserName == "" || currentUserName == undefined) {
        showNotification("Username ist leer");
        return;
    }
    getUserId(currentUserName,
        function(m) {
            currentUserId = m.userID;
            connectToServices();
        },
        function(m) {
            registerUser(currentUserName,
                function(m) {
                    currentUserId = m.userID;
                    showNotification("Neuer Benutzer angelegt");
                    connectToServices();
                },
                function() {
                    showNotification("Benutzer konnte nicht angelegt werden");
                }
            );
        }
    );
}

var connectToServices = function() {
    console.log("User ID for " + currentUserName + " is " + currentUserId);
    connectToChat();
    loadAllPokemon();
    loadPokemonTeam();
}

var showNotification = function(message, timeInMilliseconds = 2000) {
    $("#notification-banner").removeClass("fadeOut").addClass("fadeIn").css("display", "flex").html(message);

    setTimeout(function(){
        $('#notification-banner').removeClass('fadeIn').addClass('fadeOut');
    },timeInMilliseconds);
    setTimeout(function(){
        $('#notification-banner').css("display", "none");
    },timeInMilliseconds + 1000);
}