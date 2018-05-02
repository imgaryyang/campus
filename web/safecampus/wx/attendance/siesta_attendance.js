(function ($) {
    $(function () {
        $("#attend").on("click", function () {
            var roomId = $("#roomsel").val();
            var type = $("#nav").attr("type");
            if (type == 2) {
                if (roomId == null) {
                    alert("请选择午休室");
                    return;
                }
                window.location.href = "/wx/attendance/siestastudent?key=" + roomId;
            } else if (type == 3) {
                if (roomId == null) {
                    alert("请选择托管室");
                    return;
                }
                window.location.href = "/wx/attendance/trusteestudent?key=" + roomId;
            } else if (type == 0) {
                if (roomId == null) {
                    alert("请选择班级");
                    return;
                }
                window.location.href = "/wx/attendance/classesStudentPage?key=" + roomId;
            } else if (type == 1) {
                if (roomId == null) {
                    alert("请选择校巴");
                    return;
                }
                selecttype();
            } else if (type == 4) {
                if (roomId == null) {
                    alert("请选择班级");
                    return;
                }
                selectFaceType();
            }

        })

        var roomId = $("#roomsel").val();
        Cyan.Arachne.form.key = roomId;
        var atttype = $("#nav").attr("type");
        querySiertaData(0, atttype, {
            callback: function (data) {
                var html = template("attenItem", data);
                $("#attendInfo").html(html);
            }
        })

        $("#roomsel").on("change", function () {
            var roomId = $("#roomsel").val();
            Cyan.Arachne.form.key = roomId;
            querySiertaData(0, atttype, {
                callback: function (data) {
                    var html = template("attenItem", data);
                    $("#attendInfo").html(html);
                }
            })
        })

    })
    window.toHistory = function (key, state) {
        Cyan.Arachne.form.key = key;
        var atttype = $("#nav").attr("type");
        querySiertaData(state, atttype, {
            callback: function (data) {
                var html = template("attenItem", data);
                $("#attendInfo").html(html);
            }
        })
    }
    window.allHistory = function (type) {
        window.location.href="/wx/attendance/allhistory?type="+type;
    }

    function selecttype() {
        $("#selecttype").removeClass("unshowclass");
        $("#full").removeClass("unshowclass");
    }
    function selectFaceType() {
        $("#facetype").removeClass("unshowclass");
        $("#full").removeClass("unshowclass");
    }

})(jQuery)