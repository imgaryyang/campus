function returnCard(cardId)
{
    $.confirm("" +
            "确定要退卡吗？", function (result) {
        if (result == 'ok')
        {
            returnCardByCardId(cardId, {
                callback: function (result) {
                    if (result.success == 1)
                    {
                        $.message("退卡失败！");
                    }
                    else
                    {
                        $.message("退卡成功！", function () {
                            refresh();
                        });

                    }
                }
            })
        }
    })
}