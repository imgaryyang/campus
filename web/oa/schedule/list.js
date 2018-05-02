Cyan.onload(function ()
{
    addMore();
});

function more()
{
    var url = "/oa/schedule/list";
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
    {
        System.openPage(url);
    }
}