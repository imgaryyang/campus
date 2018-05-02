$.onload(function ()
{
    Cyan.Class.overwrite(window, "initialize", function ()
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要初始化的流水号");
            return;
        }

        this.inherited(function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });
});