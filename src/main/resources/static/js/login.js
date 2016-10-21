$(document).ready(function () {
    /*
     * Kirjautuminen sisään
     */
    $("#loginform").submit(function (event) {
        //Kirjautumistiedot
        var credentials = $("#loginform").serialize();
        //Alkutila
        //Piilota lomake sekä näytä spinner -animaatio kun POST -pyyntö tehdään palvelimelle
        $("#loginform").hide();
        $("#errorbox").hide();
        $("#successbox").hide();
        $("#spinner").show();
        //Tehdään /login -reittiin POST -pyyntö, joka lähettää 
        //käyttäjätunnuksen ja salasanan lisäksi jslogin -nimisen lomaketiedon,
        //jolla tarkistetaan tuliko kirjautumispyyntö lomakkeen vai tämän scriptin kautta.
        //Kirjautumisen todentaminen tapahtuu palvelimen puolella, mutta tämä skripti toimii
        //käyttäjän selaimessa.
        //tilanteissa joissa sivusto hidastelee, tämä tapa kirjautua on käyttäjäystävällisempi.
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
        event.preventDefault();
        return false;
    });
});