Cyan.Menu = function (items)
{
    this.items = items;
    if (this.init)
        this.init();
};

Cyan.Menu.prototype.showWith = function (component)
{
    var position = Cyan.Elements.getPosition(component);
    var size = Cyan.Elements.getComponentSize(component);
    this.showAt(position.x, position.y + size.height + 2);
};

Cyan.Menu.getAction = function (item)
{
    var action = Cyan.toFunction(item.action);

    return action == null ? null : function ()
    {
        action.apply(item);
    };
};

Cyan.Menu.bindWidget = function (component, widget)
{
    component = Cyan.$(component);
    Cyan.run(function ()
    {
        return component.clientWidth;
    }, function ()
    {
        var menu = new Cyan.Menu(widget);
        menu.width = component.clientWidth - 4;
        menu.height = 200;
        Cyan.attach(component, "click", function ()
        {
            var position = Cyan.Elements.getPosition(component);
            var size = Cyan.Elements.getComponentSize(component);
            menu.showAt(position.x, position.y + size.height + 4);
        });
    });
};

Cyan.Menu.prototype.getWidth = function ()
{
    var element = this.getElement();
    return element ? element.clientWidth : 0;
};

Cyan.Menu.prototype.getHeight = function ()
{
    var element = this.getElement();
    return element ? element.clientHeight : 0;
};

Cyan.importAdapter("menu");