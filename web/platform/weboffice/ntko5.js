System.Office.prototype.id = System.Office.ID = "TANGER_OCX";
Cyan.importJs("/platform/weboffice/ntko5config.js");

System.Office.prototype.acceptXml = false;
System.Office.prototype.pdfSupportType = 1;

System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = System.Office.prototype.top || 72;

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
    var s = "<OBJECT style='left:0;top:0;height:100%;width:100%' type='application/ntko-plug' ";

    if (Cyan.navigator.isIE())
    {
        s += "classid='clsid:" + this.classId + "'";
    }
    else
    {
        s += "clsid='{" + this.classId + "}'"
    }

    s += " id='" + this.id + "' codebase='" + this.codebase +
            "' ForOnDocumentOpened='onAfterOpenFromURL' ForOnSaveToURL='onAfterSaveToURL'>\n";

    if (System.Office.prototype.productCaption)
        s += "<param name='ProductCaption' value='" + System.Office.prototype.productCaption + "'>";

    if (System.Office.prototype.productKey)
        s += "<param name='ProductKey' value='" + System.Office.prototype.productKey + "'>";

    s += "</OBJECT>";
    div.innerHTML = s;

    el.appendChild(div);

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

function onAfterOpenFromURL()
{
    var office = window.openingOffice;

    if (!office)
        return;

    office.loaded = true;
    try
    {
        office.getDocument().Application.Options.ConfirmConversions = false;
    }
    catch (e)
    {
    }

    var openFromURLCallback = office.openFromURLCallback;

    office.openFromURLCallback = null;
    window.openingOffice = null;

    if (openFromURLCallback)
        openFromURLCallback();
}

function onAfterSaveToURL(type, code, result)
{
    var office = window.savingOffice;

    if (!office)
        return;

    var saveToURLCallback = office.saveToURLCallback;
    office.saveToURLCallback = null;
    window.savingOffice = null;

    if (saveToURLCallback)
    {
        saveToURLCallback(result);
    }
}

System.Office.prototype.getFileType = function ()
{
    var type = this.getActiveX().DocType;

    if (type == 1)
        return "doc";
    else if (type == 2)
        return "xls";
    else if (type == 3)
        return "ppt";
    else if (type == 6)
        return "wps";
    else if (type == 51)
        return "pdf";
    else if (type == 52)
        return "tif";
    else
        return "doc";
};

System.Office.prototype.getDocument = function ()
{
    if (this.loaded)
        return this.getActiveX().ActiveDocument;
    else
        return null;
};

System.Office.prototype.saveAs0 = function (fileName)
{
    this.getActiveX().ShowDialog(2);
};

System.Office.prototype.print0 = function ()
{
    this.getActiveX().PrintOut(true);
};

System.Office.prototype.init = function ()
{
};

System.Office.prototype.init0 = function ()
{
    var activeX = this.getActiveX();
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

    try
    {
        activeX.activate(true);
        return true;
    }
    catch (e)
    {
        return false;
    }
};

System.Office.prototype.release = function ()
{
    this.getActiveX().Close();
};

System.Office.prototype.create = function (type)
{
    var s;
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
    else if (type == "xls" || type == "xlsx")
    {
        s = "Excel.Sheet";
    }
    else if (type == "ppt" || type == "pptx")
    {
        s = "PowerPoint.Show";
    }
    else if (type == "wps")
    {
        s = "WPS.Document";
    }

    try
    {
        this.getActiveX().CreateNew(s);
        this.loaded = true;
    }
    catch (e)
    {
        if (type == "doc")
        {
            try
            {
                this.getActiveX().CreateNew("WPS.Document");
                this.loaded = true;
            }
            catch (e1)
            {
                alert(e.message);
            }
        }
        else if (type == "wps")
        {
            try
            {
                this.getActiveX().CreateNew("Word.Document");
                this.loaded = true;
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
};

System.Office.prototype.load = function (path, type, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdToUrl(path, function (path)
    {
        var s = "Word.Document";
        if (type == "doc" || type == "docx")
        {
            if (office.wpsFirst)
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
        else if (type == "pdf")
        {
            s = "PDF.NtkoDocument";
        }
        else if (type == "tif")
        {
            s = "TIF.NtkoDocument";
        }

        if (!Cyan.navigator.isIE())
        {
            office.openFromURLCallback = callback;
            window.openingOffice = office;
        }

        var f = function ()
        {
            try
            {
                office.getActiveX().OpenFromURL(path, false, s);
            }
            catch (e)
            {
                try
                {
                    if (type == "wps")
                    {
                        s = "Word.Document";
                        office.getActiveX().OpenFromURL(path, false, s);
                    }
                    else if (type == "doc" || type == "docx")
                    {
                        s = "WPS.Document";
                        office.getActiveX().OpenFromURL(path, false, s);
                    }
                    else
                    {
                        alert(e.message);
                    }
                }
                catch (e1)
                {
                    try
                    {
                        office.getActiveX().OpenFromURL(path);
                    }
                    catch (e2)
                    {
                        alert(e.message);
                    }
                }
            }

            if (Cyan.navigator.isIE())
            {
                office.loaded = true;
                try
                {
                    office.getDocument().Application.Options.ConfirmConversions = false;
                }
                catch (e)
                {
                }

                if (callback)
                    callback();
            }
        };

        if (office.getDocument() == null)
        {
            f();
        }
        else
        {
            office.getActiveX().Close();
            setTimeout(f, 500);
        }
    });
};

System.Office.prototype.save = function (url, parms, callback)
{
    var office = this;
    Cyan.Arachne.formatSessionIdAndCsrfTokenToUrl(url, function ()
    {
        var activeX = office.getActiveX();

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
        url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

        var save = function ()
        {
            var result;
            var fileType = office.getFileType();
            office.setSaved(false);
            if (fileType == "doc")
                result = activeX.SaveAsOtherFormatToURL(5, url, office.name, s);
            else if (fileType == "xls")
                result = activeX.SaveAsOtherFormatToURL(6, url, office.name, s);

            if (fileType != "doc" && fileType != "xls" || result.indexOf("错误") >= 0)
                result = activeX.SaveToURL(url, office.name, s);

            office.setSaved(true);

            return result;
        };

        if (Cyan.navigator.isIE())
        {
            var result = save();
            if (callback)
            {
                callback(result);
            }
        }
        else
        {
            office.saveToURLCallback = callback;
            window.savingOffice = office;
            save();
        }
    });
};

System.Office.prototype.openLocalFile = function (path, type)
{
    if (path)
    {
        var s = "Word.Document";
        if (type == "doc" || type == "docx")
            s = "Word.Document";
        else if (type == "wps")
            s = "WPS.Document";
        else if (type == "xls" || type == "xlsx")
            s = "Excel.Sheet";
        else if (type == "ppt" || type == "pptx")
            s = "PowerPoint.Show";
        else if (type == "pdf")
            s = "PDF.NtkoDocumen";
        else if (type == "tif")
            s = "TIF.NtkoDocument";

        this.getActiveX().OpenLocalFile(path, false, s);
    }
    else
    {
        this.getActiveX().ShowDialog(1);
    }
    this.initFile();
};

System.Office.prototype.addImage = function (url, left, top)
{
    this.getActiveX().AddPicFromURL(url, true, left, top, 3, 100, 1);
};

System.Office.prototype.maximize = function ()
{
    this.getActiveX().FullScreenMode = true;
    return true;
};