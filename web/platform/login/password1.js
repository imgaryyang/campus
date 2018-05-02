Cyan.setAdapter("extjs");
Cyan.importJs("widgets/window.js");

Cyan.onload(function ()
{
    Cyan.Validator.markInvalidMessage = function (element, error)
    {
        Cyan.$("message").innerHTML = error;
    };

    Cyan.Class.overwrite(Cyan.Validator, "clearInvalid", function (element, callback)
    {
        Cyan.$("message").innerHTML = "";
        this.inherited(element, callback);
    });

    Cyan.Class.overwrite(window, "savePassword", function ()
    {
        this.inherited(Cyan.$("oldPassword").value, Cyan.$("password").value, {
            callback: function (ret)
            {
                Cyan.message("修改成功", function ()
                {
                    Cyan.Window.closeWindow(ret);
                });
            },
            error: function (error)
            {
                if (error)
                {
                    Cyan.$("message").innerHTML = error.message;
                    if (error.component)
                    {
                        Cyan.focus(error.component)
                    }
                }
            }
        });
    });

    window.closeWindow = function ()
    {
        Cyan.Window.closeWindow(null);
    };
});
