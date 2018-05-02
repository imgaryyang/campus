Cyan.importJs("widgets/bubble.js");

System.Shortcut = function (id, action)
{
    this.id = id;
    this.action = action;
    System.Shortcut.shortcuts[id] = this;
};

System.Shortcut.inited = false;

System.Shortcut.shortcuts = {};

System.Shortcut.tempShortcuts = [];

System.Shortcut.getShortcut = function (id)
{
    return System.Shortcut.shortcuts[id];
};

System.Shortcut.defaultShortcutAction = function ()
{
    System.Menu.menus[this.id].go();
};

System.Shortcut.prototype.invoke = function ()
{
    this.flash(false);
    this.hideMessage();

    this.action();
};

System.Shortcut.prototype.flash = function (flash)
{
    if (flash)
    {
        if (!this.flash$)
            this.flash$ = new Cyan.Render.Flash(this.div, "shortcut_icon_flash");

        this.flash$.start();
    }
    else if (this.flash$)
    {
        this.flash$.stop();
    }
};

System.Shortcut.prototype.hideMessage = function ()
{
    if (System.Shortcut.bubble && System.Shortcut.bubble.shortcut == this)
        System.Shortcut.bubble.hide();
};

System.Shortcut.prototype.showMessage = function (message)
{
    if (!System.Shortcut.bubble)
        System.Shortcut.bubble = new Cyan.Bubble("top_right");

    System.Shortcut.bubble.shortcut = this;
    var this$ = this;
    System.Shortcut.bubble.showWith(this.div, message, function ()
    {
        this$.flash(false);
    });
};

System.Shortcut.addShortcut = function (id, icon, title, action)
{
    if (System.Shortcut.inited)
    {
        if (!action)
            action = System.Shortcut.defaultShortcutAction;

        var shortcut = new System.Shortcut(id, action);

        var shortcutsDiv = Cyan.$("shortcuts");
        var div = document.createElement("DIV");
        div.className = "shortcut_icon";
        div.title = title;
        shortcut.div = div;
        shortcutsDiv.appendChild(div);

        var img = document.createElement("IMG");
        img.src = System.baseStylePath + "/icons/" + icon + ".png";
        div.appendChild(img);

        Cyan.attach(div, "click", function ()
        {
            shortcut.invoke();
        });
    }
    else
    {
        System.Shortcut.tempShortcuts.push({id: id, icon: icon, title: title, action: action});
    }
};

System.Shortcut.initShortcuts = function (groupId, callback)
{
    Cyan.Arachne.get("/desktop/shortcuts/user?groupId=" + groupId, null, function (shortcuts)
    {
        System.Shortcut.inited = true;

        var i, shortcut;
        for (i = 0; i < shortcuts.length; i++)
        {
            shortcut = shortcuts[i];
            System.Shortcut.addShortcut(shortcut.appId, shortcut.icon, shortcut.shortcutName,
                    shortcut.action ? Cyan.toFunction(shortcut.action) : null);
        }

        for (i = 0; i < System.Shortcut.tempShortcuts.length; i++)
        {
            shortcut = System.Shortcut.tempShortcuts[i];
            System.Shortcut.addShortcut(shortcut.id, shortcut.icon, shortcut.title, shortcut.action);
        }
        System.Shortcut.tempShortcuts.length = 0;
    });
};

Cyan.onload(function ()
{
    System.Shortcut.initShortcuts(System.Menu.group);
});