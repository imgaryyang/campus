Cyan.importJs("/platform/commons/crud.js");

function goTOStudenExamPage(examId) {
    System.openPage("/campus/tutorship/subjectscorecrud?examId=" + examId)
}

function showView(examId, studentId) {
    System.showModal("/campus/tutorship/subjectscorecrud/toScoreView?examId=" + examId + "&studentId=" + studentId, function (ret) {
        if (ret)
            refresh()
    })
}


function impScore(examId) {
    System.showModal("/campus/tutorship/subjectscoreimp.imp?examId=" + examId, function (ret) {
        if (ret) {
            refresh();
        }
    })
}


function saveStuScore() {
    saveScore(function () {
        $.message("保存成功!", function () {
            closeWindow(true);
        });
    })
}

/**
 * 重写保存后的函数，实现修改成功后刷新列表
 */
function afterSave()
{
    refresh();
}

function sendScoreMsg(examId) {
    Cyan.confirm("确定发送成绩单?", function (ret) {
        if (ret == "ok") {
            sendScoreMsg0(examId, function () {
                $.message("发送成功!");
            });
        }
    });
}