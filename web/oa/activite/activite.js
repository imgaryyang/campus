$.onload(function ()
{
    var save0 = window.save;
    window.save = function ()
    {
        save0(function ()
        {
            $.message("保存成功", function ()
            {
                System.getMenuByUrl("/oa/activite/crud").go();
                System.closePage();
            });
        });
    };
    setTimeout(function ()
    {
        window.show = function (key, forward)
        {
            System.openPage("/oa/activite/crud/" + key);
        };
    }, 100);
    Cyan.Class.overwrite(window, "issue", function ()
    {
        var issue = this.inherited;
        save0(function (activiteId)
        {
            issue(activiteId || Cyan.Arachne.form.entity.activiteId, function ()
            {
                $.message("发布成功", function ()
                {
                    System.getMenuByUrl("/oa/activite/crud").go();
                    System.closePage();
                });
            });
        });
    });
    Cyan.Class.overwrite(window, "end", function ()
    {
        var end = this.inherited;
        save0(function (activiteId)
        {
            end(activiteId || Cyan.Arachne.form.entity.activiteId, function ()
            {
                $.message("操作成功", function ()
                {
                    System.getMenuByUrl("/oa/activite/crud").go();
                    System.closePage();
                });
            });
        });
    });

    Cyan.Class.overwrite(window, "cancel", function ()
    {
        var cancel = this.inherited;
        save0(function (activiteId)
        {
            cancel(activiteId || Cyan.Arachne.form.entity.activiteId, function ()
            {
                $.message("操作成功", function ()
                {
                    System.getMenuByUrl("/oa/activite/crud").go();
                    System.closePage();
                });
            });
        });
    });
});

function showMembers(activiteId)
{
    System.openPage("/oa/activite/member?activiteId=" + activiteId);
}

function endPtl1(activiteId)
{
    System.openPage("/oa/activite/activieclose/" + activiteId);
}


function end1s()
{
    end1(
            function (ret)
            {
                if (ret == "ok")
                {
                    $.message("操作成功.");
                    System.getMenuByUrl("/oa/activite/crud").go();
                    System.closePage();
                }
                else
                {
                    $.message("操作失败.");
                    Cyan.Window.closeWindow(false);
                }
            }
    );
}




