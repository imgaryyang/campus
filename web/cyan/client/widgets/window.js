/**
 * 创建window类
 * @param name 窗口的名称
 */
Cyan.Window = function (name)
{
    if (name)
    {
        this.name = name;
        Cyan.Window.windows[name] = this;
    }
    if (this.init)
        this.init();
};

Cyan.Window.FrameWindowPool = [];

Cyan.Window.getTopWindow = function ()
{
    //寻找最外层的页面
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Cyan.Window)
            win = win.parent;
    }
    catch (e)
    {
    }

    return win;
};

/**
 * 创建一个窗体
 * @param name 窗体的名称
 */
Cyan.Window.create = function (name)
{
    //寻找最外层的页面
    var topWindow = Cyan.Window.getTopWindow();

    //窗体如果已经创建则使用之前创建的窗体，如果没有则创建之
    var win = topWindow.Cyan.Window.windows[name];
    if (!win)
        win = new topWindow.Cyan.Window(name);

    win.opener = window;

    return win;
};

Cyan.Window.createFrameWindow = function (name)
{
    var topWindow = Cyan.Window.getTopWindow();

    if (topWindow.Cyan.Window.FrameWindowPool.length)
    {
        win = topWindow.Cyan.Window.FrameWindowPool.pop();
        win.opener = window;
        if (name)
        {
            this.name = name;
            Cyan.Window.windows[name] = win;
        }

        return win;
    }
    else
    {
        var win = Cyan.Window.create(name);
        win.createFrame();
        win.opener = window;

        return win;
    }
};

/**
 * 引入window的适配器
 */
Cyan.importAdapter("window");

/**
 * 记录大开过的所有窗体和对应的frame
 */
Cyan.Window.frames = {};
Cyan.Window.windows = {};

/**
 * 在窗体内嵌入一个frame，以便打开一个url
 */
Cyan.Window.prototype.createFrame = function ()
{
    //之前已经创建了
    if (this.frame)
        return false;

    //获取一个还没有使用的frame名称
    while (Cyan.Window.frames[this.frame = "WindowFrame$" + Math.random().toString().substring(2)]);
    //创建窗体并且创建一个显示正在loading的div
    this.setHtml("<iframe frameborder='0' style='width:100%;height:100%;' id='" + this.frame + "' name='" + this.frame +
    "'></iframe><div class='cyan-window-loading' style='display: none'>" + Cyan.titles["loading"] + ". . </div>");

    this.setDestoryWhenClose(false);
    Cyan.Window.frames[this.frame] = this;

    return true;
};

/**
 * 获得创建中的iframe
 */
Cyan.Window.prototype.getFrame = function ()
{
    return document.getElementById(this.frame);
};

/**
 * 打开一个url，将窗体中的iframe转向这个url
 * @param url 要打开的url
 * @param option 定义新打开的窗口的属性，例如宽度，高度，标题等
 */
Cyan.Window.prototype.open = function (url, option)
{
    this.createFrame();
    this.create();

    if (option)
    {
        if (option.width && option.height)
            this.setSize(option.width, option.height);

        if (option.title)
            this.setTitle(option.title);
    }

    if (this.setClosable)
        this.setClosable(!option || option.closable == null || option.closable);

    this.getFrame().src = Cyan.formatUrl(url);
    //窗体已经显示，显示loading的div
    if (this.isVisible())
        this.setLoading();
    else
        this.mask(true);
};

Cyan.Window.prototype.initFrameLoad = function ()
{
    var win = this;
    if (this.frame)
    {
        Cyan.attach(this.getFrame(), "load", function ()
        {
            win.onFrameLoad();
        });
    }
};

Cyan.Window.prototype.onFrameLoad = function ()
{
    var frame = this.getFrame();
    if (!Cyan.endsWith(frame.src, "empty.html"))
    {
        var win = this;
        window.setTimeout(function ()
        {
            var contentWindow = frame.contentWindow;
            if (!win.isVisible())
            {
                //如果窗口还未显示，用当前页面的大小初始化窗口的大小，并显示窗口
                var size = Cyan.getBodySize(contentWindow);

                var width = size.width;
                var height = size.height;

                //限制窗口的最小尺寸
                if (width < 100)
                    width = 100;
                if (height < 100)
                    height = 100;

                Cyan.Window.initDialog(width, height, null, win);
            }
            else
            {
                win.setTitle(contentWindow.document.title);
                //页面已经加载完毕，取消loading并激活窗口
                win.stopLoading();
                win.focus();
                win.mask(false);
            }

            if (contentWindow.onunload)
            {
                //让窗口关闭时调用页面的onunload
                win.onunload = contentWindow.onunload;
                contentWindow.onunload = null;
            }

            contentWindow.close = Cyan.Window.closeWindow;

            if (contentWindow.$ && contentWindow.$.withCyan)
            {
                contentWindow.$.opener = win.opener;
            }

            win = null;
        }, 10);
    }
};

/**
 * 窗体关闭的时候释放资源
 */
Cyan.Window.prototype.release = function ()
{
    //调用回调函数
    try
    {
        if (this.callback)
            this.callback(this.returnValue);
    }
    finally
    {
        try
        {
            this.opener.callback = null;
        }
        catch (e)
        {
        }
    }

    if (this.frame)
    {
        var iframe = Cyan.$(this.frame);
        iframe.src = Cyan.getRealPath("empty.html");
        this.name = null;
        this.returnValue = null;
        this.onunload = null;
        this.restore();
        this.setTitle("");
        Cyan.Window.FrameWindowPool.push(this);
    }
    if (this.name)
        Cyan.Window.windows[this.name] = null;
};

/**
 * 设置窗口关闭时的动作
 * @param action 窗口关闭时触发的动作
 */
Cyan.Window.prototype.setCloseAction = function (action)
{
    this.closeAction = action;
};

/**
 * 设置窗口为dialog模式
 */
Cyan.Window.prototype.setDialog = function ()
{
    this.setResizable(true);
    this.setMaximizable(false);
    this.setMinimizable(false);
    this.setModal(false);
};

/**
 * 设置窗口为window模式
 */
Cyan.Window.prototype.setWindow = function ()
{
    this.setResizable(true);
    this.setMaximizable(true);
    this.setMinimizable(true);
    this.setModal(false);
};

Cyan.Window.showDialog = function (url, name, reload)
{
    var win = Cyan.Window.createFrameWindow(name || url);
    win.setDialog();
    if (reload != false || !win.isVisible())
        win.open(url);

    return win;
};

Cyan.Window.showModal = function (url, callback, option)
{
    var win = Cyan.Window.createFrameWindow();
    win.setModal(true);
    win.callback = callback;
    win.open(url, option);

    window.callback = callback;

    return win;
};

Cyan.Window.openWindow = function (url, name, reload)
{
    var win = Cyan.Window.createFrameWindow(name || url);
    win.setWindow();
    if (reload != false || !win.isVisible())
        win.open(url);

    return win;
};

//setModal由Adapter定义

/**
 * 设置loading
 */
Cyan.Window.prototype.setLoading = function ()
{
    //隐藏iframe，让iframe下面的loading div显示
    var frame = this.getFrame();
    frame.style.display = "none";
    frame.nextSibling.display = "";
};

/**
 * 停止loading
 */
Cyan.Window.prototype.stopLoading = function ()
{
    //显示iframe
    var frame = this.getFrame();
    frame.style.width = "100%";
    frame.style.height = "100%";
    frame.style.display = "";
    frame.nextSibling.display = "none";
};

/**
 * 在iframe里面的页面获取对应的窗口
 */
Cyan.Window.getWindow = function (name)
{
    if (name)
    {
        var win = Cyan.Window.getTopWindow();

        return win.Cyan.Window.windows[name];
    }
    else
    {
        var b = false;
        try
        {
            b = parent.Cyan && parent.Cyan.Window;
        }
        catch (e)
        {
        }
        if (b)
        {
            var frame = window.frameElement;
            return frame ? parent.Cyan.Window.frames[frame.id] : null;
        }
    }

    return null;
};

/**
 * iframe打开的页面中获得打开这个窗口的页面，相当于ie的dialog的opener
 */
Cyan.Window.getOpener = function ()
{
    var win = Cyan.Window.getWindow();
    return win ? win.opener : null;
};

/**
 * 在iframe打开的页面中关闭窗口
 * @param returnValue 设置返回值
 */
Cyan.Window.closeWindow = function (returnValue)
{
    var win = Cyan.Window.getWindow();
    if (win)
    {
        if (typeof returnValue != "undefined")
            win.returnValue = returnValue;
        if (typeof win.returnValue == "undefined")
            win.returnValue = window.returnValue;
        win.close();
    }
};

/**
 * 设置窗口的返回值
 * @param returnValue
 */
Cyan.Window.setReturnValue = function (returnValue)
{
    var win = Cyan.Window.getWindow();
    if (win)
        win.returnValue = returnValue;
};

Cyan.Window.initDialog = function (width, height, title, win)
{
    if (!win)
        win = Cyan.Window.getWindow();

    if (win)
    {
        win.mask(false);

        //当前页面是某个窗口对应的iframe
        if (title)
            win.setTitle(title);

        win.initDialogSize(width, height);

        //窗口居中
        win.center();

        //如果窗口还未显示，显示之，并对window模式的窗口最大化
        win.show(null);
        if (win.isMaximizable())
            win.maximize();
    }
};

Cyan.onload(function ()
{
    if (window.$ && $.withCyan)
    {
        $.modal = Cyan.Window.showModal;
        $.dialog = Cyan.Window.showDialog;
        $.open = Cyan.Window.openWindow;
    }
});