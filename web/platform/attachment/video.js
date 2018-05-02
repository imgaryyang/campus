function ok()
{
    save(function (uuid)
    {
        var path = "/attachment/" + uuid + "/1";
        var name = Cyan.$("file").value;

        var name0 = Cyan.getFileName(name, false);
        name = Cyan.getFileName(name, true);

        if (name0.indexOf(".") >= 0 || name0.indexOf(" ") >= 0 || name0.indexOf("/") >= 0 || name0.indexOf(":") >= 0)
        {
            path += "/" + Cyan.replaceAll(Cyan.replaceAll(Cyan.replaceAll(name0, ".", ""), " ", ":"));

            var extName = Cyan.getExtName(name);
            if (extName)
                path += "." + extName;
        }
        else
        {
            path += "/" + name;
        }

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
    Cyan.Window.closeWindow();
}