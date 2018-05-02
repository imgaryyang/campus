Cyan.onload(function () {
    var div = $$("#homepage")[0];
    div.style.paddingTop = (document.documentElement.clientHeight - div.clientHeight) / 2 + "px";
});

function homeConditionSearch(){
    var condition = Cyan.$("condition").value;
    condition = Cyan.trim(condition)
    if (!condition) {
        $.error("请输入查询关键字");
        return false;
    }
    Cyan.Arachne.form.condition = condition;
    Cyan.Arachne.form.wsCheck = $$("#wsCheckBox")[0].checked;
    Cyan.Arachne.form.emailCheck = $$("#emailCheckBox")[0].checked;
    Cyan.Arachne.form.menuCheck = $$("#menuCheckBox")[0].checked;
    Cyan.Arachne.form.userCheck = $$("#userCheckBox")[0].checked;
    turnToSearch();
}

function leaderHomeConditionSearch(){
    var condition = Cyan.$("condition").value;
    condition = Cyan.trim(condition)
    if (!condition) {
        $.error("请输入查询关键字");
        return false;
    }
    Cyan.Arachne.form.condition = condition;
    Cyan.Arachne.form.wsCheck = $$("#wsCheckBox")[0].checked;
    Cyan.Arachne.form.menuCheck = $$("#menuCheckBox")[0].checked;
    Cyan.Arachne.form.userCheck = $$("#userCheckBox")[0].checked;
    turnToLeaderSearch();
}