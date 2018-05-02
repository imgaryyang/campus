(function ($) {
    $(function () {
        var roomId = $("#roomselhis").val();
        Cyan.Arachne.form.key = roomId;
        var atttype = $("#nav").attr("type");
        querySiertaData(1, atttype, {
            callback: function (data) {
                var html = template("attenItemhis", data);
                $("#attendInfohis").html(html);
            }
        })

        $("#roomselhis").on("change", function () {
            var roomId = $("#roomselhis").val();
            Cyan.Arachne.form.key = roomId;
            querySiertaData(1, atttype, {
                callback: function (data) {
                    var html = template("attenItemhis", data);
                    $("#attendInfohis").html(html);
                }
            })
        })


    })
    window.allHistory = function (type) {
       window.location.href="/wx/attendance/allhistory?type="+type;
    }
})(jQuery)