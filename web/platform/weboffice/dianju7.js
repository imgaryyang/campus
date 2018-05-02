System.Office.prototype.id = System.Office.ID = "WebOffice";
System.Office.prototype.classId = "E77E049B-23FC-4DB8-B756-60529A35FAD5";
System.Office.prototype.codebase = "/platform/weboffice/WebOffice.cab#Version=7,0,0,8";
System.Office.prototype.pdfSupportType = 1;

System.Office.closeConfirmConversions = function ()
{
    try
    {
        //设置打开xml时不提示格式转化
        var wordApplication = new ActiveXObject("Word.Application");
        wordApplication.Options.ConfirmConversions = false;
        wordApplication.Quit();
        delete wordApplication;
    }
    catch (e)
    {
    }
};

//禁止关闭文档
document.write("<script for='" + System.Office.ID +
        "' event='NotifyWordEvent(event)'>System.Office.notifyEvent(event);</script>");

System.Office.notifyEvent = function (event)
{
    if (event == 'DocumentBeforeClose')
    {
        Cyan.$(System.Office.ID).lContinue = 0;
    }
};
System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动15个像素，以隐藏顶端工具栏
    var top = 15;

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
    div.style.position = "relative";
    div.innerHTML = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" +
            this.classId + "' id='" + this.id + "' codebase='" + this.codebase + "'></OBJECT>";

    el.appendChild(div);

    this.getActiveX().ShowToolBar = false;

    if (this.init)
        this.init();

    this.setUnload();

    if (callback)
        callback();
};

System.Office.prototype.getFileType = function ()
{
    var type = this.getActiveX().DocType;

    if (type == 11)
        return "doc";
    else if (type == 12)
        return "xls";
    else if (type == 12)
        return "ppt";
    else if (type == 31)
        return "pdf";
    else if (type == 21)
        return "wps";

    return type;
};

System.Office.prototype.getDocument = function ()
{
    return this.getActiveX().GetDocumentObject();
};

System.Office.prototype.saveAs0 = function (fileName)
{
    this.getActiveX().ShowDialog(84);
};

System.Office.prototype.print0 = function ()
{
    this.getActiveX().PrintDoc(1);
};

System.Office.prototype.init = function ()
{
    this.getActiveX().HideMenuItem(0x01 + 0x02 + 0x04 + 0x10 + 0x20 + 0x1000 + 0x2000);
};

System.Office.prototype.release = function ()
{
    try
    {
        this.getActiveX().OptionFlag |= 0x0010;
    }
    catch (e)
    {
    }
    try
    {
        this.setSaved();
    }
    catch (e)
    {
    }
    try
    {
        //  this.getDocument().Application.Quit();
    }
    catch (e)
    {
    }
    try
    {
        this.getActiveX().Close();
    }
    catch (e)
    {
    }
};

System.Office.prototype.create = function (type)
{
    try
    {
        this.getActiveX().LoadOriginalFile("", type);
    }
    catch (e)
    {
        alert(e.message);
    }
};

System.Office.prototype.load = function (path, type, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdToUrl(System.Office.encodeUrl(path), function (path)
    {
        try
        {
            office.getActiveX().LoadOriginalFile(path, type);
        }
        catch (e)
        {
            alert(e.message);
        }
        try
        {
            office.getDocument().Application.Options.ConfirmConversions = false;
        }
        catch (e)
        {
        }

        if (callback)
            callback();
    });
};

System.Office.prototype.save = function (url, parms, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdAndCsrfTokenToUrl(url, function ()
    {
        var activeX = office.getActiveX();
        activeX.HttpInit();

        if (parms)
        {
            for (var i = 0; i < parms.length; i++)
            {
                var parm = parms[i];
                activeX.HttpAddPostString(parm.name, "=?" + parm.value + "?=");
            }
        }
        activeX.HttpAddPostCurrFile(office.name, "");
        url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

        office.setSaved(false);
        var result = activeX.HttpPost(System.Office.encodeUrl(url));
        office.setSaved(true);

        if (callback)
            callback(result);
    });
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

System.Office.prototype.openLocalFile = function (path, type)
{
    if (path)
        this.getActiveX().LoadOriginalFile(path, type);
    else
        this.getActiveX().OpenFileDlg();
    this.initFile();
};