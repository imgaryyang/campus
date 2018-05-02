Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "sendMessage", function ()
    {
        this.inherited(function ()
        {
            Cyan.message("发送成功", function ()
            {
                closeWindow();
            });
        });
    });

    Cyan.Class.overwrite(window, "deleteStep", function (stepId)
    {
        var deleteStep = this.inherited;
        Cyan.confirm("此操作不能恢复,确定撤回此环节?", function (ret)
        {
            if (ret == "ok")
            {
                deleteStep(stepId, function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        Cyan.$$("#step" + stepId).remove();
                    });
                });
            }
        });
    });

    if (Cyan.$("printButton"))
        loadPrint();
});

function showMessage(groupId)
{
    message(groupId, {target: "_modal"})
}

function loadPrint()
{
    var frame = Cyan.$("printFrame");
    if (!frame)
    {
        frame = document.createElement("IFRAME");
        frame.style.width = "0";
        frame.style.height = "0";
        frame.style.borderWidth = "0";
        frame.id = "printFrame";

        document.body.appendChild(frame);
    }

    var url = window.location.pathname + "?print=true";
    url += "&random=" + Math.random().toString();

    frame.src = System.formatUrl(url);
}

function printTrack()
{
    var win = Cyan.$("printFrame").contentWindow;
    win.focus();
    win.print();
}