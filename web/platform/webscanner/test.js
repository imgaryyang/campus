Cyan.onload(function ()
{
    var upload = new Cyan.FileUpload("file", false);
    upload.bind($("upload"));

    upload.onchange = function ()
    {
        uploadImg();
    };
});

function scan()
{
    System.Scanner.init();
    if (System.Scanner.scan())
    {
        save(System.Scanner.toBase64(), showImage);
    }
}

function showImage()
{
    var ul = $$("ul")[0];
    var li = document.createElement("LI");

    ul.appendChild(li);

    var img = document.createElement("IMG");
    li.appendChild(img);

    img.src = "/webscaner/test/img?" + Math.random();
}

function uploadImg()
{
    upload({
        callback:showImage,
        form:$$("form")[0]
    });
}

