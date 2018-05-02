$(document).ready(function () {
    window.clearDate = function () {
        $("#dateStart").val("");
        $(".date_bg").click();
        var key = $("#relationkey").val();
        Cyan.Arachne.form.hisDate = null;
        Cyan.Arachne.form.key = key;
        var atttype = $("#nav").attr("type");
        querySiertaData(1, atttype, {
            callback: function (data) {
                var html = template("attenItemhis", data);
                $("#attendInfohis").html(html);
            }
        })
    }
    var calendar = new datePicker();
    calendar.init({
        'trigger': '#dateStart', /*按钮选择器，用于触发弹出插件*/
        'type': 'date', /*模式：date日期；datetime日期时间；time时间；ym年月；*/
        'minDate': '1900-1-1', /*最小日期*/
        'maxDate': '2100-12-31', /*最大日期*/
        'onSubmit': function () {/*确认时触发事件*/
            var theSelectData = calendar.value;
            var key = $("#relationkey").val();
            Cyan.Arachne.form.hisDate = theSelectData;
            Cyan.Arachne.form.key = key;
            var atttype = $("#nav").attr("type");
            querySiertaData(1, atttype, {
                callback: function (data) {
                    var html = template("attenItemhis", data);
                    $("#attendInfohis").html(html);
                }
            })
        },
        'onClose': function () {/*取消时触发事件*/
        }
    });

})

