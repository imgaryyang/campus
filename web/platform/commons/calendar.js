Cyan.importJs("widgets/tooltip.js");

var tip;
var currentItemId;
var currentDiv;
var noShowDiv = false;
var itemMap = {};
var DATEFORMAT;

mainBody.getRenderer = function (columnIndex)
{
    return Cyan.Arachne.form.type == "month" || columnIndex ? renderer : null;
};

Cyan.onload(function ()
{
    DATEFORMAT = Cyan.Arachne.form.type == "month" ? "yyyyMMdd" : "yyyyMMddHH";
    setTimeout(selDate, 500);
});


function renderer(calendarItem)
{
    if (calendarItem)
    {
        var html = "<div onmouseover='calendarOver(this)' class='calendar " + Cyan.Arachne.form.type;

        if (Cyan.Arachne.form.type == "month" && calendarItem.startTime.isToday())
            html += " today";

        html += "'";

        html += " id='" + getCalendarDivId(calendarItem.startTime) + "'";
        html += " startTime='" + Cyan.Date.format(calendarItem.startTime, DATEFORMAT) + "'";
        html += " endTime='" + Cyan.Date.format(calendarItem.endTime, DATEFORMAT) + "'";
        html += " onclick='calendarClick(this)' ondblclick='calendarDblClick(this)'";
        html += ">";

        if (calendarItem.name)
        {
            html += "<div class='title'>" + calendarItem.name + "</div>";
        }

        var items = calendarItem.items;
        if (items)
        {
            var n = items.length;
            for (var i = 0; i < n; i++)
            {
                var item = items[i];

                var itemId = getItemId(item);

                itemMap[itemId] = item;

                html += "<div class='item' id='" + itemId + "'>";

                html += createItemHtml(item);

                html += "</div>";
            }
        }

        html += "</div>";

        return html;
    }

    return null;
}

function getStartTime(div)
{
    return getDivTime(div, "startTime");
}

function getEndTime(div)
{
    return getDivTime(div, "endTime");
}

function getDivTime(div, name)
{
    var time = div[name];
    if (!time || Cyan.isString(time))
    {
        if (!time)
            time = div.getAttribute(name);
        div[name] = time = Cyan.Date.parse(time, DATEFORMAT);
    }
    return time;
}

function getCalendarDivId(time)
{
    return Cyan.Date.format(time, DATEFORMAT);
}

function next()
{
    var time = Cyan.Arachne.form.time;
    var type = Cyan.Arachne.form.type;
    if (type == "day" || type == "weekday")
        time.setDate(time.getDate() + 1);
    else if (type == "week")
        time.setDate(time.getDate() + 7);
    else
        time.setMonth(time.getMonth() + 1);

    Cyan.Arachne.form.date = time;

    reloadCalendars();
}

function previous()
{
    var time = Cyan.Arachne.form.time;
    var type = Cyan.Arachne.form.type;

    if (type == "day" || type == "weekday")
        time.setDate(time.getDate() - 1);
    else if (type == "week")
        time.setDate(time.getDate() - 7);
    else
        time.setMonth(time.getMonth() - 1);

    Cyan.Arachne.form.date = time;

    reloadCalendars();
}

function turn(time)
{
    time.setHours(0, 0, 0, 0);
    Cyan.Arachne.form.date = new Date(time.getTime());

    var type = Cyan.Arachne.form.type;
    if (type == "week")
        time.setDate(time.getDate() - time.getDay());
    else if (type == "month")
        time.setDate(1);

    if (time.getTime() != Cyan.Arachne.form.time.getTime())
    {
        Cyan.Arachne.form.time = time;
        reloadCalendars();
    }
    else
    {
        selDate();
    }
}

function today()
{
    var time = new Date();
    time.setHours(0, 0, 0, 0);
    Cyan.Arachne.form.date = new Date(time.getTime());

    var type = Cyan.Arachne.form.type;
    if (type == "week")
        time.setDate(time.getDate() - time.getDay());
    else if (type == "month")
        time.setDate(1);

    if (time.getTime() != Cyan.Arachne.form.time.getTime())
    {
        Cyan.Arachne.form.time = time;
        reloadCalendars();
    }
    else
    {
        selDate();
    }
}

function selDate()
{
    if (tip)
        tip.hide();

    if (Cyan.Arachne.form.autoSelect)
    {
        var date = Cyan.Arachne.form.date;
        var scrollRow;
        var scrollCol;
        if (Cyan.Arachne.form.type == "day")
        {
            scrollCol = 1;
            scrollRow = new Date().getHours();
        }
        else if (Cyan.Arachne.form.type == "week")
        {
            scrollCol = date.getDay() + 1;
            scrollRow = new Date().getHours();
        }
        else if (Cyan.Arachne.form.type == "month")
        {
            scrollCol = date.getDay();
            scrollRow = parseInt((Cyan.Arachne.form.time.getDay() + date.getDate() ) / 7);
        }

        if (scrollRow || scrollCol)
        {
            try
            {
                if (Cyan.Arachne.form.type != "month")
                {
                    var r = scrollRow + 5;
                    if (r > 23)
                        r = 23;
                    mainBody.select(r, scrollCol);
                }
                mainBody.select(scrollRow, scrollCol);
            }
            catch (e)
            {
            }
        }
    }
}

function reloadCalendars()
{
    closeTip();
    refresh(refreshView);
}

function closeTip()
{
    currentDiv = null;
    noShowDiv = true;
    if (tip)
        tip.hide();
}

function restartTip()
{
    noShowDiv = false;
}

function calendarClick()
{
}

function calendarDblClick()
{
}

function refreshView()
{
    var time = Cyan.Arachne.form.time;
    var type = Cyan.Arachne.form.type;

    var i;
    if (type == "day")
    {
        mainBody.setHeader(1, Cyan.Date.format(time, "mon dd"));
    }
    else if (type == "week")
    {
        time = new Date(time);
        for (i = 0; i < 7; i++)
        {
            mainBody.setHeader(i + 1, Cyan.Date.format(time, "E(M/dd)"));
            time.setDate(time.getDate() + 1);
        }
    }
    else if (type == "weekday")
    {
        var time0 = time;

        mainBody.setHeader(4, Cyan.Date.format(time, "E(M/dd)"));

        time = new Date(time0);
        for (i = 1; i <= 3; i++)
        {
            time.setDate(time.getDate() + 1);
            mainBody.setHeader(4 + i, Cyan.Date.format(time, "E(M/dd)"));
        }

        time = new Date(time0);
        for (i = 1; i <= 3; i++)
        {
            time.setDate(time.getDate() - 1);
            mainBody.setHeader(4 - i, Cyan.Date.format(time, "E(M/dd)"));
        }
    }

    selDate();
    restartTip();
}

function calendarOver(div)
{
    if (currentDiv != div && tip)
        tip.hide();
}

function showTip(div, event, itemId)
{
    if (currentDiv != div && tip)
        tip.hide();

    if (!noShowDiv)
    {
        currentDiv = div;
        setTimeout(function ()
        {
            if (currentDiv == div)
                showTip0(div, itemId);
        }, 600);
        if (event)
            new Cyan.Event(event).stop();
    }
}

function showTip0(div, itemId)
{
    if (noShowDiv)
        return;

    currentDiv = div;

    if (!tip)
    {
        tip = new Cyan.ToolTip();
        tip.html = createTipHtml();

        tip.create();
    }

    currentItemId = itemId;
    var item;

    if (itemId)
        item = itemMap[itemId];

    renderTip(item);

    var tipSize = getTipSize(item);
    var width = tipSize.width;
    var height = tipSize.height;

    var position = Cyan.Elements.getPosition(div);
    var size = Cyan.Elements.getComponentSize(div);
    if (size.width > 300)
        size.width = 300;

    var x = position.x + size.width / 2 - width / 2, y = position.y - height - 20;

    if (x <= 10)
        x = 10;
    else if (x + width > document.body.clientWidth - 10)
        x = document.body.clientWidth - 10 - width;

    if (y <= 10)
        y = position.y + size.height + 20;

    tip.showAt(x, y);
}

function appendItem(item, itemDiv)
{
    var time = getItemTime(item);
    var calendarDiv = $(getCalendarDivId(time));
    if (calendarDiv)
    {
        if (!itemDiv)
        {
            itemDiv = document.createElement("DIV");
            itemDiv.id = getItemId(item);
            itemDiv.className = "item";
        }

        itemDiv.innerHTML = createItemHtml(item);

        time = time.getTime();
        if (!$$(calendarDiv).$(".item").each(function ()
                {
                    var time2 = getItemTime(itemMap[this.id]).getTime();
                    if (time2 >= time)
                    {
                        if (this != itemDiv)
                        {
                            var div = this;
                            if (time2 == time)
                            {
                                div = div.nextSibling;
                                if (!div)
                                    return false;
                            }
                            if (div.previousSibling != itemDiv)
                                calendarDiv.insertBefore(itemDiv, div);
                        }

                        return true;
                    }
                }))
        {
            calendarDiv.appendChild(itemDiv);
        }

        return true;
    }
    else
    {
        return false;
    }
}