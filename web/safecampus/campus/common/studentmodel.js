function ok() {
    var keys = Cyan.$$("#keys").checkedValues();
    if (keys.length == 0) {
        Cyan.message("请选择学生");
        return;
    }
    Cyan.Window.closeWindow(keys);
}