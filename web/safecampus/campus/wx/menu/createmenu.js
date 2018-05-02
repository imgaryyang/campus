$.onload(function () {
    Cyan.Class.overwrite(window, "createMenu", function () {
        this.inherited(function () {
            Cyan.message("生成成功",function()
            {
                closeWindow(true);
            });
        });
    });
});