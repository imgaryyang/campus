System.Office.prototype.id = System.Office.ID = "webwps_id";
System.Office.prototype.type = "application/x-wps";

System.Office.prototype.acceptXml = false;
System.Office.prototype.pdfSupportType = 0;

System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = 0;

    var div = document.createElement("DIV");
    div.style.width = "100%";

    this.resize = function ()
    {
        var height = (el.clientHeight + top) + "px";
        div.style.height = height;
        var activeX = this.getActiveX();
        if (activeX && activeX.style)
            activeX.style.height = height;
    };

    this.resize();
    div.style.top = -top + "px";
    div.style.position = "absolute";

    var data = "/platform/weboffice/empty.wps";

    var s = "<OBJECT style='left:0;top:0;height:100%;width:100%' type='" +
            this.type + "' id='" + this.id + "' name='webwps' data='" + System.Office.encodeUrl(data) + "'>";

    s += "<param name='quality' value='high'>";
    s += "<param name='bgcolor' value='#ffffff'>";
    s += "<param name='Enabled' value='1'>";
    s += "<param name='allowFullScreen' value='true'>";

    s += "</OBJECT>";


    div.innerHTML = s;

    el.appendChild(div);

    if (this.init)
        this.init();

    this.setUnload();

    if (callback)
    {
        var office = this;
        Cyan.run(function ()
        {
            return office.getActiveX().Application;
        }, callback);
    }
};

System.Office.encodeUrl = function (path)
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

System.Office.prototype.getFileType = function ()
{
    return "wps";
};

System.Office.prototype.getDocument = function ()
{
    try
    {
        return this.getActiveX().Application.Documents.ActiveDocument();
    }
    catch (e)
    {
        return null;
    }
};

System.Office.prototype.print0 = function ()
{
    this.getActiveX().Application.print();
};

System.Office.prototype.init = function ()
{
};

System.Office.prototype.release = function ()
{
};

System.Office.prototype.create = function (type)
{
    try
    {
        this.getActiveX().Application.createDocument("wps");
    }
    catch (e)
    {
        alert(e.message);
    }
};

System.Office.prototype.load = function (path, type, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdAndCsrfTokenToUrl(System.Office.encodeUrl(path), function (path)
    {
        try
        {
            office.getActiveX().Application.openDocumentRemote(path, false);
        }
        catch (e)
        {
            alert(e.message);
        }

        Cyan.wait("");
        if (callback)
        {
            setTimeout(callback, 50);
        }
    });
};

System.Office.prototype.save = function (url, parms, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdAndCsrfTokenToUrl(url, function ()
    {
        var s = "";
        if (parms)
        {
            for (var i = 0; i < parms.length; i++)
            {
                var parm = parms[i];
                if (s)
                    s += "&";
                s += parm.name + "=" + parm.value;
            }
        }
        url = System.Office.encodeUrl(url);

        if (s)
            url += (url.indexOf("?") >= 0 ? "&" : "?") + s;

        url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

        office.setSaved(false);
        office.getActiveX().Application.saveURL(url, "aa.wps");
        office.setSaved(true);

        if (callback)
            callback();
    });
};

System.Office.prototype.openLocalFile = function (path, type)
{
    if (path)
    {
        this.getActiveX().Application.openDocument(path, false);
    }
    else
    {
    }
    this.initFile();
};

System.Office.prototype.addImage = function (url, left, top)
{
};