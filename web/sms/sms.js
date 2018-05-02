Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "sendSms", function ()
    {
        this.inherited({
            callback: function ()
            {
                Cyan.message("发送短信成功", function (result)
                {
                    if (result)
                    {
                        System.goMenu(null, "/sms/mt");
                        System.closePage();
                    }
                    else
                    {
                        Cyan.message("发送短信失败");
                    }
                });
            },
            error: function ()
            {
                Cyan.message("发送短信失败");
            }
        });
    });
});