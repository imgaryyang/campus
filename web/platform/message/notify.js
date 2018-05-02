function send()
{
    sendNotify({
        callback: function ()
        {
            Cyan.message("发送系统广播成功", function ()
            {
                System.goMenu(null, "/message/notify/query");
                System.closePage();
            });
        },
        error: function ()
        {
            Cyan.message("发送系统广播失败");
        },
        wait: true
    });
}
