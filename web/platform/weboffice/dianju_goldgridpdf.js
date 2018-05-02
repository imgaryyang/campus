System.Office.prototype.id = System.Office.ID = "WebOffice";
System.Office.prototype.classId = "E77E049B-23FC-4DB8-B756-60529A35FAD5";
System.Office.prototype.codebase = "/platform/weboffice/WebOffice.ocx#Version=3,0,0,0";

System.Office.prototype.pdfId = System.Office.PDFID = "iWebPdf";
System.Office.prototype.pdfClassId = "39E08D82-C8AC-4934-BE07-F6E816FD47A1";
System.Office.prototype.pdfCodebase = "/platform/weboffice/iWebPDF.cab#version=7,2,0,338";

System.Office.prototype.pdfSupportType = 0;

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
System.Office.prototype.getPdfActiveX = function ()
{
    return Cyan.$(this.pdfId);
};
System.Office.prototype.render = function (el, callback)
{
    el = Cyan.$(el);

    //控件绝对位置往上移动28个像素，以隐藏顶端工具栏
    var top = 28;
    var pdfTop = 48;

    var div = document.createElement("DIV");
    div.style.width = "100%";

    this.resize = function ()
    {
        var top1 = top;
        if (this.pdf)
            top1 = pdfTop;
        div.style.top = -top1 + "px";
        div.style.height = (el.clientHeight + top1) + "px";
    };

    this.resize();
    div.style.top = -top + "px";
    div.style.position = "absolute";
    div.innerHTML = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" +
    this.classId + "' id='" + this.id + "' codebase='" + this.codebase + "'></OBJECT>";

    el.appendChild(div);

    this.div = div;

    if (this.init)
        this.init();

    this.setUnload();

    if (callback)
        callback();
};

System.Office.prototype.getFileType = function ()
{
    if (this.pdf)
        return "pdf";

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
    if (this.pdf)
    {
        this.getPdfActiveX().FileName = fileName;
        this.getPdfActiveX().WebSaveLocalFile(this.getPdfActiveX().WebSaveLocalDialog());
    }
    else
    {
        this.getActiveX().SaveAs(fileName, 0);
    }
};

System.Office.prototype.print0 = function ()
{
    if (this.pdf)
    {
        this.getPdfActiveX().WebPrint(0, "", 0, 0, true);
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
        this.getPdfActiveX().WebClose();
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
            this.div.innerHTML += "<OBJECT style='left:0;top:0;height:100%;width:100%;display:none' classid='clsid:" +
            this.pdfClassId + "' id='" + this.pdfId + "' codebase='" + this.pdfCodebase + "'></OBJECT>";
            this.pdfInited = true;
        }


        try
        {
            this.getPdfActiveX().EnableTools("打开文档;保存文档;关闭文档", false);
        }
        catch (e)
        {
            return false;
        }

        this.pdf = true;
        this.getActiveX().style.display = "none";
        this.getPdfActiveX().style.display = "";
        this.getPdfActiveX().ShowSigns = 1;
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
            return;
    }

    this.showPdf(false);
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
    if (type == "pdf")
    {
        if (this.showPdf(true))
        {
            this.getPdfActiveX().WebOpenUrlFile(System.Office.encodeUrl(path));
            return;
        }
    }

    this.showPdf(false);
    try
    {
        this.getActiveX().LoadOriginalFile(System.Office.encodeUrl(path), type);
    }
    catch (e)
    {
        alert(e.message);
    }
    try
    {
        this.getDocument().Application.Options.ConfirmConversions = false;
    }
    catch (e)
    {
    }

    if (callback)
        callback();
};

System.Office.prototype.save = function (url, parms, callback)
{
    if (this.pdf)
    {
        this.getPdfActiveX().WebSaveLocalFile("c:\\test.pdf");
        this.getActiveX().LoadOriginalFile("c:\\test.pdf", "pdf");
    }

    var activeX = this.getActiveX();

    activeX.HttpInit();

    if (parms)
    {
        for (var i = 0; i < parms.length; i++)
        {
            var parm = parms[i];
            activeX.HttpAddPostString(parm.name, "=?" + parm.value + "?=");
        }
    }
    activeX.HttpAddPostCurrFile(this.name, "");
    url += (url.indexOf("?") >= 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true";

    var result = activeX.HttpPost(System.Office.encodeUrl(url));

    if (callback)
    {
        callback(result);
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

System.Office.prototype.openLocalFile = function (path, type)
{
    if (path)
    {
        if (type == "pdf")
        {
            if (this.showPdf(true))
            {
                this.getPdfActiveX().WebOpenLocalFile(path);
                this.initFile();
                return;
            }
        }

        this.showPdf(false);
        this.getActiveX().LoadOriginalFile(path, type);
    }
    else
    {
        if (type == "pdf")
        {
            if (this.showPdf(true))
            {
                this.getPdfActiveX().WebOpenLocalFile(this.getPdfActiveX().WebOpenLocalDialog());
                this.initFile();
                return;
            }
        }

        this.getActiveX().OpenFileDlg();
        this.showPdf(false);
    }
    this.initFile();
};