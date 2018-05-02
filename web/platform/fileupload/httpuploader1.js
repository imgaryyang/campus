if (!window.System)
    window.System = {};

System.HttpUploader = function ()
{
};

System.HttpUploader.prototype.codebase = "/platform/fileupload/HttpUploader.cab#version=2,5,37,20873";
System.HttpUploader.prototype.classId = "7AAE6FD3-C2F2-49d5-A790-1103848B3531";
System.HttpUploader.prototype.id = "HttpUploader";

System.HttpUploader.prototype.create = function ()
{
    var div = document.createElement("DIV");
    div.style.width = "0";
    div.style.hegith = "0";

    var s = "<OBJECT style='left:0;top:0;height:0;width:0' classid='clsid:" +
            this.classId + "' id='" + this.id + "' codebase='" + this.codebase + "'>";

    s += "</OBJECT>";

    div.innerHTML = s;

    document.body.appendChild(div);

    this.init();
};

System.HttpUploader.prototype.init = function ()
{
    var uploader = this;
    var activeX = this.getActiveX();
    activeX.EncodeType = "utf-8";
    activeX.CompanyLicensed = "哈哈哈";
    activeX.License = "";
    activeX.FileID = 0;
    activeX.OnStateChanged = function (obj, state)
    {
        if (state == 3)
        {
            if (uploader.callback)
            {
                uploader.callback(activeX.Response);
            }
        }
    };
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

    var activeX = this.getActiveX();
    activeX.LocalFile = path;
    activeX.PostUrl = System.HttpUploader.encodeUrl(url);

    activeX.ClearFields();
    activeX.AddField("$Accept$", "application/json");
    activeX.AddField("$ajax$", "true");

    if (args)
    {
        for (var name in args)
            activeX.AddField(name, args[name]);
    }

    activeX.Post();
};
