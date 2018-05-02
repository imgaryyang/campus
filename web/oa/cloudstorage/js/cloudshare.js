$j(function () {
    var shareRecords = Cyan.Arachne.form.shareRecords;
    if (shareRecords) {
        setTimeout(function () {
            var n = shareRecords.length;
            for (var i = 0; i < n; i++) {
                var share = shareRecords[i];
                shareSelector.getSelector().addSelected({
                    type: share.type.toLowerCase(), id: share.type == "DEPT" ? share.deptId :
                        share.userId, name: share.name
                });
            }

            var word = Cyan.$("word");
            if (word) {
                shareSelector.getSelector().bindQuery(word);
            }
        }, 100);
    }
});


function saveShare() {
    var shareName = $j("#shareName").val();
    var shareId = $j("#shareId").val();
    if (shareName == "" || !shareName) {
        errorMsg("分享名字不能为空！")
        return;
    }
    var members = shareSelector.getSelector().selected;
    var n = members.length;
    var shares = Cyan.Arachne.form.shareRecords = new Array(n);
    for (var i = 0; i < n; i++) {
        var member = members[i];
        shares[i] = member.type == "dept" ? {type: "dept", deptId: member.id, userId: 0} :
        {type: "user", userId: member.id, deptId: 0};
    }
    saveShareRecord(shareName,shareId, function (result) {
        if (result) {
            closeWindow(function () {
                return true;
            });
        }else{
            errorMsg("分享失败");
        }

    });
}


/**
 * 弹出框
 * @param msg
 * @param callback
 */
function errorMsg(msg, callback) {
    $j("#errorCont").text(msg);
    $j("#qxzwj").fadeIn(300, callback).delay(1000).fadeOut(300);
}
