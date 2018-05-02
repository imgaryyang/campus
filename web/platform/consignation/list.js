Cyan.onload(function ()
{
    addMore();
});

function more()
{
    var url = "/Consignation?type=1&rejectable=true";
    var menu = System.getMenuByUrl(url);
    if (menu)
    {
        menu.go();
    }
    else
    {
        url = "/Consignation?type=1";
        menu = System.getMenuByUrl(url);
        if (menu)
            menu.go();
        else
            System.openPage(url);
    }
}