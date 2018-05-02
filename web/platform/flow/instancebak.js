Cyan.importJs("/platform/flow/flowtags.js");

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "receoverInstance", function (instanceId)
    {
        var receoverInstance = this.inherited;
        Cyan.confirm("此操作将不可恢复，确定要恢复", function (ret)
        {
            if (ret == "ok")
            {
                receoverInstance(instanceId, function ()
                {
                    refresh()
                });
            }
        });
    });
});

function display(instanceId, flowTag)
{
    getLastStepId(instanceId, function (stepId)
    {
        System.openPage(getUrl(flowTag, stepId, true));
    });
}