Cyan.importJs("widgets/menu.js");
Cyan.importCss("/platform/help/help.css");

function initHelp()
{
    var menu;
    System.Help = System.Agent.load("lion");
    System.Help.init = function()
    {
        if (!menu)
        {
            menu = new Cyan.Menu([
                {
                    text:"隐藏精灵",
                    action:function()
                    {
                        System.Help.hide();
                    }
                },
                {
                    text:"不再提示",
                    action:function()
                    {
                        System.Help.hide();
                        System.Help.closed = true;
                    }
                }
            ]);

            var showMenu = function(event)
            {
                System.Help.closeSpeak();

                var position = Cyan.Elements.getPosition(this);
                var size = Cyan.Elements.getComponentSize(this);
                menu.showAt(event.pageX, event.pageY);

                event.stop();

                return false;
            };

            Cyan.attach(System.Help.getImg(), "click", showMenu);
            Cyan.attach(System.Help.getImg(), "contextmenu", showMenu);
        }

    };
    System.Help.showMessage = function(content, autoClose)
    {
        if (!menu)
        {
            System.Help.show(function()
            {
                System.Help.init();
                System.Help.speak(content, autoClose ? 10000 : -1);
            });
        }
        else
        {
            System.Help.speak(content, autoClose ? 10000 : -1);
        }
    };

    System.Help.showHelp = function(help)
    {
        var s = help.content;

        s = "<div class=\"help\"><div class=\"help_content\">" + s + "</div>";
        s += "<div class=\"help_action\">";

        s += "<span class=\"help_exclude\">[<a  href=\"javascript:System.Help.excludeHelp(" + help.helpId +
                ")\">知道了</a>]</span>";

        System.Help.showMessage(s, true);
    };

    System.Help.excludeHelp = function(helpId)
    {
        System.Help.closeSpeak();
        Cyan.Arachne.get("/help/" + helpId + "/exclude", null);
    };

    System.Help.showRemind = function(index, first)
    {
        var remind = System.Help.reminds[index];

        var s = "<a href=\"javascript:System.Help.goRemind(" + index + ")\">" + remind.content + "</a>";
        s = "<div class=\"help_remind\"><div class=\"help_remind_content\">" + s + "</div>";
        s += "<div class=\"help_remind_nav\">";

        if (index > 0)
            s += "<span class=\"help_remind_pre\"><a  href=\"javascript: System.Help.showRemind(" + (index - 1) +
                    ")\"><<<上一条</a></span>";
        if (index < System.Help.reminds.length - 1)
            s += "<span class=\"help_remind_next\"><a href=\"javascript: System.Help.showRemind(" + (index + 1) +
                    ")\">下一条>>></a></span>";

        if (first)
            System.Help.showMessage(s);
        else
            System.Help.speak0(s);
    };

    System.Help.goRemind = function(index)
    {
        var remind = System.Help.reminds[index];
        if (remind.appId)
            System.goMenu(remind.appId);

        Cyan.Arachne.get("/remind/" + remind.remindId + "/read", null);
    };

    System.getComet().setHandler("com.gzzm.platform.help.HelpInfo", function(help)
    {
        if (!System.Help.closed)
            System.Help.showHelp(help);
    });

    Cyan.Arachne.get("/reminds", null, function(reminds)
    {
        if (reminds && reminds.length)
        {
            System.Help.reminds = reminds;
            System.Help.showRemind(0, true);
        }
    });

    System.addShortcut("help", "help", "帮助精灵", showHelp);
}

function showHelp()
{
    if (System.Help)
    {
        if (System.Help.isVisible())
        {
            System.Help.jump();
        }
        else
        {
            System.Help.closed = false;
            System.Help.show(System.Help.init);
        }
    }
    else
    {
        initHelp();
        System.Help.show(System.Help.init);
    }
    System.Help.speak0("我是帮助小精灵，我会在您需要的时候为您提供有用信息。", 10000);
}