Cyan.onload(function ()
{
    Ext.Window.DD.prototype.startDrag0 = Ext.Window.DD.prototype.startDrag;
    Ext.Window.DD.prototype.startDrag = function ()
    {
        var result = this.startDrag0();
        this.win.maskDom = this.proxy.dom;
        return result;
    };

    var b = true;
    try
    {
        b = (window.parent == window || !window.parent.Cyan || !window.parent.Cyan.Window);
    }
    catch (e)
    {
    }

    if (b)
    {
        var div = document.createElement("DIV");
        div.id = "ext$window$anim";
        div.style.position = "absolute";
        div.style.width = "0";
        div.style.height = "0";
        div.style.left = "0";
        div.style.height = "0";
        document.body.appendChild(div);
        Cyan.Window.EXTANIMDIV = div;
    }
});
Cyan.Window.doms = [];
Cyan.Window.frameIndex = 500;
Cyan.Window.prototype.created = false;
Cyan.Window.prototype.init = function ()
{
    var win = this;
    this.win = new Ext.Window({
        width: 50, height: 50, layout: "fit", footer: true, listeners: {
            render: function ()
            {
                win.maskFrame = document.createElement("IFRAME");
                win.maskFrame.style.display = "none";
                win.maskFrame.style.position = "absolute";
                win.maskFrame.style.zIndex = Cyan.Window.frameIndex++;
                win.maskFrame.style.borderWidth = "0";
                win.maskFrame.frameSize = 0;
                win.maskFrame.style.filter = "alpha(opacity=0)";
                win.maskFrame.style.opacity = "0";
                document.body.appendChild(win.maskFrame);
            }, beforedestroy: function ()
            {
                win.release();
                document.body.removeChild(win.maskFrame);
                return true;
            }, beforehide: function ()
            {
                if (win.onhide ? win.onhide() : true)
                {
                    if (win.created && win.frame)
                    {
                        var result = win.onunload ? win.onunload() : true;

                        try
                        {
                            if (result)
                            {
                                win.release();
                                var frame = win.getFrame();
                                frame.style.width = "10px";
                                frame.style.height = "10px";
                            }
                        }
                        finally
                        {
                            if (result)
                            {
                                try
                                {
                                    win.maskFrame.style.display = "none";
                                }
                                catch (e)
                                {
                                }
                            }
                        }
                        return result;
                    }
                    else
                    {
                        try
                        {
                            win.maskFrame.style.display = "none";
                        }
                        catch (e)
                        {
                        }
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }, beforeclose: function ()
            {
                return win.onhide ? win.onhide() : (win.onunload ? win.onunload() : true);
            }, resize: function ()
            {
                win.showMaskFrame();
                if (win.onresize)
                    win.onresize();
            }, show: function ()
            {
                win.showMaskFrame();
            }, move: function ()
            {
                win.showMaskFrame();
                this.maskDom = null;
            }
        }
    });
    this.win.close0 = this.win.doClose;
    this.closeAction = this.close;
    this.win.close = function ()
    {
        win.closeAction();
    };
    this.win.doClose = function ()
    {
        win.closeAction();
    };
    this.showMaskFrame = function ()
    {
        if (this.win.el.dom)
        {
            var position, x, y;
            if (this.win.maskDom)
            {
                position = Cyan.Elements.getPosition(this.win.maskDom);
                x = position.x;
                y = position.y;
            }
            else
            {
                position = Cyan.Elements.getPosition(this.win.el.dom);
                x = position.x;
                y = position.y;
            }
            var size = Cyan.Elements.getComponentSize(this.win.el.dom);
            try
            {
                this.maskFrame.style.left = x + "px";
                this.maskFrame.style.top = y + "px";
                this.maskFrame.style.width = size.width + "px";
                this.maskFrame.style.height = size.height + "px";
                this.maskFrame.style.display = "";
            }
            catch (e)
            {
            }
        }
    };
    Cyan.attach(document.body, "mousemove", function ()
    {
        win.showMaskFrame();
    });
};
Cyan.Window.prototype.getExtWin = function ()
{
    return this.win;
};
Cyan.Window.prototype.setTitle = function (title)
{
    this.title = title;
    this.win.setTitle(title);
};
Cyan.Window.prototype.getTitle = function ()
{
    return this.title;
};
Cyan.Window.prototype.getFooter = function ()
{
    return this.win.footer.dom;
};
Cyan.Window.prototype.setSize = function (width, height)
{
    if (this.frame)
    {
        var frame = this.getFrame();
        frame.style.width = "100%";
        frame.style.height = "100%";
    }

    this.win.setSize(width, height);
};
Cyan.Window.prototype.getSize = function ()
{
    return this.win.getSize();
};
Cyan.Window.prototype.getWidth = function ()
{
    return this.win.getSize().width;
};
Cyan.Window.prototype.getHeight = function ()
{
    return this.win.getSize().height;
};
Cyan.Window.prototype.setWidth = function (width)
{
    if (this.frame)
    {
        var frame = this.getFrame();
        frame.style.width = "100%";
        frame.style.height = "100%";
    }

    this.win.setWidth(width);
};
Cyan.Window.prototype.setHeight = function (height)
{
    if (this.frame)
    {
        var frame = this.getFrame();
        frame.style.width = "100%";
        frame.style.height = "100%";
    }

    this.win.setHeight(height);
};
Cyan.Window.prototype.setPosition = function (x, y)
{
    this.win.setPosition(x, y);
};
Cyan.Window.prototype.getPosition = function ()
{
    var p = this.win.getPosition();
    return {x: p[0], y: p[1]};
};
Cyan.Window.prototype.getLeft = function ()
{
    return this.win.getPosition()[0];
};
Cyan.Window.prototype.getTop = function ()
{
    return this.win.getPosition()[1];
};
Cyan.Window.prototype.setModal = function (modal)
{
    if (modal)
    {
        this.win.maximizable = false;
        this.win.collapsible = false;
        this.win.resizable = false;
    }
    this.win.modal = modal;
};
Cyan.Window.prototype.setMaximizable = function (maximizable)
{
    this.win.maximizable = maximizable;
};
Cyan.Window.prototype.setMinimizable = function (minimizable)
{
    this.win.collapsible = minimizable;
};
Cyan.Window.prototype.setResizable = function (resizable)
{
    this.win.resizable = resizable;
};
Cyan.Window.prototype.setDestoryWhenClose = function (destoryWhenClose)
{
    this.win.closeAction = destoryWhenClose ? "close" : "hide";
};
Cyan.Window.prototype.setHtml = function (html)
{
    this.panel = new Ext.Panel({
        border: false,
        html: html,
        baseCls: "x-plain"
    });
    this.win.add(this.panel);
};
Cyan.Window.prototype.center = function ()
{
    this.win.center();
};
Cyan.Window.prototype.show = function (callback)
{
    //    var position = this.win.getPosition();
    //    var size = this.win.getSize();
    //    if (Cyan.Window.EXTANIMDIV)
    //    {
    //        Cyan.Window.EXTANIMDIV.style.left = position[0] + size.width / 2;
    //        Cyan.Window.EXTANIMDIV.style.top = position[1] + size.height / 2;
    //    }
    this.win.show(null, callback);
    this.created = true;
};
Cyan.Window.prototype.hide = function ()
{
    this.win.hide();
};
Cyan.Window.prototype.close = function ()
{
    if (this.frame || this.win.closeAction == "hide")
        this.win.hide();
    else
        this.win.close0();
};
Cyan.Window.prototype.isVisible = function ()
{
    return this.win.isVisible();
};
Cyan.Window.prototype.create = function ()
{
    if (!this.created)
    {
        this.win.show();
        this.win.hide();
        Cyan.Window.doms.push(this.win.el.dom);
        this.created = true;
        this.initFrameLoad();
    }
};
Cyan.Window.prototype.focus = function ()
{
    this.win.show();
    if (this.win.collapsed)
        this.win.expand();
};
Cyan.Window.prototype.maximize = function ()
{
    this.win.show();
    this.win.maximize();
};
Cyan.Window.prototype.restore = function ()
{
    this.win.show();
    this.win.restore();
};
Cyan.Window.prototype.isMaximizable = function ()
{
    return this.win.maximizable;
};
Cyan.Window.prototype.isMinimizable = function ()
{
    return this.win.minimizable;
};
Cyan.Window.prototype.isResizable = function ()
{
    return this.win.resizable;
};
Cyan.Window.prototype.isModal = function ()
{
    return this.win.modal;
};
Cyan.Window.prototype.mask = function (visible)
{
    if (!Cyan.Window.mask)
    {
        Cyan.Window.mask = new Ext.LoadMask(Ext.getBody(),
                {
                    msg: Cyan.titles["waiting"] + "..."
                });
    }

    if (visible)
    {
        Cyan.Window.mask.show();
    }
    else
    {
        Cyan.Window.mask.hide();
    }
};
Cyan.Window.prototype.initDialogSize = function (width, height)
{
    this.setSize(width + 20, height + 35);
};