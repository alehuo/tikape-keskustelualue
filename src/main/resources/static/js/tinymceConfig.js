tinymce.init(
        {
            selector: 'textarea',
            menubar: false,
            plugins: ["bbcode", "autolink", "link", "image", "textcolor"],
            toolbar1: "undo redo | bold italic | forecolor | link image",
            entity_encoding: "raw",
            setup: function (ed) {
                ed.on('keyup', function (e) {
                    var count = characterCount();
                    document.getElementById("chrCount").innerHTML = count;
                });
            }
        }
);
/**
 * Palauttaa merkkien lukumäärän TinyMCE -kentässä
 * @returns {.tinymce@call;trim.length|Window.length}
 */
function characterCount() {
    //Tekstikentän sisältö
    var body = tinymce.get("body").getBody();
    var content = tinymce.trim(body.innerText || body.textContent);
    return content.length;
}
;
/**
 * Palauttaa, voiko lomakkeen lähettää
 * @returns {undefined|Boolean}
 */
function canSubmit() {
    //Maksimi kirjainten lukumäärä
    var max = 4096;
    //Tekstikentän kirjainten määrä
    var count = characterCount();
    //Tarkistus
    if (count > max) {
        //Lähetetään käyttäjälle ilmoitus
        alert("Viesti saa sisältää maksimissaan " + max + " merkkiä.")
        return false;
    }
    return;
}
