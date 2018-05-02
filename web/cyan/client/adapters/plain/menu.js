Cyan.Plain.importCss("menu");
Cyan.importJs("render.js");

Cyan.Menu.zIndex = 7000;

Cyan.Menu.prototype.init = function ()
{
    this.zIndex = Cyan.Menu.zIndex++;

    if (this.items instanceof Cyan.Widget)
    {
        var menu = this;
        var widget = this.items;
        widget.autoRender = false;
        widget.onselect = function ()
        {
            if (!widget.loading)
            {
                menu.hide();
                if (menu.onselect)
                    menu.onselect(widget);

                if (widget.clearSelections)
                    widget.clearSelections();
            }
        };
        widget.oncheck = function ()
        {
            if (menu.onselect)
                menu.onselect(widget);

            if (widget.clearSelections)
                widget.clearSelections();
        };
    }
};

Cyan.Menu.prototype.create = function ()
{
    if (!this.created)
    {
        var div = document.createElement("div");
        div.className = "cyan-menu";
        div.style.display = "none";
        div.style.zIndex = this.zIndex;
        document.body.appendChild(div);

        this.el = Cyan.$$(div);

        var items = this.items;
        if (Cyan.isArray(items))
        {
            var itemsDiv = document.createElement("div");
            itemsDiv.className = "cyan-menu-items";
            div.appendChild(itemsDiv);
            var n = items.length;
            for (var i = 0; i < n; i++)
            {
                var item = items[i];
                if (item == "_")
                    this.createSeparator(itemsDiv);
                else
                    this.createMenuItem(item, itemsDiv);
            }
        }
        else if (items instanceof Cyan.Widget)
        {
            var widget = items;
            if ((widget.search || widget.bindSearch) && widget.searchable)
            {
                var searchDiv = document.createElement("div");
                searchDiv.className = "cyan-widget-search";
                div.appendChild(searchDiv);

                var input = document.createElement("INPUT");
                searchDiv.appendChild(input);

                Cyan.attach(div, "mousemove", function ()
                {
                    try
                    {
                        input.focus();
                    }
                    catch (e)
                    {
                    }
                });

                if (widget.bindSearch)
                {
                    widget.bindSearch(input);
                }
                else
                {
                    var lastSearchText;
                    Cyan.attach(input, "keyup", function ()
                    {
                        var text = this.value;
                        var input = this;
                        if (text == "")
                        {
                            widget.search(null);
                        }
                        else
                        {
                            window.setTimeout(function ()
                            {
                                if (text == input.value)
                                {
                                    if (lastSearchText == text)
                                        return;
                                    lastSearchText = text;

                                    widget.search(text);
                                }
                            }, 300);
                        }
                    });
                }
            }

            var widgetDiv = document.createElement("div");
            widgetDiv.className = "cyan-widget";
            div.appendChild(widgetDiv);

            var width = widget.width || this.width;
            var height = widget.height || this.height;

            if (!width && (Cyan.Tree && widget instanceof Cyan.Tree ||
                    Cyan.List && widget instanceof Cyan.List))
            {
                width = 200;
            }

            if (!height && (Cyan.Tree && widget instanceof Cyan.Tree ||
                    Cyan.List && widget instanceof Cyan.List))
            {
                height = 240;
            }

            if (width)
                widgetDiv.style.width = width + "px";
            if (height)
                widgetDiv.style.height = height + "px";

            if (widget.init)
                widget.init(widgetDiv);
            if (widget.render && !widget.autoRender)
                widget.render();
        }

        var menu = this;
        Cyan.attach(document.body, "mousedown", function (event)
        {
            if (!event.isOn(div))
                menu.hide();
        });

        this.created = true;
    }
};

Cyan.Menu.prototype.createMenuItem = function (item, itemsDiv)
{
    var div = document.createElement("div");
    div.className = "cyan-menu-item";

    div.innerHTML = Cyan.escapeHtml(item.text);
    //if (item.icon)
    //    div.style.backgroundImage = "url(\"" + item.icon + "\")";

    var action;
    if (item.action)
        action = Cyan.toFunction(item.action);

    var menu = this;
    div.onclick = function ()
    {
        menu.hide();
        if (action)
            action.apply(item);
    };

    itemsDiv.appendChild(div);
};

Cyan.Menu.prototype.createSeparator = function (itemsDiv)
{
    var div = document.createElement("div");
    div.className = "cyan-menu-separator";
    itemsDiv.appendChild(div);
};

Cyan.Menu.prototype.getElement = function ()
{
    return this.el[0];
};

Cyan.Menu.prototype.showAt = function (x, y)
{
    this.create();

    this.el.css("left", x + "px");
    this.el.css("top", y + "px");
    this.el.show();

    var bodyWidth = window.document.documentElement.clientWidth;
    var width = this.el[0].clientWidth;
    if (width + x > bodyWidth - 10)
    {
        x = bodyWidth - width - 10;
        this.el.css("left", x + "px");
    }
};

Cyan.Menu.prototype.hide = function ()
{
    if (this.created)
        this.el.hide();
};