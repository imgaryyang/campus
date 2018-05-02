function ok()
{
    var values = Cyan.$$("#keys").checkedValues();

    if (!values.length)
    {
        Cyan.message("请至少选择一个文件");
        return;
    }

    Cyan.Window.closeWindow(values);
}