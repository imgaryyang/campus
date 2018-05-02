(function ($) {

    window.sendAttend = function (relationId, type) {
        var arrchecked = [];
        $($(".kqmx a")).each(function () {
            if ($(this).attr("class").indexOf("hadcheck") != -1) {
                //选中的学生
                console.log($(this).attr("id"));
                arrchecked.add($(this).attr("id"));
                console.log($(this).attr("id"))

            }
        })
        Cyan.Arachne.form.key = relationId;
        if (arrchecked.length != 0) {
            if (type == 1 || type == 4) {
                var attstatus = $("#attstatus").attr("value");
                Cyan.Arachne.form.attstatus = attstatus;
                //执行保存
                saveBuschecked(arrchecked, type, {
                    callback: function (result) {
                        if (result.finish) {
                            if (type == 1) {
                                window.location.href = '/wx/attendance/busfinish?aoId=' + result.aoId;
                            } else if (type == 4) {
                                window.location.href = '/wx/attendance/finish?aoId=' + result.aoId;
                            }
                        } else {
                            window.location.href = '/wx/attendance/changestate?aoId=' + result.aoId;
                        }
                    }
                })
            } else {
                saveChecked(arrchecked, type, {
                    callback: function (result) {
                        if (result.finish) {
                            window.location.href = '/wx/attendance/finish?aoId=' + result.aoId;
                        } else {
                            window.location.href = '/wx/attendance/changestate?aoId=' + result.aoId;
                        }
                    }
                })
            }
        }
    }


})(jQuery)