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
    $("#login-button").addClass("loading");
    connectToChat(function() {
        getUserId(currentUserName,
            function(m) {
                currentUserId = m.userID;
                connectToServices();
                $("#login-button").removeClass("loading");
                loginAnimation();
            },
            function(m) {
                registerUser(currentUserName,
                    function(m) {
                        currentUserId = m.userID;
                        $("#login-button").removeClass("loading");
                        showNotification("Neuer Benutzer angelegt");
                        connectToServices();
                        loginAnimation();
                    },
                    function() {
                        $("#login-button").removeClass("loading");
                        showNotification("Benutzer konnte nicht angelegt werden");
                    }
                );
            }
        );
    });
}

var connectToServices = function() {
    console.log("User ID for " + currentUserName + " is " + currentUserId);
    loadAllPokemon();
    loadPokemonTeam();
}

var loginAnimation = function() {
    document.getElementById("login-area").classList.add("slideOutDown");  // hide / remove login area
    document.getElementById("tabs-area").classList.add("animated", "forward", "fadeIn", "delay-2s");
    document.getElementById("tabs-area").style.visibility = 'visible';
    document.getElementById("tabs-menu").classList.add("animated", "forward", "slideInUp", "delay-1s");
    document.getElementById("tabs-menu").style.visibility = 'visible';
    setTimeout(function(){
        $('#tabs-menu').removeClass("delay-1s");
    }, 2000);
    $("#send").attr("disabled", false);
    $("#login-button").attr("disabled", true);
    $("#username-input").attr("disabled", true);
    $("#msg").focus();
    $("#login-menu").attr("hidden", true);
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