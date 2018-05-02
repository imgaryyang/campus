$.importJs("/safecampus/campus/common/studentselect.js");

$.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var busSiteId = Cyan.Arachne.form.busSiteId;
        if (busSiteId == null || busSiteId.indexOf("-") < 0) {
            Cyan.message("请先选择站点");
        }
        else {
            System.selectStudents(function (result) {
                if (result) {
                    addStudents(result, function () {
                        Cyan.message("操作成功", function () {
                            refresh();
                        });
                    });
                }
            });
        }
    });
});