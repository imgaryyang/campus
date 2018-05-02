Cyan.importJs("/platform/weboffice/office.js");

Cyan.onload(function ()
{
    if (window.Ext)
        Ext.form.Field.prototype.msgTarget = "under";

    var save0 = window.save;
    Cyan.Class.overwrite(window, "save", function (commit)
    {
        this.inherited(commit, {
            callback: function (result)
            {
                if (Cyan.isString(result))
                {
                    Cyan.message(result);
                }
                else
                {
                    Cyan.Arachne.form.recordId = result;
                    Cyan.message(commit ? "提交成功" : "保存成功，如果确认已经填写完毕，请按提交按钮", function ()
                    {
                        System.closePage();
                    });
                }
            }
        });
    });

    Cyan.$$("input:radio").each(function ()
    {
        Cyan.attach(this, "click", radioClick)
    });

    Cyan.$$("input:checkbox").each(function ()
    {
        if (this.value == "-1")
            Cyan.attach(this, "click", checkboxClick)
    });

    if (Cyan.Arachne.form.costTime != null && Cyan.Arachne.form.costTime < Cyan.Arachne.form.timeLimit)
    {
        var i;
        var f = function ()
        {
            updateCostTime(Cyan.Arachne.form.recordId, Cyan.Arachne.form.costTime, function ()
            {
                if (Cyan.Arachne.form.costTime >= Cyan.Arachne.form.timeLimit)
                {
                    if (i)
                        window.clearInterval(i);
                    save0(false, function ()
                    {
                        Cyan.message("时间已经结束，请按提交按钮提交结果!", function ()
                        {
                            location.reload();
                        });
                    });
                }
            });
        };

        var setCostTime = function ()
        {
            if (Cyan.$("costTime"))
            {
                var costTime = Cyan.Arachne.form.costTime;
                var s = "";
                if (costTime >= 60)
                    s += Math.floor(costTime / 60) + "分";

                var seconds = costTime % 60;
                if (seconds > 0)
                    s += seconds + "秒";

                Cyan.$("costTime").innerHTML = s;
            }
        };

        setCostTime();

        i = setInterval(function ()
        {
            Cyan.Arachne.form.costTime += 1;

            setCostTime();

            if (Cyan.Arachne.form.recordId)
            {
                f();
            }
            else
            {
                save0(function (result)
                {
                    if (Cyan.isNumber(result))
                    {
                        Cyan.Arachne.form.recordId = result;
                        f();
                    }
                });
            }
        }, 1000);
    }
});

function radioClick()
{
    showInput(this.name.substring(9), Cyan.$$(document.getElementsByName(this.name)).checkedValues()[0] == "-1");
}

function checkboxClick()
{
    showInput(this.name.substring(9), this.checked);
}

function showInput(problemId, display)
{
    var input = Cyan.$("inputs.p" + problemId);
    if (input)
    {
        input.style.display = display ? "" : "none";
        if (!display)
            input.value = "";
    }
}

function resetVote()
{
    var form = Cyan.$$("form")[0];
    for (var i = 0; i < form.length; i++)
    {
        var component = form[i];
        if (component.nodeName == "INPUT")
        {
            if (component.type == "radio")
            {
                component.checked = false;
                if (component.value == "-1")
                    showInput(component.name.substring(9), false);
            }
            else if (component.type == "checkbox")
            {
                component.checked = false;
                if (component.value == "-1")
                    showInput(component.name.substring(9), false);
            }
            else if (component.type == "text")
            {
                component.value = "";
            }
        }
        else if (component.nodeName == "TEXTAREA")
        {
            component.value = "";
        }
    }
}

function exportVote()
{
    var url;
    if (Cyan.Arachne.form.recordId)
    {
        url = "/oa/vote/record/" + Cyan.Arachne.form.recordId + "/show?export=true";
    }
    else
    {
        url = "/oa/vote/VotePage?voteId=" + Cyan.Arachne.form.voteId + "&export=true";
    }

    window.open(System.formatUrl(url));
}

function printVote()
{
    var url;
    if (Cyan.Arachne.form.recordId)
    {
        url = "/oa/vote/record/" + Cyan.Arachne.form.recordId + "/show?export=true";
    }
    else
    {
        url = "/oa/vote/VotePage?voteId=" + Cyan.Arachne.form.voteId + "&export=true";
    }

    System.Office.print(System.formatUrl(url), "doc");
}


function showRecords()
{
    System.openPage("/oa/vote/VoteRecord?voteId=" + Cyan.Arachne.form.voteId + "&self=true");
}

function reVote()
{
    location.href = "/oa/vote/VotePage?voteId=" + Cyan.Arachne.form.voteId + "&add=true";
}