var checked = false;

window.onunload = function ()
{
    return checked;
};

function ok()
{
    if (!Cyan.$("password").value)
    {
        Cyan.message("请输入密码");
        return;
    }
    checkPassword(function (ret)
    {
        if (ret)
        {
            checked = true;
            Cyan.Window.closeWindow();
        }
        else
        {
            Cyan.error("密码错误");
        }
    });
}