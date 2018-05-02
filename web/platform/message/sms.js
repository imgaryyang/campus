System.isMobilePhone = function (s)
{
    return Cyan.startsWith(s, "#") || /^1[3|4|5|8][0-9]\d{4,8}$/.exec(s);
};

var receiverInput;
Cyan.onload(function ()
{
    var sendTo = Cyan.$("sendTo");
    if (sendTo)
    {
        receiverInput = new System.ReceiverInput("phone");
        receiverInput.isValid = System.isMobilePhone;
        receiverInput.appId = "sms_receiver";
        receiverInput.create(sendTo);
    }

    if (window.send)
    {
        var send = window.send;
        window.send = function ()
        {
            if (Cyan.$("sendTo").value == 0)
            {
                Cyan.message("请选填写收者");
                receiverInput.focus();
                return;
            }

            if (!checkReceiverInput(receiverInput))
                return;

            send({
                callback: function ()
                {
                    Cyan.message("发送短信成功", function ()
                    {
                        System.goMenu(null, "/message/sms/query");
                        System.closePage();
                    });
                },
                error: function ()
                {
                    Cyan.message("发送短信失败");
                },
                progress: true
            });
        };
    }

    Cyan.Class.overwrite(window, "cancel", function ()
    {
        var receiptIds = Cyan.$$("#keys").checkedValues();

        if (!receiptIds.length)
        {
            Cyan.message("请选择要撤消的短信");
            return;
        }

        this.inherited(function (ret)
        {
            if (ret)
                reload();
            else
                Cyan.message("所有选择的短信均已发送或撤销");
        });
    });
});

function openReceiverDialog()
{
    receiverInput.openSelectDialog("手机");
}

function showReceipts(smsId)
{
    var pageId = "sms_receipts";
    var page = System.getPage(pageId);
    if (page)
    {
        var win = page.getWindow();
        win.Cyan.Arachne.form.smsId = smsId;
        win.query();
        page.show();
    }
    else
    {
        var url = "/message/sms/receipt?smsId=" + smsId;
        System.openPage(System.formatUrl(url), pageId);
    }
}

function resend(smsId)
{
    if (!smsId)
        smsId = Cyan.Arachne.form.smsId;

    var receiptIds = Cyan.$$("#keys").checkedValues();

    var url = "/message/sms?smsId=" + smsId;
    for (var i = 0; i < receiptIds.length; i++)
        url += "&receiptIds=" + receiptIds[i];

    System.openPage((System.getMenuByUrl("/message/sms") || System.getMenuByUrl("/message/sms?dept=true") ||
    System.getMenuByUrl("/message/sms?requireReply=true") ||
    System.getMenuByUrl("/message/sms?dept=true&requireReply=true")).formatUrl(url));
}

function checkReceiverInput(receiverInput)
{
    var s = "";
    var errorPhoneList = receiverInput.getErrorReceiverList();
    if (errorPhoneList && errorPhoneList.length)
    {
        for (var i = 0; i < errorPhoneList.length; i++)
        {
            if (s)
                s += ",";
            s += errorPhoneList[i];
        }

        Cyan.message("以下电话号码不正确\n" + s, function ()
        {
            receiverInput.focus();
        });
        return false;
    }
    else
    {
        return true;
    }
}