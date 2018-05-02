Cyan.Options = function (options)
{
    if (options instanceof Cyan.Elements)
        return options.options();
    if (Cyan.isString(options))
        options = Cyan.$(options);
    if (options.nodeName == "SELECT")
        options = options.options;
    else if (options.select && options.select.options)
        options = options.select.options;
    if (!options.values)
        Cyan.clone(Cyan.Options.prototype, options);


    return options;
};
Cyan.Options.prototype.values = function ()
{
    return Cyan.get(this, "value");
};
Cyan.Options.prototype.selecteds = function ()
{
    return Cyan.search(this, "selected");
};
Cyan.Options.prototype.selected = function ()
{
    return this.selecteds[0];
};
Cyan.Options.prototype.selectedIndexes = function ()
{
    return Cyan.get(Cyan.search(this, "selected"), "index");
};
Cyan.Options.prototype.getSelectedIndex = function ()
{
    var selected = this.selected();
    return selected ? selected.index : -1;
};
Cyan.Options.prototype.selectedValues = function ()
{
    return Cyan.get(Cyan.search(this, "selected"), "value");
};
Cyan.Options.prototype.allValues = function ()
{
    return Cyan.get(this, "value");
};
Cyan.Options.prototype.select = function (value)
{
    if (!value)
        value = "";
    Cyan.each(this, function ()
    {
        this.selected = value instanceof Array ? Cyan.contains(value, this.value) : this.value == value.toString();
    });
};
Cyan.Options.prototype.selectAll = function (selected)
{
    Cyan.each(this, "this.selected=" + (typeof selected == "undefined" ? Cyan.any(this, "!selected") : !!selected));
};
Cyan.Options.prototype.addOption = function (value, text, cssText)
{
    var option0;
    if ((value.value || value.value == "") && (value.text || value.text == ""))
    {
        option0 = value;
        value = option0.value;
        text = option0.text;
    }
    else
    {
        value = value ? value.toString() : "";
        text = text ? text.toString() : "";
    }
    var option = Cyan.searchFirst(this, function ()
    {
        return this.value == value;
    });
    if (option)
        option.text = text;
    else
    {
        option = option0 || new Option(text, value);
        if (this.onoptionclick)
            option.onclick = this.onoptionclick;
        this[this.length] = option;
    }
    if (cssText)
        option.style.cssText = cssText;
    return option;
};
Cyan.Options.prototype.set = function (value, text, index)
{
    var option;
    if (Cyan.isNumber(index))
    {
        option = this[index];
        if (option)
        {
            option.value = value;
            option.text = text;
        }
    }
    else
    {
        option = Cyan.searchFirst(this, function ()
        {
            return this.value == value;
        });
        if (option)
            option.text = text;
    }
};
Cyan.Options.prototype.remove = function (value)
{
    if (Cyan.isNumber(value))
    {
        this[value] = null;
    }
    else
    {
        for (var i = 0; i < this.length; i++)
        {
            if (this[i].value == value)
            {
                this[i] = null;
                break;
            }
        }
    }
};
Cyan.Options.prototype.clear = function ()
{
    this.length = 0;
};
Cyan.Options.prototype.clearSelected = function ()
{
    for (var i = 0; i < this.length; i++)
    {
        if (this[i].selected)
        {
            this[i] = null;
            i--;
        }
    }
};
Cyan.Options.prototype.refreshOptions = function (options)
{
    this.length = 0;
    for (var i = 0; options.length; i++)
    {
        var option = new Option(options[i].text, options[i].value);
        option.selected = options[i].selected;
        if (this.onoptionclick)
            option.onclick = this.onoptionclick;
        this[i] = option;
    }
};
Cyan.Options.prototype.indexOf = function (value)
{
    if (value.value || value.value == "")
        value = value.value;
    for (var i = 0; i < this.length; i++)
        if (this[i].value == value)
            return i;
    return -1;
};
Cyan.Options.prototype.copySelectedTo = function (options)
{
    options = Cyan.Options(options);
    Cyan.each(this, function ()
    {
        if (this.selected)
            options.addOption(this.value, this.text).selected = true;
    });
};
Cyan.Options.prototype.copyAllTo = function (options)
{
    options = Cyan.Options(options);
    Cyan.each(this, function ()
    {
        options.addOption(this.value, this.text).selected = true;
    });
};
Cyan.Options.prototype.moveSelectedTo = function (options)
{
    options = Cyan.Options(options);
    for (var i = 0; i < this.length; i++)
    {
        var option = this[i];
        if (option.selected)
        {
            options.addOption(option.value, option.text).selected = true;
            this[i--] = null;
        }
    }
};
Cyan.Options.prototype.moveAllTo = function (options)
{
    options = Cyan.Options(options);
    while (this.length > 0)
    {
        var option = this[0];
        options.addOption(option.value, option.text).selected = true;
        this[0] = null;
    }
};
Cyan.Options.prototype.up = function ()
{
    var options = this;
    Cyan.each(this, function (index)
    {
        if (this.selected && index)
        {
            var option2 = options[index - 1];
            if (!option2.selected)
                Cyan.Options.exchange(this, option2);
        }
    });
};
Cyan.Options.prototype.down = function ()
{
    for (var i = this.length - 2; i >= 0; i--)
    {
        var option = this[i];
        if (option.selected)
        {
            var option2 = this[i + 1];
            if (!option2.selected)
                Cyan.Options.exchange(option, option2);
        }
    }
};
Cyan.Options.prototype.exchange = function (i, j)
{
    if (i >= 0 && i < this.length && j >= 0 && j < this.length && i != j)
        Cyan.Options.exchange(this[i], this[j]);
};
Cyan.Options.prototype.sort = function (comparator)
{
    Cyan.sort(this, comparator || Cyan.Options.compare, this.exchange);
};
Cyan.Options.prototype.moveTo = function (i, j)
{
    if (!Cyan.isNumber(i))
        i = this.indexOf(i);
    if (i < 0)
        i = this.selectedIndex;
    if (i < 0 || i >= this.length)
        return;
    if (!Cyan.isNumber(j))
        j = this.indexOf(j);
    if (j < 0 || j >= this.length || i == j)
        return;

    var k;
    if (i > j)
        for (k = j; k < i; k++)
            this.exchange(k, i);
    else
        for (k = j; k > i; k--)
            this.exchange(k, i);
};
Cyan.Options.compare = function (option1, option2)
{
    return option1.value > option2.value ? 1 : option1.value == option2.value ? 0 : -1;
};
Cyan.Options.exchange = function (option1, option2)
{
    var value = option1.value, text = option1.text, selected = option1.selected;
    option1.value = option2.value;
    option1.text = option2.text;
    option1.selected = option2.selected;
    option2.value = value;
    option2.text = text;
    option2.selected = selected;
};
Cyan.Elements.prototype.options = function ()
{
    if (!this.options0)
        this.options0 = this.first && this.first.options ? Cyan.Options(this.first.options) : null;
    return this.options0;
};
Cyan.Elements.prototype.up = function ()
{
    if (this.first.up instanceof Function)
        this.first.up();
    else if (this.first.options)
        this.options().up();
};
Cyan.Elements.prototype.down = function ()
{
    if (this.first.down instanceof Function)
        this.first.down();
    else if (this.first.options)
        this.options().down();
};
Cyan.Elements.prototype.allItems = function ()
{
    if (this.first.allItems instanceof Function)
        return this.first.allItems();
    else if (this.first.options)
        return this.options();
};
Cyan.Elements.prototype.selectedItems = function ()
{
    if (this.first.selectedItems instanceof Function)
        return this.first.selectedItems();
    else if (this.first.options)
        return this.options().selecteds();
};
Cyan.Elements.prototype.selectedItem = function ()
{
    if (this.first.selectedItem instanceof Function)
        return this.first.selectedItem();
    else if (this.first.options)
        return this.options().selected();
};
Cyan.Elements.prototype.selectedIndexes = function ()
{
    if (this.first.selectedIndexes instanceof Function)
        return this.first.selectedIndexes();
    else if (this.first.options)
        return this.options().selectedIndexes();
};
Cyan.Elements.prototype.selectedIndex = function ()
{
    if (this.first.selectedIndex instanceof Function)
        return this.first.selectedIndex();
    else if (this.first.options)
        return this.options().getSelectedIndex();
};
Cyan.Elements.prototype.selectedValues = function ()
{
    if (this.first.selectedValues instanceof Function)
        return this.first.selectedValues();
    else if (this.first.options)
        return this.options().selectedValues();
};
Cyan.Elements.prototype.allValues = function ()
{
    if (this.first.allValues instanceof Function)
        return this.first.allValues();
    else if (this.first.options)
        return this.options().allValues();
};
Cyan.Elements.prototype.selectAll = function (selected)
{
    if (this.first.selectAll instanceof Function)
        return this.first.selectAll(selected);
    else if (this.first.options)
        return this.options().selectAll(selected);
};
Cyan.Elements.prototype.addItem = function (value, text)
{
    if (this.first.addItem instanceof Function)
        return this.first.addItem(value, text);
    else if (this.first.options)
        return this.options().addOption(value, text);
};
Cyan.Elements.prototype.removeItem = function (value)
{
    if (this.first.removeItem instanceof Function)
        return this.first.removeItem(value);
    else if (this.first.options)
        return this.options().remove(value.toString());
};
Cyan.Elements.prototype.clearSelected = function ()
{
    if (this.first.clearSelected instanceof Function)
        return this.first.clearSelected();
    else if (this.first.options)
        return this.options().clearSelected();
};
Cyan.Elements.prototype.clearAll = function ()
{
    if (this.first.clearAll instanceof Function)
        return this.first.clearAll();
    else if (this.first.options)
        return this.options().clear();
};
Cyan.Elements.prototype.refresh = function (selectable)
{
    if (selectable)
    {
        if (this.first.refresh instanceof Function)
            return this.first.refresh(selectable);
        else if (this.first.options)
        {
            var component = this.first;
            var oldValue = component.value;

            var options = component.options;
            options.length = 0;
            for (var i = 0; i < selectable.length; i++)
            {
                var option = selectable[i];
                var item = new Option(Cyan.toString(option), Cyan.valueOf(option));
                if (option.value == oldValue)
                    item.selected = true;
                options[options.length] = item;
            }
        }
    }
};
Cyan.Combox = {};
Cyan.Combox.makeEditable = function (combox)
{
    var w0 = navigator.isIE() ? 18 : 19, width = combox.style.width, height;
    width = width ? parseInt(width.substring(0, width.length - 2)) : 120;
    if (navigator.isIE())
        height = 21 + "px";
    else
        height = combox.style.height;
    var div0 = document.createElement("SPAN");
    var div = document.createElement("SPAN");
    div0.style.position = combox.style.position;
    div0.style.width = width + "px";
    div0.style.height = combox.style.height ? combox.style.height : height;
    div0.style.left = combox.style.left;
    div0.style.top = combox.style.top;
    div0.style.marginLeft = combox.style.marginLeft;
    div0.style.marginTop = combox.style.marginTop;
    div0.style.marginRight = combox.style.marginRight;
    div0.style.marginBottom = combox.style.marginBottom;
    div.style.width = width + "px";
    div.style.height = combox.style.height ? combox.style.height : height;
    div.style.margin = "0";
    div.style.marginLeft = div.style.marginRight = div.style.marginTop = div.style.marginBottom = "0";
    if (div0.style.position != "absolute")
    {
        div.style.position = "relative";
        if (Cyan.navigator.isIE())
            div.style.top = combox.style.height ?
                    ((Cyan.toInt(combox.style.height) - (Cyan.Elements.isElementBefore(combox) ? 17 : 19)) +
                    "px") : ( Cyan.Elements.isElementBefore(combox) ? 4 : 2);
        else if (Cyan.navigator.isFF())
            div.style.top = (Cyan.Elements.isElementBefore(combox) ? -15 : -10) + "px";
        else if (Cyan.navigator.isOpera())
        {
            div0.style.marginRight =
                    ((combox.style.marginRight ? combox.style.marginRight.toInt() : 0) + width + 5) + "px";
            div.style.top = Cyan.Elements.isElementBefore(combox) ? -1 : 4;
        }
    }
    div0.appendChild(div);
    var span = document.createElement("SPAN");
    span.style.marginLeft = width - w0;
    span.style.width = w0;
    span.style.overflow = "hidden";
    span.style.position = "absolute";
    span.style.left = "0";
    span.style.top = "0";
    div.appendChild(span);
    var select = document.createElement("SELECT");
    select.style.cssText = combox.style.cssText;
    select.style.position = "relative";
    select.style.left = select.style.top = "0";
    select.style.width = width;
    select.style.marginLeft = w0 - width;
    select.style.marginTop = select.style.marginBottom = select.style.marginRight = "0";
    if (Cyan.navigator.isFF() && select.style.height)
        select.style.height = (select.style.height.toInt() - 2) + "px";
    else if (Cyan.navigator.isOpera() && !select.style.height)
        select.style.height = "22px";
    span.appendChild(select);
    var input = document.createElement("INPUT"), name = combox.name, id = combox.id;
    if (!id)
        id = name;
    else if (!name)
        name = id;
    input.name = name;
    input.id = id;
    input.style.cssText = combox.style.cssText;
    input.style.width = width - w0;
    input.style.position = "absolute";
    input.style.left = "0";
    input.style.top = "0";
    input.style.height = height;
    input.style.marginLeft = input.style.marginTop = input.style.marginBottom = input.style.marginRight = "0";
    div.appendChild(input);
    var attributes = combox.attributes;
    var valueExists = false;
    for (var j = 0; j < attributes.length; j++)
    {
        var attribute = attributes[j];
        name = attribute.name;
        var lowerName = name.toLowerCase();
        if (lowerName != "style" && lowerName != "name" && lowerName != "type")
        {
            if (lowerName == "value")
            {
                if (!Cyan.navigator.isOpera())
                {
                    input.value = attribute.value;
                    valueExists = true;
                }
            }
            else
            {
                try
                {
                    if (combox[name] != null)
                        input[name] = combox[name];
                    continue;
                }
                catch (e)
                {
                }
                var value = attribute.value;
                if (value != null && value != "" && value != "null")
                    input.setAttribute(name, value);
            }
        }
    }
    if (!valueExists)
        input.value = combox.value;
    Cyan.each(combox.options, function ()
    {
        var newOption = new Option(this.text, this.value);
        newOption.selected = this.selected;
        newOption.style.cssText = this.style.cssText;
        if (!Cyan.navigator.isIE() && !navigator.isSafari())
            newOption.onclick = Cyan.Combox.editableComboxOnClick;
        select.options[select.options.length] = newOption;
    });
    input.select$ = select;
    select.input = input;
    if (navigator.isIE() || navigator.isSafari())
        Cyan.attach(select, "change", function ()
        {
            if (!input.disabled)
                input.value = select.value;
        });
    else
        select.options.onoptionclick = Cyan.Combox.editableComboxOnClick;
    Cyan.attach(input, "change", function ()
    {
        this.select$.value = this.value;
    });
    combox.parentNode.replaceChild(div0, combox);
    if (div0.style.position != "absolute" && (navigator.isFF() || navigator.isSafari()))
    {
        var input0 = document.createElement("INPUT");
        input0.style.width = width + "px";
        input0.style.height = "0";
        input0.style.marginRight = div0.style.marginRight;
        input0.selectable = "false";
        div0.parentNode.insertBefore(input0, div0.nextSibling);
    }
    select.value = input.value;
    select.selectable = "false";
    input.keyEventRendered$ = false;
    if (input.form && Cyan.Event)
        Cyan.Event.renderField(input);
};
Cyan.Combox.editableComboxOnClick = function ()
{
    if (!this.parentNode.input.disabled)
        this.parentNode.input.value = this.value;
};
Cyan.Combox.isMakeEditable = function (select)
{
    return select.getAttribute("editable") == "true";
};

Cyan.Combox.makeAllEditable = function ()
{
    Cyan.each(Cyan.search(document.getElementsByTagName("SELECT"), function ()
    {
        return Cyan.Combox.isMakeEditable(this);
    }), function ()
    {
        Cyan.Combox.makeEditable(this);
    });
};

Cyan.onload(function ()
{
    if (window.$ && $.withCyan)
    {
        $.copyOptions = function (from, to, all)
        {
            new Cyan.Options(from)[all ? "copyAllTo" : "copySelectedTo"](to);
        };

        $.moveOptions = function (from, to, all)
        {
            new Cyan.Options(from)[all ? "moveAllTo" : "moveSelectedTo"](to);
        };
    }
});