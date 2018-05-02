System.Menu = {
    menus: {},
    go: function (url, title)
    {
        var s = url || this.url;
        if (s && s.indexOf("javascript:") == 0)
        {
            eval(s.substring(11));
        }
        else
        {
            if (!title)
                title = this.title;
            System.Menu.goMenu(this, url, title);
        }
    },
    goWith: function (args, leftId)
    {
        var url = this.url;
        if (url && url.indexOf("javascript:") == 0)
        {
            eval(url.substring(11));
            return;
        }

        if (args)
        {
            var page = this.getPage();
            var name, value;
            if (page)
            {
                var win = page.getWindow();
                if (win.Cyan && win.Cyan.Arachne)
                {
                    var form = win.Cyan.Arachne.form;
                    for (name in args)
                    {
                        if ((value = args[name]) != null)
                        {
                            var text;
                            if (value.value)
                            {
                                text = value.text;
                                value = value.value;
                            }
                            var component = win.Cyan.$$$(name);
                            if (component.length)
                            {
                                component.setValue(value);
                                if (text && component.setText)
                                    component.setText(text);
                            }
                            Cyan.setValue(form, name, value);
                        }
                    }

                    if (leftId && win.left && win.left.getItemById)
                    {
                        var item = win.left.getItemById(leftId);
                        if (item && item.select)
                            item.select();
                    }
                }
                page.show();

                return;
            }

            var first = true;
            for (name in args)
            {
                if ((value = args[name]) != null)
                {
                    if (value.value)
                        value = value.value;

                    url += (first && url.indexOf("?") >= 0 ? "&" : "?") + name + "=" +
                    encodeURIComponent(Cyan.toString(value));
                }
            }

            this.go(url);
        }
        else
        {
            this.go();
        }
    },
    goMenu: function (menu, url, title)
    {
        if (url)
        {
            if (url.indexOf("://") > 0)
            {
                window.open(url);
            }
            else
            {
                if (menu)
                {
                    menu.select();
                    url = menu.formatUrl(url);
                }
                System.Menu.openPage(url, menu ? menu.menuId : null, title || (menu ? menu.title : null));
            }
        }
        else if (menu.url)
        {
            if (menu.url.indexOf("://") > 0)
            {
                window.open(menu.url);
            }
            else
            {
                menu.select();
                System.Menu.openPage("/menu/" + menu.menuId, menu.menuId, menu.title);
            }
        }
    },
    formatUrl: function (url)
    {
        if (this.menuId)
            url += ( url.indexOf("?") >= 0 ? "&" : "?") + "menuId$=" + this.menuId;
        return url;
    },
    initMenu: function (menu)
    {
        menu.formatUrl = System.Menu.formatUrl;
        menu.go = System.Menu.go;
        menu.goWith = System.Menu.goWith;
        menu.select = System.Menu.select;
        menu.getPage = System.Menu.getPage;
    },
    getMenuByUrl: function (url)
    {
        for (var menuId in System.Menu.menus)
        {
            var menu = System.Menu.menus[menuId];
            if (menu.url == url)
            {
                return menu;
            }
        }
        return null;
    },
    openPage: function (url)
    {
        Cyan.$("mainFrame").src = Cyan.formatUrl(url);
        return "mainFrame";
    },
    getPage: function (id)
    {
        return null;
    }
};

System.clipboardPrefix = "$$SystemWithCyan$$";

System.putToClipboard = function (obj)
{
    System.clipboardData = obj;
    System.clipboardId = "" + Math.random();
    if (window.clipboardData)
    {
        var text = System.clipboardPrefix + obj.type + "$$" + System.clipboardId + "$$" + obj.content;
        try
        {
            window.clipboardData.setData("Text", text);
        }
        catch (e)
        {
        }
    }
};

System.getClipboardData = function (type)
{
    var text;
    if (window.clipboardData)
    {
        try
        {
            text = window.clipboardData.getData("Text");
        }
        catch (e)
        {
        }
    }

    if (text)
    {
        if (text.startsWith(System.clipboardPrefix))
            text = text.substring(System.clipboardPrefix.length);
        var index = text.indexOf("$$");
        if (index > 0)
        {
            if (text.substring(0, index) != type)
                return null;

            text = text.substring(index + 2);
            index = text.indexOf("$$");
            if (index > 0)
            {
                var clipboardId = text.substring(0, index);
                text = text.substring(index + 2);

                var result = {type: type, content: text};

                if (System.clipboardData && System.clipboardData.type == type && System.clipboardId == clipboardId)
                    result.obj = System.clipboardData;
                return result;
            }
        }
    }
    else if (System.clipboardData && System.clipboardData.type == type)
    {
        return {obj: System.clipboardData};
    }

    return null;
};

window.setTimeout(function ()
{
    var menuId = Cyan.getCookie("login.menuId");
    if (menuId)
    {
        var menu = System.Menu.menus[menuId];
        if (menu)
            menu.go();
    }
}, 50);