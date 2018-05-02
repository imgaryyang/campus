Cyan.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var add = this.inherited;
        var classesId = Cyan.Arachne.form.classesId;
        if (classesId == null || classesId <= 0) {
            Cyan.message("请选择班级");
        }
        else {
            add();
        }
    });
    Cyan.Class.overwrite(window, "showImp", function (forward) {
        var showImp = this.inherited;
        var classesId = Cyan.Arachne.form.classesId;
        if (classesId == null || classesId <= 0) {
            Cyan.message("请选择班级");
        }
        else {
            showImp(forward);
        }
    });
});

