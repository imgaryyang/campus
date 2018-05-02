Cyan.onload(function ()
{
    Cyan.importCss("/platform/commons/ownedselector.css");
});

System.OwnedSelector = Cyan.Class.extend(function (id, root, selected, name)
{
    this.id = id;
    this.root = root;
    this.selected = selected;
    this.name = name;
}, Cyan.Widget);

System.OwnedSelector.prototype.create = function ()
{
    this.itemSelector = new Cyan.ItemSelector(this.id, this, this.selected);
    this.itemSelector.name = this.name;

    var selector = this;
    setTimeout(function ()
    {
        selector.itemSelector.init(selector.id);
    }, 500);
};

System.OwnedSelector.prototype.init = function (div)
{
    div = Cyan.$(div);
    var ownedSelector = this;

    if (!this.itemSelector)
    {
        div.className = "ownedselector_single";
        var queryDiv = document.createElement("DIV");
        queryDiv.className = "ownedselector_query";

        var queryInput = document.createElement("INPUT");
        queryInput.className = "search";

        if (!this.clearable)
            queryInput.className += " noclear";

        queryDiv.appendChild(queryInput);
        Cyan.$$(queryInput).onkeyup(function ()
        {
            ownedSelector.query(this.value);
        });

        Cyan.attach(div, "mousemove", function ()
        {
            try
            {
                queryInput.focus();
            }
            catch (e)
            {
            }
        });

        if (this.clearable)
        {
            var clearButton = document.createElement("BUTTON");
            clearButton.className = "clear";
            clearButton.onclick = function ()
            {
                itemList.clearAll();
                if (ownedSelector.onselect)
                {
                    ownedSelector.onselect(ownedSelector);
                }
            };
            queryDiv.appendChild(clearButton);
        }

        div.appendChild(queryDiv);
    }

    var component = window[this.id + "$object"];
    this.component = component;

    var treeDiv = document.createElement("DIV");
    treeDiv.className = "nodetree";
    div.appendChild(treeDiv);

    this.tree = new Cyan.Tree(null, this.root);
    this.tree.rootVisible = false;
    this.tree.lazyLoadNode = function (id, callback)
    {
        component.loadChildren(id, {wait: false, check: false, callback: callback});
    };
    this.tree.searchByText = function (text, callback)
    {
        component.searchNode(text, {wait: false, check: false, callback: callback});
    };
    this.tree.autoRender = true;
    if (this.itemSelector)
        this.tree.enableSearch();

    var itemsDiv = document.createElement("DIV");
    itemsDiv.className = "items";
    div.appendChild(itemsDiv);

    var itemSelector = this.itemSelector || new Cyan.ItemSelector(this.id, this, this.selected);
    var itemList = this.itemList =
            itemSelector.createListComponent("from$" + this.name, [], itemsDiv, this.ondblclick, this.onselect);

    this.tree.onclick = function ()
    {
        component.loadItems(this.id, {
            wait: false, check: false, callback: function (items)
            {
                ownedSelector.setItems(items);
            }
        });
    };
    this.tree.init(treeDiv);
};

System.OwnedSelector.prototype.selectedValues = function ()
{
    return this.itemList.selectedValues();
};

System.OwnedSelector.prototype.selectedItems = function ()
{
    return this.itemList.selectedItems();
};

System.OwnedSelector.prototype.allItems = function ()
{
    return this.itemList.allItems();
};

System.OwnedSelector.prototype.up = function ()
{
    this.itemSelector.up();
};

System.OwnedSelector.prototype.down = function ()
{
    this.itemSelector.down();
};

System.OwnedSelector.prototype.query = function (s)
{
    if (s)
    {
        var selector = this;
        this.component.queryItems(s, {
            wait: false, check: false, callback: function (items)
            {
                if (items.length)
                {
                    selector.setItems(items);
                }
                else
                {
                    selector.tree.search(s);
                }
            }
        });
    }
    else
    {
        this.setItems(null);
        this.tree.search("");
    }
};

System.OwnedSelector.prototype.setItems = function (items)
{
    this.itemList.clearAll();
    if (items)
    {
        var n = items.length;
        for (var i = 0; i < n; i++)
        {
            var item = items[i];
            this.itemList.addItem(item.id, item.text);
        }
    }
};

System.OwnedSelector.prototype.addSelected = function (item)
{
    var value = item.valueOf();

    if (!this.selected.searchFirst(function ()
            {
                return this.valueOf() == value;
            }))
    {
        this.selected.push(item);
        this.itemSelector.addItem(value, item.toString());
    }
};

System.OwnedSelector.prototype.bindQuery = function (component)
{
    component = Cyan.$(component);
    var selector = this;
    var lastText;
    Cyan.$$(component).onkeyup(function ()
    {
        var s = component.value;
        setTimeout(function ()
        {
            if (component.value == s && s != lastText)
            {
                lastText = s;
                selector.query(s, component);
            }
        }, 300);
    });
};