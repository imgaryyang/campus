$.onload(function()
{
    if (window.clearMails)
    {
        Cyan.Class.overwrite(window, "clearMails", function(catalogId)
        {
            var inherited = this.inherited;
            $.confirm(catalogId == -4 ? "确定彻底删除邮件?邮件将不能恢复" : "确定删除该目录的邮件?", function (ret)
            {
                if (ret == 'ok')
                {
                    inherited(catalogId, {
                        callback:function(msg)
                        {
                            $.message("操作成功", function()
                            {
                                refresh();
                            });
                        },
                        wait:true
                    });
                }
            });
        });
    }
});

function openCatalog(catalogId)
{
    var url = "/oa/mail/list";
    if (catalogId < 0)
    {
        if (catalogId == -1)
            url += "?type=received";
        else if (catalogId == -2)
            url += "?type=draft";
        else if (catalogId == -3)
            url += "?type=sended";
        else if (catalogId == -4)
            url += "?deleted=true";

        System.getMenuByUrl(url).go();
    }
    else
    {
        System.getMenuByUrl(url).goWith({catalogId:catalogId}, catalogId);
    }
}