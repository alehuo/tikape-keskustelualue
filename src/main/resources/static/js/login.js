$(document).ready(function () {
    function login() {
        //Kirjautumistiedot
        var credentials = $("#loginform").serialize();
        //Piilota lomake sekä näytä spinner -animaatio kun POST -pyyntö tehdään palvelimelle
        $("#loginform").hide();
        $("#errorbox").hide();
        $("#successbox").hide();
        $("#spinner").show();
        $.ajax({
            type: "POST",
            url: "/login",
            data: credentials + "&jslogin=1"
        }).done(function (data) {
            if (data === "ok") {
                $("#successbox").show();
                $("#spinner").hide();
                window.location.href = '/';
            } else {
                $("#spinner").hide();
                $("#loginform").show();
                $("#errorbox").show();
            }
        });
    }
    $("#loginform").submit(function (event) {
        login();
        event.preventDefault();
        return false;
    });
});