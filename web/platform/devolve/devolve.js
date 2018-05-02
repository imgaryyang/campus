function ok()
{
    Cyan.confirm("此操作将不能恢复，确定要移交?", function (ret)
    {
        if (ret == "ok")
        {
            devolve({
                callback:function ()
                {
                    Cyan.message("移交成功", cancel);
                },
                wait:true,
                form:Cyan.$$("form")[0]
            });
        }
    });
}

function cancel()
{
    try
    {
        System.closePage();
    }
    catch (e)
    {
    }

    try
    {
        closeWindow()
    }
    catch (e)
    {
    }
}