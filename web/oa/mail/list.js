Cyan.onload(function ()
{
    var labels = [
        {title: "收件箱", action: receivedBox},
        {title: "新邮件<font id='newCount' color='red'></font>", action: showNew},
        {title: "草稿箱", action: draftBox},
        {title: "发件箱", action: sendedBox}
    ];

    var labelsDiv = document.createElement("div");
    labelsDiv.id = "labels";
    for (var i = 0; i < labels.length; i++)
    {
        var label = labels[i];

        var span = document.createElement("span");
        span.innerHTML = label.title;
        span.onclick = label.action;
        span.className = i ? "label" : "label_selected";

        labelsDiv.appendChild(span);
    }

    var topRightDiv = document.createElement("div");
    topRightDiv.id = "top_right";
    topRightDiv.innerHTML = "<span><a href='#' onclick='writeMail()'>写邮件</a></span>";

    Cyan.$("top").appendChild(labelsDiv);
    Cyan.$("top").appendChild(topRightDiv);

    refreshCount();
});

function showMail(mailId)
{
    System.openPage("/oa/mail/show/" + mailId);
    return false;
}

function writeMail()
{
    var menu = System.getMenuByUrl("/oa/mail/new");
    if (menu)
        menu.go();
}

function receivedBox()
{
    showBox("received", "", this);
}

function showNew()
{
    showBox("received", "false", this);
}

function draftBox()
{
    showBox("draft", "", this);
}

function sendedBox()
{
    showBox("sended", "", this);
}

function showBox(type, readed, label)
{
    if (selectLabel(label))
    {
        Cyan.Arachne.form.type = type;
        Cyan.Arachne.form.readed = readed;
        loadList(1);
    }
}

function selectLabel(label0)
{
    if (label0.className == "label_selected")
        return false;

    var labels = Cyan.$$("#labels span");

    for (var i = 0; i < labels.length; i++)
    {
        var label = labels[i];

        if (label == label0)
            label.className = "label_selected";
        else
            label.className = "label";
    }

    return true;
}

function more()
{
    var menu = null;
    if (Cyan.Arachne.form.type == "received")
    {
        menu = System.getMenuByUrl("/oa/mail/list?type=received");
        if (!menu)
            menu = System.getMenuByUrl("/oa/mail/list?type=received&showReply=false");
    }
    else
    {
        menu = System.getMenuByUrl("/oa/mail/list?type=" + Cyan.Arachne.form.type);
    }

    if (menu)
        menu.go();
}

System.reload = function (callback)
{
    mainList.reload(function ()
    {
        refreshCount(callback);
    });
};

function refreshCount(callback)
{
    var type0 = Cyan.Arachne.form.type;
    var readed0 = Cyan.Arachne.form.readed;

    Cyan.Arachne.form.type = "received";
    Cyan.Arachne.form.readed = "false";
    loadCount(function (count)
    {
        if (count)
            Cyan.$("newCount").innerHTML = "(" + count + ")";
        else
            Cyan.$("newCount").innerHTML = "";

        if (callback)
            callback();
    });
    Cyan.Arachne.form.type = type0;
    Cyan.Arachne.form.readed = readed0;
}