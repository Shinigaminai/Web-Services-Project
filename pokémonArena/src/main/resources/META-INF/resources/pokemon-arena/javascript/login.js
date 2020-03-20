var username;
var userid;

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
    username = $("#username-input").val();
    userid = getUserId(name);
    if(userid == false) {
        userid = registerUser(name);
    }
    console.log("User ID for " + name + " is " + id);
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