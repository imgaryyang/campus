function showNotice(noticeId) {
    window.open(System.formatUrl("/oa/notice/read/" + noticeId));
    window.refresh();
}

function reset() {
    Cyan.$("entity.title").value = "";
    Cyan.$("entity.invalidTime").value = "";
    Cyan.$("entity.url").value = "";
    Cyan.$("entity.topInvalidTime").value = "";
    Cyan.$$$("entity.content").setValue("");
}

function changeType() {
    var type = Cyan.$("entity.type").value;
    if (type == "edit") {
        Cyan.$("url_div").style.display = "none";
        Cyan.$("file_div").style.display = "none";
        Cyan.$("content_div").style.display = "block";
        Cyan.Window.getWindow().setHeight(560);
    }
    else if (type == "url") {
        Cyan.$("url_div").style.display = "block";
        Cyan.$("file_div").style.display = "none";
        Cyan.$("content_div").style.display = "none";
        Cyan.Window.getWindow().setHeight(250);
    }
    else {
        Cyan.$("file_div").style.display = "block";
        Cyan.$("url_div").style.display = "none";
        Cyan.$("content_div").style.display = "none";
        Cyan.Window.getWindow().setHeight(250);
    }
}

function openTrack(noticeId) {
    track(noticeId, {target: "_modal"});
}

function doPublish() {
    if (Cyan.$$("#keys").checkeds().length) {
        publish(function (result) {
            Cyan.message("发布成功", function () {
                mainBody.reload();
            });
        });
    }
    else {
        Cyan.message("请选择要发布的信息");
    }
}

function doCancelPublish() {
    if (Cyan.$$("#keys").checkeds().length) {
        cancelPublish(function (result) {
            Cyan.message("取消发布成功", function () {
                mainBody.reload();
            });
        });
    }
    else {
        Cyan.message("请选择要取消发布的信息");
    }
}

function submitInfo() {
    var invalidDate = new Date((Cyan.$$("#invalidTime").val() + " 00:00:00"));
    invalidDate.setDate(invalidDate.getDate() + 1);
    var invalidTime = invalidDate.getTime();
    var nowTime = new Date().getTime();
    if (invalidTime - nowTime < 0) {
        Cyan.error("有效时间不能小于当前时间");
    } else {
        save();
    }
}
