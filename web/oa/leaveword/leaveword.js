$.onload(function ()
{
    Cyan.Class.overwrite(window, "commit", function (leaveWordId)
    {
        this.inherited(leaveWordId, function (ret)
        {
            if (ret)
                Cyan.message("操作成功", refresh);
            else
                Cyan.message("已经提交，不能再提交", refresh);
        });
    });

    Cyan.Class.overwrite(window, "withdraw", function (leaveWordId)
    {
        this.inherited(leaveWordId, function (ret)
        {
            if (ret)
                Cyan.message("操作成功", refresh);
            else
                Cyan.message("未提交或者已经被受理，不能撤回", refresh);
        });
    });
});

function accept(leaveWordId)
{
    System.openPage("/oa/leaveword/flow/start?leaveWordId=" + leaveWordId);
}