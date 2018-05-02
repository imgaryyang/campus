Cyan.onload(function ()
{
    setTimeout(function ()
    {
        Cyan.$$(".attribute").onclick(function ()
        {
            var input = this;
            setTimeout(function ()
            {
                input.focus();
            }, 10);
        }).onkeydown(function (event)
                {
//                    event.stop();
                });

        Cyan.$$(".number").onfocus(
                function (event)
                {
                    this.oldValue = this.value;
                    $$(this).removeClass("number");
                }).onblur(function (event)
                {
                    if (this.value != "")
                    {
                        var value = parseInt(this.value);
                        if (!value && value != 0)
                            value = this.oldValue;
                        if (this.value != value)
                            this.value = value;
                    }
                    Cyan.$$(this).addClass("number");
                });
    }, 500);

    Cyan.Class.overwrite(window, "doSave", function ()
    {
        this.inherited({
            form:Cyan.$$("form")[0],
            callback:function ()
            {
                Cyan.message("保存成功");
            }
        })
    });
});