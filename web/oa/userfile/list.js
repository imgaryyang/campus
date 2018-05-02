Cyan.onload(function ()
{
    addMore();
});

function more()
{
    var url = "/oa/userfile/share";
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
    {
        System.openPage(url);
    }
}