Cyan.onload(function ()
{
    hideAction("restore");

    resize();

    window.office = new System.Office("content");
    office.render(Cyan.$("word"), function ()
    {
        office.setUserName(System.userName);
        office.setEditType(1);
        openFile(function ()
        {
            if (office.isShowRevisions())
            {
                showAction("hideRevisions");
                hideAction("showRevisions");
            }
            else
            {
                showAction("showRevisions");
                hideAction("hideRevisions");
            }
        });
    });

    Cyan.Class.overwrite(office, "onunload", function ()
    {
        try
        {
            if (!office.isSaved())
            {
                if (confirm("公文未保存，是否保存?"))
                    save();
            }
        }
        catch (e)
        {
        }
        try
        {
            releaseText();
        }
        catch (e)
        {
        }
        return this.inherited();
    });

    Cyan.attach(window, "resize", function ()
    {
        window.setTimeout(resize, 100);
    });

    Cyan.Class.overwrite(window, "save", function (callback)
    {
        this.inherited({
            target: office,
            callback: callback,
            wait: false
        });
    });
});

function resize()
{
    var bodyHeight = document.documentElement.clientHeight;
    if (window.bodyHeight == bodyHeight)
        return;
    window.bodyHeight = bodyHeight;

    var buttonsHeight = Cyan.Elements.getComponentSize(Cyan.$("buttons")).height;

    Cyan.$("word").style.height = (bodyHeight - buttonsHeight) + "px";
    if (window.office && window.office.resize)
        window.office.resize();
}

function openFile(callback)
{
    var form = Cyan.Arachne.form;
    office.openFile("/attachment/" + form.encodedId + "/" + form.attachmentNo, form.fileType, callback);
}

function saveText()
{
    window.save(function ()
    {
        alert("保存成功");
    });
}

function acceptAllRevisions()
{
    office.acceptAllRevisions();
    alert("文件已去除痕迹，请保存");
}

function printWord()
{
    office.print();
}

function saveAs()
{
    var form = Cyan.Arachne.form;
    office.saveAs(Cyan.Arachne.form.fileName);
}

function showHistory()
{
    var form = Cyan.Arachne.form;
    System.showModal("/attachment/" + form.encodedId + "/" + form.attachmentNo + "/baks", function (bakId)
    {
        if (bakId)
        {
            window.setTimeout(function ()
            {
                office.openFile("/attachment/" + form.encodedId + "/" + form.attachmentNo + "/bak/" + bakId);
                office.setSaved(false);
            }, 1);
        }
    });
}

function showAction(actionId)
{
    var action = Cyan.$(actionId);
    if (action)
    {
        if (action.nextSibling && action.nextSibling.nodeName == "#text")
            action.nextSibling.nodeValue = " ";
        action.style.display = "";
    }
}

function hideAction(actionId)
{
    var action = Cyan.$(actionId);
    if (action)
    {
        if (action.nextSibling && action.nextSibling.nodeName == "#text")
            action.nextSibling.nodeValue = "";
        action.style.display = "none";
    }
}

function showRevisions()
{
    office.showRevisions();
    hideAction("showRevisions");
    showAction("hideRevisions");
}

function hideRevisions()
{
    office.hideRevisions();
    hideAction("hideRevisions");
    showAction("showRevisions");
}

function maximize()
{
    System.maximize();
    hideAction("maximize");
    showAction("restore");

    window.setTimeout(resize, 100);
}

function restore()
{
    if (System.isMaximized())
    {
        System.restore();
        hideAction("restore");
        showAction("maximize");
        showAction("exit");
        window.setTimeout(resize, 100);
    }
}

function exit()
{
    restore();
    System.closePage();
}

Cyan.Arachne.errorHandler = function (error)
{
    if (error)
        alert(error.message);
};