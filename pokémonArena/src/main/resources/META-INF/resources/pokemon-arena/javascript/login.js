var initLogin = function() {
    $("#connect-button").click(function () {
        connectToChat();
        loadAllPokemon();
    });
    $("#send").click(sendMessage);

    $("#username-input").keypress(function(event){
        if(event.keyCode == 13 || event.which == 13) {
            connectToChat();
            loadAllPokemon();
        }
    });

    $('.tab-button').click(function(e){
        e.preventDefault();

        var index = $(this).index();

        $('#tabs-area').attr('data-tab', index);
        console.log("set data-tab = ", index);
    });

    $("#username-input").focus();
};