function createItemHtml(item)
{
    return createScheduleHtml(item);
}

function createScheduleHtml(schedule)
{
    var scheduleId = schedule.scheduleId;

    var title = schedule.title;

//    if (schedule.address)
//        title += "(" + schedule.address + ")";

    if (title.length > 23 && Cyan.Arachne.form.type != "day")
        title = title.substring(0, 20) + "...";

/*    var formatDate = schedule.startTime.formatDate("HH:mm");
    if (formatDate == "00:00")
        formatDate = "";
    var text = formatDate + "&nbsp;" + title;*/

    var html = "<a href='#'";

    if (schedule.priority && schedule.priority.color)
        html += " style='color:" + schedule.priority.color + "'";

    html += " onmouseover='showTip(this.parentNode.parentNode,event||window.event," + scheduleId + ");'";
    html += " onclick='showSchedule(" + scheduleId + ",this,event||window.event);return false;'>";

    if (schedule.type && schedule.type.icon)
        html += "<img src='/oa/schedule/icons/" + schedule.type.icon + "'>";

    html += title + "</a>";
    return html;
}

function getItemId(item)
{
    return item.scheduleId;
}

function getItemTime(item)
{
    return item.startTime;
}

function createTipHtml()
{
    var html = "<div id='tip'><div id='schedule_content'>";

    html += "<div id='typeName'></div>";
    html += "<div class='label'>事件:</div><div id='title' class='content'></div>";
    html += "<div class='label'>时间:</div><div id='time' class='content'></div>";
    html += "<div class='label'>地点:</div><div id='address' class='content'></div>";
    html += "<div class='label'>优先级:</div><div id='priority' class='content'></div>";

    html += "</div>";
    html += "<div id='operators'>";

    html += "<span id='detail'>[<a href='#' onclick='showSchedule();return false'>详细信息</a>]</span>";
    if (!Cyan.Arachne.form.readOnly)
        html += "<span id='delete'>[<a href='#' onclick='deleteSchedule();return false'>删除日程</a>]</span>";


    if (!Cyan.Arachne.form.readOnly)
        html += "<span id='add'>[<a href='#' onclick='addSchedule();return false'>新增日程</a>]</span>";

    html += "</div></div>";
    return html;
}

function renderTip(schedule)
{
    if (schedule)
    {
        $("schedule_content").style.display = "block";
        $("detail").style.display = "inline";

        $("title").innerHTML = schedule.title;
        $("time").innerHTML =
                schedule.startTime.formatDate("M月d日(E) HH:mm") + " - " + schedule.endTime.formatDate(" HH:mm");
        $("address").innerHTML = schedule.address || "";
        $("priority").innerHTML = schedule.priority ? schedule.priority.name : "";

        var typeName = "";
        if (schedule.link || (Cyan.Arachne.form.tag == "user" && schedule.tag == "dept"))
        {
            $("delete").style.display = "none";
        }
        else
        {
            $("delete").style.display = "inline";
        }

        if (Cyan.Arachne.form.tag == "user" && schedule.tag == "dept")
        {
            typeName = "单位日程";
        }
        else
        {
            if (schedule.type && schedule.type.name)
                typeName = schedule.type.name;
            else
                typeName = "";
        }

        if (!Cyan.Arachne.form.tag)
        {
            $("delete").style.display = "none";
            $("add").style.display = "none";
        }

        if (schedule.state)
        {
            typeName += "(" + (schedule.state == "notStarted" ? "未开始" :
                    schedule.state == "going" ? "进行中" : "已结束") + ")";
        }

        $("typeName").innerHTML = typeName;
    }
    else
    {
        $("schedule_content").style.display = "none";
        $("detail").style.display = "none";
        $("delete").style.display = "none";
    }
}

function getTipSize(schedule)
{
    return schedule ? {width: 300, height: 170} : {width: 130, height: 40};
}

function calendarClick(div)
{
    if (Cyan.Arachne.form.tag)
        showTip0(div);
}

function calendarDblClick(div)
{
    if (!Cyan.Arachne.form.readOnly && Cyan.Arachne.form.tag)
        addScheduleWithDiv(div);
}

function showSchedule(scheduleId, element, event)
{
    if (!scheduleId)
        scheduleId = currentItemId;

    closeTip();
    var url = "/oa/schedule/list/" + scheduleId;

    if (Cyan.Arachne.form.tag)
        url += "?tag=" + Cyan.Arachne.form.tag;

    if (Cyan.Arachne.form.readOnly)
        url += "&readOnly=true";
    Cyan.Window.showModal(url, function (ret)
    {
        restartTip();
        if (ret)
        {
            loadSchedule(scheduleId, function (schedule)
            {
                var oldSchedule = itemMap[scheduleId];

                var div = $(scheduleId.toString());
                if (oldSchedule.startTime.getTime() == schedule.startTime.getTime())
                {
                    div.innerHTML = createScheduleHtml(schedule);
                }
                else
                {
                    if (!appendItem(schedule, div))
                        div.parentNode.removeChild(div);
                }

                itemMap[scheduleId] = schedule;
            });
        }
    });

    if (event)
        new Cyan.Event(event).stop();
}

function addScheduleWithDiv(div)
{
    window.currentDiv = div;
    addSchedule();
}

function addSchedule(startTime, endTime)
{
    if (!startTime || !endTime)
    {
        var div = currentDiv;
        if (div)
        {
            if (div.className == "schedule")
                div = div.parentNode;

            if (!startTime)
                startTime = getStartTime(div);

            if (!endTime)
                endTime = getEndTime(div);
        }
    }

    closeTip();

    var url = "/oa/schedule/list.new?tag=" + Cyan.Arachne.form.tag;
    if (startTime)
        url += "&entity.startTime=" + startTime.formatDate();
    if (endTime)
        url += "&entity.endTime=" + endTime.formatDate();
    if (Cyan.Arachne.form.tag == "dept" && Cyan.Arachne.form.deptId)
        url += "&deptId=" + Cyan.Arachne.form.deptId;

    Cyan.Window.showModal(url, function (scheduleIds)
    {
        if (scheduleIds)
        {
            refresh();
         }
        restartTip();
    });
}

function deleteSchedule(scheduleId)
{
    if (!scheduleId)
        scheduleId = currentItemId;

    closeTip();
    $.confirm("确定删除日程\"" + itemMap[scheduleId].title + "\"", function (ret)
    {
        if (ret == "ok")

            removeSchedule(scheduleId, function ()
            {
                restartTip();
                refresh()
            });
    });
}