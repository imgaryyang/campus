Cyan.importJs("combox.js");

Cyan.ItemSelector = Cyan.Class.extend(function (id, from, to, fromLegend, toLegend)
{
    this.id = id;
    this.from = from;
    this.to = to;
    this.fromLegend = fromLegend;
    this.toLegend = toLegend;
}, Cyan.Widget);

Cyan.ItemSelector.prototype.fromClass = "from";
Cyan.ItemSelector.prototype.fromLegendClass = "legend";
Cyan.ItemSelector.prototype.fromContentClass = "content";
Cyan.ItemSelector.prototype.toClass = "to";
Cyan.ItemSelector.prototype.toLegendClass = "legend";
Cyan.ItemSelector.prototype.buttonsClass = "buttons";

Cyan.ItemSelector.prototype.init = function (el)
{
    var selector = this;
    setTimeout(function ()
    {
        selector.init0(el);
    }, 50);
};

Cyan.ItemSelector.prototype.init0 = function (el)
{
    var div = Cyan.$(el);

    if (!this.from)
        this.from = [];

    if (!this.to)
        this.to = [];

    var formContainer = document.createElement("DIV");
    formContainer.className = this.fromClass;
    div.appendChild(formContainer);

    if (this.fromLegend)
    {
        var formLegendDiv = document.createElement("DIV");
        formLegendDiv.className = this.fromLegendClass;
        formContainer.appendChild(formLegendDiv);
        formLegendDiv.innerHTML = this.fromLegend;
    }

    var fromDiv = document.createElement("DIV");
    fromDiv.className = this.fromContentClass;
    formContainer.appendChild(fromDiv);

    var itemselector = this;
    var dblclick = function ()
    {
        itemselector.select();
    };
    if (Cyan.isArray(this.from))
    {
        if (this.to && this.to.length)
        {
            var to = this.to;
            Cyan.Array.removeCase(this.from, function ()
            {
                var id = Cyan.valueOf(this).toString();
                return Cyan.searchFirst(to, function ()
                {
                    return id == Cyan.valueOf(this).toString();
                });
            });
        }
        this.from = this.createListComponent("from$" + this.name, this.from, fromDiv, dblclick);
    }
    else
    {
        this.from.ondblclick = dblclick;
        this.from.autoRender = true;
        this.from.init(fromDiv);
    }

    var buttonsDiv = document.createElement("DIV");
    buttonsDiv.className = this.buttonsClass;
    div.appendChild(buttonsDiv);
    this.createButtons(buttonsDiv);

    var toContainer = document.createElement("DIV");
    toContainer.className = this.toClass;
    div.appendChild(toContainer);

    if (this.toLegend)
    {
        var toLegendDiv = document.createElement("DIV");
        toLegendDiv.className = this.toLegendClass;
        toContainer.appendChild(toLegendDiv);
        toLegendDiv.innerHTML = this.toLegend;
    }

    var toDiv = document.createElement("DIV");
    toContainer.appendChild(toDiv);
    this.to = this.createToComponent(toDiv);
};

Cyan.ItemSelector.prototype.createButtons = function (div)
{
    this.createButton(div, ">", "select");
    if (this.from.allItems || this.from.eachItem)
        this.createButton(div, "=>", "selectAll");
    this.createButton(div, "<", "remove");
    this.createButton(div, "<=", "removeAll");
};

Cyan.ItemSelector.prototype.createButton = function (div, text, action)
{
    var button = Cyan.Elements.createButton(text);
    div.appendChild(button);
    var itemSelector = this;
    Cyan.attach(button, "click", function ()
    {
        itemSelector[action].apply(itemSelector);
    });
};

Cyan.ItemSelector.prototype.createToComponent = function (div)
{
    var itemselector = this;
    var to = this.createListComponent("to$" + this.name, this.to, div, function ()
    {
        itemselector.remove();
    });

    var div1;
    if (this.name)
    {
        //display="none"之后在ie下不能多选
        div1 = document.createElement("DIV");
        div1.style.position = "absolute";
        div1.style.display = "none";
        div1.style.width = "0";
        div1.style.height = "0";
        div1.innerHTML = "<select name='" + this.name + "' id='" + this.name + "' size='3' multiple='true'></select>";
    }

    if (div1)
    {
        div.appendChild(div1);
        var select1 = Cyan.$(this.name);
        select1.style.visibility = "hidden";
        var n = this.to.length;
        for (var i = 0; i < n; i++)
        {
            var option = this.to[i];
            option = new Option("", Cyan.valueOf(option).toString());
            option.selected = true;
            if (select1)
                select1.options[i] = option;
        }
        this.valueComponent = Cyan.$$(select1);
        this.valueComponent.selectAll(true);
    }

    return to;
};

Cyan.ItemSelector.prototype.createListComponent = function (name, data, div, dblclick, click)
{
    div.innerHTML = "<select name='" + name + "' id='" + name + "' size='3' multiple='true'></select>";

    var select = div.childNodes[0];

    var n = data.length;
    for (var i = 0; i < n; i++)
    {
        var option = data[i];
        select.options[i] = new Option(Cyan.toString(option), Cyan.valueOf(option).toString());
    }

    if (dblclick)
        select.ondblclick = dblclick;

    if (click)
        select.onclick = click;

    return Cyan.$$(select);
};

Cyan.ItemSelector.prototype.select = function ()
{
    var item, value, text;
    var items = this.from.selectedItems();
    for (var i = 0; i < items.length; i++)
    {
        item = items[i];
        value = Cyan.valueOf(item).toString();
        if (value)
        {
            text = Cyan.toString(item);
            this.to.addItem(value, text);
            if (this.valueComponent)
                this.valueComponent.addItem(value, text);
        }
    }
    if (this.from.clearSelected)
        this.from.clearSelected();
    if (this.valueComponent)
        this.valueComponent.selectAll(true);
};

Cyan.ItemSelector.prototype.selectAll = function ()
{
    var selector = this;
    var add = function ()
    {
        var value = Cyan.valueOf(this).toString();
        if (value)
        {
            var text = Cyan.toString(this);
            selector.to.addItem(value, text);
            if (selector.valueComponent)
                selector.valueComponent.addItem(value, text);
        }
    };

    var f = function ()
    {
        if (selector.from.clearAll)
            selector.from.clearAll();

        if (selector.valueComponent)
            selector.valueComponent.selectAll(true);
    };

    if (this.from.allItems)
    {
        Cyan.each(this.from.allItems(), add);
        f();
    }
    else if (this.from.eachItem)
    {
        this.from.eachItem(add, f);
    }
};

Cyan.ItemSelector.prototype.remove = function ()
{
    var i;
    if (this.from.addItem)
    {
        var items = this.to.selectedItems();
        for (i = 0; i < items.length; i++)
        {
            var item = items[i];
            var value = Cyan.valueOf(item).toString();
            this.from.addItem(value, Cyan.toString(item));
            if (this.valueComponent)
                this.valueComponent.removeItem(value);
        }
    }
    else if (this.valueComponent)
    {
        var values = this.to.selectedValues();
        for (i = 0; i < values.length; i++)
            this.valueComponent.removeItem(values[i]);
    }

    this.to.clearSelected();
    if (this.valueComponent)
        this.valueComponent.selectAll(true);
};

Cyan.ItemSelector.prototype.removeAll = function ()
{
    if (this.from.addItem)
    {
        var items = this.to.allItems();
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            var value = Cyan.valueOf(item).toString();
            this.from.addItem(value, Cyan.toString(item));
        }
    }

    this.to.clearAll();
    if (this.valueComponent)
        this.valueComponent.clearAll();
};

Cyan.ItemSelector.prototype.addItem = function (value, text)
{
    this.to.addItem(value, text);
    if (this.valueComponent)
    {
        this.valueComponent.addItem(value, text);
        this.valueComponent.selectAll(true);
    }
    if (this.from.removeItem)
        this.from.removeItem(value);
};

Cyan.ItemSelector.prototype.up = function ()
{
    this.to.up();
    this.refreshValues();
};

Cyan.ItemSelector.prototype.down = function ()
{
    this.to.down();
    this.refreshValues();
};

Cyan.ItemSelector.prototype.refreshValues = function ()
{
    if (this.valueComponent)
    {
        this.valueComponent.clearAll();
        var items = this.to.allItems();
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            var value = Cyan.valueOf(item).toString();
            var text = Cyan.toString(item);
            this.valueComponent.addItem(value, text);
        }
        this.valueComponent.selectAll(true);
    }
};


Cyan.ItemSelector.prototype.selectedValues = function ()
{
    return this.to.allValues();
};

Cyan.ItemSelector.prototype.selectedItems = function ()
{
    return this.to.allItems();
};


/**
 * 引入itemselector的适配器
 */
Cyan.importAdapter("itemselector");