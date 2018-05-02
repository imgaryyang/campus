Cyan.importJs("/platform/ca/cert.js");
Cyan.importJs("/platform/login/login_key_config.js");

function key_login(path, api)
{
    var random = Cyan.$("random").value;
    if (!random)
    {
        Cyan.message("登录超时，请刷新页面重新登录");
        return;
    }

    if (!api || Cyan.isString(api))
        api = System.Cert.getApi(api);

    if (!api)
    {
        Cyan.message("获取证书错误，请检查您的证书和设备");
        return;
    }

    api.sign(random, function (cert, sign)
    {
        Cyan.$("cert").value = cert;
        Cyan.$("sign").value = sign;

        if (api.getValidTime && window.key_login_days_ahead_for_remind && window.getKeyLoginRemindMessage)
        {
            try
            {
                var d = api.getValidTime(cert);
                var day = (d - new Date()) / (1000 * 60 * 60 * 24);
                if (day < key_login_days_ahead_for_remind)
                {
                    Cyan.message(getKeyLoginRemindMessage(Math.round(day)), function ()
                    {
                        //点击确定后再执行登录
                        login(path);
                    });
                    return;
                }
            }
            catch (e)
            {
                //如果有效期检验错误，不影响正常登录
            }
        }

        login(path);
    });
}