function more(typeId)
{
    var url = "/oa/notice/read";
    var url0 = url;
    if (Cyan.Arachne.form.typeId0)
        url += "?typeId0=" + Cyan.Arachne.form.typeId0;
    else if (Cyan.Arachne.form.sortId)
        url += "?sortId=" + Cyan.Arachne.form.sortId;


    var menu = System.getMenuByUrl(url);
    if (!menu)
        menu = System.getMenuByUrl(url0);

    if (typeId)
    {
        menu.goWith({typeId: typeId}, typeId);
    }
    else
    {
        if (menu.getPage())
            menu.goWith({typeId: 0}, 0);
        else
            menu.go();
    }
}

function showNotice(noticeId)
{
    window.open(System.formatUrl("/oa/notice/read/" + noticeId));
}