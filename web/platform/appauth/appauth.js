Cyan.onload(function ()
{
    if (window.receiverSelector)
    {
        setTimeout(function ()
        {
            var selector = receiverSelector.getSelector();
            selector.bindQuery("word");
        }, 50);
    }

    Cyan.Class.overwrite(window, "addDepts", function ()
    {
        var deptIds = Cyan.$$$("deptIds").selectedValues();
        if (deptIds.length == 0)
        {
            Cyan.message("请选择部门");
            return;
        }
        Cyan.Arachne.form.objectIds = [];
        for (var i = 0; i < deptIds.length; i++)
        {
            Cyan.Arachne.form.objectIds.push(deptIds[i].substring(5))
        }

        this.inherited(function ()
        {
            Cyan.message("添加部门成功", function ()
            {
                Cyan.Window.setReturnValue(true);
                Cyan.Window.closeWindow();
            });
        });
    });

    Cyan.Class.overwrite(window, "addUsers", function ()
    {
        var userIds = Cyan.$$$("userIds").selectedValues();
        if (userIds.length == 0)
        {
            Cyan.message("请选择用户");
            return;
        }
        Cyan.Arachne.form.objectIds = [];
        for (var i = 0; i < userIds.length; i++)
        {
            Cyan.Arachne.form.objectIds.push(userIds[i].substring(5))
        }

        this.inherited(function ()
        {
            Cyan.message("添加用户成功", function ()
            {
                Cyan.Window.setReturnValue(true);
                Cyan.Window.closeWindow();
            });
        });
    });

    Cyan.Class.overwrite(window, "addStations", function ()
    {
        var stationIds = $$$("stationIds").selectedValues();
        if (stationIds.length == 0)
        {
            Cyan.message("请选择岗位");
            return;
        }
        Cyan.Arachne.form.objectIds = [];
        for (var i = 0; i < stationIds.length; i++)
        {
            Cyan.Arachne.form.objectIds.push(stationIds[i].substring(8))
        }

        this.inherited(function ()
        {
            Cyan.message("添加岗位成功", function ()
            {
                Cyan.Window.setReturnValue(true);
                Cyan.Window.closeWindow();
            });
        });
    });

    Cyan.Class.overwrite(window, "showAdd", function (forward)
    {
        this.inherited(forward, {
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                    mainBody.reload();
            }
        });
    });

    Cyan.Class.overwrite(window, "showScope", function (appType, appId, authType, objectId)
    {
        this.inherited(appType, appId, authType, objectId, {
            target: "_modal"
        });
    });

    Cyan.Class.overwrite(window, "setScope", function ()
    {
        this.inherited(function ()
        {
            Cyan.message("设置成功", function ()
            {
                Cyan.Window.closeWindow();
            });

        });
    });
});

function showAddUsers()
{
    showAdd("user");
}

function showAddDepts()
{
    showAdd("dept");
}

function showAddStations()
{
    showAdd("station");
}