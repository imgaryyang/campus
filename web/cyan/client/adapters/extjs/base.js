if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment)
{
    Range.prototype.createContextualFragment = function (html)
    {
        var frag = document.createDocumentFragment(), div = document.createElement("div");
        frag.appendChild(div);
        div.outerHTML = html;
        return frag;
    };
}

//导入extjs的js
Cyan.importJs("../../extjs/adapter/ext/ext-base.js");
Cyan.importJs("../../extjs/ext-all.js");

Cyan.onload(function ()
{
    Cyan.Extjs.initBlank();
    Ext.isReady = true;
});

Cyan.initExtLanguage = function ()
{
    var language = window.languageForCyan$;
    var index = language.indexOf("-");
    if (index > 0)
        language = language.substring(0, index) + "_" + language.substring(index + 1).toUpperCase();
    //根据当前的语言，导入extjs的相应语言包
    Cyan.importJs("../../extjs/src/locale/ext-lang-" + language + ".js");
};
Cyan.initExtLanguage();

//导入extjs相应的css
Cyan.importCss("../../extjs/resources/css/ext-all.css");
Cyan.importCss("adapters/extjs/extend.css");

/**
 * 重写信息提示窗口
 * @param message 提示信息
 * @param callback 回调函数
 */
Cyan.message = function (message, callback)
{
    message += " ";

    //调用extjs的消息提示显示
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Ext)
            win = win.parent;
    }
    catch (e)
    {
    }
    win.Cyan.Extjs.initDialog();
    win.Ext.MessageBox.show({
        title: Cyan.titles.message,
        msg: Cyan.escapeHtml(message),
        buttons: Ext.MessageBox.OK,
        fn: Cyan.Event ? Cyan.Event.wrapCallback(callback) : callback,
        icon: Ext.MessageBox.INFO
    });

    //标识进度窗口和等待窗口没有被打开
    Ext.MessageBox.progressing = false;
    Ext.MessageBox.waiting = false;
};

/**
 * 重写错误提示窗口
 * @param message 错误信息
 * @param callback 回调函数
 */
Cyan.error = function (message, callback)
{
    message += " ";

    //调用extjs的消息提示信息
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Ext)
            win = win.parent;
    }
    catch (e)
    {
    }
    win.Cyan.Extjs.initDialog();
    win.Ext.MessageBox.show({
        title: Cyan.titles.error,
        msg: Cyan.escapeHtml(message),
        buttons: Ext.MessageBox.OK,
        fn: Cyan.Event ? Cyan.Event.wrapCallback(callback) : callback,
        icon: Ext.MessageBox["ERROR"]
    });

    //标识进度窗口和等待窗口没有被打开
    Ext.MessageBox.progressing = false;
    Ext.MessageBox.waiting = false;
};

Cyan.confirm = function (message, callback, type)
{
    message += " ";

    //调用extjs的消息提示显示
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Ext)
            win = win.parent;
    }
    catch (e)
    {
    }
    win.Cyan.Extjs.initDialog();
    win.Ext.MessageBox.show({
        title: Cyan.titles.confirm,
        msg: Cyan.escapeHtml(message),
        buttons: type ? Ext.MessageBox.YESNOCANCEL : Ext.MessageBox.OKCANCEL,
        fn: Cyan.Event ? Cyan.Event.wrapCallback(callback) : callback,
        icon: Ext.MessageBox.QUESTION
    });

    //标识进度窗口和等待窗口没有被打开
    win.Ext.MessageBox.progressing = false;
    win.Ext.MessageBox.waiting = false;
};

Cyan.prompt = function (message, defaultValue, callback, multiline)
{
    //保证消息的长度，保证窗口的大小
    message += " ";

    //调用extjs的prompt功能
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Ext)
            win = win.parent;
    }
    catch (e)
    {
    }

    win.Cyan.Extjs.initDialog();

    var f = callback;
    if (callback)
        callback = function (btn, text)
        {
            f(btn == "ok" ? text : null);
        };

    win.Ext.MessageBox.show({
        title: Cyan.titles.prompt,
        msg: Cyan.escapeHtml(message),
        prompt: true,
        value: defaultValue,
        multiline: multiline,
        fn: Cyan.Event ? Cyan.Event.wrapCallback(callback) : callback,
        buttons: Ext.MessageBox.OKCANCEL,
        width: multiline ? 300 : 200
    });

    //标识进度窗口和等待窗口没有被打开
    win.Ext.MessageBox.progressing = false;
    win.Ext.MessageBox.waiting = false;
};

Cyan.tip = function (message)
{
    if (!Cyan.Extjs.msgCt)
    {
        Cyan.Extjs.msgCt = Ext.DomHelper.insertFirst(document.body, {id: 'ext-msg-div'}, true);
        Cyan.Extjs.msgCt.alignTo(document, 't-t');
        Cyan.Extjs.msgCt.alignTo(document, 't-t');

        var html = ['<div class="msg">',
            '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
            '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc" id="ext-msg-div-content">',
            'empty</div></div></div><div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
            '</div>'].join('');

        Cyan.Extjs.msgCtM = Ext.DomHelper.append(Cyan.Extjs.msgCt, {html: html}, true);
    }
    Cyan.$("ext-msg-div-content").childNodes[0].nodeValue = message;
    Cyan.Extjs.msgCtM.slideIn('t').pause(1).ghost('t');
};

/**
 * 显示进度对话框
 * @param percentage 进度百分比
 * @param title 进度标题
 * @param message 进度信息
 */
Cyan.displayProgress = function (percentage, title, message)
{
    //标识等待窗口没有被打开
    if (percentage >= 0)
    {
        var progressing = Ext.MessageBox.progressing;

        Ext.MessageBox.waiting = false;
        Ext.MessageBox.progressing = true;

        if (message && message.length > 60)
            message = message.substring(0, 57) + "...";

//        if (progressing && Ext.MessageBox.isVisible())
//        {
//            Ext.MessageBox.progress(title, message, "");
//        }
//        else
        {
            Ext.MessageBox.show({
                title: title,
                msg: message,
                width: 800,
                progress: true,
                closable: false
            });
        }

        //标识进度窗口被打开
        Ext.MessageBox.updateProgress(percentage, Math.round(percentage * 100) + "% ");
    }
    else if (Ext.MessageBox.progressing)
    {
        //关闭进度窗口
        Ext.MessageBox.hide();

        Ext.MessageBox.progressing = false;
        Ext.MessageBox.waiting = false;
    }
};

Cyan.closeProgress = function ()
{
    if (Ext.MessageBox.progressing)
    {
        //关闭进度窗口
        Ext.MessageBox.hide();

        Ext.MessageBox.progressing = false;
        Ext.MessageBox.waiting = false;
    }
};

/**
 * 等待窗口
 * @param message 等待消息
 */
Cyan.wait = function (message)
{
    if (message)
    {
        Ext.MessageBox.progressing = false;
        //显示进度窗口
        Ext.MessageBox.waiting = true;
        Ext.MessageBox.show({
            msg: message,
            width: 300,
            wait: true,
            waitConfig: {interval: 200},
            icon: 'ext-mb-waiting'
        });
        //标识进度窗口被打开
        Ext.MessageBox.progressing = false;
    }
    else if (Ext.MessageBox.waiting)
    {
        //关闭等待窗口
        Ext.MessageBox.hide();
        Ext.MessageBox.waiting = false;
        Ext.MessageBox.progressing = false;
    }
};

Cyan.customConfirm = function (buttons, cfg)
{
    //调用extjs的prompt功能
    var win = window;
    try
    {
        while (win.parent != win && win.parent.Cyan && win.parent.Ext)
            win = win.parent;
    }
    catch (e)
    {
    }

    var buttonClicked = false;

    var bs = [];
    for (var i = 0; i < buttons.length; i++)
    {
        var button = buttons[i];
        bs[i] = {
            button: button,
            text: button.text,
            handler: function ()
            {
                buttonClicked = true;
                dialog.close();
                if (this.button.callback)
                    this.button.callback();
            }
        };
    }

    var dcfg = {
        title: Cyan.titles.confirm,
        resizable: false,
        constrain: true,
        constrainHeader: true,
        minimizable: false,
        maximizable: false,
        stateful: false,
        modal: true,
        shim: true,
        buttonAlign: "center",
        width: (cfg ? cfg.width : null) || 250,
        height: (cfg ? cfg.height : null) || (cfg && cfg.message ? 115 : 0),
        minHeight: 80,
        plain: true,
        footer: true,
        closable: true,
        border: false,
        buttons: bs
    };

    if (cfg)
    {
        if (cfg.close)
        {
            dcfg.listeners = {
                beforedestroy: function ()
                {
                    if (!buttonClicked)
                        cfg.close();
                    return true;
                }
            };
        }

        if (cfg.message)
        {
            dcfg.html =
                    "<div class='x-window-dlg'><div class='x-window-body'><div class='ext-mb-icon ext-mb-question'></div><div class='ext-mb-content'><span class='ext-mb-text'>" +
                    cfg.message + "</span><br/></div></div>";
        }
    }

    var dialog = new win.Ext.Window(dcfg);
    dialog.show();
};

Cyan.Extjs =
{
    Event: function (target, x, y)
    {
        this.xy = [x, y];
        this.target = target;
    },
    initDialog: function ()
    {
        var dialog = Ext.MessageBox.getDialog("");
        dialog.mask.dom.style.zIndex = 25000;
        dialog.el.dom.style.zIndex = 25001;
    },
    initBlank: function ()
    {
        Ext.BLANK_IMAGE_URL = Cyan.getRealPath("../../extjs/resources/images/default/tree/s.gif");
    }
};
Cyan.Extjs.Event.prototype.getXY = function ()
{
    return this.xy;
};
Cyan.Extjs.Event.prototype.getTarget = function ()
{
    return this.target;
};
Cyan.Extjs.Event.prototype.preventDefault = function ()
{
};

Cyan.onload(function ()
{
    var f = function ()
    {
        var width;
        if (this instanceof Ext.menu.DateMenu)
        {
            width = 180;
        }
        else if (this instanceof Ext.menu.ColorMenu)
        {
            width = 155;
        }
        else if (this.items.length == 1)
        {
            var item = this.items.get(0);
            if (item instanceof Cyan.Extjs.WidgetItem)
                width = item.component.el.dom.clientWidth + "px";
        }

        if (width)
            this.setWidth(width);
    };
    if (Cyan.navigator.name == "IE10")
    {
        Cyan.Class.overwrite(Ext.menu.Menu, "afterRender", function ()
        {
            this.inherited();
            this.on("beforeshow", f);
            this.on("show", f);
        });
    }
});