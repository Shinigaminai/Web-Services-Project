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
    connectToChat();
    loadAllPokemon();
    currentUserName = $("#username-input").val();
    currentUserId = getUserId(name);
    if(currentUserId == false) {
        currentUserId = registerUser(name);
    }
    console.log("User ID for " + currentUserName + " is " + currentUserId);
    loadPokemonTeam();
}

var getUserId = function(name) {
    $.get("http://" + location.host + "/users/" + name, function(m){
        return m.id;
    })
    .fail(function() {
        return false;
    });
}

var registerUser = function(name) {
    $.post("http://" + location.host + "/users/" + name, function(m){
        console.log("Registered as new user");
        return m.id;
    })
    .fail(function() {
        return false;
    });
}

var showNotification = function(message, timeInMilliseconds) {
    $("#notification-banner").removeClass("fadeOut").addClass("fadeIn").css("display", "flex").html(message);

    setTimeout(function(){
        $('#notification-banner').removeClass('fadeIn').addClass('fadeOut');
    },timeInMilliseconds);
    setTimeout(function(){
        $('#notification-banner').css("display", "none");
    },timeInMilliseconds + 1000);
}