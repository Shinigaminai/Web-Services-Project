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

$('#tabs-menu a').on('click', function(e){
    e.preventDefault();

    var index = $(this).index();

    $('#subpage-area').attr('data-tab', index);
});