function ok()
{
    imp(function()
    {
        Cyan.message("导入成功", function()
        {
            closeWindow(true);
        });
    });
}