function allWritable(element)
{
    while (element.nodeName != "FIELDSET" && element.nodeName != "BODY")
    {
        element = element.parentNode;
    }

    Cyan.$$(element).$(":checkbox").each(function ()
    {
        if (this.value == "writable")
        {
            this.checked = true;
        }
        else if (this.value == "hidden" || this.value == "readonly")
        {
            this.checked = false;
        }
    });
}

function allReadonly(element)
{
    while (element.nodeName != "FIELDSET" && element.nodeName != "BODY")
    {
        element = element.parentNode;
    }

    Cyan.$$(element).$(":checkbox").each(function ()
    {
        if (this.value == "readonly")
        {
            this.checked = true;
        }
        else if (this.value == "hidden" || this.value == "writable")
        {
            this.checked = false;
        }
    });
}