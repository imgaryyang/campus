System.Office.prototype.id = System.Office.ID = "TANGER_OCX";
Cyan.importJs("/platform/weboffice/ntko5config.js");

System.Office.prototype.dianjuId = System.Office.DIANJU_ID = System.Office.ID = "WebOffice";
System.Office.prototype.dianjuClassId = "E77E049B-23FC-4DB8-B756-60529A35FAD5";
System.Office.prototype.dianjuCodebase = "/platform/weboffice/WebOffice.ocx#Version=3,0,0,0";

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
document.write("<script for='" + System.Office.DIANJU_ID +
        "' event='NotifyWordEvent(event)'>System.Office.notifyEvent(event);</script>");

System.Office.notifyEvent = function (event)
{
    if (event == 'DocumentBeforeClose')
    {
        Cyan.$(System.Office.ID).lContinue = 0;
    }
};

System.Office.prototype.getActiveX = function ()
{
    return Cyan.$(this.dianjuId);
};

System.Office.prototype.getTifActiveX = function ()
{
    return Cyan.$(this.id);
};

System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = 28;
    var tifTop = System.Office.prototype.top || 72;

    var div = document.createElement("DIV");
    div.style.width = "100%";

    this.resize = function ()
    {
        var top1 = top;
        if (this.tif)
            top1 = tifTop;
        var height = (el.clientHeight + top1) + "px";
        div.style.height = height;
        var activeX = this.getActiveX();
        if (activeX && activeX.style)
        {
            activeX.style.height = "0";
            setTimeout(function ()
            {
                activeX.style.height = height;
            }, 10);
        }
    };

    this.resize();
    div.style.top = -top + "px";
    div.style.position = "absolute";
    div.innerHTML = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" +
            this.dianjuClassId + "' id='" + this.dianjuId + "' codebase='" + this.dianjuCodebase + "'></OBJECT>";

    el.appendChild(div);

    this.div = div;

    this.resize();

    this.setUnload();

    var office = this;
    var f = function ()
    {
        if (office.init)
            office.init();

        if (callback)
            callback();
    };

    this.loading(function ()
    {
        return office.init0();
    }, f);
};

System.Office.prototype.getFileType = function ()
{
    if (this.tif)
        return "tif";

    var type = this.getActiveX().DocType;

    if (type == 11)
        return "doc";
    else if (type == 12)
        return "xls";
    else if (type == 12)
        return "ppt";
    else if (type == 31)
        return "pdf";

    return type;
};

System.Office.prototype.getDocument = function ()
{
    return this.getActiveX().GetDocumentObject();
};

System.Office.prototype.saveAs0 = function (fileName)
{
    if (this.tif)
    {
        this.getTifActiveX().ShowDialog(2);
    }
    else
    {
        this.getActiveX().SaveAs(fileName, 0);
    }
};

System.Office.prototype.print0 = function ()
{
    if (this.tif)
    {
        this.getTifActiveX().PrintOut(true);
    }
    else
    {
        this.getActiveX().PrintDoc(1);
    }
};

System.Office.prototype.init = function ()
{
    this.getActiveX().HideMenuItem(0x01 + 0x02 + 0x04 + 0x10 + 0x20 + 0x1000 + 0x2000);
};

System.Office.prototype.init0 = function ()
{
    try
    {
        this.getActiveX().IsOpened();
        return true;
    }
    catch (e)
    {
        return false;
    }
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
    try
    {
        this.getTifActiveX().Close();
    }
    catch (e)
    {
    }
};

System.Office.prototype.isSaved = function ()
{
    if (this.tif)
        return true;

    try
    {
        return this.getDocument().Saved;
    }
    catch (e)
    {
        return true;
    }
};

System.Office.prototype.showTif = function (visible)
{
    if (visible)
    {
        if (!this.tifInited)
        {
            var s = "<OBJECT style='left:0;top:0;height:100%;width:100%;display:none' classid='clsid:" + this.classId +
                    "' id='" + this.id + "' codebase='" + this.codebase +
                    "'>\n";

            if (System.Office.prototype.productCaption)
                s += "<param name='ProductCaption' value='" + System.Office.prototype.productCaption + "'>";

            if (System.Office.prototype.productKey)
                s += "<param name='ProductKey' value='" + System.Office.prototype.productKey + "'>";

            s += "</OBJECT>";

            this.div.innerHTML += s;
            this.tifInited = true;
        }

        var activeX = this.getTifActiveX();
        if (this.oledocCodebase)
        {
            try
            {
                activeX.AddDocTypePlugin(".pdf", "PDF.NtkoDocument", "4.0.0.0", this.oledocCodebase, 51, true);
                activeX.AddDocTypePlugin(".tif", "TIF.NtkoDocument", "4.0.0.0", this.oledocCodebase, 52);
            }
            catch (e)
            {
            }
        }

        this.tif = true;
        this.getActiveX().style.display = "none";
        this.getTifActiveX().style.display = "";
        this.setSaved(true);
        this.resize();

        return true;
    }
    else
    {
        this.tif = false;
        this.getActiveX().style.display = "";
        try
        {
            this.getTifActiveX().style.display = "none";
        }
        catch (e)
        {
        }
        return false;
    }
};

System.Office.prototype.create = function (type)
{
    if (type == "tif")
    {
        if (this.showTif(true))
            return;
    }

    this.showTif(false);
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
    Cyan.Arachne.formatSessionIdToUrl(path, function (path)
    {
        if (type == "tif")
        {
            if (office.showTif(true))
            {
                office.getTifActiveX().OpenFromURL(path, false, "TIF.NtkoDocument");
                if (callback)
                    callback();
                return;
            }
        }
        this.showTif(false);
        try
        {
            office.getActiveX().LoadOriginalFile(System.Office.encodeUrl(path), type);
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
    if (this.tif)
        return;

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
    {
        this.getActiveX().LoadOriginalFile(path, type);
        this.initFile();
    }
    else
    {
        if (this.getActiveX().OpenFileDlg())
        {
            this.initFile();
        }
    }
};