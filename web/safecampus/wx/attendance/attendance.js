$(document).ready(function () {
    //限制字符个数
    $(".jsmmz dd").each(function () {
        var maxwidth = 4;
        if ($(this).text().length > maxwidth) {
            $(this).text($(this).text().substring(0, maxwidth));
            $(this).html($(this).html() + '...');
        }
    });

});

function selectThis(obj) {
    alert("aaa");
    var relationId = $(obj).val();
    window.location.href = "/wx/attendance/busatt?relationId=" + relationId;
}

function showcla(id, sta) {
    var status = $("#showclass").attr("data-status");
    if (status == 0) {
        $("#showclass").addClass("bgclass").siblings().removeClass("unshowclass");
        $("#showclass").attr("data-status", 1);
    } else if (status == 1 && sta == 0) {
        window.location.href = "/wx/attendance/classatt?relationId=" + id;
    } else if (status == 1 && sta == 1) {
        window.location.href = "/wx/attendance/busatt?relationId=" + id;
    }
}

function showclaHis(id, sta) {
    var status = $("#showclass").attr("data-status");
    if (status == 0) {
        $("#showclass").addClass("bgclass").siblings().removeClass("unshowclass");
        $("#showclass").attr("data-status", 1);
    } else if (status == 1 && sta == 0) {
        window.location.href = "/wx/attendance/history?relationId=" + id;
    } else if (status == 1 && sta == 1) {
        window.location.href = "/wx/attendance/bushistory?relationId=" + id;
    }
}

function unshow() {
    $("#showclass").attr("data-status", 0);
    $("#showclass").removeClass("bgclass").siblings().addClass("unshowclass");

}

function unshowcla() {
    $("#showclass").removeClass("bgclass").siblings().addClass("unshowclass");
}

function showClass(id, sta) {
    if (sta == 0) {
        window.location.href = "/wx/attendance/classatt?relationId=" + id;
    } else if (sta == 1) {
        window.location.href = "/wx/attendance/busatt?relationId=" + id;
    }
}
function showclaHistory(id, sta) {
    if (sta == 0) {
        window.location.href = "/wx/attendance/history?relationId=" + id;
    }
    else if (sta == 1) {
        window.location.href = "/wx/attendance/bushistory?relationId=" + id;
    }
}

function showAttendanceDetail(id, sta) {
    if (sta == 0) {
        window.location.href = "/wx/attendance/attendanceDetail?aoId=" + id;
    } else if (sta == 1) {
        window.location.href = "/wx/attendance/busattendanceDetail?aoId=" + id;
    }
}


function showBusAttendance(date, id) {
    window.location.href = "/wx/attendance/busatt?relationId=" + id + "&date=" + date;
}


function showHover(obj) {
    $(obj).find("a").attr("class", "hover");
    var a = $(obj).siblings().length;
    $($(obj).siblings()).each(function () {
        $(this).find("a :eq(0)").attr("class", "");
    });
}

function recordMessage(attId, sta, aoId, status, state) {
    var attendanceStatus;
    $($("#" + attId).find("a")).each(function () {
        if ($(this).attr("class") == 'hover') {
            attendanceStatus = $(this).attr("value");
        }
    });
    var count = $("#unsendcount").text();
    Cyan.Arachne.form.attId = attId;
    Cyan.Arachne.form.attendanceStatus = attendanceStatus;
    Cyan.Arachne.form.aoId = aoId;
    saveRecord(status, function () {
        if (count == 1) {
            var finishsta = Cyan.Arachne.form.finishState;
            var relationId = Cyan.Arachne.form.relationId;
            var type = Cyan.Arachne.form.type
            toFinish(finishsta,relationId,type,aoId);
        } else {
            if (sta == 0) {
                window.location.href = "/wx/attendance/changestate?aoId=" + aoId + "&finishState=" + state;
            } else if (sta == 1) {
                window.location.href = "/wx/attendance/buschangestate?attendanceStatus=" + attendanceStatus + "&finishState=" + state + "&aoId=" + aoId;
            }
        }
    });
}


function sendMessage(relationId, sta) {
    var num = $("#hadchecked").text();
    var allnum = $("#allcount").text();
    var attstatus = $("#attstatus").attr("value");
    var arrchecked = new Array();
    if (sta == 1) {
        var attstatus = $("#attstatus").attr("value");
        Cyan.Arachne.form.attstatus = attstatus;
    }
    Cyan.Arachne.form.relationId = relationId;
    if (parseInt(num) == parseInt(allnum)) {
        //全选走这里
        //插入该班全部正常到达的学生考勤记录了，并导向完成考勤页面
        saveAll({
            callback: function (aoId) {
                if (aoId == -1) {
                    alert("今天已考勤完成");
                } else {
                    if (sta == 0) {
                        window.location.href = "/wx/attendance/finish?aoId=" + aoId;
                    } else if (sta == 1) {
                        console.log(1);
                        window.location.href = "/wx/attendance/busfinish?aoId=" + aoId;
                    }
                }
            }
        });


    } else {
        //选部分的走这里
        //根据取得的学生Id按照不同的情况存储考勤记录，分为正常和未到，未到的暂不发送考勤记录
        $($(".kqmx a")).each(function () {
            if ($(this).attr("class").indexOf("hadcheck") != -1) {
                //选中的学生
                console.log($(this).attr("id"));
                arrchecked.add($(this).attr("id"));

            }
        })
        saveChecked(arrchecked, {
            callback: function (aoId) {
                //导向该班未发送学生页面
                if (aoId == -1) {
                    alert("今天已考勤完成");
                }
                else {
                    if (sta == 0) {
                        window.location.href = "/wx/attendance/changestate?aoId=" + aoId;
                    } else if (sta == 1) {
                        window.location.href = "/wx/attendance/buschangestate?aoId=" + aoId;
                    }
                }
            }
        });

    }
}

function gethover(id) {
    var status = $("#" + id).attr("data-status");
    if (status == 0) {
        $("#" + id).attr("data-status", 1).addClass("hadcheck").removeClass("notcheck");
        $("#hadchecked").text(parseInt($("#hadchecked").text()) + 1)
    } else if (status == 1) {
        $("#" + id).attr("data-status", 0).removeClass("hadcheck").addClass("notcheck");
        var num = $("#hadchecked").text();
        $("#hadchecked").text(parseInt($("#hadchecked").text()) - 1)
    }

}

function allChecked(all) {
    var num = $("#allchecked").attr("data-status");
    if (num == 0) {
        $(all).addClass("hadcheck").removeClass("notcheck");
        $($(".kqmx a")).each(function () {
            $(this).addClass("hadcheck").removeClass("notcheck");
            $(this).attr("data-status", 1);
        });
        $("#allchecked").attr("data-status", 1);
        $("#hadchecked").text($("#allcount").text());
    } else if (num == 1) {
        $(all).removeClass("hadcheck").addClass("notcheck");
        $($(".kqmx a")).each(function () {
            $(this).removeClass("hadcheck").addClass("notcheck");
            $(this).attr("data-status", 0);
        });
        $("#allchecked").attr("data-status", 0);
        $("#hadchecked").text(0)
    }
}

function continueAtt(relationId, sta) {
    if (sta == 0) {
        window.location.href = "/wx/attendance/classes?key=" + relationId;
    } else if (sta == 1) {
        window.location.href = "/wx/attendance/busroute?key=" + relationId;
    } else if (sta == 2) {
        window.location.href = "/wx/attendance/siesta?key=" + relationId;
    } else if (sta == 3) {
        window.location.href = "/wx/attendance/trustee?key=" + relationId;
    } else if (sta == 4) {
        window.location.href = "/wx/attendance/face?key=" + relationId;
    }

}

function onTopClick(id) {
    window.location.hash = "#" + id;
}

/*
 function selecttype() {
 $("#selecttype").removeClass("unshowclass");
 $("#full").removeClass("unshowclass");
 }
 */

function checkthis(a) {
    $(a).addClass("showbg").siblings().removeClass("showbg");
    console.log($(a).attr("value"))
    $(a).attr("status", 1).siblings().attr("status", 0);
}

function unshowdiv() {
    $("#selecttype").addClass("unshowclass");
    $("#facetype").addClass("unshowclass");
    $("#full").addClass("unshowclass");
}

function startAtt(obj, id, type) {
    var roomId = $("#roomsel").val();
    $(obj).css("background", "#f9b126");
    var val = -1;
    $(".aa").each(function () {
        if ($(this).attr("status") == 1) {
            val = $(this).attr("value");
        }
    })
    if (val == -1) {
        alert("请选择考勤类型");
        return;
    }
    if (type == 1) {
        window.location.href = "/wx/attendance/busStudentPage?key=" + roomId + "&attstatus=" + val;
    } else if (type == 4) {
        window.location.href = "/wx/attendance/faceStudentPage?key=" + roomId + "&attstatus=" + val;
    }

}

function toFinish(finishState, id, type, aoId) {
    var href;
    if (finishState == 1) {
        //跳回首页
        if (type == 0) {
            href = "/wx/attendance/classes?key=" + id;
        } else if (type == 1) {
            href = "/wx/attendance/busroute?key=" + id;
        } else if (type == 2) {
            href = "/wx/attendance/siesta?key=" + id;
        } else if (type == 3) {
            href = "/wx/attendance/trustee?key=" + id;
        } else if (type == 4) {
            href = "/wx/attendance/face?key=" + id;
        }
        window.location.href = href;
    } else {
        //跳到完成页面
        if (type == 1) {
            href = "/wx/attendance/busfinish?aoId=" + aoId;
        } else {
            href = "/wx/attendance/finish?aoId=" + aoId;
        }
        window.location.href = href;
    }
}


function queryHis(obj, key) {
    var date = obj.value;
    if (date == "") {
        return;
    }
    Cyan.Arachne.form.hisDate = date;
    Cyan.Arachne.form.key = key;
    var atttype = $("#nav").attr("type");
    querySiertaData(1, atttype, {
        callback: function (data) {
            var html = template("attenItemhis", data);
            $("#attendInfohis").html(html);
        }
    })
}