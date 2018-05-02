var items = [];

mainList.render = function (item)
{


    var html = "<div";
    if (item.item.style)
        html += " style='" + item.item.style + "'";
    html += ">";

    var href = "<a href='#' onclick='goItem(" + items.length + ");return false;'>";

    html += "<span class='title'>" + href + item.item.title + "</a></span>";
    html += "<span class='count'>" + href + item.count + "</span>";

    html += "</div>";

    items.push(item.item);

    return html;
};

function goItem(index)
{
    var item = items[index];

    var menu;
    if (item.menuId)
    {
        menu = System.getMenu(item.menuId);
    }

    if (!menu)
    {
        menu = System.getMenuByUrl(item.url);
    }

    if (menu)
    {
        menu.goWith(item.args);
    }
    else
    {
        var url = item.url;
        if (url && url.indexOf("javascript:") == 0)
        {
            System.getMainWindow().eval(url.substring(11));
        }
        else if (url.indexOf("://") > 0)
        {
            window.open(url);
        }
        else
        {
            System.openPage(sl, null, item.title);
        }
    }
}