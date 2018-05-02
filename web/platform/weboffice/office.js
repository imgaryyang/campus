System.Office = function (name)
{
    System.stopFlash();

    this.name = name;
    if (!this.id)
        this.id = "office$" + Math.random().toString().substring(2);

    //0可修改，并非修订状态，1可修改，修订状态，2只读
    this.editType = 0;
};

System.Office.prototype.password = "gzzm2010";
System.Office.prototype.protectionType = 1;

System.Office.prototype._NEW = 18;
System.Office.prototype._OPEN = 23;
System.Office.prototype._SAVE = 3;
System.Office.prototype._CLOSE = 106;
System.Office.prototype._EXIT = 752;
System.Office.prototype._FULL_SCREEN = 178;
System.Office.prototype._NEW_BLANK = 2520;
System.Office.prototype._TRACK_REVISIONS = 2041;
System.Office.prototype._ACCEPT_TRACK_REVISIONS = 6240;
System.Office.prototype._REJECT_TRACK_REVISIONS = 6243;
System.Office.prototype._ACCEPT_TRACK_REVISIONS1 = 1715;
System.Office.prototype._REJECT_TRACK_REVISIONS1 = 1716;
System.Office.prototype._FILE = 30002;
System.Office.prototype._VIEW = 30004;
System.Office.prototype._TOOLS = 30007;
System.Office.prototype._TOOLS_BAR = 30045;
System.Office.prototype._WINDOWS = 30009;

System.Office.prototype.acceptXml = true;


System.Office.prototype.render = function (el, callbback)
{
    el = Cyan.$(el);

    el.innerHTML = "<OBJECT style='left:0;top:0;height:100%;width:100%' classid='clsid:" +
            this.classId + "' id='" + this.id + "' codebase='" + this.codebase + "'></OBJECT>";
    if (this.init)
    {
        try
        {
            this.init();
        }
        catch (e)
        {
            alert("word控件加载失败，请到登录页面下载word控件进行安装")
        }
    }

    this.setUnload();

    if (callbback)
        callbback();
};

System.Office.prototype.loading = function (init0, init)
{
    if (init0())
    {
        init();
    }
    else
    {
        Cyan.displayProgress(0, "加载控件", "office控件未加载，正在加载控件");
        var interval = window.setInterval(function ()
        {
            if (init0())
            {
                window.clearInterval(interval);
                Cyan.displayProgress(-1, "", "");
                init();
            }
        }, 500);
    }
};

System.Office.prototype.setUnload = function ()
{
    var office = this;
    window.onunload = function ()
    {
        System.startFlash();

        if (!office.onunload())
            return false;

        if (window.parent != window)
            window.parent.Cyan.wait("正在关闭office");
        try
        {
            if (office.release)
                office.release();
        }
        finally
        {
            if (window.parent != window)
                window.parent.Cyan.wait("");
        }

        return true;
    };
};

System.Office.prototype.getActiveX = function ()
{
    return Cyan.$(this.id);
};

System.Office.prototype.onunload = function ()
{
    try
    {
        //关闭之前还原系统的用户名
        this.resetUserName();
    }
    catch (e)
    {
    }
    try
    {
        //还原状态栏的显示状态
        this.resetStatusBar();
    }
    catch (e)
    {
    }

    return true;
};

System.Office.prototype.newFile = function (type)
{
    if (!type)
        type = this.fileType;

    if (!type)
        type = "doc";

    this.create(type);
    this.fileType = type;

    this.initFile();

    try
    {
        this.getDocument().Activate();
    }
    catch (e)
    {
    }
};

System.Office.prototype.openFile = function (path, type, callback)
{
    if (!path)
        return;

    if (!type)
        type = this.fileType;

    if (!type)
        type = "doc";


    var office = this;
    this.fileType = type;
    Cyan.wait("正在加载文档");
    this.load(path, type, function ()
    {
        office.afterLoad();
        if (callback)
            callback();
    });
};

System.Office.prototype.afterLoad = function ()
{
    Cyan.wait("");

    try
    {
        //this.getDocument().Save();
    }
    catch (e)
    {
    }

    this.setSaved();
    this.initFile();

    try
    {
        this.getDocument().Activate();
    }
    catch (e)
    {
    }
};

System.Office.prototype.initFile = function ()
{
    if (this.fileType == "doc" || this.fileType == "wps" || this.fileType == "docx")
        this.initDocFile();

    this.initUserName();
    this.initEditType();
};

System.Office.prototype.doGet = function (url, ajax)
{
    this.openFile(url, ajax.fileType, ajax.handleObject);
};

System.Office.prototype.doPost = function (url, content, ajax)
{
    var parms;
    if (content)
    {
        parms = [];
        var parameters = ajax.toParameters(content);
        parameters.each(function ()
        {
            if (this.name)
                parms.push({name: this.name, value: encodeURIComponent(this.value)});
        });
    }

    var result = this.save(url, parms, function (result)
    {
        if (result)
            result = Cyan.Ajax.eval(result);

        if (result instanceof Error && ajax.errorHandle)
            ajax.errorHandle(result);
        else if (ajax.handleObject)
            ajax.handleObject(result);
    });
};

System.Office.prototype.getControl = function (parent, id)
{
    if (parent)
    {
        var controls = parent.Controls;
        var n = controls.Count;
        var control;
        for (var i = 1; i <= n; i++)
        {
            control = controls(i);
            if (control.Id == id)
                return control;
        }
        try
        {
            control = controls(id);
            var debug = Cyan.$("debug");
            if (debug)
                debug.innerHTML += control.Id + ":" + control.Caption + ":" + id + "<BR>";
            return control;
        }
        catch (e)
        {
            //命令可能不存在
        }
    }
    return null;
};

System.Office.prototype.getMenu = function (menu)
{
    try
    {
        return this.getControl(this.getDocument().CommandBars("Menu Bar"), menu);
    }
    catch (e)
    {
        return null;
    }
};

System.Office.prototype.getMenuItem = function (menu, item)
{
    return this.getControl(this.getMenu(menu), item);
};

System.Office.prototype.getBar = function (bar)
{
    try
    {
        return this.getDocument().CommandBars(bar);
    }
    catch (e)
    {
        //工具栏不存在
        return null;
    }
};

System.Office.prototype.getButton = function (bar, button)
{
    return this.getControl(this.getBar(bar), button);
};

System.Office.prototype.setControlVisible = function (control, visible)
{
    if (control)
        control.Visible = visible;
};

System.Office.prototype.setControlEnabled = function (control, enabled)
{
    if (control)
        control.Enabled = enabled;
};

System.Office.prototype.setMenuVisible = function (menu, visible)
{
    this.setControlVisible(this.getMenu(menu), visible);
};

System.Office.prototype.hideMenu = function (menu)
{
    this.setMenuVisible(menu, false);
};

System.Office.prototype.showMenu = function (menu)
{
    this.setMenuVisible(menu, true);
};

System.Office.prototype.setMenuEnabled = function (menu, enabled)
{
    this.setControlEnabled(this.getMenu(menu), enabled);
};

System.Office.prototype.disableMenu = function (menu)
{
    this.setMenuEnabled(menu, false);
};

System.Office.prototype.enableMenu = function (menu)
{
    this.setMenuEnabled(menu, true);
};

System.Office.prototype.setMenuItemVisible = function (menu, item, visible)
{
    try
    {
        this.setControlVisible(this.getMenuItem(menu, item), visible);
    }
    catch (e)
    {
    }
};

System.Office.prototype.hideMenuItem = function (menu, item)
{
    this.setMenuItemVisible(menu, item, false);
};

System.Office.prototype.showMenuItem = function (menu, item)
{
    this.setMenuItemVisible(menu, item, true);
};

System.Office.prototype.setMenuItemEnabled = function (menu, item, enabled)
{
    try
    {
        this.setControlEnabled(this.getMenuItem(menu, item), enabled);
    }
    catch (e)
    {
    }
};

System.Office.prototype.disableMenuItem = function (menu, item)
{
    this.setMenuItemEnabled(menu, item, false);
};

System.Office.prototype.enableMenuItem = function (menu, item)
{
    this.setMenuItemEnabled(menu, item, true);
};

System.Office.prototype.setBarVisible = function (bar, visible)
{
    bar = this.getBar(bar);
    if (bar)
        bar.Visible = visible;
};

System.Office.prototype.hideBar = function (bar)
{
    this.setBarVisible(bar, false);
};

System.Office.prototype.showBar = function (bar)
{
    this.setBarVisible(bar, true);
};

System.Office.prototype.setButtonVisible = function (bar, button, visible)
{
    this.setControlVisible(this.getButton(bar, button), visible);
};

System.Office.prototype.hideButton = function (bar, button)
{
    this.setButtonVisible(bar, button, false);
};

System.Office.prototype.showButton = function (bar, button)
{
    this.setButtonVisible(bar, button, true);
};

System.Office.prototype.setButtonEnabled = function (bar, button, enabled)
{
    this.setControlEnabled(this.getButton(bar, button), enabled);
};

System.Office.prototype.enableButton = function (bar, button)
{
    this.setButtonEnabled(bar, button, false);
};

System.Office.prototype.disableButton = function (bar, button)
{
    this.setButtonEnabled(bar, button, true);
};

System.Office.prototype.setStatusBarVisible = function (visible)
{
    try
    {
        var application = this.getDocument().Application;
        if (this.displayStatusBar == null)
            this.displayStatusBar = application.DisplayStatusBar;
        application.DisplayStatusBar = visible;
    }
    catch (e)
    {
    }
};

System.Office.prototype.hideStatusBar = function ()
{
    this.setStatusBarVisible(false);
};

System.Office.prototype.showStatusBar = function ()
{
    this.setStatusBarVisible(true);
};

System.Office.prototype.resetStatusBar = function ()
{
    if (this.displayStatusBar != null)
        this.getDocument().Application.DisplayStatusBar = this.displayStatusBar;
};

System.Office.prototype.disableCustomization = function ()
{
    //不允许在工具栏上右键菜单，以重定义工具栏
    try
    {
        this.getDocument().CommandBars("Toolbar List").Enabled = false;
    }
    catch (e)
    {
    }
};

System.Office.prototype.initDocFile = function ()
{
    this.disableCustomization();
    this.initDocMenu();
    this.initDocToolBar();

    this.hideStatusBar();

    try
    {
        this.getDocument().ActiveWindow.View.Zoom.Percentage = 100;
    }
    catch (e)
    {
    }
};

System.Office.prototype.initDocMenu = function ()
{
    //隐藏新建按钮
    this.hideMenuItem(this._FILE, this._NEW);
    //隐藏打开菜单
    this.hideMenuItem(this._FILE, this._OPEN);
    //隐藏保存菜单
    this.hideMenuItem(this._FILE, this._SAVE);
    //隐藏保存菜单
    this.hideMenuItem(this._FILE, this._CLOSE);
    //隐藏退出菜单
    this.hideMenuItem(this._FILE, this._EXIT);

    //隐藏工具栏和全屏显示菜单
    this.hideMenuItem(this._VIEW, this._TOOLS_BAR);
    this.hideMenuItem(this._VIEW, this._FULL_SCREEN);

    //隐藏工具和窗口菜单
    this.hideMenu(this._TOOLS);
    this.hideMenu(this._WINDOWS);
};

System.Office.prototype.initDocToolBar = function ()
{
    this.showBar("Standard");
    //隐藏新建按钮
    this.hideButton("Standard", this._NEW_BLANK);
    //隐藏打开按钮
    this.hideButton("Standard", this._OPEN);
    //隐藏保存按钮
    this.hideButton("Standard", this._SAVE);

    this.showBar("Formatting");

    //隐藏画图工具栏，以空出更多的空间
    this.showBar("Drawing");

    //显示审阅工具栏，防止用户自己关闭之后再也无法打开
    this.showBar("Reviewing");
    //隐藏接受文档修订的按钮
    this.hideButton("Reviewing", this._ACCEPT_TRACK_REVISIONS, false);
    //隐藏拒绝文档修订的按钮
    this.hideButton("Reviewing", this._REJECT_TRACK_REVISIONS, false);
    //隐藏打开和关闭修订的按钮
    this.hideButton("Reviewing", this._TRACK_REVISIONS, false);

    //屏蔽右键菜单的接受拒绝和修订
    this.hideButton("Track Changes", this._ACCEPT_TRACK_REVISIONS1, false);
    this.hideButton("Track Changes", this._REJECT_TRACK_REVISIONS1, false);
    this.hideButton("Track Changes", this._TRACK_REVISIONS, false);
};

System.Office.prototype.enableOpen = function ()
{
};

System.Office.prototype.disableOpen = function ()
{
};

System.Office.prototype.getText = function ()
{
    try
    {
        return this.getDocument().Content.Text;
    }
    catch (e)
    {
    }
    return "";
};

System.Office.prototype.setUserName = function (userName)
{
    this.userName = userName;
    if (this.getDocument())
        this.initUserName();
};

System.Office.prototype.initUserName = function ()
{
    if (this.userName && (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "docx"))
    {
        try
        {
            var application = this.getDocument().Application;
            if (!this.oldUserName)
                this.oldUserName = application.userName;

            application.UserName = this.userName;
        }
        catch (e)
        {
        }
    }
};

System.Office.prototype.resetUserName = function ()
{
    if (this.oldUserName &&
            (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "docx"))
        this.getDocument().Application.UserName = this.oldUserName;
};

System.Office.prototype.print = function ()
{
    if (this.print0)
    {
        this.print0();
    }
    else if (this.getFileType() == "doc" || this.getFileType() == "docx")
    {
        this.getDocument().Application.Dialogs(88).Show();
    }
};

System.Office.prototype.saveAs = function (fileName)
{
    fileName = fileName.replace(/[\\/:\*\?"<>\|]/g, "");
    if (this.getFileType() == "doc" || this.getFileType() == "docx")
    {
        var dialog = this.getDocument().Application.Dialogs(84);
        if (fileName)
        {
            try
            {
                dialog.Name = fileName;
            }
            catch (e)
            {
            }
        }
        dialog.Show();
    }
    else if (this.saveAs0)
    {
        this.saveAs0(fileName);
    }
};

System.Office.prototype.saveAsTxt = function (fileName)
{
    fileName = fileName.replace(/[\\/:\*\?"<>\|]/g, "");
    if (this.getFileType() == "doc" || this.getFileType() == "docx")
    {
        var dialog = this.getDocument().Application.Dialogs(84);
        if (fileName)
        {
            try
            {
                dialog.Name = fileName;
            }
            catch (e)
            {
            }
        }
        dialog.Format = 2;
        dialog.Show();
    }
    else if (this.saveAs0)
    {
        this.saveAs0(fileName);
    }
};

System.Office.prototype.isSaved = function ()
{
    try
    {
        return this.getDocument().Saved;
    }
    catch (e)
    {
        return true;
    }
};

System.Office.prototype.setSaved = function (saved)
{
    try
    {
        this.getDocument().Saved = saved != false;
    }
    catch (e)
    {
    }
};

System.Office.prototype.isProtected = function ()
{
    return this.getDocument().ProtectionType != -1;
};

System.Office.prototype.protect = function ()
{
    try
    {
        if (!this.isProtected())
            this.getDocument().Protect(this.protectionType, false, this.password);
    }
    catch (e)
    {
    }
};

System.Office.prototype.unProtect = function ()
{
    try
    {
        if (this.isProtected())
            this.getDocument().UnProtect(this.password);
    }
    catch (e)
    {
    }
};

System.Office.prototype.setTrackRevisions = function (trackRevisions)
{
    try
    {
        if (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "docx")
            this.getDocument().TrackRevisions = trackRevisions;
    }
    catch (e)
    {
    }
};

System.Office.prototype.isTrackRevisions = function ()
{
    try
    {
        return (this.getFileType() == "doc" || this.getFileType() == "docx") && this.getDocument().TrackRevisions;
    }
    catch (e)
    {
        return false;
    }
};

System.Office.prototype.acceptAllRevisions = function ()
{
    try
    {
        if (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "docx")
            this.getDocument().AcceptAllRevisions();
    }
    catch (e)
    {
    }
};

System.Office.prototype.rejectAllRevisions = function ()
{
    try
    {
        if (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "docx")
            this.getDocument().RejectAllRevisions();
    }
    catch (e)
    {
    }
};

System.Office.prototype.hasRevisions = function ()
{
    var fileType = this.getFileType();
    if (fileType == "doc" || fileType == "wps" || fileType == "docx")
    {
        var revisions = this.getDocument().Revisions;
        if (revisions && revisions.Count > 0)
            return true;
    }
    return false;
};

System.Office.prototype.showRevisions = function ()
{
    if (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "wps")
        this.getDocument().ActiveWindow.View.ShowRevisionsAndComments = true;
};

System.Office.prototype.hideRevisions = function ()
{
    if (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "wps")
        this.getDocument().ActiveWindow.View.ShowRevisionsAndComments = false;
};

System.Office.prototype.isShowRevisions = function ()
{
    return (this.getFileType() == "doc" || this.getFileType() == "wps" || this.getFileType() == "wps") &&
            this.getDocument().ActiveWindow.View.ShowRevisionsAndComments;
};

System.Office.prototype.setEditType = function (editType)
{
    var saved = this.isSaved();

    if (editType == "editable")
        editType = 0;
    else if (editType == "track")
        editType = 1;
    else if (editType == "readOnly")
        editType = 2;

    this.editType = editType;

    if (this.getDocument() != null)
        this.initEditType();

    if (saved)
        this.setSaved(true);
};

System.Office.prototype.initEditType = function ()
{
    if (this.fileType == "doc" || this.fileType == "wps" || this.fileType == "docx")
        this.initDocEditType();
};

System.Office.prototype.initDocEditType = function ()
{
    if (this.editType == 0)
    {
        //可编辑，不留痕
        this.unProtect();
        this.setTrackRevisions(false);

        this.enableOpen();
    }
    else if (this.editType == 1)
    {
        //可编辑，留痕
        this.unProtect();
        this.setTrackRevisions(true);

        this.disableOpen();
    }
    else
    {
        //不可编辑
        this.protect();

        this.disableOpen();
    }
};

System.Office.prototype.hitchedByTemplate = function (path, bookmark, callback)
{
    var doc = this.getDocument(), exists = false, bookmarkObject, length;
    try
    {
        bookmarkObject = doc.Bookmarks(bookmark);
    }
    catch (e)
    {
        try
        {
            bookmarkObject = doc.Bookmarks.Item(bookmark);
        }
        catch (e1)
        {
            //所要求的标签不存在
        }
    }
    if (bookmarkObject)
    {
        length = bookmarkObject.Range.End - bookmarkObject.Range.Start;
        exists = true;
    }

    var localPath;
    if (navigator.platform.toLowerCase().startsWith("win"))
        localPath = "d:\\temp.doc";
    else
        localPath = "/home/temp.wps";

    doc.SaveAs(localPath);

    if (!exists)
        length = doc.Paragraphs.Last.Range.End;

    var office = this;

    this.openFile(path, "doc", function ()
    {
        var doc = office.getDocument();
        var trackRevisions = doc.TrackRevisions;
        if (trackRevisions)
            doc.TrackRevisions = false;

        try
        {
            bookmarkObject = doc.Bookmarks(bookmark);
        }
        catch (e)
        {
            try
            {
                bookmarkObject = doc.Bookmarks.Item(bookmark);
            }
            catch (e1)
            {
                //所要求的标签不存在
            }
        }

        if (bookmarkObject)
        {
            var range = bookmarkObject.Range;
            var start = range.Start;

            range.InsertFile(localPath, exists ? bookmark : "");

            range = doc.Range(start, start + length);
            doc.Bookmarks.Add(bookmark, range);
        }

        if (trackRevisions)
            doc.TrackRevisions = true;

        if (callback)
            callback();
    });
};

System.Office.prototype.setText = function (bookmark, text)
{
    if (!bookmark)
        return;

    if (!text)
        text = "";

    var bookmarkObject;
    try
    {
        bookmarkObject = this.getDocument().Bookmarks(bookmark);
    }
    catch (e)
    {
        try
        {
            bookmarkObject = this.getDocument().Bookmarks.Item(bookmark);
        }
        catch (e1)
        {
            //书签不存在
        }
    }

    if (bookmarkObject)
    {
        var range = bookmarkObject.Range;
        var oldText = range.Text;
        if (oldText.endsWith("\r"))
            range.End = range.End - 1;
        range.Text = text;

        try
        {
            bookmarkObject.Delete();
        }
        catch (e)
        {
            //标签替换后可能已经删除
        }
    }
};

System.Office.prototype.clearBookmarks = function ()
{
    var bookmarks = this.getDocument().Bookmarks;
    while (bookmarks.Count > 0)
    {
        var bookmark = bookmarks(1);
        var range = bookmark.Range;
        var oldText = range.Text;
        if (oldText.endsWith("\r"))
            range.End = range.End - 1;
        range.Text = "";

        try
        {
            bookmark.Delete();
        }
        catch (e)
        {
            //标签替换后可能已经删除
        }
    }
};

System.Office.getHiddenOffice = function (callback)
{
    if (!System.Office.hiddenOffice)
    {
        System.Office.hiddenOffice = new System.Office();

        var div = document.createElement("DIV");
        div.style.width = "0";
        div.style.height = "0";
        div.style.position = "absolute";
        div.style.left = "-500px";
        div.style.top = "-500px";
        document.body.appendChild(div);

        var f;
        if (callback)
        {
            f = function ()
            {
                if (callback)
                    callback(System.Office.hiddenOffice);
            };
        }

        System.Office.hiddenOffice.render(div, f);
    }
    else if (callback)
    {
        callback(System.Office.hiddenOffice);
    }

    return System.Office.hiddenOffice;
};

System.Office.print = function (path, type)
{
    var office = System.Office.getHiddenOffice();
    office.openFile(path, type);
    office.print();
};

System.Office.prototype.getBookmark = function (name)
{
    if (!name)
        return;

    var bookmark;
    try
    {
        bookmark = this.getDocument().Bookmarks(name);
    }
    catch (e)
    {
        try
        {
            bookmark = this.getDocument().Bookmarks.Item(name);
        }
        catch (e1)
        {
            //书签不存在
        }
    }

    if (bookmark)
        return new System.Office.Bookmark(bookmark);

    return null;
};

System.Office.prototype.getBookmarks = function (filter)
{
    var doc = this.getDocument();
    var n = doc.Bookmarks.Count;
    var result = [];
    for (var i = 0; i < n; i++)
    {
        var bookmark;
        try
        {
            bookmark = doc.Bookmarks(i + 1);
        }
        catch (e)
        {
            bookmark = doc.Bookmarks.Item(i + 1);
        }
        var b = new System.Office.Bookmark(bookmark);
        if (!filter || filter(b))
            result.push(b);
    }

    return result;
};

System.Office.Bookmark = function (bookmark)
{
    this.bookmark = bookmark;
};

System.Office.Bookmark.prototype.getName = function ()
{
    return this.bookmark.Name;
};

System.Office.Bookmark.prototype.clear = function ()
{
    var bookmark = this.bookmark;
    var range = bookmark.Range;
    var oldText = range.Text;
    if (oldText.endsWith("\r"))
        range.End = range.End - 1;
    range.Text = "";

    try
    {
        bookmark.Delete();
    }
    catch (e)
    {
        //标签替换后可能已经删除
    }
};

System.Office.Bookmark.prototype.getText = function ()
{
    var range = this.bookmark.Range;
    var oldText = range.Text;
    if (oldText.endsWith("\r"))
        range.End = range.End - 1;

    return range.Text;
};

System.Office.Bookmark.prototype.setText = function (text)
{
    var bookmark = this.bookmark;
    var range = bookmark.Range;
    var oldText = range.Text;
    if (oldText.endsWith("\r"))
        range.End = range.End - 1;
    range.Text = text;

    try
    {
        bookmark.Delete();
    }
    catch (e)
    {
        //标签替换后可能已经删除
    }
};

System.Office.prototype.getParagraphs = function ()
{
    var paragraphs = this.getDocument().Paragraphs;
    var n = paragraphs.Count;
    var result = [];
    for (var i = 0; i < n; i++)
    {
        result.push(new System.Office.Paragraph(paragraphs(i + 1)));
    }

    return result;
};

System.Office.Paragraph = function (paragraph)
{
    this.paragraph = paragraph;
};

System.Office.Paragraph.prototype.getText = function ()
{
    var range = this.paragraph.Range;
    return range.Text;
};

System.Office.Paragraph.prototype.setText = function (text)
{
    var paragraph = this.paragraph;
    var range = paragraph.Range;
    range.Text = text;
};

System.Office.prototype.replaceText = function (s1, s2)
{
    var paragraphs = this.getParagraphs();
    for (var i = 0; i < paragraphs.length; i++)
    {
        var paragraph = paragraphs[i];

        var text = paragraph.getText();
        var text1 = Cyan.replaceAll(text, s1, s2);

        if (text != text1)
            paragraph.setText(text1);
    }
};

System.Office.prototype.getVersion = function ()
{
    return this.getDocument().Application.Version;
};

System.Office.setImp = function (imp)
{
    Cyan.setCookie("officeType", imp);
};

Cyan.importJs("/platform/weboffice/imp.js");