$.onload(function()
{
    if (window.send)
    {
        var send = window.send;
        window.send = function()
        {
            if ($$("#method").checkeds().length == 0)
            {
                $.message("至少选择一个消息发送方式");
                return;
            }
            send({
                callback:function()
                {
                    $.message("发送消息成功");
                },
                error:function()
                {
                    $.message("发送消息失败");
                },
                progress:true
            });
        };
    }
});

function openReceiverDialog(field)
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
    System.openPage(System.getMenuByUrl("/message/sms").formatUrl("/message/sms?smsId=" + smsId));
}