$.importJs("/safecampus/campus/common/studentselect.js");

$.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var value = Cyan.Arachne.form.troomId;
        if (!value || value == 0) {
            Cyan.message("请选择托管室");
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