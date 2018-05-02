function sendMail(userId, userName)
{
    var menu = System.getMenuByUrl("/oa/mail/new");

    var url = "/oa/mail/new?mailTo=" + encodeURIComponent("\"" + userName + "\"<" + userId + "@local>");
    if (menu)
        menu.go(url);
    else
        Cyan.message("您没有发送邮件的权限");
}

function sendSms(userId, userName)
{
    var menu = System.getMenuByUrl("/message/sms");

    var url = "/message/sms?sendTo=" + encodeURIComponent("\"" + userName + "\"<" + userId + "@local>");
    if (menu)
        menu.go(url);
    else
        Cyan.message("您没有发送短信的权限");
}

function sendIm(userId, userName)
{
    System.showIm(userId, userName);
}