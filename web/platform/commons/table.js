System.reload = function (callback)
{
    loadPage(Cyan.Arachne.form.pageNo, function (result)
    {
        var tbody = Cyan.$$("#table tbody");
        tbody.$("tr").remove();
        tbody = tbody.first;

        var ths = Cyan.$$("#table th");

        result.records.each(function ()
        {
            var tr = document.createElement("tr");

            tbody.appendChild(tr);

            this.cells.each(function (index)
            {
                var td = document.createElement("td");
                tr.appendChild(td);
                td.innerHTML = this;
                td.style.textAlign = ths[index].style.textAlign;
            });
        });

        var pageDiv = Cyan.$("page");

        Cyan.$$("#page span").remove();

        var pageCount = result.pageCount;
        var pageNo = result.pageNo;

        if (pageCount > 10)
            pageCount = 10;

        if (pageCount > 1)
        {
            for (var i = 1; i <= pageCount; i++)
            {
                var span = document.createElement("span");

                if (i == pageNo)
                {
                    span.innerHTML = "[" + i + "]";
                    span.className = "selected";
                }
                else
                {
                    span.innerHTML = "[<a href='#' onclick='loadList(" + i + ");return false;'>" + i + "</a>]";
                }

                pageDiv.appendChild(span);
            }
        }

        if (callback)
            callback();

        retsetCount(result.totalCount);
    });
};

Cyan.onload(function ()
{
    System.reload();
});

function addAction(text, action)
{
    var span = document.createElement("span");
    span.innerHTML = "<a href=\"#\" onclick=\"return false;\">" + text + "</a>";
    span.onclick = action;

    Cyan.$("actions").appendChild(span);
}

function addMore()
{
    addAction("更多...", window.more);
}

function loadList(pageNo)
{
    Cyan.Arachne.form.pageNo = pageNo;
    System.reload();
}

function retsetCount(count)
{
    var iframe = window.frameElement;
    if (iframe && window.parent.setCount)
    {
        window.parent.setCount(iframe, count);
    }
}