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

        Cyan.Window.closeWindow({
            path: path,
            name: name
        });
    });
}

function cancel()
{
    Cyan.Window.closeWindow();
}