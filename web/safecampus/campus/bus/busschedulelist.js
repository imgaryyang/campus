mainBody.getRenderer = function (columnIndex, html) {
    return columnIndex > 1 && columnIndex < 9 ? renderer : html;
};

function refreshView() {
    var time = Cyan.Arachne.form.time;

    var i;
    time = new Date(time);
    for (i = 0; i < 7; i++) {
        mainBody.setHeader(i + 2, Cyan.Date.format(time, "E(M/dd)"));
        time.setDate(time.getDate() + 1);
    }
    selDate();
    restartTip();
}

function delete0(id) {
    Cyan.confirm("确定删除记录?", function (ret) {
        if (ret == "ok")
            deleteBusTeacher(id, function () {
                refresh(refreshView());
            });
    });
}

function edit(id) {
    var time = Cyan.Arachne.form.time;
    System.showModal("/campus/bus/schedule/show/" + id + "/" + time.getTime(), function (ret) {
        if (ret) {
            mainBody.reload();
        }
    }, {width: 1000, height: 300});
}

function createItemHtml(item) {
    var type = item.scheduleType;
    var scheduleType;
    if (type == "ALLDAY") {
        scheduleType = "全天";
    } else if (type == "MORNING") {
        scheduleType = "早班";
    } else if (type == "NIGHT") {
        scheduleType = "晚班";
    }
    return "<span>" + scheduleType + "</span>";
}

function getItemId(item) {
    return item.busScheduleId;
}

function getItemTime(item) {
    return item.scheduleTime;
}

function addSchedule() {
    var weekTime = Cyan.Arachne.form.time;
    var busId = Cyan.Arachne.form.busId;
    if (busId == null || busId == 0) {
        Cyan.message("请先选择校巴");
        return "";
    } else {
        System.showModal("/campus/bus/schedule/add/" + busId + "/" + weekTime.getTime(), function (ret) {
            if (ret) {
                mainBody.reload();
            }
        }, {width: 1000, height: 300});
    }
}