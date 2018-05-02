Cyan.importJs("/platform/opinion/opinion.js");

Cyan.onload(function ()
{
    window.saveData = window.save;
    window.save = function ()
    {
        if (!checkData())
        {
            return;
        }

        saveData({
            callback:function ()
            {
                Cyan.message("保存成功", function ()
                {
                    System.closePage();
                });
            },
            progress:true
        });
    };

    Cyan.Class.overwrite(window, "setState", function (state)
    {
        var setState = this.inherited;

        var f = function ()
        {
            if (state == "NOPASSED")
            {
                var s = "请输入审核不通过的原因";
                System.Opinion.title = s;
                System.Opinion.remark = s;

                System.Opinion.edit_simple("", function (text)
                {
                    if (text)
                    {
                        setState(state, text, {
                            callback:function ()
                            {
                                Cyan.message("操作成功", System.closePage);
                            },
                            wait:true
                        });
                    }
                });
            }
            else
            {
                setState(state, null, {
                    callback:function ()
                    {
                        Cyan.message("操作成功", System.closePage);
                    },
                    wait:true
                });
            }
        };

        if (Cyan.Arachne.form.audit)
        {
            f();
        }
        else
        {
            if (!checkData())
                return;

            saveData({
                callback:f,
                progress:true
            })
        }
    });
});

function printForm()
{
    saveData({
        callback:function ()
        {
            window.open("/oa/formsurvey/body/" + Cyan.Arachne.form.recordId + "/print/show");
        },
        progress:true
    });
}