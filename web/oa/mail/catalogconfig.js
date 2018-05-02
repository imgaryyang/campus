Cyan.onload(function ()
{
    typeChange();
});

function typeChange()
{
    if (Cyan.$("entity.type").value == "IN")
    {
        showComponent("entity.sender");
        hideComponent("entity.mailFrom");
    }
    else
    {
        showComponent("entity.mailFrom");
        hideComponent("entity.sender");
    }
}