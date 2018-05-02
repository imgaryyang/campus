

function importStudentsDialog() {
    var classesId = Cyan.Arachne.form.classesId;

    if (classesId!=null && classesId >0) {
        System.showModal(encodeUrl("/campus/classes/student/import"), function (ret) {
            if (ret) {
                mainBody.reload();
                if (Cyan.List && left instanceof Cyan.List)
                    left.reload();
            }
        });
    } else {
        Cyan.message("请在左侧选择班级")
    }
}

function encodeUrl(url) {

    //左侧树的值
    var groupId = Cyan.Arachne.form.classesId;
    console.log(groupId);
    if (groupId && groupId != null)
        url += "?classesId=" + groupId;
    return url;
}


function importStu() {

    importStudents(Cyan.Arachne.form.classesId, {
        callback: function (count) {
            var c = count.split("-");
            $.message("导入成功,共导入 " + c[0] + " 条记录,  更新记录 " + c[1] + " 条,  待处理重复记录 " + c[2] + " 条",
                function () {
                    Cyan.Window.closeWindow(true);
                });
        },
        error: function () {
            $.message("导入数据错误", function () {
                Cyan.Window.closeWindow(false);
            });
        },
        progress: true
    });
}
