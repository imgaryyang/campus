Cyan.setAdapter("extjs");
Cyan.importJs("widgets/window.js");

Cyan.onload(function ()
{
    try
    {
        var activeElement = document.activeElement;
        if (!activeElement || activeElement.nodeName != "INPUT")
            Cyan.$("loginName").focus();
    }
    catch (e)
    {
    }

    Ext.form.Field.prototype.msgTarget = "side";
    var login = window.login;
    var refresh = function ()
    {
        var captcha = Cyan.$("captcha$img");
        if (captcha)
            Cyan.reloadImg(captcha);
    };
    var error = function (error)
    {
        Cyan.error(error.message, refresh);
    };

    window.login = function (forward)
    {
        var selectDept = function ()
        {
            Cyan.Window.showModal("/login/selectDept", function (ret)
            {
                if (ret)
                    window.location.href = forward;
                else
                    refresh();
            });
        };

        var loginProcess = function (result)
        {
            if (result.modifyPassword)
            {
                Cyan.Window.showModal("/login/password?message=" + encodeURIComponent(result.message),
                        function (result)
                        {
                            if (result)
                                loginProcess(result);
                            else
                                refresh();
                        });
            }
            else if (result.logoutOthers)
            {
                Cyan.confirm("您已经在其他机器登录，是否令原来的登录无效?", function (ret)
                {
                    if (ret == "yes")
                    {
                        logoutOthers(function ()
                        {
                            result.logoutOthers = false;
                            loginProcess(result);
                        });
                    }
                    else if (ret == "no")
                    {
                        result.logoutOthers = false;
                        loginProcess(result);
                    }
                }, 1);

            }
            else if (result.selectDept)
            {
                selectDept();
            }
            else
            {
                window.location.href = forward;
            }
        };

        login({
            validate: true,
            encrypteds: ["loginName", "password"],
            callback: loginProcess,
            error: error
        });
    };
});