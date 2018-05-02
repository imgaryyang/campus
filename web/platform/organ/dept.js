$.onload(function ()
{
    Cyan.Class.overwrite(window, "initTree", function ()
    {
        this.inherited(function ()
        {
            Cyan.message("初始化成功");
        });
    });
});