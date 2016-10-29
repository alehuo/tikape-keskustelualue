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
function characterCount() {
    var body = tinymce.get("tinymceTextarea").getBody();
    var content = tinymce.trim(body.innerText || body.textContent);
    return content.length;
}
;
function canSubmit() {
    var max = 4096;
    var count = characterCount();
    if (count > max) {
        alert("Viesti saa sisältää maksimissaan " + max + " merkkiä.")
        return false;
    }
    return;
}
