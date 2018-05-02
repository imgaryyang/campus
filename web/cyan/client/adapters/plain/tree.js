Cyan.Plain.importCss("tree");
Cyan.importJs("render.js");
Cyan.importJs("event.js");

Cyan.Tree.prototype.init0 = function (el)
{
    this.inited = true;
    this.el = Cyan.$(el);
    this.selected = [];
    this.rootNode = this.toTreeNode(this.root);
    this.rootNode.last = true;

    var getNodeDiv = function (div)
    {
        while (div.className != "cyan-tree-node" && div.className != "cyan-tree-node cyan-tree-node-selected")
        {
            div = div.parentNode;
        }

        return div;
    };

    var tree = this;
    this.nodeIndentClick = function (event)
    {
        var div = getNodeDiv(this);
        var node = tree.getNodeById(div.nodeId);

        if (!node.leaf)
        {
            if (node.expanded)
                node.collapse(false, true);
            else
                node.expand(false, true);
        }

        event.stop();
    };

    var checking = false;
    this.nodeClick = function (event)
    {
        if (checking)
        {
            checking = false;
            return;
        }

        var div = getNodeDiv(this);
        var node = tree.getNodeById(div.nodeId);

        tree.click(node.value);

        if (tree.multiple && event.ctrlKey)
        {
            if (node.isSelected())
                tree.unselectNode(node);
            else
                tree.selectNode(node, true);
        }
        else
        {
            tree.selectNode(node, false);
        }
    };

    this.nodeDblClick = function (event)
    {
        var div = getNodeDiv(this);
        var node = tree.getNodeById(div.nodeId);

        if (!node.leaf)
        {
            if (node.expanded)
                node.collapse(false, true);
            else
                node.expand(false, true);
        }

        tree.dblclick(node.value);
    };

    this.nodeCheck = function (event)
    {
        var div = getNodeDiv(this);
        var node = tree.getNodeById(div.nodeId);

        tree.check(node.value);

        checking = true;
    };

    if (this.autoRender)
        this.render();
};

Cyan.Tree.prototype.getRoot = function ()
{
    return this.rootNode;
};

Cyan.Tree.prototype.getNodeById = function (id)
{
    return this.loadedNodes ? this.loadedNodes[id] : null;
};

Cyan.Tree.prototype.toTreeNode = function (node)
{
    if (!this.loadedNodes)
        this.loadedNodes = {};

    var treeNode = new Cyan.Tree.TreeNode(node, this);
    treeNode.leaf = node.leaf;
    treeNode.text = node.text;
    treeNode.value = node;
    treeNode.tip = node.tip;
    if (!node.leaf)
        treeNode.expanded = false;

    this.loadedNodes[node.id] = treeNode;

    if (node.children && node.children.length)
    {
        var n = node.children.length;
        treeNode.children = new Array(n);
        for (var i = 0; i < n; i++)
        {
            var childNode = this.toTreeNode(node.children[i]);
            childNode.parentNode = treeNode;
            childNode.last = i == n - 1;
            treeNode.children[i] = childNode;
        }
    }

    return treeNode;
};

Cyan.Tree.prototype.render = function ()
{
    if (!this.inited)
    {
        this.autoRender = true;
        return;
    }

    var treeDiv = document.createElement("div");
    treeDiv.className = "cyan-tree";
    this.el.appendChild(treeDiv);

    if (this.rootVisible)
    {
        this.getRoot().render(treeDiv);
    }
    else
    {
        var root = this.getRoot();
        if (root.children)
        {
            var children = root.children;
            var n = children.length;
            for (var i = 0; i < n; i++)
            {
                children[i].render(treeDiv);
            }
        }
        else
        {
            this.getLoader().load(root);
        }
    }

    if (this.rootVisible && !this.getRoot().leaf)
        this.getRoot().expand(false, false);
};

Cyan.Tree.prototype.selectNode = function (node, append)
{
    if (!this.isSelectable(node.value))
        return;

    var id = node.id;
    var nodeEl;
    if (node.el)
        nodeEl = Cyan.$(node.el);

    if (append)
    {
        Cyan.Array.add(this.selected, id);
    }
    else
    {
        Cyan.$$(this.el).$(".cyan-tree-node-selected").each(function ()
        {
            if (this != nodeEl)
                this.className = "cyan-tree-node";
        });
        this.selected = [id];
    }

    if (node.el)
    {
        if (nodeEl && nodeEl.className == "cyan-tree-node")
            nodeEl.className = "cyan-tree-node cyan-tree-node-selected";
    }

    this.select(node.value);
};

Cyan.Tree.prototype.unselectNode = function (node)
{
    var id = node.id;
    Cyan.Array.removeElement(this.selected, id);

    if (node.el)
    {
        var nodeEl = Cyan.$(node.el);
        if (nodeEl.className == "cyan-tree-node cyan-tree-node-selected")
        {
            nodeEl.className = "cyan-tree-node";
        }
    }
};

Cyan.Tree.prototype.clearSelections = function ()
{
    this.selected = [];
    Cyan.$$(this.el).$(".cyan-tree-node-selected").each(function ()
    {
        this.className = "cyan-tree-node";
    });
};

Cyan.Tree.prototype.getTopSelectedNodes = function ()
{
    if (this.multiple)
    {
        var result = [];
        var n = this.selected.length;
        for (var i = 0; i < n; i++)
        {
            var node = this.getNodeById(this.selected[i]);
            if (node && node.isTopSelected())
                result.push(node);
        }
        return result;
    }
    else
    {
        var selectedId = this.selected[0];
        return selectedId ? [this.getNodeById(selectedId)] : [];
    }
};

Cyan.Tree.prototype.getTopSelectedIds = function ()
{
    if (this.multiple)
    {
        var result = [];
        var n = this.selected.length;
        for (var i = 0; i < n; i++)
        {
            var nodeId = this.selected[i];
            var node = this.getNodeById(nodeId);
            if (node && node.isTopSelected())
                result.push(nodeId);
        }
        return result;
    }
    else
    {
        var selectedId = this.selected[0];
        return selectedId ? [selectedId] : [];
    }
};

Cyan.Tree.prototype.filter = function (filter)
{
    var rootNode = this.rootNode;
    Cyan.through(rootNode, function ()
    {
        var nodeEl = Cyan.$(this.el);
        if (nodeEl)
            nodeEl.style.display = !filter || filter(this) ? "" : "none";
    }, function ()
    {
        return this.childrenRendered || this == rootNode ? this.children : null;
    });
};

Cyan.Tree.prototype.getHeight = function (el)
{
    el = el || Cyan.$$(this.el).$(".cyan-tree")[0];
    var height = 0;
    var childNodes = el.childNodes;
    var n = childNodes.length;
    for (var i = 0; i < n; i++)
    {
        var node = childNodes[i];
        if (node.className == "cyan-tree-node" || node.className == "cyan-tree-node cyan-tree-node-selected")
        {
            if (!this.nodeHeight)
            {
                this.nodeHeight = Cyan.toInt(Cyan.Elements.getCss(node, "height"));
            }

            height += this.nodeHeight;
        }
        else if (node.className == "cyan-tree-sub" && node.style.display != "none")
        {
            height += this.getHeight(node);
        }
    }

    return height;
};

Cyan.Tree.prototype.getSelectedNodes = function ()
{
    return Cyan.Array.map(this.selected, function (id)
    {
        return this.getNodeById(id);
    }, this);
};

Cyan.Tree.prototype.getSelectedId = function ()
{
    return this.selected[0];
};

Cyan.Tree.prototype.getSelectedIds = function ()
{
    return Cyan.clone(this.selected);
};

Cyan.Tree.prototype.getSelectedNode = function ()
{
    var selectedId = this.selected[0];
    return selectedId ? this.getNodeById(selectedId) : null;
};

Cyan.Tree.TreeNode.prototype.getChildren = function ()
{
    return Cyan.clone(this.children);
};

Cyan.Tree.TreeNode.prototype.getValue = function ()
{
    return this.value;
};

Cyan.Tree.TreeNode.prototype.getText = function ()
{
    return this.text;
};

Cyan.Tree.TreeNode.prototype.setLoaded = function ()
{
    if (!this.leaf && !this.children)
        this.children = [];
};

Cyan.Tree.TreeNode.prototype.isLeaf = function ()
{
    return this.leaf;
};

Cyan.Tree.TreeNode.prototype.isLoaded = function ()
{
    return this.leaf || this.children != null;
};

Cyan.Tree.TreeNode.prototype.isExpanded = function ()
{
    return this.expanded;
};

Cyan.Tree.TreeNode.prototype.isLast = function ()
{
    return this.last;
};

Cyan.Tree.TreeNode.prototype.getParent = function ()
{
    return this.parentNode;
};

Cyan.Tree.TreeNode.prototype.render = function (parentDiv, nextNodeDiv)
{
    var id = this.el || Cyan.generateId("cyan_tree_node");
    if (!this.el)
        this.el = id;

    var div = document.createElement("div");
    div.className = "cyan-tree-node";
    div.id = id;
    div.nodeId = this.id;
    if (this.tip)
        div.title = this.tip;

    Cyan.Elements.disableSelection(div);

    if (nextNodeDiv)
        parentDiv.insertBefore(div, nextNodeDiv);
    else
        parentDiv.appendChild(div);

    Cyan.attach(div, "click", this.tree.nodeClick);
    Cyan.attach(div, "dblclick", this.tree.nodeDblClick);

    var node = this, indentDiv1;
    while (node != null && (node != this.tree.rootNode || this.tree.rootVisible))
    {
        var last = node.last;
        var indentDiv = document.createElement("div");

        var className = "cyan-tree-node-indent";
        if (node == this)
        {
            if (node.leaf)
                className += last ? " cyan-tree-elbow-end" : " cyan-tree-elbow";
            else if (node.expanded)
                className += last ? " cyan-tree-elbow-end-minus" : " cyan-tree-elbow-minus";
            else
                className += last ? " cyan-tree-elbow-end-plus" : " cyan-tree-elbow-plus";
        }
        else if (!last)
        {
            className += " cyan-tree-elbow-line";
        }

        indentDiv.className = className;
        if (indentDiv1)
            div.insertBefore(indentDiv, indentDiv1);
        else
            div.appendChild(indentDiv);
        indentDiv1 = indentDiv;

        if (node == this)
            Cyan.attach(indentDiv, "click", this.tree.nodeIndentClick);

        node = node.parentNode;
    }

    var iconDiv = document.createElement("div");
    div.appendChild(iconDiv);
    var icon = this.getIcon();
    var iconClass = this.getIconCls();
    if (iconClass)
        iconClass = iconClass + " cyan-tree-node-icon";
    else
        iconClass = "cyan-tree-node-icon";
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

        if (this.leaf)
            iconSpan.className = "cyan-tree-leaf";
        else if (this.expanded)
            iconSpan.className = "cyan-tree-folder-open";
        else
            iconSpan.className = "cyan-tree-folder";
    }

    var loadingDiv = document.createElement("div");
    loadingDiv.style.display = "none";
    loadingDiv.className = "cyan-tree-node-loading";
    div.appendChild(loadingDiv);

    var checkboxName = this.tree.checkboxName;
    if (checkboxName)
    {
        var checked = this.tree.isChecked(this.value);
        if (checked != null)
        {
            var checkBoxDiv = document.createElement("div");
            checkBoxDiv.className = "cyan-tree-node-checkbox";
            div.appendChild(checkBoxDiv);
            var checkbox = document.createElement("input");
            checkbox.type = this.tree.checkMode || "checkbox";
            checkbox.name = checkboxName;
            checkbox.value = this.id;
            checkbox.checked = checked;
            checkBoxDiv.appendChild(checkbox);
            Cyan.attach(checkbox, "click", this.tree.nodeCheck);
        }
    }

    var textDiv = document.createElement("div");
    var textClass = this.getTextCls();
    if (textClass)
        textClass = textClass + " cyan-tree-node-text";
    else
        textClass = "cyan-tree-node-text";
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

    if (!this.leaf && this.expanded)
    {
        this.renderChildren(false, false);
    }
};

Cyan.Tree.TreeNode.prototype.updateEl = function (descendant)
{
    if (this.el)
    {
        var el = Cyan.$$$(this.el);
        el[0].title = this.tip || "";

        var indentDivs = el.$(".cyan-tree-node-indent");

        var n = indentDivs.length;
        var node = this;
        for (var i = n - 1; i > 0; i--)
        {
            var last = node.last;
            var indentDiv = indentDivs[i];

            var className = "cyan-tree-node-indent";
            if (node == this)
            {
                if (node.leaf)
                    className += last ? " cyan-tree-elbow-end" : " cyan-tree-elbow";
                else if (node.expanded)
                    className += last ? " cyan-tree-elbow-end-minus" : " cyan-tree-elbow-minus";
                else
                    className += last ? " cyan-tree-elbow-end-plus" : " cyan-tree-elbow-plus";
            }
            else if (!last)
            {
                className += " cyan-tree-elbow-line";
            }

            indentDiv.className = className;

            node = node.parentNode;
        }

        var icon = this.getIcon();
        var iconClass = this.getIconCls();
        if (iconClass)
            iconClass = iconClass + " cyan-tree-node-icon";
        else
            iconClass = "cyan-tree-node-icon";
        var iconDiv = el.$(".cyan-tree-node-icon")[0];
        iconDiv.className = iconClass;

        if (icon)
        {
            el.$(".cyan-tree-node-icon span").hide();
            var img = el.$(".cyan-tree-node-icon img")[0];
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
            el.$(".cyan-tree-node-icon img").hide();
            var iconSpan = el.$(".cyan-tree-node-icon span")[0];
            if (iconSpan)
            {
                iconSpan.style.display = "";
            }
            else
            {
                iconSpan = document.createElement("span");
                iconDiv.appendChild(iconSpan);
            }

            if (this.leaf)
                iconSpan.className = "cyan-tree-leaf";
            else if (this.expanded)
                iconSpan.className = "cyan-tree-folder-open";
            else
                iconSpan.className = "cyan-tree-folder";
        }

        var checkboxName = this.tree.checkboxName;
        if (checkboxName)
        {
            var checked = this.tree.isChecked(this.value);
            var checkBoxDiv = el.$(".cyan-tree-node-checkbox")[0];
            if (checked != null)
            {
                if (checkBoxDiv)
                {
                    checkBoxDiv.style.display = "";
                    el.$(".cyan-tree-node-checkbox :checkbox").check(checked);
                }
                else
                {
                    checkBoxDiv = document.createElement("div");
                    checkBoxDiv.className = "cyan-tree-node-checkbox";
                    el[0].insertBefore(checkBoxDiv, iconDiv.nextSibling);
                    var checkbox = document.createElement("input");
                    checkbox.type = this.tree.checkMode || "checkbox";
                    checkbox.name = checkboxName;
                    checkbox.value = this.id;
                    checkbox.checked = checked;
                    checkBoxDiv.appendChild(checkbox);
                    checkbox.onclick = this.tree.nodeCheck;
                }
            }
            else if (checkBoxDiv)
            {
                checkBoxDiv.style.display = "none";
                el.$(".cyan-tree-node-checkbox :checkbox").check(false);
            }
        }

        var textClass = this.getTextCls();
        if (textClass)
            textClass = textClass + " cyan-tree-node-text";
        else
            textClass = "cyan-tree-node-text";
        var textDiv = el.$(".cyan-tree-node-text")[0];
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

        if (this.leaf && this.childrenRendered)
        {
            var childrenDiv = this.getChildrenDiv();
            childrenDiv.parentNode.removeChild(childrenDiv);
            this.childrenRendered = false;
        }

        if (descendant && this.childrenRendered)
        {
            n = this.children.length;
            for (i = 0; i < n; i++)
                this.children[i].updateEl(true);
        }
    }
};

Cyan.Tree.TreeNode.prototype.getChildrenDiv = function ()
{
    var el = Cyan.$(this.el);
    var nextSibling = el.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-tree-sub")
        return nextSibling;
};

Cyan.Tree.TreeNode.prototype.renderChildren = function ()
{
    var el = Cyan.$(this.el);
    var nextSibling = el.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-tree-sub")
        return nextSibling;

    var childNodesDiv = document.createElement("div");
    childNodesDiv.className = "cyan-tree-sub";

    if (nextSibling)
        el.parentNode.insertBefore(childNodesDiv, nextSibling);
    else
        el.parentNode.appendChild(childNodesDiv);

    var children = this.getChildren();
    var n = children.length;
    for (var i = 0; i < n; i++)
    {
        var child = children[i];
        child.render(childNodesDiv);
    }

    this.childrenRendered = true;

    return childNodesDiv;
};

Cyan.Tree.TreeNode.prototype.startLoading = function ()
{
    var el = Cyan.$$$(this.el);
    el.$(".cyan-tree-node-icon").hide();
    el.$(".cyan-tree-node-loading").show();
};

Cyan.Tree.TreeNode.prototype.stopLoading = function ()
{
    var el = Cyan.$$$(this.el);
    el.$(".cyan-tree-node-loading").hide();
    el.$(".cyan-tree-node-icon").show();
};

Cyan.Tree.TreeNode.prototype.expand = function (deep, anim, callback)
{
    if (this.loading || this.leaf)
        return;

    if (this.expanded)
    {
        if (callback)
            callback();
        return;
    }

    this.expanded = true;
    this.loading = true;

    var node = this;
    var f = function ()
    {
        var last = node.last;
        var el = Cyan.$$$(node.el);
        el.$(".cyan-tree-node-indent:last").set("className",
                "cyan-tree-node-indent " + (last ? " cyan-tree-elbow-end-minus" : " cyan-tree-elbow-minus"));
        el.$(".cyan-tree-folder").set("className", "cyan-tree-folder-open");

        var subNodesDiv = node.renderChildren();
        subNodesDiv.style.display = "";
        if (anim)
        {
            subNodesDiv.style.display = "none";
            subNodesDiv.style.height = node.tree.getHeight(subNodesDiv) + "px";
            var fadeIn = new Cyan.Render.PositionFade(subNodesDiv);
            fadeIn.onComplete = function ()
            {
                subNodesDiv.style.height = "";
                node.loading = false;
                if (callback)
                    callback();
            };
            fadeIn.fadeIn(1, 2);
        }
        else
        {
            node.loading = false;
            if (callback)
                callback();
        }
    };

    if (this.children)
    {
        f();
    }
    else
    {
        this.startLoading();
        this.tree.getLoader().load(this, function ()
        {
            node.stopLoading();
            f();
        });
    }
};

Cyan.Tree.TreeNode.prototype.collapse = function (deep, anim, callback)
{
    if (this.loading || this.leaf)
        return;

    if (!this.expanded)
    {
        if (callback)
            callback();
        return;
    }

    this.expanded = false;
    this.loading = true;

    var node = this;

    var last = this.last;
    var el = Cyan.$$$(node.el);
    el.$(".cyan-tree-node-indent:last").set("className",
            "cyan-tree-node-indent " + (last ? " cyan-tree-elbow-end-plus" : " cyan-tree-elbow-plus"));
    el.$(".cyan-tree-folder-open").set("className", "cyan-tree-folder");

    var nodeEl = Cyan.$(this.el);
    var nextSibling = nodeEl.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-tree-sub")
    {
        var subNodesDiv = nextSibling;
        if (anim)
        {
            subNodesDiv.style.height = node.tree.getHeight(subNodesDiv) + "px";
            var fadeOut = new Cyan.Render.PositionFade(subNodesDiv);
            fadeOut.onComplete = function ()
            {
                subNodesDiv.style.height = "";
                node.loading = false;

                if (callback)
                    callback();
            };
            fadeOut.fadeOut(1, 2);
        }
        else
        {
            subNodesDiv.style.display = "none";
            node.loading = false;

            if (callback)
                callback();
        }
    }
    else if (callback)
    {
        callback();
    }
};

Cyan.Tree.TreeNode.prototype.select = function ()
{
    this.tree.selectNode(this, false);
};

Cyan.Tree.TreeNode.prototype.isSelected = function ()
{
    return Cyan.Array.contains(this.tree.selected, this.id);
};

Cyan.Tree.TreeNode.prototype.isTopSelected = function ()
{
    var node = this;
    while (node = node.parentNode)
    {
        if (node.isSelected())
            return false;
    }
    return true;
};

Cyan.Tree.TreeNode.prototype.ensureVisible = function ()
{
    if (this.parentNode && (this.parentNode != this.tree.rootNode || this.tree.rootVisible))
    {
        this.parentNode.ensureVisible();
        this.parentNode.expand(false, false);
    }

    Cyan.Elements.scrollYTo(this.tree.el, Cyan.$(this.el));
};

Cyan.Tree.TreeNode.prototype.appendChild = function (node)
{
    return this.insertChildBefore(node, null);
};

Cyan.Tree.TreeNode.prototype.insertChildBefore = function (node, nextNodeId)
{
    var nextNode, nextNodeIndex;
    if (nextNodeId && this.children)
    {
        var n = this.children.length;
        for (var i = 0; i < n; i++)
        {
            var childNode = this.children[i];
            if (childNode.id == nextNodeId)
            {
                nextNode = childNode;
                nextNodeIndex = i;
                break;
            }
        }
    }

    var treeNode = this.tree.toTreeNode(node);

    if (!this.children)
        this.children = [];

    treeNode.parentNode = this;
    if (nextNode)
    {
        Cyan.Array.insert(this.children, nextNodeIndex, treeNode);
    }
    else
    {
        treeNode.last = true;
        this.children.push(treeNode);
    }

    if (this.childrenRendered)
        treeNode.render(this.getChildrenDiv(), nextNode ? Cyan.$(nextNode.el) : null);
    else if (this == this.tree.rootNode && !this.tree.rootVisible)
        treeNode.render(Cyan.$$(this.tree.el).$(".cyan-tree")[0], nextNode ? Cyan.$(nextNode.el) : null);

    if (this.leaf)
    {
        this.leaf = false;
        this.updateEl(false);
    }
    else if (this.children.length > 1 && !nextNode)
    {
        var lastChild = this.children[this.children.length - 2];
        lastChild.last = false;
        lastChild.updateEl(true);
    }

    return treeNode;
};

Cyan.Tree.TreeNode.prototype.update = function (node)
{
    this.value = node;
    this.text = node.text;
    this.tip = node.tip;
    this.leaf = node.leaf;
    if (this.leaf)
    {
        this.children = null;
        this.expanded = false;
    }
    this.updateEl(false);
};

Cyan.Tree.TreeNode.prototype.setText = function (text)
{
    this.text = text;
    if (this.el)
        Cyan.$$$(this.el).$(".cyan-tree-node-text").html(text);
};

Cyan.Tree.TreeNode.prototype.getNextNode = function ()
{
    if (this.last && !this.parentNode)
        return null;

    var nodes = this.parentNode.children;
    if (nodes)
    {
        var n = nodes.length;
        for (var i = 0; i < n; i++)
        {
            if (nodes[i] == this)
                return i < n - 1 ? nodes[i + 1] : null;
        }
    }
    return null;
};

Cyan.Tree.TreeNode.prototype.getPreviousNode = function ()
{
    var nodes = this.parentNode.children;
    if (nodes)
    {
        var n = nodes.length;
        for (var i = 0; i < n; i++)
        {
            if (nodes[i] == this)
                return i ? nodes[i - 1] : null;
        }
    }
    return null;
};

Cyan.Tree.TreeNode.prototype.remove = function ()
{
    var tree = this.tree;
    if (this == tree.rootNode)
        return;

    this.through(function ()
    {
        tree.loadedNodes[this.id] = null;
    }, true);

    var selectedNode;
    var previousNode = this.getPreviousNode();
    if (tree.selected.length == 1 && tree.selected[0] == this.id)
    {
        if (previousNode)
        {
            selectedNode = previousNode;
        }
        else
        {
            var nextNode = this.getNextNode();
            if (nextNode)
                selectedNode = nextNode;
            else
                selectedNode = this.parentNode;
        }
        tree.selected = [];
    }
    else
    {
        Cyan.Array.removeElement(tree.selected, this.id);
    }

    var children = this.parentNode.children;
    Cyan.Array.removeElement(children, this);

    if (this.el)
    {
        var el = Cyan.$(this.el);
        var nextSibling = el.nextSibling;
        if (nextSibling && nextSibling.className == "cyan-tree-sub")
            el.parentNode.removeChild(nextSibling);
        el.parentNode.removeChild(el);
    }

    if (children.length == 0)
    {
        this.parentNode.leaf = true;
        this.parentNode.updateEl(false);
    }
    else if (this.last && previousNode)
    {
        previousNode.last = true;
        previousNode.updateEl(true);
    }

    if (selectedNode && (selectedNode != tree.rootNode || tree.rootVisible))
        selectedNode.select();
};

Cyan.Tree.TreeNode.prototype.move = function (offest)
{
    if (offest == 0)
        return false;

    var parentNode = this.parentNode;
    var nodes = parentNode.children;
    var index = Cyan.Array.indexOf(nodes, this);
    var position = index + offest;

    if (position < 0)
        position = 0;

    if (position >= nodes.length)
        position = nodes.length - 1;

    if (position == index)
        return false;

    var other = nodes[position];
    nodes[position] = this;
    nodes[index] = other;

    if (this.last)
    {
        this.last = false;
        other.last = true;
        this.updateEl(true);
        other.updateEl(true);
    }
    else if (other.last)
    {
        this.last = true;
        other.last = false;
        this.updateEl(true);
        other.updateEl(true);
    }

    if (this.el)
    {
        var sub, sub1;
        var el = Cyan.$((offest < 0 ? this : other).el);
        var el1 = Cyan.$((offest < 0 ? other : this).el);
        var nextSibling = el.nextSibling;
        if (nextSibling && nextSibling.className == "cyan-tree-sub")
        {
            sub = nextSibling;
            nextSibling = sub.nextSibling;
        }

        el.parentNode.insertBefore(el, el1);
        if (sub)
            el.parentNode.insertBefore(sub, el1);

        if (el1.nextSibling && el1.nextSibling.className == "cyan-tree-sub")
        {
            sub1 = el1.nextSibling;
            if (nextSibling)
                el.parentNode.insertBefore(sub1, nextSibling);
            else
                el.parentNode.appendChild(sub1);
            nextSibling = sub1;
        }

        if (nextSibling)
            el.parentNode.insertBefore(el1, nextSibling);
        else
            el.parentNode.appendChild(el1);
    }
    return true;
};

Cyan.Tree.TreeNode.prototype.moveTo = function (parent, afterNode)
{
};