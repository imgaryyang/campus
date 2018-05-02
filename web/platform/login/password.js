Cyan.onload(function ()
{
    if (window.Ext)
    {
        Ext.form.Field.prototype.msgTarget = "under";
    }

    Cyan.Class.overwrite(window, "save", function ()
    {
        this.inherited(function ()
        {
            Cyan.message("修改成功", function ()
            {
                System.closePage();
            });
        });
    });
});