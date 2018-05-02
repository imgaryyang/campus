var mainList = new System.SimpleList("list");

System.reload = function (callback)
{
    mainList.reload(callback);
};

function loadList(pageNo, callback)
{
    Cyan.Arachne.form.pageNo = pageNo;
    mainList.reload(callback);
}

Cyan.onload(function ()
{
    mainList.load = function (callback)
    {
        loadPage(Cyan.Arachne.form.pageNo, function (result)
        {
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

            callback(result.records);
            retsetCount(result.totalCount);
        });
    };

    mainList.reload();
});

function more()
{
    System.more();
}

function retsetCount(count)
{
    var iframe = window.frameElement;
    if (iframe && window.parent.setCount)
    {
        window.parent.setCount(iframe, count);
    }
}