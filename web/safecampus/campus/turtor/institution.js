
function chooseFile() {
    var preview = document.getElementById("imageId");
    var file = document.getElementById("t_file").files[0];
    var reader = new FileReader();
    reader.onloadend = function () {
        preview.src = reader.result;
    }
    if (file) {
        reader.readAsDataURL(file);
    } else {
        preview.src = "";
    }
}


function saveInst() {
    save(function () {
        closeWindow(function () {
            mainBody.reload();
        })
    })
}















