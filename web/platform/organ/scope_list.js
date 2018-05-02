Cyan.onload(function ()
{
    window.afterSave = function ()
    {
        mainBody.reload();
    };

    setTimeout(function ()
    {
        Cyan.Class.overwrite(window, "showSortList", function (forward)
        {
            if (forward == "parent")
            {
                var record = mainBody.getSelectedRow();
                var checked = false;
                if (record)
                {
                    if (record.hasChildren)
                    {
                        Cyan.Arachne.form.parentScopeId = record.key;
                        checked = true;
                    }
                }

                if (!checked)
                {
                    Cyan.message("请选择一个目录");
                    return;
                }
            }

            this.inherited(forward);
            Cyan.Arachne.form.parentScopeId = null;
        });
    }, 100);
});