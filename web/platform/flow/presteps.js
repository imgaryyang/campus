function ok()
{
    var preStepIds = Cyan.$$("#stepIds").checkedValues();
    if (!preStepIds.length)
    {
        Cyan.message("请至少选择一个人");
        return;
    }

    Cyan.Window.closeWindow(preStepIds);
}

function cancel()
{
    Cyan.Window.closeWindow(null);
}