function editPayment(id, s) {
    if (s == "未发送")
    //编辑
        window.show(id, "");
    else
    //补发
        System.openPage("/campus/pay/payItem/sendPage/" + id);
}

function viewPayItemBills(id, s) {
    if (s == "未发送")
    //发送
        System.openPage("/campus/pay/payItem/sendPage/" + id);
    else
    //查看所有账单
        System.openPage("/campus/pay/billscrud?payItemId=" + id);
}

function viewPayItemList(payItemId, s) {
    if (s == 0)
    //发送
        System.openPage("/campus/pay/unpaidbillscrud?payItemId=" + payItemId);
    else
        System.openPage("/campus/pay/paidbillscrud?payItemId=" + payItemId);

}

