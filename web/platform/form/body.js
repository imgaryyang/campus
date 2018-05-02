$.onload(function ()
{
    Cyan.Class.overwrite(window, "save", function ()
    {
        this.inherited({
            callback:function ()
            {
                $.message("保存成功", function ()
                {
                    System.closePage();
                });
            },
            progress:true
        });
    });
});