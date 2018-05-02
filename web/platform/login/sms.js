function sendSms0()
{
    Cyan.Arachne.doGet("/login/sms/send", arguments, 3)
}

function sendSms()
{
    sendSms0({
        callback: function (result)
        {
            if (result)
                Cyan.message(result);
            else
                Cyan.message("发生成功，稍后您的手机将收到短信验证码");
        },
        validate: false
    });

    return false;
}