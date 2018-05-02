$.onload(function () {
    Cyan.Class.overwrite(window, "add", function () {
        var levelId = Cyan.Arachne.form.levelId;
        if (levelId == null || levelId == 0) {
            Cyan.message("请先选择学校级别");
        }
        else {
            this.inherited();
        }
    });
});