System.isMobilePhone = function(s)
{
    return  /^1[3|4|5|8][0-9]\d{4,8}$/.exec(s);
};

var receiverInput;
Cyan.onload(function()
{

    var receiver = Cyan.$("entity.receiver") || Cyan.$("receiver");
    if (receiver)
    {
        receiverInput = new System.ReceiverInput("phone");
        receiverInput.isValid = System.isMobilePhone;
        receiverInput.create(receiver);
    }

    window.add = function(forward)
    {
        add0(forward, {
            target:"_page"
        });
    };

    window.show = function(key, forward)
    {
        show0(key, forward, {
            target:"_page"
        });
    };

    Cyan.Class.overwrite(window, "reset", function()
    {
        this.inherited();
        if (receiverInput)
            receiverInput.clear();
        $$(document.getElementsByName("entity.options")).setValue("");
    });

    Cyan.Class.overwrite(window, "send", function()
    {
        if (!beforeSave())
            return;

        this.inherited(function()
        {
            Cyan.message("发送成功", function()
            {
                System.closePage();
            })
        });
    });

    if (window.chart)
    {
        chart.getToolTip = chart.getLabelToolTip = function(series, key, name, value)
        {
            var total = 0;
            for (var i = 0; i < chart.data.length; i++)
                total += chart.data[i].values[0];

            return name + "：" + value + "票,占" + ((value / total) * 100).toFixed(2) + "%";
        }
    }
});

function updateSuccess()
{
    Cyan.message(System.Crud.messages.updateSuccess, function()
    {
        System.closePage();
    });
}

function beforeSave()
{
    if (receiverInput)
    {
        if ((Cyan.$("entity.receiver") || Cyan.$("receiver")).value == 0)
        {
            Cyan.message("请选填写收者");
            receiverInput.focus();
            return false;
        }

        return checkReceiverInput(receiverInput);
    }

    return true;
}

function checkReceiverInput(receiverInput)
{
    var s = "";
    var errorPhoneList = receiverInput.getErrorReceiverList();
    if (errorPhoneList && errorPhoneList.length)
    {
        for (var i = 0; i < errorPhoneList.length; i++)
        {
            if (s)
                s += ",";
            s += errorPhoneList[i];
        }

        Cyan.message("以下电话号码不正确<br>" + s, function()
        {
            receiverInput.focus();
        });
        return false;
    }
    else
    {
        return true;
    }
}

function openReceiverDialog()
{
    receiverInput.openSelectDialog("手机");
}

function upOption(obj)
{
    var tr = obj.parentNode.parentNode;
    var previous = tr.previousSibling;
    while (previous && previous.nodeName != "TR")
        previous = previous.previousSibling;
    if (previous)
        tr.parentNode.insertBefore(tr, previous);
}

function downOption(obj)
{
    var tr = obj.parentNode.parentNode;
    var next = tr.nextSibling;
    while (next && next.nodeName != "TR")
        next = next.nextSibling;
    if (next)
        tr.parentNode.insertBefore(next, tr);
}

function deleteOption(obj)
{
    var tr = obj.parentNode.parentNode;
    if (tr.rowIndex == 1)
    {
        var next = tr.nextSibling;
        while (next && next.nodeName != "TR")
            next = next.nextSibling;
        if (!next)
        {
            Cyan.message("至少需要一个选项");
            return;
        }
    }
    tr.parentNode.removeChild(tr);
}

function addOption()
{
    var trs = Cyan.$$(".sublist tr");
    var tr = trs[trs.length - 1];

    var newTr = document.createElement("tr");
    tr.parentNode.appendChild(newTr);

    for (var i = 0; i < tr.childNodes.length; i++)
    {
        var td = tr.childNodes[i];
        if (td.nodeName == "TD")
        {
            var newTd = document.createElement("td");
            newTd.innerHTML = td.innerHTML;
            newTd.className = td.className;
            newTr.appendChild(newTd);

            var span = td.childNodes[0];
            if (span && span.nodeName == "SPAN" && span.onclick)
            {
                if (!newTd.childNodes[0].onclick)
                    newTd.childNodes[0].onclick = span.onclick;
            }
        }
    }
    Cyan.Validator.validate(newTr, 2);


    var contents = document.getElementsByName("entity.options");
    contents[contents.length - 1].value = "";
}

function showReplys(voteId)
{
    System.openPage("/message/sms/vote/reply/list?voteId=" + voteId);
}

function showStat(voteId)
{
    System.openPage("/message/sms/vote/reply/stat?voteId=" + voteId);
}