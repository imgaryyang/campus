Cyan.importJs("event.js");

Cyan.List = Cyan.Class.extend(function (name, items)
{
    this.name = name;
    this.checkboxName = name + "$checkbox";
    this.items = items;
    this.checkMode = "checkbox";
    this.loaded = false;
}, Cyan.Widget);

Cyan.List.ListItem = function (item, list)
{
    this.id = item.id;
    this.list = list;
};

Cyan.importAdapter("list");

Cyan.List.prototype.getLoader = function ()
{
    if (!this.loader)
    {
        var list = this;
        this.loader = {
            load: function (callback)
            {
                list.loadItems(function (result)
                {
                    for (var i = 0; i < result.length; i++)
                        list.addItem(result[i]);
                    callback();
                });
            }
        };
    }
    return this.loader;
};

Cyan.List.prototype.reload = function (replace, callback)
{
    if (replace == null)
    {
        replace = true;
    }
    else if (replace instanceof Function && !callback)
    {
        callback = replace;
        replace = true;
    }

    var list = this;
    this.loadItems(function (result)
    {
        list.refresh(result, replace);
        if (callback)
            callback();
    });
};

Cyan.List.prototype.refresh = function (result, replace)
{
    if (!this.loaded)
    {
        this.items = result;
        return;
    }

    var i, list = this, oldItems = list.getItems();
    if (replace)
    {
        var nextItem;
        for (i = result.length - 1; i >= 0; i--)
        {
            var newItem = result[i];
            var b = false;
            for (var j = 0; j < oldItems.length; j++)
            {
                if (oldItems[j] && oldItems[j].id == newItem.id)
                {
                    b = true;
                    nextItem = oldItems[j];
                    nextItem.update(newItem);
                    oldItems[j] = null;
                    break;
                }
            }

            if (!b)
            {
                if (nextItem)
                    nextItem = list.insertItemBefore(newItem, nextItem.id);
                else
                    nextItem = list.addItem(newItem);
            }
        }

        for (i = 0; i < oldItems.length; i++)
        {
            if (oldItems[i])
                oldItems[i].remove();
        }
    }
    else
    {
        list.clear();
        for (i = 0; i < result.length; i++)
            list.addItem(result[i]);
    }
};

Cyan.List.prototype.isChecked = function (item)
{
    return item.checked;
};

Cyan.List.prototype.getProperty = function (item, name)
{
    return item[name] ? item[name] : item.properties ? item.properties[name] : null;
};

Cyan.List.prototype.getIcon = function (item)
{
    return this.getProperty(item, "icon");
};

Cyan.List.prototype.getIconCls = function (item)
{
    return this.getProperty(item, "iconCls");
};

Cyan.List.prototype.getHref = function (item)
{
    return this.getProperty(item, "href");
};

Cyan.List.prototype.getHrefTarget = function (item)
{
    return this.getProperty(item, "hrefTarget");
};

Cyan.List.prototype.getTextCls = function (item)
{
    return this.getProperty(item, "textCls");
};

Cyan.List.prototype.getTextStyle = function (item)
{
    return this.getProperty(item, "textStyle");
};

Cyan.List.prototype.getAction = function (item, name)
{
    var action = this.getProperty(item, name);
    if (action)
    {
        if (Cyan.isString(action))
        {
            action = new Function("", action);
            item[name] = action;
        }
    }
    else
    {
        action = this[name];
        if (Cyan.isString(action))
        {
            action = new Function("", action);
            this[name] = action;
        }
    }

    return action;
};
Cyan.List.prototype.invokeAction = function (item, name)
{
    var action = this.getAction(item, name);
    if (action)
        action.apply(item);
};
Cyan.List.prototype.dblclick = function (item)
{
    this.invokeAction(item, "ondblclick");
};
Cyan.List.prototype.click = function (item)
{
    this.invokeAction(item, "onclick");
};
Cyan.List.prototype.select = function (item)
{
    this.invokeAction(item, "onselect");
};
Cyan.List.prototype.check = function (item)
{
    this.invokeAction(item, "oncheck");
};
Cyan.List.prototype.getStartDrag = function ()
{
    if (!this.startDrag && this.startdrag)
        this.startDrag = this.startdrag;

    if (this.startDrag && Cyan.isString(this.startDrag))
        this.startDrag = new Function("event", this.startDrag);

    if (this.startDrag)
        this.startDrag = Cyan.Event.action(this.startDrag);

    return this.startDrag;
};
Cyan.List.prototype.getEndDrag = function ()
{
    if (!this.endDrag && this.enddrag)
        this.endDrag = this.enddrag;

    if (this.endDrag && Cyan.isString(this.endDrag))
        this.endDrag = new Function("event", this.endDrag);

    if (this.endDrag)
        this.endDrag = Cyan.Event.action(this.endDrag);

    return this.endDrag;
};
Cyan.List.prototype.getOnDragOver = function ()
{
    if (!this.onDragOver && this.ondragover)
        this.onDragOver = this.ondragover;

    if (this.onDragOver && Cyan.isString(this.onDragOver))
        this.onDragOver = new Function("event", this.onDragOver);

    if (this.onDragOver)
        this.onDragOver = Cyan.Event.action(this.onDragOver);

    return this.onDragOver;
};
Cyan.List.prototype.getOnDrop = function ()
{
    if (!this.onDrop && this.ondrop)
        this.onDrop = this.ondrop;

    if (this.onDrop && Cyan.isString(this.onDrop))
        this.onDrop = new Function("event", this.onDrop);

    if (this.onDrop)
        this.onDrop = Cyan.Event.action(this.onDrop);

    return this.onDrop;
};

Cyan.List.prototype.selectedItems = function ()
{
    var list = this;
    var checkboxs = Cyan.$$("input").search(function ()
    {
        return this.name == list.checkboxName;
    });
    if (checkboxs.length)
    {
        return Cyan.get(checkboxs.checkedValues(), function ()
        {
            return list.getItemById(this).toOption();
        });
    }
    var item = this.getSelectedItem();
    return item ? [item.toOption()] : [];
};

Cyan.List.prototype.eachItem = function (fn, callback)
{
    Cyan.each(this.getItems(), fn);
    if (callback)
        callback();
};

Cyan.List.ListItem.prototype.toOption = function ()
{
    return {value: this.id, text: this.getText()};
};

Cyan.List.prototype.bindSearch = function (component)
{
    component = Cyan.$(component);
    var list = this;
    var lastText;
    Cyan.$$(component).onkeyup(function ()
    {
        var text = component.value;
        setTimeout(function ()
        {
            if (text && text == component.value)
            {
                if (list.searchByText)
                {
                    list.search(function (callback)
                    {
                        list.searchByText(text, function (result)
                        {
                            if (text == component.value)
                            {
                                callback(result);
                            }
                        });
                    });
                }
                else
                {
                    list.search(text);
                }
            }
            else
            {
                list.search(null);
            }
        }, 300);
    });
};

Cyan.List.prototype.enableSearch = function (text)
{
    if (!text)
        text = (Cyan.titles["search"] || "search") + ":";

    var list = this;
    var quickInput = new Cyan.QuickInput(text);
    quickInput.process = function (text)
    {
        if (text == "")
        {
            list.search("");
        }
        else
        {
            window.setTimeout(function ()
            {
                if (text == quickInput.input.value)
                {
                    if (list.lastSearchText == text)
                        return;
                    list.lastSearchText = text;

                    list.search(function (callback)
                    {
                        list.searchByText(text, function (result)
                        {
                            if (text == quickInput.input.value)
                            {
                                callback(result);
                            }
                        });
                    });
                }
            }, 300);
        }
    };
    quickInput.getPosition = function ()
    {
        var position = list.getPosition();
        return {x: position.x, y: position.y - 24};
    };
    quickInput.init();
};

Cyan.List.prototype.getPosition = function ()
{
    return Cyan.Elements.getPosition(this.el);
};

Cyan.List.prototype.search = function (searcher)
{
    var list = this;
    if (Cyan.isString(searcher))
    {
        var text = searcher;
        if (text == this.lastSearchText)
            return;
        this.lastSearchText = text;

        if (text)
        {
            if (this.searchByText)
            {
                searcher = function (callback)
                {
                    list.searchByText(text, callback);
                };
            }
            else
            {
                this.filter(function (item)
                {
                    return item.getText().indexOf(text) >= 0;
                });

                return;
            }
        }
        else
        {
            searcher = null;
        }
    }

    if (searcher)
    {
        searcher(function (ids)
        {
            list.filterByIds(ids || []);
        });
    }
    else
    {
        this.filter(null);
    }
};

Cyan.List.prototype.filterByIds = function (ids)
{
    this.filter(function (item)
    {
        return Cyan.Array.contains(ids, item.id);
    });
};

Cyan.List.prototype.getText = function (id, callback)
{
    var item = this.getItemById(id);
    if (!item && this.items)
    {
        item = Cyan.searchFirst(this.items, function ()
        {
            return this.id == id
        });
    }

    var text;
    if (item)
        text = item.text;
    else
        text = "";

    if (callback)
        callback(text);

    return text;
};

Cyan.List.prototype.setSelectedValues = function (values)
{
    if (values)
    {
        if (this.checkMode == "checkbox")
        {
            var list = this;
            var checkboxs = Cyan.$$("input").search(function ()
            {
                return this.name == list.checkboxName;
            });

            if (checkboxs.length)
            {
                Cyan.each(checkboxs, function ()
                {
                    this.checked = Cyan.Array.contains(values, this.value);
                });
            }
            else
            {
                Cyan.each(this.items, function ()
                {
                    if (this.checked != null)
                        this.checked = Cyan.Array.contains(values, this.id);
                });
            }
        }
    }
};

Cyan.List.prototype.clearSelectedItems = function ()
{
    var list = this;
    var checkboxs = Cyan.$$("input").search(function ()
    {
        return this.name == list.checkboxName;
    });
    if (checkboxs.length)
    {
        checkboxs.each(function ()
        {
            this.checked = false;
        });
    }
    else
    {
        this.clearSelections();
    }
};

Cyan.List.ListItem.prototype.getProperty = function (name)
{
    return this.tree.getProperty(this.value, name);
};

Cyan.List.ListItem.prototype.getIcon = function ()
{
    return this.list.getIcon(this.value);
};

Cyan.List.ListItem.prototype.getIconCls = function ()
{
    return this.list.getIconCls(this.value);
};

Cyan.List.ListItem.prototype.getHref = function ()
{
    return this.list.getHref(this.value);
};

Cyan.List.ListItem.prototype.getHrefTarget = function ()
{
    return this.list.getHrefTarget(this.value);
};

Cyan.List.ListItem.prototype.getTextCls = function ()
{
    return this.list.getTextCls(this.value);
};

Cyan.List.ListItem.prototype.getTextStyle = function ()
{
    return this.list.getTextCls(this.value);
};

Cyan.List.ListItem.prototype.getAction = function (name)
{
    return this.list.getAction(this.value, name);
};
Cyan.List.ListItem.prototype.invokeAction = function (name)
{
    return this.list.invokeAction(this.value, name);
};

Cyan.List.prototype.searchable = true;