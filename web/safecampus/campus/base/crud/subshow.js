Cyan.importJs("/safecampus/campus/base/base.js");

var returnValue;
/**
 * 重写新增成功方法，
 * @param key 主键
 */
function addSuccess(key)
{
    Sesame.tip(System.Crud.messages.addSuccess, function ()
    {
        if (key)
        {
            if (!returnValue)
                returnValue = [key];
            else
                returnValue.push(key);
            Cyan.Window.setReturnValue(returnValue);
        }
        reset();
    });
}

function updateSuccess()
{
    Sesame.tip(System.Crud.messages.updateSuccess, function ()
    {
        setTimeout(function ()
        {
            Cyan.Window.setReturnValue(true);
            Cyan.Window.closeWindow();
        }, 1500);
    });
}

function reset()
{
    Cyan.$$("form").reset();
}
