Cyan.importJs("widgets/window.js");
Cyan.importJs("/platform/commons/init.js");

window.System = {
    stylePath: null,
    userName: null
};

Cyan.onunload(function ()
{
    if (System.page)
        System.page = null;
});


/**
 * 导入一个某功相关的语言js，所有定义语言的js文件放在/locale/语言名称下
 * @param name 功能的路径
 */
System.importLanguage = function (name)
{
    Cyan.importJs("/locale/" + window.languageForCyan$ + "/" + name + ".js");
};

System.getMenuId = function ()
{
    return System.menuId || Cyan.getUrlParameter("menuId$");
};

System.formatUrl = function (url)
{
    if (!url)
        return url;

    //在后面附加上菜单ID
    var menuId = System.getMenuId();
    if (menuId)
        url += ( url.indexOf("?") >= 0 ? "&" : "?") + "menuId$=" + menuId;
    return url;
};

System.getTitle = function ()
{
    var title = Cyan.$("app_title");
    if (title)
        title = title.innerHTML;

    if (!title)
        title = document.title;

    if (!title)
        title = System.title;

    return title;
};

System.getRemark = function ()
{
    var remark = Cyan.$("app_remark");
    return remark ? remark.innerHTML : System.remark;
};

System.setTitle = function (title)
{
    if (System.page && System.page.setTitle)
        System.page.setTitle(title);
};

System.setRemark = function (remark)
{
};

System.closePage = function ()
{
    if (System.page && System.page.closePage)
        System.page.closePage();
};

if (Cyan.Arachne)
{
    Cyan.Class.overwrite(Cyan.Arachne, "call", function (url, args, method, type, target, component)
    {
        return this.inherited(System.formatUrl(url), args, method, type, target, component);
    });
}

System.redirect = function (url, target)
{
    url = Cyan.formatUrl(System.formatUrl(url));
    if (!target || target == "_self")
        Cyan.Ajax.redirect(url);
    else if (target == "_blank")
        Cyan.Ajax.open(url);
    else
        document.getElementById(target).src = url;
};

System.showDialog = function (url, name, reload)
{
    return Cyan.Window.showDialog(System.formatUrl(url), name, reload);
};

System.showModal = function (url, callback, option)
{
    return Cyan.Window.showModal(System.formatUrl(url), callback, option);
};

System.openWindow = function (url, name, reload)
{
    return Cyan.Window.openWindow(System.formatUrl(url), name, reload);
};

System.getMainWindow = function ()
{
    var win = window;
    while ((!win.System || !win.System.Menu) && win != win.parent)
        win = win.parent;
    return win;
};

System.getTopWin = function ()
{
    var win = window;
    try
    {
        while (win.parent && win.parent != win && win.parent.System)
            win = win.parent;
    }
    catch (e)
    {
    }
    return win;
};

System.getImWindow = function ()
{
    var win = window;
    while ((!win.System || !win.System.Im) && win != win.parent)
        win = win.parent;
    return win;
};

function stopFlash()
{
    if (Cyan.Render)
        Cyan.Render.Flash.stop();
}

function startFlash()
{
    if (Cyan.Render)
        Cyan.Render.Flash.start();
}

System.stopFlash = function ()
{
    var mainWindow = System.getMainWindow();
    if (mainWindow.stopFlash)
        mainWindow.stopFlash();

    var topWin = System.getTopWin();
    if (topWin && topWin != mainWindow)
        topWin.stopFlash();
};

System.startFlash = function ()
{
    var mainWindow = System.getMainWindow();
    if (mainWindow.startFlash)
        mainWindow.startFlash();

    var topWin = System.getTopWin();
    if (topWin && topWin != mainWindow)
        topWin.startFlash();
};

System.openPage = function (url, name, title)
{
    if (!name)
        name = url;
    url = Cyan.formatUrl(System.formatUrl(url));
    return System.getMainWindow().System.Menu.openPage(url, name, title, window);
};

System.goMenu = function (menuId, url)
{
    var Menu = System.getMainWindow().System.Menu;
    Menu.goMenu(menuId ? Menu.menus[menuId] : Menu.getMenuByUrl(url), url);
};

System.getMenuByUrl = function (url)
{
    return System.getMainWindow().System.Menu.getMenuByUrl(url);
};

System.getMenu = function (menuId)
{
    return System.getMainWindow().System.Menu.menus[menuId];
};

System.getPage = function (name)
{
    return System.getMainWindow().System.Menu.getPage(name);
};

System.showIm = function (userId, userName)
{
    System.getImWindow().System.Im.getUserWindow({
        userId: userId,
        userName: userName
    }, true);
};

System.logout = function (indexPage)
{
    var url = "/login/out";
    if (indexPage)
        url += "?indexPage=" + indexPage;
    System.getTopWin().location.href = url;
};

System.selectDept = function ()
{
    Cyan.Window.showModal("/login/selectDept", function (ret)
    {
        if (ret)
            System.getTopWin().location.reload();
    });
};

System.lock = function ()
{
    Cyan.Window.showModal("/user/lock", null, {closable: false});
};

System.showImage = function (url, attachmentId)
{
    var topWin = System.getTopWin();
    if (topWin != window)
        topWin.System.showImage(url, window, attachmentId);
};

System.reload = function ()
{
    if (!!System.autoReload)
    {
        if (window.mainBody && mainBody.reload)
            mainBody.reload();

        if (window.chart && chart.reload)
            chart.reload();

        if (window.left && left.reload)
            left.reload();

        if (window.subBody && subBody.reload)
            subBody.reload();
    }
};

System._page = {
    open: function (url)
    {
        System.openPage(url);
    },
    createPostFrame: function (url)
    {
        return System.openPage(null, url);
    }
};

if (Cyan.Ajax)
    Cyan.Ajax.customTarget["_page"] = System._page;

System.Comet = {
    handlers: {},
    resets: {},
    reset: function ()
    {
        for (var name in System.Comet.resets)
        {
            try
            {
                System.Comet.resets[name]();
            }
            catch (e)
            {
            }
        }
    },
    connect0: function ()
    {
        var url = "/platform/message/comet/CometPage!connect.comet";
        Cyan.Arachne.comet(url, arguments, System.Comet);
        System.Comet.lastTime = new Date();
    },
    connect: function ()
    {
        this.setHandler("com.gzzm.platform.message.comet.CometConnection", function (connection)
        {
            System.Comet.id = connection.id;
            Cyan.attach(window, "unload", function ()
            {
                if (System.Comet.id)
                    Cyan.Arachne.get("/comet/disconnect/" + System.Comet.id);
            });
        });
        this.setHandler("com.gzzm.platform.message.comet.Ping", function (ping)
        {
        });
        var callback = function (ret, id)
        {
            Cyan.Arachne.doPost("/callback", arguments, 3);
        };
        this.setHandler("com.gzzm.platform.commons.callback.CallbackMessage", function (message)
        {
            var f = function ()
            {
                var method = message.method;
                if (method)
                {
                    method = eval(method);
                    if (method instanceof Function)
                    {
                        var args = message.args || [];
                        var id = message.id;
                        args.push(function (ret)
                        {
                            callback(ret, id);
                        });

                        method.apply(window, args);
                    }
                }
            };

            if (message.js)
            {
                Cyan.importJs(message.js, f);
            }
            else
            {
                f();
            }
        });
        this.connect0(System.Comet.callback);
    },
    reConnect: function ()
    {
        System.Comet.connect0(System.Comet.callback);
        System.Comet.reset();
    },
    callback: function (result, end)
    {
        if (end)
        {
            if (!System.Comet.lastTime)
            {
                System.Comet.reConnect();
            }
            else
            {
                var l = 1000 * 60 * 20;
                var now = new Date().getTime();
                var last = System.Comet.lastTime.getTime();
                var timeout = l - (now - last);
                if (timeout <= 0)
                    System.Comet.reConnect();
                else
                    setTimeout(System.Comet.reConnect, timeout);
            }
        }
        else
        {
            if (result && result.type)
            {
                var handler = System.Comet.handlers[result.type];
                if (handler)
                    handler(result.message);
            }
        }
    },
    setHandler: function (type, handler)
    {
        System.Comet.handlers[type] = handler;
    },
    setReset: function (name, reset)
    {
        System.Comet.resets[name] = reset;
    }
};

System.getComet = function ()
{
    return System.getTopWin().System.Comet;
};

System.addShortcut = function (id, icon, title, action)
{
    Cyan.run(function ()
    {
        var mainWindow = System.getMainWindow();
        return mainWindow.System && mainWindow.System.Shortcut && mainWindow.System.Shortcut.addShortcut;
    }, function ()
    {
        System.getMainWindow().System.Shortcut.addShortcut(id, icon, title, action);
    });
};

System.getShortcut = function (id)
{
    return System.getMainWindow().System.Shortcut.getShortcut(id);
};

System.maximize = function (closeMessage)
{
    System.getMainWindow().maximize(closeMessage);
};

System.restore = function ()
{
    System.getMainWindow().restore();
};

System.isMaximized = function ()
{
    return System.getMainWindow().isMaximized();
};

System.camera = function (callback, attachmentId)
{
    System.getTopWin().System.Camera.show(callback, attachmentId);
};

System.scan = function (format, callback, attachmentId)
{
    System.getTopWin().System.Scanner.scanAndStore(format, callback, attachmentId);
};

System.clearMenuId = function (url)
{
    if (!url)
        return url;
    return url.replace(/(&|\?)menuId\$=[0-9]+/, "");
};

System.more = function ()
{
    var url = location.pathname + System.clearMenuId(location.search.replace(/(&|\?)page=((list)|(table))/, ""));
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
        System.openPage(url);
};

Cyan.onload(function ()
{
    window.closeWindow = function (returnValue)
    {
        Cyan.Window.closeWindow(returnValue);
    };
});