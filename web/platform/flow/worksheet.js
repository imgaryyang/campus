var receivedText = "收到的文件";
var waitforText = "需等待文件";

if (window.mainBody)
{
    Cyan.overwrite(mainBody, "getRenderer", function (columnIndex)
    {
        return columnIndex == "remark" ? remarkRenderer : this.inherited(columnIndex);
    });
}

function remarkRenderer(remark)
{
    var i;

    var html = "<div class='item_remark'>";

    html += "<div class='list'>";
    html += "<div class='label'>" + receivedText + "：</div>";
    html += "<div class='items'>";

    for (i = 0; i < remark.subItems.length; i++)
    {
        var subItem = remark.subItems[i];
        html += "<div class='item'>";
        html += "<div class='sourceName'>" + subItem.sourceName + "</div>";
        html += "<div class='time'>" + Date.format(subItem.receiveTime, "yyyy-MM-dd HH:mm") + "</div>";
        html += "</div>";
    }

    html += "</div>";
    html += "</div>";

    if (remark.waitForItems && remark.waitForItems.length)
    {
        html += "<div class='list'>";
        html += "<div class='label'>" + waitforText + "：</div>";
        html += "<div class='items'>";

        for (i = 0; i < remark.waitForItems.length; i++)
        {
            var waitForItem = remark.waitForItems[i];
            html += "<div class='item'>";
            html += "<div class='waitName'>" + waitForItem.receiverList.join(" ") + "</div>";
            html += "</div>";
        }

        html += "</div>";
        html += "</div>";
    }

    html += "</div>";

    return html;
}

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "accept", function (stepId)
    {
        this.inherited(stepId, function (ret)
        {
            if (ret)
                refresh();
        });
    });

    Cyan.Class.overwrite(window, "cancelAccept", function (stepId)
    {
        this.inherited(stepId, function ()
        {
            refresh();
        });
    });

    Cyan.Class.overwrite(window, "catalog", function (catalogId)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要收藏的记录");
            return;
        }

        this.inherited(catalogId, function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "cancelCatalog", function ()
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要取消收藏的文件");
            return;
        }

        this.inherited(function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "consign", function (consignee)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要委托的事项");
            return;
        }

        this.inherited(consignee, function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "end", function ()
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要阅读的记录");
            return;
        }

        this.inherited(function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "hide", function ()
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要隐藏的记录");
            return;
        }

        this.inherited(function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "unHide", function ()
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要显示的记录");
            return;
        }

        this.inherited(function ()
        {
            Cyan.message("操作成功", function ()
            {
                refresh();
            });
        });
    });

    Cyan.Class.overwrite(window, "stop", function (stepId)
    {
        var stop = this.inherited;
        Cyan.confirm("此操作将不能恢复,确定要办结此事项?", function (ret)
        {
            if (ret == "ok")
            {
                stop(stepId, {
                    callback: function (ret)
                    {
                        if (ret)
                        {
                            Cyan.message(ret);
                        }
                        else
                        {
                            Cyan.message("操作成功", function ()
                            {
                                refresh();
                            });
                        }
                    },
                    wait: true
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "stopAll", function (consignee)
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要办结的事项");
            return;
        }

        var stopAll = this.inherited;
        Cyan.confirm("确定要办结选择的事项，此操作将不能恢复?", function (ret)
        {
            if (ret == "ok")
            {
                stopAll({
                    callback: function ()
                    {
                        Cyan.message("操作成功", function ()
                        {
                            refresh();
                        });
                    },
                    form: Cyan.$$("form")[0]
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "cancelSend", function (stepId)
    {
        this.inherited(stepId, {
            callback: function (ret)
            {
                if (Cyan.isBoolean(ret) || ret == null)
                {
                    if (ret || ret == null)
                    {
                        Cyan.message("操作成功", function ()
                        {
                            refresh();
                        });
                    }
                    else
                    {
                        Cyan.message("文件已被下环节接收，不能撤回");
                    }
                }
                else
                {
                    Cyan.message(ret);
                }
            },
            wait: true
        });
    });
});

function track(stepId)
{
    System.openPage("/flow/track/step/" + stepId);
}

window.canMoveTo = function (stepIds, groupId)
{
    return groupId != "0";
};
