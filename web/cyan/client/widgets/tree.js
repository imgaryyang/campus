Cyan.importJs("event.js");
Cyan.importJs("widgets/quickinput.js");

Cyan.Tree = Cyan.Class.extend(function (name, root)
{
    this.name = name;
    this.root = root;
    this.checkboxName = name + "$checkbox";
    this.multiple = false;
    this.checkMode = "checkbox";
    this.singleExpand = false;
    this.rootVisible = true;
}, Cyan.Widget);

Cyan.Tree.prototype.init = function (el)
{
    this.el = Cyan.$(el);

    if (!el)
        this.autoRender = false;

    if (this.autoRender == "false" || this.autoRender == null)
        this.autoRender = false;

    if (this.multiple == "false" || this.multiple == null)
        this.multiple = false;

    if (this.singleExpand == "false" || this.singleExpand == null)
        this.singleExpand = false;

    if (this.rootVisible == "false" || this.rootVisible == null)
        this.rootVisible = false;

    if (this.checkMode != "checkbox" && this.checkMode != "radio")
        this.checkMode = "checkbox";

    var tree = this;
    Cyan.onload(function ()
    {
        tree.init0(el);
        if (tree.changedBeforeLoad)
            tree.reload();
    });
};

Cyan.Tree.prototype.reload = function (callback)
{
    var root = this.getRoot();
    if (root)
        root.reloadDescendants(callback);
    else
        this.changedBeforeLoad = true;
};

Cyan.Tree.TreeNode = function (node, tree, id)
{
    if (id == null)
        this.id = node.id;
    else
        this.id = id;
    this.tree = tree;
};

Cyan.importAdapter("tree");

Cyan.Tree.prototype.getLoader = function ()
{
    if (!this.loader)
    {
        var tree = this;
        this.loader = {
            load: function (node, callback)
            {
                tree.lazyLoadNode(node.id, function (result)
                {
                    for (var i = 0; i < result.length; i++)
                        node.appendChild(result[i]);
                    if (callback)
                        callback();
                });
            }
        };
    }
    return this.loader;
};

Cyan.Tree.prototype.expandByPath = function (path, callback)
{
    if (path.length == 1)
    {
        if (callback)
            callback();
    }
    else
    {
        var tree = this;
        var index = 0;
        var fn = function ()
        {
            var node = tree.getNodeById(path[index++]);
            node.expand(false, false, index == path.length - 1 ? callback : fn);
        };
        fn();
    }
};

Cyan.Tree.prototype.expandByPaths = function (paths, callback)
{
    if (paths && paths.length)
    {
        var tree = this;
        var index = 0;
        var fn = function ()
        {
            var path = paths[index++];
            tree.expandByPath(path, index == paths.length ? callback : fn);
        };
        fn();
    }
    else if (callback)
    {
        callback();
    }
};

Cyan.Tree.TreeNode.prototype.toOption = function ()
{
    var value;
    var value0 = this.getValue().value;
    if (value0 != null)
    {
        if (Cyan.isBaseType(value0))
        {
            value = value0;
        }
        else if (value0.valueOf)
        {
            var value1 = value0.valueOf();
            if (Cyan.isBaseType(value1))
            {
                value = value1;
            }
        }
    }

    if (value == null)
        value = this.id;

    return {value: value, text: this.getNodeText()};
};

Cyan.Tree.TreeNode.prototype.getNodeText = function ()
{
    return this.getValue().nodeText || this.getText();
};

/**
 * 循环调用节点的每个子节点，如果节点的子节点没有加载则先加载节点
 */
Cyan.Tree.TreeNode.prototype.eachChild = function (callable, loadedOnly)
{
    if (!this.isLeaf())
    {
        if (this.isLoaded())
        {
            Cyan.each(this.getChildren(), callable);
        }
        else if (!loadedOnly)
        {
            var loader = this.tree.getLoader();
            if (loader)
            {
                var node = this;
                loader.load(this, function ()
                {
                    node.setLoaded();
                    Cyan.each(node.getChildren(), callable);
                });
            }
        }
    }
};

Cyan.Tree.TreeNode.prototype.through = function (callable, loadedOnly, finish)
{
    Cyan.lazyThrough(this, function (node, callback)
    {
        if (Cyan.getFunctionParameters(callable).length == 1)
        {
            callable.apply(node, [callback]);
        }
        else
        {
            callable.apply(node);
            callback();
        }
    }, function (node, callback)
    {
        if (node.isLoaded())
        {
            if (node.isLeaf())
                callback(null);
            else
                callback(node.getChildren());
        }
        else if (!loadedOnly)
        {
            var loader = node.tree.getLoader();
            if (loader)
            {
                loader.load(node, function ()
                {
                    node.setLoaded();
                    if (node.isLeaf())
                        callback(null);
                    else
                        callback(node.getChildren());
                });
            }
        }
        else
        {
            callback(null);
        }
    }, finish);
};

Cyan.Tree.TreeNode.prototype.expandAndThrough = function (callable, finish)
{
    Cyan.lazyThrough(this, function (node, callback)
    {
        callable.apply(node);
        node.expand(false, false, callback);
    }, function (node, callback)
    {
        if (!node.isLeaf() && node.isLoaded())
            callback(node.getChildren());
        else
            callback(null);
    }, finish);
};

Cyan.Tree.TreeNode.prototype.reloadDescendants = function (callback)
{
    Cyan.lazyThrough(this, function (node, callback)
    {
        node.reloadChildren(true, callback);
    }, function (node, callback)
    {
        if (!node.isLeaf() && node.isLoaded())
        {
            callback(node.getChildren());
        }
        else
        {
            callback(null);
        }
    }, callback);
};

Cyan.Tree.TreeNode.prototype.reloadChildren = function (replace, callback)
{
    if (this.isLeaf() || this.isLoaded())
    {
        var node = this;
        this.tree.lazyLoadNode(node.id, function (result)
        {
            var i, j, oldNode, newNode, oldChildren = node.getChildren(), expanded = node.isExpanded();
            if (replace)
            {
                for (i = 0; i < oldChildren.length; i++)
                {
                    oldNode = oldChildren[i];
                    var exists = false;
                    for (j = 0; j < result.length; j++)
                    {
                        newNode = result[j];
                        if (oldNode.id == newNode.id)
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                    {
                        oldNode.remove();
                        oldChildren[i] = null;
                    }
                }

                var nextNode;
                for (i = result.length - 1; i >= 0; i--)
                {
                    newNode = result[i];
                    var b = false;
                    for (j = 0; j < oldChildren.length; j++)
                    {
                        if (oldChildren[j] && oldChildren[j].id == newNode.id)
                        {
                            b = true;
                            nextNode = oldChildren[j];
                            nextNode.update(newNode);
                            oldChildren[j] = null;
                            break;
                        }
                    }

                    if (!b)
                    {
                        if (nextNode)
                        {
                            nextNode = node.insertChildBefore(newNode, nextNode.id);
                        }
                        else
                        {
                            nextNode = node.appendChild(newNode);
                        }
                    }
                }
            }
            else
            {
                for (i = 0; i < oldChildren.length; i++)
                    oldChildren[i].remove();
                for (i = 0; i < result.length; i++)
                    node.appendChild(result[i]);
            }

            if (expanded && !node.isLeaf())
                node.expand(false, false, callback);
            else if (callback)
                callback();
        });
    }
    else if (callback)
    {
        callback();
    }
};

Cyan.Tree.prototype.isSelectable = function (node)
{
    return node.selectable == null || !!node.selectable;
};

Cyan.Tree.prototype.isChecked = function (node)
{
    if (node.checked == null)
        return null;
    if (this.initialSelectedItems)
    {
        var option = this.getNodeById(node.id).toOption();
        if (Cyan.searchFirst(this.initialSelectedItems, function ()
                {
                    return this.value == option.value
                }))
        {
            return true;
        }
    }
    return node.checked;
};

Cyan.Tree.prototype.getProperty = function (node, name)
{
    return node[name] ? node[name] : node.properties ? node.properties[name] : null;
};

Cyan.Tree.prototype.getIcon = function (node)
{
    return this.getProperty(node, "icon");
};

Cyan.Tree.prototype.getIconCls = function (node)
{
    return this.getProperty(node, "iconCls");
};

Cyan.Tree.prototype.getHref = function (node)
{
    return this.getProperty(node, "href");
};

Cyan.Tree.prototype.getHrefTarget = function (node)
{
    return this.getProperty(node, "hrefTarget");
};

Cyan.Tree.prototype.getTextCls = function (node)
{
    return this.getProperty(node, "textCls");
};

Cyan.Tree.prototype.getTextStyle = function (node)
{
    return this.getProperty(node, "textStyle");
};

Cyan.Tree.prototype.getAction = function (node, name)
{
    var action = this.getProperty(node, name);
    if (action)
    {
        if (Cyan.isString(action))
        {
            action = new Function("", action);
            node[name] = action;
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
Cyan.Tree.prototype.invokeAction = function (node, name)
{
    var action = this.getAction(node, name);
    if (action)
        action.apply(node);
};
Cyan.Tree.prototype.dblclick = function (node)
{
    this.invokeAction(node, "ondblclick");
};
Cyan.Tree.prototype.click = function (node)
{
    this.invokeAction(node, "onclick");
};
Cyan.Tree.prototype.select = function (node)
{
    this.invokeAction(node, "onselect");
};
Cyan.Tree.prototype.check = function (node)
{
    this.invokeAction(node, "oncheck");
};
Cyan.Tree.prototype.getStartDrag = function ()
{
    if (!this.startDrag && this.startdrag)
        this.startDrag = this.startdrag;

    if (this.startDrag && Cyan.isString(this.startDrag))
        this.startDrag = new Function("event", this.startDrag);

    if (this.startDrag)
        this.startDrag = Cyan.Event.action(this.startDrag);

    return this.startDrag;
};
Cyan.Tree.prototype.getEndDrag = function ()
{
    if (!this.endDrag && this.enddrag)
        this.endDrag = this.enddrag;

    if (this.endDrag && Cyan.isString(this.endDrag))
        this.endDrag = new Function("event", this.endDrag);

    if (this.endDrag)
        this.endDrag = Cyan.Event.action(this.endDrag);

    return this.endDrag;
};
Cyan.Tree.prototype.getOnDragOver = function ()
{
    if (!this.onDragOver && this.ondragover)
        this.onDragOver = this.ondragover;

    if (this.onDragOver && Cyan.isString(this.onDragOver))
        this.onDragOver = new Function("event", this.onDragOver);

    if (this.onDragOver)
        this.onDragOver = Cyan.Event.action(this.onDragOver);

    return this.onDragOver;
};
Cyan.Tree.prototype.getOnDrop = function ()
{
    if (!this.onDrop && this.ondrop)
        this.onDrop = this.ondrop;

    if (this.onDrop && Cyan.isString(this.onDrop))
        this.onDrop = new Function("event", this.onDrop);

    if (this.onDrop)
        this.onDrop = Cyan.Event.action(this.onDrop);

    return this.onDrop;
};
Cyan.Tree.prototype.selectedItems = function ()
{
    var tree = this;
    var checkboxs = Cyan.$$("input").search(function ()
    {
        return this.name == tree.checkboxName;
    });
    if (checkboxs.length)
    {
        var items = Cyan.get(checkboxs.checkedValues(), function ()
        {
            return tree.getNodeById(this).toOption();
        });

        if (this.initialSelectedItems)
        {
            this.initialSelectedItems.each(function ()
            {
                var value = this.value;
                if (!Cyan.searchFirst(items, function ()
                        {
                            return this.value == value;
                        }))
                {
                    if (!checkboxs.searchFirst(function ()
                            {
                                var option = tree.getNodeById(this.value).toOption();
                                return option.value == value;
                            }))
                    {
                        items.push(this);
                    }
                }
            });
        }

        return items;
    }
    return Cyan.get(this.getSelectedNodes(), Cyan.Tree.TreeNode.prototype.toOption);
};

Cyan.Tree.prototype.clearSelectedItems = function ()
{
    var tree = this;
    var checkboxs = Cyan.$$("input").search(function ()
    {
        return this.name == tree.checkboxName;
    });
    if (checkboxs.length)
    {
        checkboxs.each(function ()
        {
            this.checked = false;
        });

        this.initialSelectedItems = null;
    }
    else
    {
        this.clearSelections();
    }
};

Cyan.Tree.prototype.eachItem = function (fn, callback)
{
    var loaded = {};
    var fn1 = function (callback)
    {
        if (loaded[this.id])
            return false;
        loaded[this.id] = true;
        fn.apply(this.toOption());

        if (callback)
            callback();
    };
    Cyan.lazyEach(this.getSelectedNodes(), function (callback)
    {
        this.through(fn1, false, callback);
    }, callback);
};

Cyan.Tree.prototype.bindSearch = function (component)
{
    component = Cyan.$(component);
    var tree = this;
    var lastText;
    Cyan.$$(component).onkeyup(function ()
    {
        var text = component.value;
        setTimeout(function ()
        {
            if (text && text == component.value)
            {
                tree.search(function (callback)
                {
                    tree.searchByText(text, function (result)
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
                tree.search(null);
            }
        }, 300);
    });
};

Cyan.Tree.prototype.enableSearch = function (text)
{
    if (!text)
        text = (Cyan.titles["search"] || "search") + ":";

    var tree = this;
    var quickInput = new Cyan.QuickInput(text);
    quickInput.process = function (text)
    {
        if (text == "")
        {
            tree.search("");
        }
        else
        {
            window.setTimeout(function ()
            {
                if (text == quickInput.input.value)
                {
                    if (tree.lastSearchText == text)
                        return;
                    tree.lastSearchText = text;

                    tree.search(function (callback)
                    {
                        tree.searchByText(text, function (result)
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
        var position = tree.getPosition();
        return {x: position.x, y: position.y - 24};
    };
    quickInput.init();
};

Cyan.Tree.prototype.getPosition = function ()
{
    return Cyan.Elements.getPosition(this.el);
};

Cyan.Tree.prototype.search = function (searcher)
{
    var tree = this;
    if (Cyan.isString(searcher))
    {
        if (!this.searchByText)
            return;

        var text = searcher;
        if (text == this.lastSearchText)
            return;
        this.lastSearchText = text;

        searcher = text ? function (callback)
        {
            tree.searchByText(text, callback);
        } : null;
    }

    if (searcher)
    {
        searcher(function (paths)
        {
            tree.filterByPaths(paths || []);
        });
    }
    else
    {
        this.filter(null);
    }
};

Cyan.Tree.prototype.filterByPaths = function (paths)
{
    if (paths.length > 10)
        paths.length = 10;

    var tree = this;
    this.expandByPaths(paths, function ()
    {
        tree.filter(function (node)
        {
            var nodeId = node.id;
            for (var i = 0; i < paths.length; i++)
            {
                if (Cyan.Array.contains(paths[i], nodeId))
                    return true;
            }
            return false;
        });
    });
};

Cyan.Tree.prototype.getText = function (id, callback)
{
    var node = this.getNodeById(id);
    if (node)
    {
        var text = node.nodeText || node.text;

        if (callback)
            callback(text);

        return text;
    }
    else if (this.loadNode)
    {
        this.loadNode(id, function (node)
        {
            var text;
            if (node)
            {
                text = node.nodeText || node.text;
            }
            else
            {
                text = "";
            }

            if (callback)
                callback(text);
        });
    }
    else
    {
        if (callback)
            callback("");
        return "";
    }
};

Cyan.Tree.prototype.setSelectedItems = function (items)
{
    if (items)
    {
        this.initialSelectedItems = items;
        if (this.checkMode == "checkbox")
        {
            var list = this;
            var checkboxs = Cyan.$$("input").search(function ()
            {
                return this.name == list.checkboxName;
            });

            if (checkboxs.length)
            {
                var tree = this;
                Cyan.each(checkboxs, function ()
                {
                    var option = tree.getNodeById(this.value).toOption();
                    if (option)
                    {
                        this.checked = !!Cyan.searchFirst(items, function ()
                        {
                            return this.value == option.value
                        });
                    }
                });
            }
        }
    }
};

Cyan.Tree.TreeNode.prototype.getProperty = function (name)
{
    return this.tree.getProperty(this.value, name);
};

Cyan.Tree.TreeNode.prototype.getIcon = function ()
{
    return this.tree.getIcon(this.value);
};

Cyan.Tree.TreeNode.prototype.getIconCls = function ()
{
    return this.tree.getIconCls(this.value);
};

Cyan.Tree.TreeNode.prototype.getHref = function ()
{
    return this.tree.getHref(this.value);
};

Cyan.Tree.TreeNode.prototype.getHrefTarget = function ()
{
    return this.tree.getHrefTarget(this.value);
};

Cyan.Tree.TreeNode.prototype.getTextCls = function ()
{
    return this.tree.getTextCls(this.value);
};

Cyan.Tree.TreeNode.prototype.getTextStyle = function ()
{
    return this.tree.getTextCls(this.value);
};

Cyan.Tree.TreeNode.prototype.getAction = function (name)
{
    return this.tree.getAction(this.value, name);
};
Cyan.Tree.TreeNode.prototype.invokeAction = function (name)
{
    return this.tree.invokeAction(this.value, name);
};