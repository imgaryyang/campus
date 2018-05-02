Cyan.onload(function ()
{
    deptSelector.from.enableSearch();
    var saveDepts = window.saveDepts;
    window.saveDepts = function ()
    {
        var deptIds = deptSelector.selectedValues();
        if (!deptIds.length)
        {
            Cyan.message("请至少选择一个部门");
            return;
        }
        saveDepts(deptIds, function (ret)
        {
            Cyan.message("保存成功", function ()
            {
                if (ret)
                    Cyan.Window.getOpener().refresh();
                closeWindow();
            });
        });
    };
});