Cyan.Plain.importCss("window");
Cyan.Plain.importLanguage("window");
Cyan.importJs("widgets/mask.js");
Cyan.importJs("event.js");

Cyan.Window.prototype.init = function ()
{
    this.mask0 = new Cyan.Mask(document.body);
    this.zIndex = Cyan.Mask.zIndex++;

    this.width = 200;
    this.height = 200;
    this.visible = false;
    this.closable = true;
    this.destoryWhenClose = false;
};

Cyan.Window.prototype.setHeaderHeight = function (headerHeight)
{
    this.headerHeight = headerHeight;
    this.setHeight(this.height);
};

Cyan.Window.prototype.create = function ()
{
    if (!this.created)
    {
        var win = this;

        var div = document.createElement("div");
        div.style.zIndex = this.zIndex;
        document.body.appendChild(div);

        if (this.left == null && this.top == null)
            this.center();

        div.style.visibility = "hidden";
        div.className = "cyan-window";
        div.style.left = this.left + "px";
        div.style.top = this.top + "px";
        div.style.width = this.width + "px";
        //div.style.height = this.height + "px";
        this.el = Cyan.$$(div);

        var headerEl = document.createElement("div");
        headerEl.className = "cyan-window-header";

        if (this.headerHeight)
            headerEl.style.height = this.headerHeight + "px";

        div.appendChild(headerEl);
        Cyan.attach(headerEl, "mousedown", function (event)
        {
            Cyan.Window.moving = win;
            event = new Cyan.Event(event);
            Cyan.Window.lastX = event.pageX;
            Cyan.Window.lastY = event.pageY;

            win.active();
        });

        var titleEl = document.createElement("div");
        titleEl.className = "cyan-window-title";
        Cyan.Elements.disableSelection(titleEl);

        if (this.title)
            titleEl.innerHTML = this.title;

        headerEl.appendChild(titleEl);

        var buttonsEl = document.createElement("div");
        buttonsEl.className = "cyan-window-buttons";
        headerEl.appendChild(buttonsEl);

        var closeButton = document.createElement("div");
        closeButton.className = "cyan-window-close";
        closeButton.onclick = function ()
        {
            win.close();
        };
        if (!this.closable)
            closeButton.style.display = "none";
        buttonsEl.appendChild(closeButton);

        var bodyEl = document.createElement("div");
        bodyEl.className = "cyan-window-body";
        if (this.html)
            bodyEl.innerHTML = this.html;
        var headerHeight = this.headerHeight || Cyan.toInt(Cyan.toInt(Cyan.Elements.getCss(headerEl, "height")));
        bodyEl.style.height = (this.height - headerHeight - 3 ) + "px";
        div.appendChild(bodyEl);

        var footerEl = document.createElement("div");
        footerEl.className = "cyan-window-footer";
        div.appendChild(footerEl);

        this.initFrameLoad();
        this.created = true;

        if (this.afterCreate)
            this.afterCreate();
    }
};

Cyan.Window.prototype.getHeaderEl = function ()
{
    return Cyan.$(this.el).$(".cyan-window-header");
};

Cyan.Window.prototype.getFooterEl = function ()
{
    return this.el.$(".cyan-window-footer");
};

Cyan.Window.prototype.getFooter = function ()
{
    return this.getFooterEl()[0];
};

Cyan.Window.prototype.getBodyEl = function ()
{
    return this.el.$(".cyan-window-body");
};

Cyan.Window.prototype.getTitleEl = function ()
{
    return this.el.$(".cyan-window-title");
};

Cyan.Window.prototype.setTitle = function (title)
{
    this.title = title;
    if (this.created)
        this.getTitleEl().html(title);
};

Cyan.Window.prototype.getTitle = function ()
{
    return this.title;
};

Cyan.Window.prototype.getSize = function ()
{
    return {width: this.width, height: this.height};
};

Cyan.Window.prototype.getWidth = function ()
{
    return this.width;
};

Cyan.Window.prototype.getHeight = function ()
{
    return this.height;
};

Cyan.Window.prototype.setSize = function (width, height)
{
    this.width = width;
    if (this.created)
        this.el.css("width", width + "px");

    this.setHeight(height);
};

Cyan.Window.prototype.setWidth = function (width)
{
    this.width = width;

    if (this.created)
    {
        this.el.css("width", width + "px");
        if (this.frame)
        {
            var frame = this.getFrame();
            if (frame)
            {
                frame.style.width = "100%";
                frame.style.height = "100%";
            }
        }
    }
};

Cyan.Window.prototype.setHeight = function (height)
{
    this.height = height;

    if (this.created)
    {
        //this.el.css("height", height + "px");

        var bodyEl = this.getBodyEl();

        var headerHeight = this.headerHeight || Cyan.toInt(this.getHeaderEl().css("height"));
        bodyEl.css("height", height - headerHeight - 3 + "px");

        if (this.frame)
        {
            var frame = this.getFrame();
            if (frame)
            {
                frame.style.width = "100%";
                frame.style.height = "100%";
            }
        }
    }
};

Cyan.Window.prototype.setPosition = function (x, y)
{
    this.left = x;
    this.top = y;

    if (this.created)
    {
        this.el.css("left", x + "px");
        this.el.css("top", y + "px");
    }
};

Cyan.Window.prototype.getPosition = function ()
{
    return {x: this.left, y: this.top};
};

Cyan.Window.prototype.getLeft = function ()
{
    return this.left;
};

Cyan.Window.prototype.getTop = function ()
{
    return this.top;
};

Cyan.Window.prototype.setModal = function (modal)
{
    this.modal = modal;
};

Cyan.Window.prototype.setClosable = function (closable)
{
    this.closable = closable;

    if (this.created)
        this.el.$(".cyan-window-close").display(closable);
};

Cyan.Window.prototype.setMaximizable = function (maximizable)
{
    this.maximizable = maximizable;
};

Cyan.Window.prototype.setMinimizable = function (minimizable)
{
    this.collapsible = minimizable;
};

Cyan.Window.prototype.setResizable = function (resizable)
{
    this.resizable = resizable;
};

Cyan.Window.prototype.setDestoryWhenClose = function (destoryWhenClose)
{
    this.destoryWhenClose = destoryWhenClose;
};

Cyan.Window.prototype.setHtml = function (html)
{
    if (this.created)
        this.getBodyEl().html(html);
    else
        this.html = html;
};

Cyan.Window.prototype.center = function ()
{
    var bodyWidth = window.innerWidth || document.body.clientWidth;
    var bodyHeight = window.innerHeight || document.body.clientHeight;

    this.setPosition(Math.floor((bodyWidth - this.width) / 2), Math.floor((bodyHeight - this.height) / 2));
};

Cyan.Window.prototype.show = function (callback)
{
    if (!this.created)
        this.create();
    else
        this.active();

    this.visible = true;

    if (this.modal)
        this.mask0.show();

    this.el.css("visibility", "visible");

    if (callback)
        callback();
};

Cyan.Window.prototype.beforeHide = function ()
{
    return (!this.onhide || this.onhide()) && (!this.created || !this.frame || !this.onunload || this.onunload());
};

Cyan.Window.prototype.hide = function ()
{
    if (this.beforeHide())
    {
        this.visible = false;
        if (this.created)
        {
            this.el.css("visibility", "hidden");

            if (this.modal)
                this.mask0.hide();
        }
        return true;
    }
    else
    {
        return false;
    }
};

Cyan.Window.prototype.destory = function ()
{
    if (this.beforeHide())
    {
        if (this.created)
        {
            this.el.remove();
            this.el = null;
            this.mask0.destory();
            this.mask0 = null;
        }
        return true;
    }
    else
    {
        return false;
    }
};

Cyan.Window.prototype.focus = function ()
{
    this.show();
};

Cyan.Window.prototype.active = function ()
{
    if (this.modal)
        this.mask0.active();

    this.zIndex = Cyan.Mask.zIndex++;
    this.el.css("z-index", this.zIndex);
};

Cyan.Window.prototype.close = function ()
{
    var b;
    if (this.frame || !this.destoryWhenClose)
        b = this.hide();
    else
        b = this.destory();

    if (b)
    {
        var frame = this.getFrame();
        if (frame)
        {
            frame.style.width = "10px";
            frame.style.height = "10px";
        }
        this.release();
    }
};

Cyan.Window.prototype.isVisible = function ()
{
    return this.visible;
};

Cyan.Window.prototype.maximize = function ()
{
};

Cyan.Window.prototype.restore = function ()
{
};

Cyan.Window.prototype.isMaximizable = function ()
{
    return this.maximizable;
};

Cyan.Window.prototype.isMinimizable = function ()
{
    return this.minimizable;
};

Cyan.Window.prototype.isResizable = function ()
{
    return this.resizable;
};

Cyan.Window.prototype.isModal = function ()
{
    return this.modal;
};

Cyan.Window.prototype.mask = function (visible)
{
    Cyan.LoadingMask.maskBody(visible);
};

Cyan.Window.prototype.initDialogSize = function (width, height)
{
    this.setSize(width + 2, height + 40);
};

(function ()
{
    var messageBox;
    var createMessageBox = function ()
    {
        if (!messageBox)
        {
            messageBox = new Cyan.Window();
            messageBox.zIndex = 25001;
            messageBox.mask0.zIndex = 25000;
            messageBox.setHtml("<div class='cyan-messagebox'><div class='cyan-messagebox-icon'></div>" +
            "<div class='cyan-messagebox-message'></div><div class='cyan-messagebox-buttons'></div></div>");
            messageBox.setSize(250, 130);
            messageBox.setModal(true);
            messageBox.close = function ()
            {
                this.hide();
                var closeCallback = this.closeCallback;
                messageBox.buttons = null;
                messageBox.closeCallback = null;
                messageBoxVisible = false;
                if (closeCallback)
                    closeCallback();
            };

            var ok = function ()
            {
                var buttons = messageBox.buttons;
                if (messageBoxVisible && buttons)
                {
                    var n = buttons.length;
                    for (var i = 0; i < n; i++)
                    {
                        var button = buttons[i];
                        if (button.ret == "ok")
                        {
                            messsageButtonClick.apply(button);
                            break;
                        }
                    }
                }
            };
            Cyan.$$(document.body).hotKey({
                'esc': function ()
                {
                    if (messageBoxVisible)
                        messageBox.close();
                },
                'enter': ok,
                'space': ok
            }, null);
        }

        return messageBox;
    };

    var messsageButtonClick = function ()
    {
        messageBox.hide();
        messageBox.buttons = null;
        messageBox.closeCallback = null;
        messageBoxVisible = false;
        if (this.callback)
            this.callback(this.ret);
    };

    var messageBoxVisible = false;

    Cyan.Window.messageBox = function (icon, title, message, buttons, closeAction, width, height)
    {
        var messageBox = createMessageBox();
        messageBox.setTitle(title || "");
        messageBox.show();
        if (!width)
        {
            width = 14 * message.length + 120;
            if (width > 600)
                width = 600;
            else if (width < 200)
                width = 200;
        }

        messageBox.setSize(width, height || (message ? 130 : 75));
        messageBox.center();
        messageBox.buttons = buttons;

        var iconEl = messageBox.el.$(".cyan-messagebox-icon");
        var messageEl = messageBox.el.$(".cyan-messagebox-message");

        var messageWidth = width - 2 - Cyan.toInt(messageEl.css("margin-left"));

        if (message)
        {
            messageEl.show();
            if (icon)
            {
                iconEl.show();
                iconEl.set("className", "cyan-messagebox-icon cyan-messagebox-icon-" + icon);
                messageWidth -= Cyan.toInt(iconEl.css("width")) + Cyan.toInt(iconEl.css("margin-left"));
            }
            else
            {
                iconEl.hide();
            }
        }
        else
        {
            messageEl.hide();
            iconEl.hide();
        }

        if (message)
            messageEl.html(Cyan.escapeHtml(message));
        messageEl.css("width", messageWidth + "px");

        var buttonsEl = messageBox.el.$(".cyan-messagebox-buttons")[0];
        buttonsEl.innerHTML = "";

        if (buttons)
        {
            buttonsEl.style.display = "";
            var n = buttons.length;
            for (var i = 0; i < n; i++)
            {
                var button = buttons[i];

                var buttonEl = Cyan.Elements.createButton(button.text);
                buttonEl.callback = button.callback;
                buttonEl.ret = button.ret;
                buttonEl.onclick = messsageButtonClick;
                buttonsEl.appendChild(buttonEl);
            }
        }
        else
        {
            buttonsEl.style.display = "none";
        }

        messageBox.closeCallback = closeAction;

        try
        {
            window.focus();
        }
        catch (e)
        {
        }

        setTimeout(function ()
        {
            if (messageBox.isVisible())
                messageBoxVisible = true;
        }, 100);
    };

    Cyan.message = function (message, callback)
    {
        if (Cyan.Event)
            callback = Cyan.Event.wrapCallback(callback);
        Cyan.Window.getTopWindow().Cyan.Window.messageBox("info", Cyan.titles.message, message, [{
            text: Cyan.Window.messageBoxTexts.ok,
            ret: "ok",
            callback: callback
        }], callback);
    };

    Cyan.error = function (message, callback)
    {
        if (Cyan.Event)
            callback = Cyan.Event.wrapCallback(callback);
        Cyan.Window.getTopWindow().Cyan.Window.messageBox("error", Cyan.titles.error, message, [{
            text: Cyan.Window.messageBoxTexts.ok,
            ret: "ok",
            callback: callback
        }], callback);
    };

    Cyan.confirm = function (message, callback, type)
    {
        if (Cyan.Event)
            callback = Cyan.Event.wrapCallback(callback);

        Cyan.Window.getTopWindow().Cyan.Window.messageBox("question", Cyan.titles.confirm, message, type ? [{
            text: Cyan.Window.messageBoxTexts.yes,
            ret: "yes",
            callback: callback
        }, {
            text: Cyan.Window.messageBoxTexts.no,
            ret: "no",
            callback: callback
        }, {
            text: Cyan.Window.messageBoxTexts.cancel,
            ret: "cancel",
            callback: callback
        }] : [{
            text: Cyan.Window.messageBoxTexts.ok,
            ret: "ok",
            callback: callback
        }, {
            text: Cyan.Window.messageBoxTexts.cancel,
            ret: "cancel",
            callback: callback
        }], callback);
    };

    Cyan.customConfirm = function (buttons, cfg)
    {
        Cyan.Window.getTopWindow().Cyan.Window.messageBox("question", Cyan.titles.confirm, cfg ? cfg.message : "",
                buttons, cfg ? cfg.close : null, cfg ? cfg.width : null, cfg ? cfg.height : null);
    };

    var progressBox;
    var createProgressBox = function ()
    {
        if (!progressBox)
        {
            progressBox = new Cyan.Window();
            progressBox.zIndex = 25001;
            progressBox.mask0.zIndex = 25000;
            progressBox.setHtml("<div class='cyan-progressbox'>" +
            "<div class='cyan-progressbox-message' style='display: none'></div>" +
            "<div class='cyan-progressbox-progress'>" +
            "<div class='cyan-progressbox-progress-bar' style='width: 0'></div>" +
            "<div class='cyan-progressbox-progress-text'></div></div>");
            progressBox.setSize(250, 130);
            progressBox.setModal(true);
            progressBox.setClosable(false);

            var percentage0 = 0;
            progressBox.updateProgress = function (percentage, text)
            {
                percentage0 = percentage;
                var s = Math.round(percentage * 100) + "%";
                this.el.$(".cyan-progressbox-progress-bar").css("width", s);
                if (text != null)
                    this.el.$(".cyan-progressbox-progress-text").html(Cyan.escapeHtml(text));
            };

            var wait = function ()
            {
                var percentage = percentage0 += 0.1;
                if (percentage > 1)
                    percentage = 0;
                progressBox.updateProgress(percentage, null);
            };

            var waitInterval;
            progressBox.startWait = function ()
            {
                progressBox.updateProgress(0, "");
                waitInterval = setInterval(wait, 300);
            };

            progressBox.stopWait = function ()
            {
                if (waitInterval)
                    clearInterval(waitInterval);
                percentage0 = 0;
            };
        }

        return progressBox;
    };

    var waiting = false;
    var progressing = false;
    Cyan.wait = function (message)
    {
        if (message)
        {
            waiting = true;
            progressing = false;
            createProgressBox();
            progressBox.stopWait();
            progressBox.setSize(300, 70);
            progressBox.show();
            progressBox.el.$(".cyan-progressbox-message").hide();
            progressBox.center();
            progressBox.setTitle(Cyan.escapeHtml(message));
            progressBox.startWait();
        }
        else if (progressBox && waiting)
        {
            progressBox.hide();
            progressBox.stopWait();

            waiting = false;
            progressing = false;
        }
    };

    Cyan.displayProgress = function (percentage, title, message)
    {
        if (percentage >= 0)
        {
            waiting = false;
            if (!progressing)
            {
                progressing = true;
                createProgressBox();
                progressBox.stopWait();
                progressBox.setSize(800, 90);
                progressBox.show();
                progressBox.el.$(".cyan-progressbox-message").show();
                progressBox.center();
                progressBox.stopWait();
            }

            progressBox.setTitle(Cyan.escapeHtml(title));
            progressBox.el.$(".cyan-progressbox-message").html(Cyan.escapeHtml(message || ""));
            progressBox.updateProgress(percentage, Math.round(percentage * 100) + "% ");
        }
        else if (progressBox && progressing)
        {
            progressBox.hide();
            progressBox.stopWait();

            waiting = false;
            progressing = false;
        }
    };

    Cyan.closeProgress = function ()
    {
        if (progressBox && progressing)
        {
            progressBox.hide();
            progressBox.stopWait();

            waiting = false;
            progressing = false;
        }
    };

    var promptBox, promptMultiline, promptCallback;
    var createPromptBox = function ()
    {
        if (!promptBox)
        {
            promptBox = new Cyan.Window();
            promptBox.zIndex = 25001;
            promptBox.mask0.zIndex = 25000;
            promptBox.setHtml("<div class='cyan-promptbox'>" +
            "<div class='cyan-promptbox-message'></div>" +
            "<div class='cyan-promptbox-input'><input><textarea style='display: none'></textarea></div>" +
            "<div class='cyan-promptbox-buttons'><button type='button'>" + Cyan.Window.messageBoxTexts.ok +
            "</button><button type='button'>" + Cyan.Window.messageBoxTexts.cancel + "</button></div></div>");
            promptBox.setTitle(Cyan.titles.prompt);
            promptBox.setModal(true);
            promptBox.getValue = function ()
            {
                return this.el.$(promptMultiline ? "textarea" : "input").getValue();
            };

            var ok = function ()
            {
                promptBox.hide();
                if (promptCallback)
                    promptCallback(promptBox.getValue());
            };

            var cancel = function ()
            {
                promptBox.hide();
                if (promptCallback)
                    promptCallback(null);
            };

            promptBox.afterCreate = function ()
            {
                var buttons = this.el.$("button");
                buttons[0].onclick = ok;
                buttons[1].onclick = cancel;

                this.el.$(":field").hotKey({
                    "\n": ok,
                    "esc": cancel
                }, null);
            };
        }

        return promptBox;
    };

    Cyan.Window.showPromptBox = function (message, defaultValue, callback, multiline)
    {
        promptMultiline = multiline;
        promptCallback = callback;
        var promptBox = createPromptBox();

        if (multiline)
            promptBox.setSize(350, 180);
        else
            promptBox.setSize(300, 115);

        promptBox.show();
        promptBox.center();

        promptBox.el.$(".cyan-promptbox-message").html(Cyan.escapeHtml(message));

        var textarea = promptBox.el.$("textarea");
        var input = promptBox.el.$("input");
        if (multiline)
        {
            input.hide();
            textarea.show();
            textarea.setValue(defaultValue);
            textarea.focus();
            Cyan.focus(textarea[0]);
        }
        else
        {
            textarea.hide();
            input.show();
            input.setValue(defaultValue);
            Cyan.focus(input[0]);
        }
    };

    Cyan.prompt = function (message, defaultValue, callback, multiline)
    {
        if (Cyan.Event)
            callback = Cyan.Event.wrapCallback(callback);

        Cyan.Window.getTopWindow().Cyan.Window.showPromptBox(message, defaultValue, callback, multiline);
    };

    Cyan.onload(function ()
    {
        Cyan.attach(document.body, "mouseup", function ()
        {
            Cyan.Window.moving = null;
            Cyan.Window.lastX = null;
            Cyan.Window.lastY = null;
        });

        Cyan.attach(document.body, "mousemove", function (event)
        {
            var win = Cyan.Window.moving;
            if (win)
            {
                var pageX = event.pageX;
                var pageY = event.pageY;

                win.setPosition(win.left + pageX - Cyan.Window.lastX, win.top + pageY - Cyan.Window.lastY);

                Cyan.Window.lastX = pageX;
                Cyan.Window.lastY = pageY;
            }
        });
    });
})();