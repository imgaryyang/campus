$.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var gradeId = Cyan.Arachne.form.gradeId;
        if (gradeId == null || gradeId == 0 || gradeId < 0) {
            Cyan.message("请先选择年级");
        }
        else {
            this.inherited();
        }
    });
    Cyan.Class.overwrite(window, "showSortList", function (forward) {
        var gradeId = Cyan.Arachne.form.gradeId;
        if (gradeId == null || gradeId == 0 || gradeId < 0) {
            Cyan.message("请先选择年级");
        }
        else {
            this.inherited(forward);
        }
    });
});