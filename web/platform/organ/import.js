Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "imp", function ()
    {
        this.inherited({
            progress:true,
            callback:function ()
            {
                Cyan.message("导入成功");
            }
        })
    });
});

function cancel()
{
    System.closePage();
}