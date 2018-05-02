Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "deleteInstance", function (instanceId)
    {
        var deleteInstance = this.inherited;
        Cyan.confirm("此操作将不可恢复，确定要删除", function (ret)
        {
            if (ret == "ok")
            {
                deleteInstance(instanceId, function ()
                {
                    refresh()
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "stopInstance", function (instanceId)
    {
        var stopInstance = this.inherited;
        Cyan.confirm("此操作将不可恢复，确定要办结", function (ret)
        {
            if (ret == "ok")
            {
                stopInstance(instanceId, function ()
                {
                    refresh()
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "stopAll", function (consignee)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要办结的流程");
            return;
        }

        var stopAll = this.inherited;
        Cyan.confirm("确定要办结选择的流程，此操作将不能恢复?", function (ret)
        {
            if (ret == "ok")
            {
                stopAll({
                    callback: function ()
                    {
                        Cyan.message("操作成功", function ()
                        {
                            refresh();
                        });
                    },
                    form: Cyan.$$("form")[0]
                });
            }
        });
    });
});

function control(instanceId)
{
    var url = getControlUrl(instanceId);
    if (!url)
        url = "/flow/control/instance/" + instanceId;
    System.openPage(url);
}

function display(instanceId, flowTag)
{
    getLastStepId(instanceId, function (stepId)
    {
        System.openPage(getUrl(flowTag, stepId, false));
    });
}

function controlForm(bodyId)
{
    System.openPage("/form/control/" + bodyId);
}