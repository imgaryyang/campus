System.Office.prototype.id = System.Office.ID = "officeControl";
Cyan.importJs("/platform/weboffice/ntkoconfig.js");

System.Office.prototype.acceptXml = false;
System.Office.prototype.pdfSupportType = 0;

System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = System.Office.prototype.top || 24;

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
    else if (type == 31)
        return "pdf";
    else
        return "doc";
};

System.Office.prototype.getDocument = function ()
{
    return this.getActiveX().ActiveDocument;
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
    }
    catch (e)
    {
        if (type == "doc" || type == "docx")
        {
            try
            {
                this.getActiveX().CreateNew("WPS.Document");
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

System.Office.prototype.load = function (path, type, callback, b)
{
    var office = this;
    Cyan.Arachne.formatSessionIdToUrl(path, function (path)
    {
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
                    alert(e.message);
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

        office.setSaved(false);
        var result = activeX.SaveToURL(url, office.name, s);
        office.setSaved(true);

        if (callback)
            callback(result);
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