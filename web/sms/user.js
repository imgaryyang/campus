function editGateway(userId, types)
{
    showGateway(userId, types, {
        target:"_modal",
        callback:function(ret)
        {
            if (ret && Cyan.Table)
                reload(userId);
        }
    });
}

function sendSms(userId)
{
    System.goMenu(null, "/sms/user/" + userId + "/sendSms");
}

$.onload(function()
{
    Cyan.Class.overwrite(window, "saveGateway", function()
    {
        this.inherited(window.updateSuccess);
    });
});