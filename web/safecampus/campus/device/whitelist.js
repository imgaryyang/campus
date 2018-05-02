function changeValue()
{

    Cyan.$("entity.upVersion").setValue("");
    $$("entity.upVersion").setValue("");
    $$("entity.upVersion").setText("");
    $("entity.upVersion").value = "";
    Cyan.Arachne.refresh(Cyan.$("entity.upVersion"));
}

function changeValue2()
{
    Cyan.$("entity.whiteListVersion").value = "";
    Cyan.Arachne.refresh(Cyan.$("entity.whiteListVersion"));
}

/**
 * 白名单升级
 */
function upLevel()
{
    $.confirm("" +
            "确定要升级版本吗？", function (result) {
        if (result == 'ok')
        {
            upWhiteList({
                callback: function (result) {
                    if (!result.success) $.message(result.msg);
                    else
                    {
                        $.message(result.msg, function () {
                            refresh();
                        });

                    }
                }
            })
        }
    });
}