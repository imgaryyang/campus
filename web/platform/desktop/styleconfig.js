function stylechange()
{
    Cyan.$('preview').src = Cyan.$("style.stylePath").value + '/preview.jpg';
}

Cyan.onload(function()
{
    Cyan.Class.overwrite(window, "save", function()
    {
        inherited(function()
        {
            Cyan.customConfirm([
                {
                    text:"立即生效",
                    callback:function()
                    {
                        refresh(function()
                        {
                            var win = System.getTopWin();
                            var location = win.location;
                            win.location.href =  location.href;
                        });
                    }
                },
                {
                    text:"下次登录时生效"
                }
            ],
            {
                message:"保存成功",
                width:300
            });
        });
    });
});