Cyan.importJs("/platform/group/member.js");

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "memberCheck", function (state)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择需要操作的人员");
            return;
        }

        this.inherited(state, {
            callback: function ()
            {
                Cyan.message("操作成功", function ()
                {
                    refresh();
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "addMembers", function ()
    {
        var addMembers = this.inherited;
        System.selectMembers({
            types: "user",
            app: "activite_members",
            deptId: Cyan.Arachne.form.activite.deptId,
            callback: function (result)
            {
                if (result)
                {
                    addMembers(Cyan.get(result, "id"), function ()
                    {
                        refresh();
                    });
                }
            }
        });
    });
});