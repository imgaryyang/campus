Cyan.importJs("widgets/targetlist.js");

System.MemberSelector = Cyan.Class.extend(function (id, root, name)
{
    this.id = id;
    this.root = root;
    this.name = name;
    this.selectable = [];
    this.selected = [];
}, Cyan.Widget);

System.MemberSelector.itemValueOf = function ()
{
    return this.type + ":" + (this.type == "custom" ? this.name : this.id);
};

System.MemberSelector.itemToString = function ()
{
    return this.name;
};

System.MemberSelector.prototype.create = function ()
{
    var selector = this;
    this.itemSelector = new Cyan.ItemSelector(this.id,
            {
                init: function (div)
                {
                    selector.init(div);
                },
                selectedItems: function ()
                {
                    var indexes = selector.memberList.selectedIndexes();
                    var items = [];
                    for (var i = 0; i < indexes.length; i++)
                    {
                        var index = indexes[i];
                        var member = selector.selectable[index];
                        var value = member.valueOf();
                        items.push({value: value, text: member.name});
                        if (!selector.selected.searchFirst(function ()
                                {
                                    return this.valueOf() == value;
                                }))
                            selector.selected.push(member);
                    }
                    return items;
                },
                allItems: function ()
                {
                    var items = [];
                    for (var i = 0; i < selector.selectable.length; i++)
                    {
                        var member = selector.selectable[i];
                        var value = member.valueOf();
                        items.push({value: value, text: member.name});
                        if (!selector.selected.searchFirst(function ()
                                {
                                    return this.valueOf() == value;
                                }))
                            selector.selected.push(member);
                    }

                    return items;
                },
                addItem: function (value, text)
                {
                    selector.selected.removeCase(function ()
                    {
                        return this.valueOf() == value;
                    });
                }
            }, this.selected);
    this.itemSelector.name = this.name;
    this.itemSelector.init(this.id);
};

System.MemberSelector.prototype.init = function (div)
{
    var memberSelector = this;
    var component = this.component = window[this.id];
    component.getSelector = function ()
    {
        return memberSelector;
    };

    var treeDiv = document.createElement("DIV");
    treeDiv.className = "membertree";

    div.appendChild(treeDiv);

    this.tree = new Cyan.Tree(null, this.root);
    this.tree.rootVisible = false;
    this.tree.lazyLoadNode = function (id, callback)
    {
        component.loadChildren(id, {wait: false, check: false, callback: callback});
    };
    this.tree.autoRender = true;

    var membersDiv = document.createElement("DIV");
    membersDiv.className = "members";
    div.appendChild(membersDiv);

    this.memberList = this.itemSelector.createListComponent("from$" +
    this.name, [], membersDiv, this.itemSelector.from.ondblclick);

    this.tree.onclick = function ()
    {
        component.loadMembers(this.id, {
            wait: false, check: false, callback: function (members)
            {
                memberSelector.clearSelectable();
                if (members)
                {
                    var n = members.length;
                    for (var i = 0; i < n; i++)
                    {
                        var member = members[i];
                        memberSelector.addSelectable(member);
                    }
                }
            }
        });
    };
    this.tree.init(treeDiv);
};

System.MemberSelector.prototype.clearSelectable = function ()
{
    this.memberList.clearAll();
    this.selectable.length = 0;
};

System.MemberSelector.prototype.addSelectable = function (member)
{
    this.selectable.push(member);
    this.memberList.addItem(member.valueOf(), member.name);
};

System.MemberSelector.prototype.addSelected = function (member)
{
    member.valueOf = System.MemberSelector.itemValueOf;
    member.toString = System.MemberSelector.itemToString;
    var value = member.valueOf();

    if (!this.selected.searchFirst(function ()
            {
                return this.valueOf() == value;
            }))
    {
        this.selected.push(member);
        this.itemSelector.addItem(value, member.name);
    }
};

System.MemberSelector.prototype.clearSelected = function ()
{
    this.selected.length = 0;
    this.itemSelector.removeAll();
};

System.MemberSelector.prototype.bindQuery = function (component)
{
    component = Cyan.$(component);
    var selector = this;
    var lastText;
    var targetList = new Cyan.TargetList();
    targetList.render = function (item)
    {
        var name = item.name;
        if (item.deptName)
            name = name + "(" + item.deptName + ")";

        return name;
    };
    targetList.width = Cyan.Elements.getComponentSize(component).width;
    targetList.initComponent(component);
    targetList.onselect = function (item)
    {
        selector.addSelected(item);
    };

    Cyan.$$(component).onkeyup(function ()
    {
        var s = component.value;
        setTimeout(function ()
        {
            if (component.value == s && s != lastText)
            {
                lastText = s;
                selector.query(s, component, function (members)
                {
                    if (members && members.length)
                    {
                        targetList.load(members);
                        targetList.showWith(component);
                    }
                    else if (targetList.isVisible())
                    {
                        targetList.hide();
                    }
                });
            }
        }, 300);
    });
};

System.MemberSelector.prototype.query = function (text, component, callback)
{
    if (text && text.length > 1)
    {
        var selector = this;
        this.component.queryMember(text, {
            wait: false,
            check: false,
            callback: function (members)
            {
                if (component && component.value != text)
                    return;

                selector.clearSelectable();
                if (members)
                {
                    var n = members.length;
                    if (n > 0)
                    {
                        for (var i = 0; i < n; i++)
                        {
                            var member = members[i];
                            selector.addSelectable(member);
                        }
                    }
                    else
                    {
                        selector.tree.search(text);
                    }

                    if (callback)
                        callback(members);
                }
            }
        });
    }
    else
    {
        this.memberList.clearAll();
        this.tree.search("");
        if (callback)
            callback([]);
    }
};

System.MemberSelector.prototype.up = function ()
{
    this.itemSelector.up();
    this.refreshSelected();
};

System.MemberSelector.prototype.down = function ()
{
    this.itemSelector.down();
    this.refreshSelected();
};

System.MemberSelector.prototype.refreshSelected = function ()
{
    var selectedValues = this.itemSelector.selectedValues();
    var n = selectedValues.length - 1;
    for (var i = 0; i < n; i++)
    {
        var value = selectedValues[i];
        for (var j = 0; j < this.selected.length; j++)
        {
            if (this.selected[j].valueOf() == value)
            {
                if (i != j)
                {
                    var temp = this.selected[i];
                    this.selected[i] = this.selected[j];
                    this.selected[j] = temp;
                }
                break;
            }
        }
    }
};
