function createItemHtml(item)
{
    return createDiaryHtml(item);
}

function createDiaryHtml(diary)
{
    var diaryId = diary.diaryId;

    var title = diary.title;

    if (title.length > 23 && Cyan.Arachne.form.type != "day")
        title = title.substring(0, 20) + "...";

    var text = title;

    var html = "<a href='#'";

    html += " onmouseover='showTip(this.parentNode.parentNode,event||window.event," + diaryId + ");'";
    html += " onclick='showDiary(" + diaryId + ",this,event||window.event);return false;'>";

    html += text + "</a>";
    return html;
}

function getItemId(item)
{
    return item.diaryId;
}

function getItemTime(item)
{
    return item.diaryTime;
}

function createTipHtml()
{
    var html = "<div id='tip'><div id='diary_content'>";

    html += "<div id='typeName'></div>";
    html += "<div class='label'>标题:</div><div id='title' class='content'></div>";
    html += "<div class='label'>时间:</div><div id='time' class='content'></div>";
    html += "</div>";

    html += "<div id='operators'>";
    html += "<span id='detail'>[<a href='#' onclick='showDiary();return false'>详细信息</a>]</span>";
    html += "<span id='add'>[<a href='#' onclick='addDiary();return false'>新增日志</a>]</span>";
    html += "<span id='delete'>[<a href='#' onclick='deleteDiary();return false'>删除日志</a>]</span>";

    html += "</div></div>";
    return html;
}

function renderTip(diary)
{
    if (diary)
    {
        $("diary_content").style.display = "block";
        $("detail").style.display = "inline";
        $("delete").style.display = "inline";

        $("title").innerHTML = diary.title;
        $("time").innerHTML = diary.diaryTime.formatDate("M月d日(E)");
    }
    else
    {
        $("diary_content").style.display = "none";
        $("detail").style.display = "none";
        $("delete").style.display = "none";
    }
}

function getTipSize(diary)
{
    return diary ? {width:300,height:130} : {width:170,height:40};
}

function calendarClick(div)
{
    showTip0(div);
}

function calendarDblClick(div)
{
    addDiaryWithDiv(div);
}

function showDiary(diaryId, event)
{
    if (!diaryId)
        diaryId = currentItemId;

    closeTip();
    Cyan.Window.showModal("/oa/diary/diaryQuery/" + diaryId, function(ret)
    {
        restartTip();
        if (ret)
        {
            loadDiary(diaryId, function(diary)
            {
                var oldDiary = itemMap[diaryId];

                var div = $(diaryId.toString());
                if (oldDiary.diaryTime.getTime() == diary.diaryTime.getTime())
                {
                    div.innerHTML = createDiaryHtml(diary);
                }
                else
                {
                    if (!appendItem(diary, div))
                        div.parentNode.removeChild(div);
                }

                itemMap[diaryId] = diary;
            });
        }
    });

    if (event)
        new Cyan.Event(event).stop();
}

function addDiaryWithDiv(div)
{
    currentDiv = div;
    addDiary();
}

function addDiary(diaryTime)
{
    if (!diaryTime)
    {
        var div = currentDiv;
        if (div)
        {
            if (div.className == "diary")
                div = div.parentNode;

            if (!diaryTime)
                diaryTime = getStartTime(div);
        }
    }

    closeTip();

    var url = "/oa/diary/diaryQuery.new";
    if (diaryTime)
        url += "?diaryTime=" + diaryTime.formatDate();

    Cyan.Window.showModal(url, function(diaryIds)
    {
        if (diaryIds)
        {
            loadDiaries(diaryIds, function(diaries)
            {
                diaries.each(function()
                {
                    itemMap[this.diaryId] = this;
                    appendItem(this);
                });
            });
        }
        restartTip();
    });
}

function deleteDiary(diaryId)
{
    if (!diaryId)
        diaryId = currentItemId;

    closeTip();
    $.confirm("确定删除日志\"" + itemMap[diaryId].title + "\"", function(ret)
    {
        if (ret == "ok")
            removeDiary(diaryId, function()
            {
                restartTip();
                var div = $(diaryId.toString());
                div.parentNode.removeChild(div);
            });
    });
}