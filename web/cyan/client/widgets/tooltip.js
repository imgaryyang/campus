Cyan.ToolTip = function ()
{
    this.closable = true;
    this.draggable = true;
};

/**
 * 引入tooltip的适配器
 */
Cyan.importAdapter("tooltip");

(function ()
{
    var tip;
    Cyan.ToolTip.render = function (component, title)
    {
        if (!title)
        {
            title = component.title;
            component.title = "";
        }

        if (title)
        {
            Cyan.attach(component, "mouseover", function ()
            {
                if (!tip)
                {
                    tip = new Cyan.ToolTip();
                    tip.closable = false;
                    tip.draggable = false;
                    tip.html = "<div id='cyan$$qtip' class='cyan_qtip'></div>";
                    tip.create();
                }

                Cyan.$("cyan$$qtip").innerHTML = title;

                var opsition = Cyan.Elements.getPosition(this);
                var size = Cyan.Elements.getComponentSize(this);
                tip.showAt(opsition.x + size.width + 4, opsition.y + size.height / 2);
            });
            Cyan.attach(component, "mouseout", function ()
            {
                if (tip)
                    tip.hide();
            });
        }
    };
})();