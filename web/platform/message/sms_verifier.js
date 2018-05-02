Cyan.importCss("/platform/message/sms_verifier.css");

function sendVerifierSms(phone, msg, successMsg)
{
    Cyan.Arachne.get("/sms_verifier", {
        phone: phone,
        msg: msg
    }, function (result)
    {
        if (result)
            Cyan.message(result);
        else
            Cyan.message(successMsg || "发送成功，稍后您的手机将收到短信验证码");
    });
}