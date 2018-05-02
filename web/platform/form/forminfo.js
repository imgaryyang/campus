$.onload(function ()
{
    Cyan.Class.overwrite(window, "publish", function (formId)
    {
        var publish = this.inherited;

        var callback = function (cover)
        {
            publish(formId, cover, {
                callback: function (version)
                {
                    $.message("发布成功,版本号为:" + version);
                }
            });
        };

        getLastForm(formId, function (form)
        {
            if (form)
            {
                if (form.used)
                {
                    Cyan.customConfirm([
                                {
                                    text: "创建新版本",
                                    callback: function ()
                                    {
                                        callback(false);
                                    }
                                },
                                {
                                    text: "覆盖原来的版本",
                                    callback: function ()
                                    {
                                        callback(true);
                                    }
                                },
                                {
                                    text: "取消"
                                }
                            ],
                            {
                                message: "表单的最后一个版本\"版本" + form.version + "\"已经被使用，是否覆盖原来的版本?",
                                width: 300
                            });
                }
                else
                {
                    callback(true);
                }
            }
            else
            {
                callback(false);
            }
        });
    });
});

function editPages(formId)
{
    System.openPage("/old/form/page?formId=" + formId);
}

function showRoles(formId)
{
    System.openPage("/forminfo_role?formId=" + formId);
}

function showComponents(formId)
{
    System.openPage("/forminfo_component?formId=" + formId);
}