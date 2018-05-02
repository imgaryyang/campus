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

flag = false;

function saveC() {

    if (!flag) {
        save()
    } else if ($("#classesId").val() == "") {
        Cyan.message("保存成功", function () {
            closeWindow(true);
        })
    } else {
        save();
    }
}

function addnew() {
    if ($("#className").val() == "" || $("#masterId").val() == "") {
        Cyan.message("请先填写班级信息")
    } else {
        System.SubList.add('subList', function (ret) {
            System.openPage();
        });
        flag = true;
    }
}