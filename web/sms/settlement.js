function reloadSm(settlementId)
{
    reloadSettlement(settlementId, {
        callback:function()
        {
            $.message("重新载入成功！");
            refresh();
        }
    });
}

function showSettlement(settlementId)
{
    System.openPage("/sms/settlement/stat?settlementId="+settlementId);
}