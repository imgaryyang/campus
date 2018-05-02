$.onload(function()
{
    Cyan.Class.overwrite(window, "start", function(gatewayId)
    {
        this.inherited(gatewayId, function()
        {
            reload(gatewayId);
        });
    });

    Cyan.Class.overwrite(window, "stop", function(gatewayId)
    {
        this.inherited(gatewayId, function()
        {
            reload(gatewayId);
        });
    });
});