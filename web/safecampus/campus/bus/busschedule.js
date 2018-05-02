function changeValue(scheduleTime, index$, type) {
    var scheduleTimes = Cyan.$$("#scheduleTime_" + index$);
    Cyan.each(scheduleTimes, function () {
        if (this.id == 'scheduleTime_' + index$ + "_" + type) {
            if (this.className == "hover") {
                this.className = "";
                $("itemMap." + scheduleTime).value = "";
            }
            else if (this.className == "") {
                this.className = "hover";
                $("itemMap." + scheduleTime).value = type;
            }
        } else {
            this.className = "";
        }
    });
}

function savesche() {
    var teacherId = Cyan.$("teacherId").value;
    if(teacherId == "")
        Cyan.message("请选择跟班老师");

    else{
        saveSchedule({
            callback: function (isnew) {
                $.message(isnew == "add" ? "新增成功" : "保存成功", function () {
                    closeWindow(true);
                });
            }
        });
    }

}