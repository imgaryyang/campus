$.importJs("/safecampus/campus/common/studentselect.js");

$.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var value = Cyan.Arachne.form.sroomId;
        if (!value || value == 0) {
            Cyan.message("请选择午休室");
            return;
        }
        System.selectStudents(function (result) {
            if (result) {
                addStudents(result, function () {
                    Cyan.message("操作成功", function () {
                        refresh();
                    });
                });
            }
        });
    });
});