Cyan.Plain.importCss("list");
Cyan.importJs("render.js");
Cyan.importJs("event.js");

Cyan.List.prototype.init = function (el)
{
    this.init0(el);
};

Cyan.List.prototype.init0 = function (el)
{
    this.el = Cyan.$(el);

    this.listItems = [];
    if (this.items)
    {
        this.items.forEach(function (item)
        {
            this.listItems.push(this.toListItem(item));
        }, this);
    }

    var list = this;
    var getItemDiv = function (div)
    {
        while (div.className != "cyan-list-item" && div.className != "cyan-list-item cyan-list-item-selected")
        {
            div = div.parentNode;
        }

        return div;
    };

    var checking = false;
    this.itemClick = function (event)
    {
        if (checking)
        {
            checking = false;
            return;
        }

        var div = getItemDiv(this);
        var item = list.getItemById(div.itemId);
        list.click(item.value);
        list.selectItem(item);
    };

    this.itemDblClick = function (event)
    {
        var div = getItemDiv(this);
        var item = list.getItemById(div.itemId);

        list.dblclick(item.value);
    };

    this.itemCheck = function (event)
    {
        var div = getItemDiv(this);
        var item = list.getItemById(div.itemId);

        list.check(item.value);

        checking = true;
    };

    if (this.autoRender)
        this.render();

    this.loaded = true;
};

Cyan.List.prototype.render = function ()
{
    var listDiv = document.createElement("div");
    listDiv.className = "cyan-list";
    this.el.appendChild(listDiv);

    this.listItems.forEach(function (item)
    {
        item.render(listDiv);
    }, this);
};

Cyan.List.prototype.toListItem = function (item)
{
    var listItem = new Cyan.List.ListItem(item, this);
    listItem.value = item;
    listItem.text = item.text;
    listItem.tip = item.tip;

    return listItem;
};

Cyan.List.prototype.selectItem = function (item)
{
    var id = item.id;
    var itemEl;
    if (item.el)
        itemEl = Cyan.$(item.el);

    Cyan.$$(this.el).$(".cyan-list-item-selected").each(function ()
    {
        if (this != itemEl)
            this.className = "cyan-list-item";
    });
    this.selectedId = id;

    if (item.el)
    {
        if (itemEl && itemEl.className == "cyan-list-item")
            itemEl.className = "cyan-list-item cyan-list-item-selected";
    }

    this.select(item.value);
};

Cyan.List.prototype.getItemById = function (id)
{
    var items = this.listItems;
    var n = items.length;
    for (var i = 0; i < n; i++)
    {
        if (items[i].id == id)
            return items[i];
    }

    return null;
};

Cyan.List.prototype.getItems = function ()
{
    return Cyan.clone(this.listItems);
};

Cyan.List.prototype.clear = function ()
{
    this.listItems = [];
    Cyan.$$(this.el).$(".cyan-list").html("");
};

Cyan.List.prototype.addItem = function (item)
{
    this.insertItemBefore(item, null);
};

Cyan.List.prototype.insertItemBefore = function (item, nextItemId)
{
    var nextItem, nextItemIndex;
    if (nextItemId)
    {
        var n = this.listItems.length;
        for (var i = 0; i < n; i++)
        {
            var item1 = this.listItems[i];
            if (item1.id == nextItemId)
            {
                nextItem = item1;
                nextItemIndex = i;
                break;
            }
        }
    }

    var listItem = this.toListItem(item);

    if (nextItem)
    {
        Cyan.Array.insert(this.listItems, nextItemIndex, listItem);
    }
    else
    {
        this.listItems.push(listItem);
    }

    if (this.loaded)
        listItem.render(Cyan.$$(this.el).$(".cyan-list")[0], nextItem ? Cyan.$(nextItem.el) : null);

    return listItem;
};


Cyan.List.prototype.getSelectedId = function ()
{
    return this.selectedId;
};

Cyan.List.prototype.getSelectedItem = function ()
{
    return this.selectedId == null ? null : this.getItemById(this.selectedId);
};

Cyan.List.prototype.clearSelections = function ()
{
    this.selectedId = null;
    Cyan.$$(this.el).$(".cyan-list-item-selected").each(function ()
    {
        this.className = "cyan-list-item";
    });
};

Cyan.List.prototype.filter = function (filter)
{
    this.listItems.forEach(function (item)
    {
        if (item.el)
            Cyan.$(item.el).style.display = !filter || filter(item) ? "" : "none";
    });
};

Cyan.List.ListItem.prototype.getValue = function ()
{
    return this.value;
};

Cyan.List.ListItem.prototype.getText = function ()
{
    return this.text;
};

Cyan.List.ListItem.prototype.setText = function (text)
{
    this.text = text;
    if (this.el)
        Cyan.$$$(this.el).$(".cyan-list-item-text").html(text);
};

Cyan.List.ListItem.prototype.render = function (parentDiv, nextItemDiv)
{
    var id = this.el || Cyan.generateId("cyan_list_item");
    if (!this.el)
        this.el = id;

    var div = document.createElement("div");
    div.className = "cyan-list-item";
    div.id = id;
    div.itemId = this.id;
    if (this.tip)
        div.title = this.tip;

    Cyan.Elements.disableSelection(div);

    if (nextItemDiv)
        parentDiv.insertBefore(div, nextItemDiv);
    else
        parentDiv.appendChild(div);

    div.onclick = this.list.itemClick;
    div.ondblclick = this.list.itemDblClick;

    var iconDiv = document.createElement("div");
    div.appendChild(iconDiv);
    var icon = this.getIcon();
    var iconClass = this.getIconCls();
    if (iconClass)
        iconClass = iconClass + " cyan-list-item-icon";
    else
        iconClass = "cyan-list-item-icon";
    iconDiv.className = iconClass;
    if (icon)
    {
        var img = document.createElement("img");
        img.src = icon;
        iconDiv.appendChild(img);
    }
    else
    {
        var iconSpan = document.createElement("span");
        iconDiv.appendChild(iconSpan);
        iconSpan.className = "cyan-list-item-icon-default";
    }

    var checkboxName = this.list.checkboxName;
    if (checkboxName)
    {
        var checked = this.list.isChecked(this.value);
        if (checked != null)
        {
            var checkBoxDiv = document.createElement("div");
            checkBoxDiv.className = "cyan-list-item-checkbox";
            div.appendChild(checkBoxDiv);
            var checkbox = document.createElement("input");
            checkbox.type = this.list.checkMode || "checkbox";
            checkbox.name = checkboxName;
            checkbox.value = this.id;
            checkbox.checked = checked;
            checkBoxDiv.appendChild(checkbox);
            checkbox.onclick = this.list.itemCheck;
        }
    }

    var textDiv = document.createElement("div");
    var textClass = this.getTextCls();
    if (textClass)
        textClass = textClass + " cyan-list-item-text";
    else
        textClass = "cyan-list-item-text";
    textDiv.className = textClass;
    var textStyle = this.getTextStyle();
    if (textStyle)
        textDiv.style.cssText = textStyle;
    div.appendChild(textDiv);

    var href = this.getHref();
    if (href)
    {
        var target = this.getHrefTarget();
        var s = "<a href=\"" + href + "\"";
        if (target)
            s += " target=\"" + target + "\"";
        s += ">" + this.text + "</a>";
        textDiv.innerHTML = s;
    }
    else
    {
        textDiv.innerHTML = this.text;
    }
};

Cyan.List.ListItem.prototype.updateEl = function ()
{
    if (this.el)
    {
        var el = Cyan.$$$(this.el);
        el[0].title = this.tip || "";

        var icon = this.getIcon();
        var iconClass = this.getIconCls();
        if (iconClass)
            iconClass = iconClass + " cyan-list-item-icon";
        else
            iconClass = "cyan-list-item-icon";
        var iconDiv = el.$(".cyan-list-item-icon")[0];
        iconDiv.className = iconClass;

        if (icon)
        {
            el.$(".cyan-list-item-icon span").hide();
            var img = el.$(".cyan-list-item-icon img")[0];
            if (img)
            {
                img.style.display = "";
            }
            else
            {
                img = document.createElement("img");
                iconDiv.appendChild(img);
            }

            img.src = icon;
        }
        else
        {
            el.$(".cyan-list-item-icon img").hide();
            var iconSpan = el.$(".cyan-list-item-icon span")[0];
            if (iconSpan)
            {
                iconSpan.style.display = "";
            }
            else
            {
                iconSpan = document.createElement("span");
                iconDiv.appendChild(iconSpan);
                iconSpan.className = "cyan-list-item-icon-default";
            }
        }

        var checkboxName = this.list.checkboxName;
        if (checkboxName)
        {
            var checked = this.list.isChecked(this.value);
            var checkBoxDiv = el.$(".cyan-list-item-checkbox")[0];
            if (checked != null)
            {
                if (checkBoxDiv)
                {
                    checkBoxDiv.style.display = "";
                    el.$(".cyan-list-item-checkbox :checkbox").check(checked);
                }
                else
                {
                    checkBoxDiv = document.createElement("div");
                    checkBoxDiv.className = "cyan-list-item-checkbox";
                    el[0].insertBefore(checkBoxDiv, iconDiv.nextSibling);
                    var checkbox = document.createElement("input");
                    checkbox.type = this.list.checkMode || "checkbox";
                    checkbox.name = checkboxName;
                    checkbox.value = this.id;
                    checkbox.checked = checked;
                    checkBoxDiv.appendChild(checkbox);
                    checkbox.onclick = this.list.itemCheck;
                }
            }
            else if (checkBoxDiv)
            {
                checkBoxDiv.style.display = "none";
                el.$(".cyan-list-item-checkbox :checkbox").check(false);
            }
        }

        var textClass = this.getTextCls();
        if (textClass)
            textClass = textClass + " cyan-list-item-text";
        else
            textClass = "cyan-list-item-text";
        var textDiv = el.$(".cyan-list-item-text")[0];
        textDiv.className = textClass;
        var textStyle = this.getTextStyle();
        if (textStyle)
            textDiv.style.cssText = textStyle;

        var href = this.getHref();
        if (href)
        {
            var target = this.getHrefTarget();
            var s = "<a href=\"" + href + "\"";
            if (target)
                s += " target=\"" + target + "\"";
            s += ">" + this.text + "</a>";
            textDiv.innerHTML = s;
        }
        else
        {
            textDiv.innerHTML = this.text;
        }
    }
};

Cyan.List.ListItem.prototype.update = function (item)
{
    this.value = item;
    this.text = item.text;
    this.tip = item.tip;
    this.updateEl();
};

Cyan.List.ListItem.prototype.remove = function ()
{
    var list = this.list;

    if (list.selectedId = this.id)
        list.selectedId = null;

    Cyan.Array.removeElement(list.listItems, this);

    if (this.el)
    {
        var el = Cyan.$(this.el);
        el.parentNode.removeChild(el);
    }
};

Cyan.List.ListItem.prototype.isSelected = function ()
{
    return this.list.selectedId == this.id;
};

Cyan.List.ListItem.prototype.select = function ()
{
    this.list.selectItem(this);
};

Cyan.List.ListItem.prototype.getNextSibling = function ()
{
    var items = this.list.listItems;
    var n = items.length;
    for (var i = 0; i < n; i++)
    {
        if (items[i] == this && i < n - 1)
            return items[i + 1];
    }

    return null;
};

Cyan.List.ListItem.prototype.getPreviousSibling = function ()
{
    var items = this.list.listItems;
    var n = items.length;
    for (var i = 0; i < n; i++)
    {
        if (items[i] == this && i > 0)
            return items[i - 1];
    }

    return null;
};

Cyan.List.ListItem.prototype.move = function (offest)
{
    if (offest == 0)
        return false;

    var items = this.listItems;

    var index = Cyan.Array.indexOf(items, this);
    var position = index + offest;

    if (position < 0)
        position = 0;

    if (position >= items.length)
        position = items.length - 1;

    if (position == index)
        return false;

    var other = items[position];
    items[position] = this;
    items[index] = other;

    if (this.el)
    {
        var el = Cyan.$((offest < 0 ? this : other).el);
        var el1 = Cyan.$((offest < 0 ? other : this).el);
        var nextSibling = el.nextSibling;

        el.parentNode.insertBefore(el, el1);
        if (nextSibling)
            el.parentNode.insertBefore(el1, nextSibling);
        else
            el.parentNode.appendChild(el1);
    }
    return true;
};