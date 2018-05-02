$.onload(function ()
{
    setTimeout(function ()
    {
        window.show = function (key, forward)
        {
            show0(key, forward, {
                target:"_page"
            });
        };
    }, 100);

    Cyan.Class.overwrite(window, "apply", function (activiteId)
    {
        if (!activiteId)
            activiteId = Cyan.Arachne.form.entity.activiteId;

        var apply = this.inherited;
        $.confirm("确定申请参加此活动", function (ret)
        {
            if (ret == "ok")
            {
                apply(activiteId, function ()
                {
                    $.message("报名成功，请等待组织者通过您的报名", function ()
                    {
                        if (window.refresh)
                        {
                            refresh();
                        }
                        else
                        {
                            System.getMenuByUrl("/oa/activite/join").go();
                            System.closePage();
                        }
                    });
                });
            }
        });
    });
});