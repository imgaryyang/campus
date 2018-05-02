Cyan.importJs("/platform/group/member.js");
Cyan.importJs("/platform/flow/flowtags.js");

Cyan.onload(function ()
{
    var callback = function ()
    {
        Cyan.message("操作成功", reload);
    };

    Cyan.Class.overwrite(window, "back", function (stepId)
    {
        var back = this.inherited;
        Cyan.confirm("此操作不能恢复,确定要打回到上一环节?", function (ret)
        {
            if (ret == "ok")
            {
                back(stepId, callback);
            }
        });
    });

    Cyan.Class.overwrite(window, "cancelSend", function (stepId)
    {
        var cancelSend = this.inherited;
        Cyan.confirm("此操作不能恢复,确定将此环节撤销回待办状态?", function (ret)
        {
            if (ret == "ok")
            {
                cancelSend(stepId, callback);
            }
        });
    });

    Cyan.Class.overwrite(window, "deleteStep", function (stepId)
    {
        var deleteStep = this.inherited;
        Cyan.confirm("此操作不能恢复,确定删除此环节?", function (ret)
        {
            if (ret == "ok")
            {
                deleteStep(stepId, callback);
            }
        });
    });

    Cyan.Class.overwrite(window, "deleteStepGroup", function (groupId)
    {
        var deleteStepGroup = this.inherited;
        Cyan.confirm("此操作不能恢复,确定删除此环节?", function (ret)
        {
            if (ret == "ok")
            {
                deleteStepGroup(groupId, callback);
            }
        });
    });

    Cyan.Class.overwrite(window, "stopStep", function (stepId)
    {
        var stopStep = this.inherited;
        Cyan.confirm("此操作不能恢复,确定办结此环节?", function (ret)
        {
            if (ret == "ok")
            {
                stopStep(stepId, callback);
            }
        });
    });

    Cyan.Class.overwrite(window, "changeReceiver", function (stepId)
    {
        var changeReceiver = this.inherited;
        System.selectMembers({
            types: ["user"], callback: function (receivers)
            {
                if (receivers)
                {
                    if (!receivers.length)
                    {
                        Cyan.message("请选择一个接收者");
                    }
                    else if (receivers.length > 1)
                    {
                        Cyan.message("只能选择一个接收者");
                    }
                    else
                    {
                        var userId = receivers[0].id;
                        changeReceiver(stepId, userId, callback);
                    }
                }
            }
        });
    });

    Cyan.Class.overwrite(window, "refreshSteps", function ()
    {
        this.inherited(callback);
    });

    Cyan.Class.overwrite(window, "restore", function ()
    {
        var restore = this.inherited;
        Cyan.confirm("此操作不能恢复,确定恢复流程?", function (ret)
        {
            if (ret == "ok")
            {
                restore(function ()
                {
                    refreshSteps();
                });
            }
        });
    });
});

function reload()
{
    Cyan.Arachne.redirect(location.href, "main");
}

function openStep(stepId)
{
    System.openPage(getUrl(Cyan.Arachne.form.flowTag, stepId, false));
}