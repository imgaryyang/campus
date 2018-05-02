Cyan.importJs("/platform/opinion/opinion.js");

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        window.add = function ()
        {
            System.openPage("/oa/formsurvey/newrecord?surveyId=" + Cyan.Arachne.form.surveyId + "&deptId=" +
                    Cyan.Arachne.form.deptId);
        };

        window.show = function (key)
        {
            System.openPage("/oa/formsurvey/body/" + key + "?audit=" + Cyan.Arachne.form.audit);
        };
    }, 100);

    Cyan.Class.overwrite(window, "setStates", function (state)
    {
        var s;

        if (state == "SUBMITED")
        {
            if (Cyan.Arachne.form.audit)
                s = "取消审核";
            else
                s = "提交";
        }
        else if (state == "PASSED")
        {
            s = "审核";
        }

        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要" + s + "的记录");
            return;
        }

        var setStates = this.inherited;

        Cyan.confirm("确定要" + s + "选择的记录", function (ret)
        {
            if (ret == "ok")
            {
                setStates(state, refresh);
            }
        });
    });

    Cyan.Class.overwrite(window, "setState", function (recordId, state)
    {
        var setState = this.inherited;

        if (state == "NOPASSED")
        {
            var s = "请输入审核不通过的原因";
            System.Opinion.title = s;
            System.Opinion.remark = s;

            System.Opinion.edit_simple("", function (text)
            {
                if (text)
                {
                    setState(recordId, state, text, refresh);
                }
            });
        }
        else
        {
            setState(recordId, state, null, refresh);
        }
    });

    Cyan.Class.overwrite(window, "modifySurvey", function (surveyId)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要修改类型的记录");
            return;
        }

        var modifySurvey = this.inherited;

        Cyan.confirm("确定要修改选择的记录的类型", function (ret)
        {
            if (ret == "ok")
            {
                modifySurvey(surveyId, {
                    form: Cyan.$$("form")[0],
                    callback: refresh
                });
            }
        });
    });
});