Cyan.onload(function ()
{
    var opener = Cyan.Window.getOpener();
    if (opener && opener.moreQueryArgs)
    {
        Cyan.$$("form").setValue(opener.moreQueryArgs);
    }
});

function query()
{
    var result = {};
    Cyan.each(Cyan.$$("form")[0], function ()
    {
        if (this.name && (this.value || this.value == ""))
        {
            var name = this.name;
            if (this.nodeName == "SELECT")
            {
                if (this.multiple == "true")
                {
                    result[name] = this.value;
                }
                else
                {
                    result[name] = [];
                    Cyan.each(this.options, function ()
                    {
                        if (this.selected)
                            result[name].push(this.value);
                    });
                }
            }
            else if (this.nodeName == "INPUT")
            {
                if (this.type == "checkbox")
                {
                    if (!result[name])
                        result[name] = [];
                    if (this.checked)
                        result[name].push(this.value);
                }
                else if (this.type == "radio")
                {
                    if (this.checked)
                        result[name] = this.value;
                    else if (!result[name])
                        result[name] = "";
                }
                else if (this.type == "text" || this.type == "password" || this.type == "hidden")
                {
                    result[name] = this.value;
                }
            }
            else if (this.nodeName == "TEXTAREA")
            {
                result[name] = this.value;
            }
        }
    });

    if (System.opener)
    {
        System.opener.queryBy(result);
        System.closePage();
    }
    else
    {
        Cyan.Window.closeWindow(result);
    }
}

function ok()
{
    query();
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
    closeWindow();
}