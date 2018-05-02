Cyan.importCss("/platform/commons/buttons.css");

Cyan.onload(function ()
{
    var buttons = Cyan.$$(".btn");
    buttons.each(function ()
    {
        initButton(this);
    });
});

function initButton(button)
{
    button.className = "btn btn_mouseout";

    Cyan.attach(button, "mouseover", function ()
    {
        this.className = "btn btn_mouseover";
    });
    Cyan.attach(button, "mouseout", function ()
    {
        this.className = "btn btn_mouseout";
    });
    Cyan.attach(button, "mousedown", function ()
    {
        this.className = "btn btn_mousedown";
    });
    Cyan.attach(button, "mouseup", function ()
    {
        this.className = "btn btn_mouseover";
    });
}