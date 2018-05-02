function editAuth()
{
    var menuId = requireSelectedKey();
    if (menuId)
        System.openPage(System.formatUrl("/MenuAuth?menuId=" + menuId));
}

function editUrl(authId)
{
    var menuId;
    if (authId)
    {
        menuId = Cyan.Arachne.form.menuId;
    }
    else
    {
        menuId = requireSelectedKey();
        authId = "";
    }

    if (menuId)
    {
        var pageId = "menuAuthUrl_" + menuId;
        var page = System.getPage(pageId);
        if (page)
        {
            var win = page.getWindow();
            win.Cyan.Arachne.refresh("authId", function ()
            {
                win.Cyan.$$("#authId").setValue(authId);
                win.Cyan.Arachne.form.authId = authId;
                win.query();
                page.show();
            });
        }
        else
        {
            var url = "/MenuAuthUrl?menuId=" + menuId;
            if (authId)
                url += "&authId=" + authId;
            System.openPage(url, pageId);
        }
    }
}

Cyan.onload(function ()
{
    var initAuths = window.initAuths;
    if (initAuths)
    {
        window.initAuths = function ()
        {
            initAuths(function (result)
            {
                if (result)
                    reload();
            });
        };
    }

    var file = Cyan.$("entity.icon");
    if (file)
    {
        Cyan.$$(file).attach("change", function ()
        {
            Cyan.Arachne.getImagePath(this, function (path)
            {
                Cyan.$("icon").src = path;
            });
        });
    }
});