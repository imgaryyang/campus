Cyan.onload(function ()
{
    resize();

    window.office = new System.Office("text");

    office.render(Cyan.$("word"), function ()
    {
        office.setEditType(0);
        openFile();
    });

    Cyan.attach(window, "resize", resize);
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

function callback()
{
}

function openFile()
{
    var url = getUrl();
    if (url.indexOf("?") > 0)
        url += "&";
    else
        url += "?";

    url += "toDoc=true";
    office.openFile(url, "doc", callback);
}

function printWord()
{
    office.print();
}

function saveAs()
{
    office.saveAs(getFileName() + ".doc");
}

function getFileName()
{
    return "打印";
}

function exit()
{
    if (System.page && System.page.closePage)
        System.page.closePage();
    else
        window.close();
}

Cyan.Arachne.errorHandler = function (error)
{
    if (error)
        alert(error.message);
};