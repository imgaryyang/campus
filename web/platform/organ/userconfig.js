Cyan.onload(function ()
{
    var batchSave = window.batchSave;
    window.batchSave = function ()
    {
        if (window.beforeSave)
        {
            if (!beforeSave())
                return;
        }
        batchSave(updateSuccess);
    };

    var batchShow = window.batchShow0 = window.batchShow;
    window.batchShow = function (forward)
    {
        if (!$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要修改的用户");
            return;
        }

        batchShow(forward, {
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                    refresh();
            }
        });
    };
});