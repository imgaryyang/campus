function beforeSave()
{
    var userId = $$$("entity.participants.userId");
    if (userId.length && userId.selectedValues().length == 0)
    {
        $.message("请选择参与者！");
        return false;
    }

    return true;
}

//覆盖掉crud.js的reset方法
function reset()
{
    $("title").value = "";
    $$("#date").setValue("");
    $$("#startHour").setValue("00");
    $$("#startMinute").setValue("00");
    $$("#endHour").setValue("00");
    $$("#endMinute").setValue("00");
    $("address").value = "";
    $("content").value = "";
    $$("#remindDate").setValue("");
    $$("#remindHour").setValue("00");
    $$("#remindMinute").setValue("00");
}

