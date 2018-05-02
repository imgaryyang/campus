$.onload(function()
{
    Cyan.Class.overwrite(window, "reSend", function()
    {
        this.inherited(function()
        {
            $.message("操作成功");
        });
    });
});