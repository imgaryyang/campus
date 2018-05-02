function ok()
{
    var file = Cyan.$("file");
    if (!file || !file.value)
    {
        Cyan.message("您未选择图片", function ()
        {
            Cyan.Window.closeWindow();
        });
        return;
    }
    save(function (uuid)
    {
        var path = "/attachment/" + uuid + "/1";

        var name = Cyan.$("file").value;
        var pos = Math.max(name.lastIndexOf("/"), name.lastIndexOf("\\"));
        if (pos >= 0)
            name = name.substring(pos + 1);
        path += "/" + name.toLowerCase();

        var width = Cyan.$("width").value;
        if (width)
            width = Cyan.toInt(width);
        else
            width = null;

        var height = Cyan.$("height").value;
        if (height)
            height = Cyan.toInt(height);
        else
            height = null;

        Cyan.Window.closeWindow({
            path: path,
            name: name,
            width: width,
            height: height
        });
    });
}

function cancel()
{
    Cyan.Window.closeWindow(null);
}

Cyan.onload(function ()
{
    var onselect = function (file)
    {
        Cyan.Arachne.getImagePath(file, function (path)
        {
            Cyan.$("img").src = path;

            var image = new Image();
            image.src = path;
            image.onload = function ()
            {
                Cyan.$("width").value = this.width;
                Cyan.$("height").value = this.height;
            };
        });
    };
    var fileUpload = new Cyan.FileUpload("file");
    fileUpload.onselect = onselect;
    fileUpload.bind(Cyan.$("img")).bind(Cyan.$("upload"))
});