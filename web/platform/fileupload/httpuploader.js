if (!window.System)
    window.System = {};

System.HttpUploader = function ()
{
};

System.HttpUploader.prototype.classId = "0339EBB3-FF19-357E-ABF5-B10F5CB1D74A";
System.HttpUploader.prototype.id = "fileToBase64";

System.HttpUploader.prototype.create = function ()
{
    var div = document.createElement("DIV");
    div.style.width = "0";
    div.style.hegith = "0";

    var s = "<OBJECT style='left:0;top:0;height:0;width:0' classid='clsid:" +
            this.classId + "' id='" + this.id + "'>";

    s += "</OBJECT>";

    div.innerHTML = s;

    document.body.appendChild(div);

    this.init();
};

System.HttpUploader.prototype.init = function ()
{
};

System.HttpUploader.prototype.getActiveX = function ()
{
    return Cyan.$(this.id);
};

System.HttpUploader.encodeUrl = function (path)
{
    var s = "";
    var href = location.href;
    var index = href.indexOf("//");
    if (index >= 0)
    {
        s += href.substring(0, index + 2);
        href = href.substring(index + 2);
    }

    index = href.indexOf("/");
    if (index >= 0)
        href = href.substring(0, index);
    s += href;

    if (!path.startsWith("/"))
        s += "/" + path;
    else
        s += path;

    return s;
};

System.HttpUploader.prototype.post = function (path, url, args, calback)
{
    this.callback = calback;

    var fileToBase64 = this.getActiveX();

    if (fileToBase64.ToBase64String(path) == 0)
    {
        var obj = {base64: fileToBase64.FileBase64String};

        if (args)
        {
            for (var name in args)
                obj[name] = args[name];
        }

        Cyan.Arachne.post(url, obj, calback);
    }
    else
    {
    }
};