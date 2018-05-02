Cyan.importJs("event.js");
Cyan.TargetList = function (items)
{
    if (!items)
        items = [];
    this.items = items;
};

Cyan.importAdapter("targetlist");

Cyan.TargetList.prototype.available = true;

Cyan.TargetList.prototype.showWith = function (component)
{
    component = Cyan.$(component);

    this.bindComponent = component;
    var position = Cyan.Elements.getPosition(component);
    var size = Cyan.Elements.getComponentSize(component);

    if (component.nodeName == "INPUT")
    {
        var ie = Cyan.navigator.isIE();
        var start1;
        if (ie)
            start1 = Cyan.Event.getSelectionStart(component);

        this.showAt(position.x, position.y + size.height);
        component.focus();

        if (ie)
        {
            var start2 = Cyan.Event.getSelectionStart(component);
            if (start1 != start2)
            {
                var rng = document.selection.createRange();
                rng.moveStart("character", start1 - start2);
                rng.select();
            }
        }
    }
    else
    {
        this.showAt(position.x, position.y + size.height);
    }
};

Cyan.TargetList.prototype.render = function (item)
{
    return Cyan.toString(item);
};

Cyan.TargetList.prototype.selectNext = function ()
{
    this.select(this.getSelectedIndex() + 1);
};

Cyan.TargetList.prototype.selectPrevious = function ()
{
    this.select(this.getSelectedIndex() - 1);
};

Cyan.TargetList.prototype.bind = function (component, loader, minChars)
{
    component = Cyan.$(component);

    if (!this.width)
        this.width = Cyan.Elements.getComponentSize(component).width;

    if (!this.width)
        this.width = 200;

    var list = this;
    if (loader)
    {
        if (!minChars)
            minChars = 1;

        var callback = function (items)
        {
            list.load(items);

            if (items.length)
            {
                list.downing = true;

                list.showWith(component);

                window.setTimeout(timeout, 100);
            }
            else if (list.isVisible())
            {
                list.hide();
            }
        };

        var timeout = function ()
        {
            list.downing = false;
        };

        Cyan.attach(component, "keyup", function (event)
        {
            if (list.available)
            {
                if (event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13 || event.keyCode == 32)
                    return;

                if (this.value && this.value.length >= minChars)
                {
                    if (list.oldValue != this.value)
                    {
                        loader(this.value, callback);
                        list.oldValue = this.value;
                    }
                    else if (list.items && list.items.length)
                    {
                        list.downing = true;
                        list.showWith(this);
                        window.setTimeout(timeout, 100);
                    }
                    else if (list.isVisible())
                    {
                        list.hide();
                    }
                }
                else if (list.isVisible())
                {
                    list.hide();
                }
            }
        });
    }
    else
    {
        Cyan.attach(component, "click", function ()
        {
            if (list.available)
                list.showWith(this);
        });
    }

    this.initComponent(component);
};

Cyan.TargetList.prototype.initComponent = function (component)
{
    var list = this;
    Cyan.attach(component, "keydown", function (event)
    {
        if (event.keyCode == 38)
        {
            if (list.isVisible() && list.bindComponent == this)
                list.selectPrevious();
        }
        else if (event.keyCode == 40)
        {
            if (list.isVisible() && list.bindComponent == this)
                list.selectNext();
        }
        else if (event.keyCode == 13 || event.keyCode == 32)
        {
            event.stop();

            if (list.isVisible() && list.bindComponent == this)
                list.makeSelect();
        }
    });
};

Cyan.TargetList.replaceCombox = function (select)
{
    Cyan.run(function ()
    {
        return select.clientWidth && select.clientHeight;
    }, function ()
    {
        Cyan.TargetList.replaceCombox0(select);
    });
};

Cyan.TargetList.replaceCombox0 = function (select)
{
    var div = document.createElement("DIV");
    var size = Cyan.Elements.getComponentSize(select);

    var hidden = document.createElement("INPUT");
    hidden.type = "hidden";
    hidden.name = select.name;
    select.parentNode.insertBefore(hidden, select);

    div.style.width = size.width + "px";
    div.className = "cyan-targetlist-div";

    var text = "";
    var value = "";
    var option = Cyan.searchFirst(select.options, "selected");
    if (!option && select.options.length)
        option = select.options[0];
    if (option)
    {
        text = option.text;
        value = option.value;
    }

    var textSpan = document.createElement("SPAN");
    div.appendChild(textSpan);

    var textNode = document.createTextNode(text);
    textSpan.appendChild(textNode);

    var iconSpan = document.createElement("SPAN");
    iconSpan.className = "cyan-targetlist-div-icon";
    div.appendChild(iconSpan);

    hidden.value = value;

    select.parentNode.insertBefore(div, select);

    select.parentNode.removeChild(select);

    var options = select.options;
    var n = options.length;
    var data = new Array(n);
    for (var i = 0; i < n; i++)
    {
        option = options[i];
        data[i] = {value: option.value, text: option.text};
    }
    var targetList = new Cyan.TargetList(data);
    targetList.width = size.width + 3;

    Cyan.attach(div, "click", function ()
    {
        targetList.showWith(this);
    });

    targetList.onselect = function (item)
    {
        hidden.value = item.value;
        textNode.nodeValue = item.text;
    };

    hidden.onvaluechange = function ()
    {
        for (var i = 0; i < data.length; i++)
        {
            var item = data[i];
            if (item.value == this.value)
            {
                textNode.nodeValue = item.text;
                break;
            }
        }
    };
    hidden.onBeforeRefresh = function ()
    {
    };
    hidden.onAfterRefresh = function (selectable)
    {
        delete data;

        var n = selectable.length;
        data = new Array(n);

        for (var i = 0; i < n; i++)
        {
            var item = selectable[i];
            var value = Cyan.valueOf(item);
            data[i] = {value: value, text: Cyan.toString(item)};
        }

        targetList.load(data);
    };
};
