Cyan.importJs("/platform/flow/flowtags.js");

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "setReaded", function ()
    {
        if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要设置为已阅读的消息");
            return;
        }

        this.inherited(function ()
        {
            refresh();
        });
    });
});

function display(instanceId, flowTag)
{
    getStepId(instanceId, function (stepId)
    {
        System.openPage(getUrl(flowTag, stepId, false));
    });
}