/**
 * Created by zy on 2017/12/1.
 */
function querySQL() {
    var sqlSentence = $("#sqlSentence").val();
    if (sqlSentence == null || sqlSentence == '') {
        alert("查询语句不能为空!");
        return;
    }
    location.href = "/safecampus/database/showsqlwindow?sqlSentence=" + encodeURIComponent(sqlSentence);
}

function queryFile() {
    var filePath = $("filePath").value;
    if (filePath == null || filePath == '') {
        $.message("文件路径不能为空!");
        return;
    }
    readFile(filePath, {
        callback: function (ret) {
            Cyan.$("contentId").innerHTML = ret;
        }
    })
}

function queryFiles() {
    var filePath = $("filePath").value;
    if (filePath == null || filePath == '') {
        $.message("文件路径不能为空!");
        return;
    }
    readFiles(filePath, {
        callback: function (ret) {
            Cyan.$("contentId").innerHTML = ret;
        }
    })
}

function queryLogFiles() {
    $("filePath").value="/home/ccoe-user/log/zmeg_new"
    queryFiles()
}