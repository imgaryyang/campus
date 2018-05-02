function ok()
{
    if (!Cyan.$$("#keys").checkeds().length)
    {
        Cyan.message("请选择意见");
        return;
    }

    select(function ()
    {
        Cyan.Window.closeWindow(true);
    });
}