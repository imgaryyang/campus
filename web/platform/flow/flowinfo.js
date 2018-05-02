$.onload(function()
{
    Cyan.Class.overwrite(window, "publish", function(flowId)
    {
        var publish = this.inherited;

        var callback = function(cover)
        {
            publish(flowId, cover, {
                callback:function(version)
                {
                    $.message("发布成功,版本号为:" + version);
                }
            });
        };

        getLastFlow(flowId, function(flow)
        {
            if (flow)
            {
                if (flow.used)
                {
                    Cyan.customConfirm([
                        {
                            text:"创建新版本",
                            callback:function()
                            {
                                callback(false);
                            }
                        },
                        {
                            text:"覆盖原来的版本",
                            callback:function()
                            {
                                callback(true);
                            }
                        },
                        {
                            text:"取消"
                        }
                    ],
                            {
                                message:"流程的最后一个版本\"版本" + flow.version + "\"已经被使用，是否覆盖原来的版本?",
                                width:300
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

