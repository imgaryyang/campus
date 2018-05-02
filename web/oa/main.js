Cyan.onload(function ()
{
    System.getMainWindow = function ()
    {
        return Cyan.$("mainFrame").contentWindow;
    };

    window.setTimeout(function ()
    {
        startComet();
        System.Im.init();

        if (window.Helper)
        {
            //帮助中心图标
            Helper.initIcon();
            // initHelp();
        }
    }, 50);

    window.setTimeout(function ()
    {
        receiveImMessages();
    }, 500);


    Cyan.attach(window, "resize", function ()
    {
        var mainFrame = Cyan.$("mainFrame");
        if (mainFrame != null)
        {
            setTimeout(function ()
            {
                var height = window.document.documentElement.clientHeight;
                mainFrame.style.height = height + "px";
            }, 50);
        }
    });
});

function showImMessage(message, sound)
{
    var content = message.content.escapeHtml();
    var action;
    var showWithIm = true;
    if (message.url)
    {
        var urls = message.url.split(",");
        for (var i = 0; i < urls.length; i++)
        {
            var url = urls[i];
            var menu = System.getMenuByUrl(url);
            if (menu != null)
            {
                var appId = menu.menuId;
                var shortcut = System.getShortcut(appId);
                if (shortcut)
                {
                    shortcut.showMessage(content);
                    shortcut.flash(true);
                    if (window.focus)
                        window.focus();
                    if (sound)
                        System.Im.soundMessage();

                    showWithIm = false;
                }
                else
                {
                    action = "System.goMenu('" + appId + "');";
                }
                break;
            }
        }

        if (showWithIm && !action)
        {
            action = "System.openPage('" + urls[0] + "');";
        }
    }

    if (showWithIm)
    {
        if (action)
        {
            content += ",点击<a href='#' onclick=\"" + action + ";return false\">这里</a>查看"
        }

        System.Im.receiveUserMessage({
            content: content,
            time: message.sendTime,
            sender: -1,
            senderName: "系统消息",
            type: "html"
        }, sound);
    }

    if (message.messageId)
        Cyan.Arachne.get("/message/im/" + message.messageId + "/read", null);

    var mainWindow = System.getMainWindow();
    if (mainWindow && mainWindow.reloadDesktop)
        mainWindow.reloadDesktop();
}

function startComet()
{
    System.getComet().connect();

    System.getComet().setHandler("com.gzzm.platform.message.ImMessage", function (message)
    {
        showImMessage(message, true);
    });
    System.getComet().setReset("message/im", receiveImMessages);
}

function receiveImMessages()
{
    Cyan.Arachne.get("/message/im/noreaded", null, function (messages)
    {
        if (messages)
        {
            for (var i = 0; i < messages.length; i++)
                showImMessage(messages[i], i == 0)
        }
    });
}

Cyan.onunload(function ()
{
    Cyan.Arachne.get("/login/exit", null, function ()
    {
    });
});