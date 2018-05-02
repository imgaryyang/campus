Cyan.onload(function () {
    Cyan.Class.overwrite(window, "noticePay", function () {

        if (Cyan.$$("#keys").checkeds().length == 0) {
            Cyan.message("请选择发送要提醒的账单");

        } else {
            var batchChoise = [];
            var keys = Cyan.$$("#keys").checkeds();
            for (var i = 0; i < keys.length; i++) {
                batchChoise[i] = keys[i].value;
            }
            console.log(batchChoise);
            noticeToPay(batchChoise,function(d){
                Cyan.message("已发送" + d + "条通知")
                refresh()
            })

        }

    });

    Cyan.Class.overwrite(window, "signPayed", function () {

        if (Cyan.$$("#keys").checkeds().length == 0) {
            Cyan.message("请选择要标注的账单");

        } else {
            var batchChoise = [];
            var keys = Cyan.$$("#keys").checkeds();
            for (var i = 0; i < keys.length; i++) {
                batchChoise[i] = keys[i].value;
            }
            signAndPayed(batchChoise,function(d){
                Cyan.message("已标注" +d + "条已缴")
                refresh()
            })
        }
    });

});

function preview(billId)
{
    System.openPage("/campus/pay/billPreview/" + billId );
}