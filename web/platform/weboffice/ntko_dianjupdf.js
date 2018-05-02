System.Office.prototype.id = System.Office.ID = "officeControl";
Cyan.importJs("/platform/weboffice/ntkoconfig.js");

System.Office.prototype.pdfId = System.Office.PDFID = "WebOffice";
System.Office.prototype.pdfClassId = "E77E049B-23FC-4DB8-B756-60529A35FAD5";
System.Office.prototype.pdfCodebase = "/platform/weboffice/WebOffice.ocx#Version=3,0,0,0";

System.Office.prototype.acceptXml = false;
System.Office.prototype.pdfSupportType = 2;

System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = System.Office.prototype.top || 24;

    var div = document.createElement("DIV");
    div.style.width = "100%";
    this.div = div;

    this.resize = function ()
    {
        var height = (el.clientHeight + top) + "px";
        div.style.height = height;
        var activeX = this.getActiveX();
        if (activeX && activeX.style)
        {
            activeX.style.height = height;
        }
        var pdfActiveX = this.getPdfActiveX();
        if (pdfActiveX && pdfActiveX.style)
        {
            pdfActiveX.style.height = "0";
            setTimeout(function ()
            {
                pdfActiveX.style.height = height;
            }, 10);
        }
    };

    this.resize();
    div.style.top = -top + "px";
    div.style.position = "relative";
    var s = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" +
            this.classId + "' id='" + this.id + "' codebase='" + this.codebase + "'>";

    if (System.Office.prototype.productCaption)
        s += "<param name='ProductCaption' value='" + System.Office.prototype.productCaption + "'>";

    if (System.Office.prototype.productKey)
        s += "<param name='ProductKey' value='" + System.Office.prototype.productKey + "'>";

    s += "</OBJECT>";

    div.innerHTML = s;

    el.appendChild(div);

    this.setUnload();

    var input = document.createElement("INPUT");
    input.style.position = "absolute";
    input.style.left = "-100px";
    input.style.top = "-100px";
    input.style.width = "10px";
    input.style.height = "10px";
    document.body.appendChild(input);

    Cyan.Class.overwrite(System, "onHide", function ()
    {
        if (this.inherited)
            this.inherited();

        try
        {
            input.focus();
        }
        catch (e)
        {
        }
    });

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

System.Office.prototype.getPdfActiveX = function ()
{
    return Cyan.$(this.pdfId);
};

System.Office.prototype.getFileType = function ()
{
    if (this.pdf)
        return "pdf";

    var type = this.getActiveX().DocType;

    if (type == 1)
        return "doc";
    else if (type == 2)
        return "xls";
    else if (type == 3)
        return "ppt";
    else if (type == 6)
        return "wps";
    else
        return "doc";
};

System.Office.prototype.getDocument = function ()
{
    return this.getActiveX().ActiveDocument;
};

System.Office.prototype.saveAs0 = function (fileName)
{
    if (this.pdf)
    {
        this.getPdfActiveX().SaveAs(fileName, 0);
    }
    else
    {
        this.getActiveX().ShowDialog(2);
    }
};

System.Office.prototype.print0 = function ()
{
    if (this.pdf)
    {
        this.getPdfActiveX().PrintDoc(1);
    }
    else
    {
        this.getActiveX().PrintOut(true);
    }
};

System.Office.prototype.init = function ()
{
};

System.Office.prototype.init0 = function ()
{
    try
    {
        this.getActiveX().Activate(true);
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
        this.getActiveX().Close();
    }
    catch (e)
    {
    }

    try
    {
        this.getPdfActiveX().Close();
    }
    catch (e)
    {
    }
};

System.Office.prototype.isSaved = function ()
{
    if (this.pdf)
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

System.Office.prototype.showPdf = function (visible)
{
    if (visible)
    {
        if (!this.pdfInited)
        {
            this.div.innerHTML += "<OBJECT style='left:0;top:0;height:100%;width:100%;' classid='clsid:" +
                    this.pdfClassId + "' id='" + this.pdfId + "' codebase='" + this.pdfCodebase + "'></OBJECT>";
            this.pdfInited = true;
        }

        this.pdf = true;
        this.getActiveX().style.display = "none";
        this.getPdfActiveX().style.display = "";
        this.setSaved(true);
        this.resize();

        return true;
    }
    else
    {
        this.pdf = false;
        this.getActiveX().style.display = "";
        try
        {
            this.getPdfActiveX().style.display = "none";
        }
        catch (e)
        {
        }
        return false;
    }
};

System.Office.prototype.create = function (type)
{
    if (type == "pdf")
    {
        if (this.showPdf(true))
        {
            try
            {
                this.getPdfActiveX().LoadOriginalFile("", type);
            }
            catch (e)
            {
                alert(e.message);
            }
            return;
        }
    }

    this.showPdf(false);
    var s;
    if (type == "doc")
    {
        if (this.wpsFirst)
        {
            type = "wps";
            s = "WPS.Document";
        }
        else
        {
            s = "Word.Document";
        }
    }
    else if (type == "xls")
    {
        s = "Excel.Sheet";
    }
    else if (type == "ppt")
    {
        s = "PowerPoint.Show";
    }

    try
    {
        this.getActiveX().CreateNew(s);
    }
    catch (e)
    {
        if (type == "doc")
        {
            try
            {
                this.getActiveX().CreateNew("WPS.Document");
            }
            catch (e1)
            {
                alert(e1.message);
            }
        }
        else
        {
            alert(e.message);
        }
    }
};

System.Office.prototype.load = function (path, type, callback, b)
{
    var office = this;
    Cyan.Arachne.formatSessionIdToUrl(path, function (path)
    {
        if (type == "pdf")
        {
            if (office.showPdf(true))
            {
                office.getPdfActiveX().LoadOriginalFile(System.Office.encodeUrl(path), type);
                if (callback)
                    callback();
                return;
            }
        }

        office.showPdf(false);

        if (!b)
        {
            try
            {
                if (office.getActiveX().ActiveDocument)
                {
                    office.getActiveX().Close();
                    setTimeout(function ()
                    {
                        office.load(path, type, callback, true);
                    }, 1000);
                    return;
                }
            }
            catch (e)
            {
            }
        }

        var s = "Word.Document";
        if (type == "doc" || type == "docx")
        {
            if (this.wpsFirst)
            {
                type = "wps";
                s = "WPS.Document";
            }

            else
            {
                s = "Word.Document";
            }
        }
        else if (type == "wps")
        {
            s = "WPS.Document";
        }
        else if (type == "xls" || type == "xlsx")
        {
            s = "Excel.Sheet";
        }
        else if (type == "ppt" || type == "pptx")
        {
            s = "PowerPoint.Show";
        }

        try
        {
            office.getActiveX().OpenFromURL(path, false, s);
        }
        catch (e)
        {
            if (type == "wps")
            {
                s = "Word.Document";
                try
                {
                    office.getActiveX().OpenFromURL(path, false, s);
                }
                catch (e1)
                {
                    alert(e1.message);
                }
            }
            else if (type == "doc" || type == "docx")
            {
                s = "WPS.Document";
                try
                {
                    office.getActiveX().OpenFromURL(path, false, s);
                }
                catch (e1)
                {
                    alert(e.message);
                }
            }
            else
            {
                alert(e.message);
            }
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
        var activeX, result, i, parm;
        if (office.pdf)
        {
            activeX = office.getPdfActiveX();

            activeX.HttpInit();
            if (parms)
            {
                for (i = 0; i < parms.length; i++)
                {
                    parm = parms[i];
                    activeX.HttpAddPostString(parm.name, "=?" + parm.value + "?=");
                }
            }
            activeX.HttpAddPostCurrFile(office.name, "");
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

            result = activeX.HttpPost(System.Office.encodeUrl(url));
        }
        else
        {
            activeX = office.getActiveX();

            var s = "";
            if (parms)
            {
                for (i = 0; i < parms.length; i++)
                {
                    parm = parms[i];
                    if (s)
                        s += "&";
                    s += parm.name + "=" + parm.value;
                }
            }
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

            office.setSaved(false);
            result = activeX.SaveToURL(url, office.name, s);
            office.setSaved(true);
        }

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
        if (type == "pdf")
        {
            if (this.showPdf(true))
            {
                this.getPdfActiveX().LoadOriginalFile(path, type);
                this.initFile();
                return;
            }
        }

        this.showPdf(false);
        this.getActiveX().OpenLocalFile(path);
    }
    else
    {
        if (type == "pdf")
        {
            if (this.showPdf(true))
            {
                this.getPdfActiveX().OpenFileDlg();
                this.initFile();
                return;
            }
        }

        this.getActiveX().ShowDialog(1);
        this.showPdf(false);
    }
    this.initFile();
};

System.Office.prototype.addImage = function (url, left, top)
{
    this.getActiveX().AddPicFromURL(url, true, left, top, 3, 100, 1);
};

System.Office.prototype.maximize = function ()
{
    if (this.pdf)
    {
        return false;
    }
    else
    {
        this.getActiveX().FullScreenMode = true;
        return true;
    }
};

System.Office.prototype.openPdfFile = function ()
{
    this.showPdf(true);
    this.getPdfActiveX().OpenFileDlg();
};