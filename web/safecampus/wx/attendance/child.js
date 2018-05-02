$(document).ready(function () {
   window.clearDate= function (){
        $("#dateStart").val("");
       $(".date_bg").click();
       var stuId=Cyan.Arachne.form.studentId;
       var state=Cyan.Arachne.form.state;
       state-=1;
       window.location.href = "/wx/attendance/childsafe?studentId=" + stuId+"&state="+state;
    }
    var calendar = new datePicker();
    calendar.init({
        'trigger': '#dateStart', /*按钮选择器，用于触发弹出插件*/
        'type': 'date',/*模式：date日期；datetime日期时间；time时间；ym年月；*/
        'minDate':'1900-1-1',/*最小日期*/
        'maxDate':'2100-12-31',/*最大日期*/
        'onSubmit':function(){/*确认时触发事件*/
            var theSelectData=calendar.value;
            var stuId=Cyan.Arachne.form.studentId;
            var state=Cyan.Arachne.form.state;
            state-=1;
            window.location.href = "/wx/attendance/childsafe?dateStart=" + theSelectData + "&studentId=" + stuId+"&state="+state;
        },
        'onClose':function(){/*取消时触发事件*/
        }
    });

    $(".kqzt").each(function(){
        var obj  = $(this);
        if(obj.text()==(new Date().formatDate("yyyy-MM-dd"))){
            obj.text("考勤中");
        }else{
            obj.text("已结束");
        }

    })

})


function hello(obj){
    obj.type='date';
    setTimeout(function(){
        obj.trigger('click');
    },10);
}
function showStudentAttendance(studentId,state) {
    window.location.href = "/wx/attendance/childsafe?studentId=" + studentId+"&state="+state;
}

function anOtherChild(stuId){

}

function queryMsg(obj,stuId) {
    var date = obj.value;
    alert(date);
    /* if (date == "") {
     alert("请选择日期");
     return;
     } else {
     window.location.href = "/wx/attendance/childsafe?dateStart=" + date + "&studentId=" + stuId;
     }*/
}
/*
function oncheck(){
    $("dd").each(function (){
       if($(this).attr("id")==$("#oncheck").val()){
           $(this).style.font=2.6rem;
       }
    })
}
setTimeout("oncheck()",100);*/
