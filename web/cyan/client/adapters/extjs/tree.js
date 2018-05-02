Ext.QuickTips.init();

Cyan.Tree.prototype.init0 = function (el)
{
    if (!Cyan.Extjs.TreeNodeUI)
    {
        Cyan.Extjs.TreeNodeUI = Ext.extend(Ext.ux.CyanTreeNodeUI, {
            isBoxChecked: function (n, a)
            {
                return n.getOwnerTree().cyanTree.isChecked(a);
            },
            getIcon: function (n, a)
            {
                var tree = n.getOwnerTree().cyanTree;
                return tree.getIcon(a) || tree.icon;
            },
            getIconCls: function (n, a)
            {
                var tree = n.getOwnerTree().cyanTree;
                return tree.getIconCls(a) || tree.iconCls;
            },
            getHref: function (n, a)
            {
                return n.getOwnerTree().cyanTree.getHref(a);
            },
            getHrefTarget: function (n, a)
            {
                var tree = n.getOwnerTree().cyanTree;
                return tree.getHrefTarget(a) || tree.hrefTarget;
            },
            getTextCls: function (n, a)
            {
                var tree = n.getOwnerTree().cyanTree;
                return tree.getTextCls(a) || tree.textCls;
            },
            getTextStyle: function (n, a)
            {
                var tree = n.getOwnerTree().cyanTree;
                return tree.getTextStyle(a) || tree.textStyle;
            }
        });

        Cyan.Extjs.TreeNode = Ext.extend(Ext.tree.TreeNode, {
            defaultUI: Cyan.Extjs.TreeNodeUI
        });

        Cyan.Extjs.AsyncTreeNode = Ext.extend(Ext.tree.AsyncTreeNode, {
            defaultUI: Cyan.Extjs.TreeNodeUI
        });
    }

    var tree = this;
    var config = {
        border: false,
        autoScroll: true,
        rootVisible: this.rootVisible,
        root: this.toExtTreeNode(this.root),
        selModel: this.multiple ? new Ext.tree.MultiSelectionModel() : new Ext.tree.DefaultSelectionModel(),
        singleExpand: this.singleExpand,
        cyanTree: this,
        checkboxName: this.checkboxName,
        checkMode: this.checkMode,
        enableDrag: !!this.enableDrag,
        enableDrop: !!this.enableDrop,
        ddGroup: this.dropGroup || this.ddGroup,
        loader: new Ext.tree.TreeLoader({
            load: function (node, callback)
            {
                var loader = tree.getLoader();
                if (loader)
                    loader.load(tree.loadedNodes[node.id], callback);
                else
                    callback();
            }
        })
    };
    if (this.autoRender && el)
        config.el = el;
    if (this.enableDrag)
    {
        config.dragConfig = {
            ddGroup: this.dragGroup || this.ddGroup,
            onBeforeDrag: function (data, e)
            {
                var node = data.node;
                //记录当前拖动上下文
                Cyan.Widget.DragContext.dragComponent = tree;
                Cyan.Widget.DragContext.dragNode = tree.getNodeById(node.id);
                Cyan.Widget.DragContext.dragIds = [node.id];

                if (tree.getStartDrag())
                    return tree.startDrag(e.browserEvent);

                return true;
            }
        };
    }

    if (this.autoRender && Cyan.$(el))
    {
        var size = Cyan.Elements.getComponentSize(Cyan.$(el));
        if (size.width)
            config.width = size.width;
        if (size.height)
            config.height = size.height;
    }

    this.extTree = new Ext.tree.TreePanel(config);
    this.extTree.on("click", function (node)
    {
        return tree.click(node.attributes);
    });
    this.extTree.on("dblclick", function (node)
    {
        return tree.dblclick(node.attributes);
    });
    this.extTree.oncheck = function (node)
    {
        return tree.check(node.attributes);
    };
    if (!this.multiple)
    {
        this.extTree.selModel.on("selectionchange", function (model, node)
        {
            if (node && node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                return tree.select(node.attributes);
        });
    }
    if (this.enableDrag)
    {
        this.extTree.on("enddrag", function (extTree, node, e)
        {
            if (tree.getEndDrag())
                tree.endDrag(e.browserEvent);

            Cyan.Widget.DragContext.dragComponent = null;
            Cyan.Widget.DragContext.dragNode = null;
            Cyan.Widget.DragContext.dragIds = null;
        });
    }
    if (this.enableDrop)
    {
        this.extTree.on("nodedragover", function (e)
        {
            var point = e.point;
            if (Cyan.Widget.DragContext.dragComponent == tree && Cyan.Widget.DragContext.dragNode)
            {
                var dragNode = Cyan.Widget.DragContext.dragNode;
                if (point == "above")
                {
                    if (e.target == dragNode.extNode.nextSibling)
                        return false;
                }
                else if (point == "below")
                {
                    if (e.target == dragNode.extNode.previousSibling)
                        return false;
                }
            }

            //设置子节点可以添加
            var n = e.target;
            if (n.leaf)
                n.leaf = false;

            Cyan.Widget.DragContext.dropNode = tree.getNodeById(e.target.id);
            Cyan.Widget.DragContext.dropId = e.target.id;
            Cyan.Widget.DragContext.dropPoint = point;

            var ret = !tree.getOnDragOver() || tree.onDragOver(e.rawEvent.browserEvent);

            Cyan.Widget.DragContext.dropNode = null;
            Cyan.Widget.DragContext.dropId = null;
            Cyan.Widget.DragContext.dropPoint = null;

            return ret;
        });
        this.extTree.on("beforenodedrop", function (e)
        {
            if (tree.extTree.dropZone.completeDrop != Cyan.Tree.completeDrop)
                tree.extTree.dropZone.completeDrop = Cyan.Tree.completeDrop;

            Cyan.Widget.DragContext.dropNode = tree.getNodeById(e.target.id);
            Cyan.Widget.DragContext.dropId = e.target.id;
            Cyan.Widget.DragContext.dropPoint = e.point;

            var ret = !tree.getOnDrop() || tree.onDrop(e.rawEvent.browserEvent);

            Cyan.Widget.DragContext.dropNode = null;
            Cyan.Widget.DragContext.dropId = null;
            Cyan.Widget.DragContext.dropPoint = null;

            return ret;
        });
    }

    if (this.name)
    {
        //创建保存选择节点的select
        if (this.multiple)
        {
            var select = document.createElement("SELECT");
            select.name = this.name + ".selected";
            select.id = select.name;
            select.size = 3;
            select.multiple = true;
            //display="none"之后在ie下不能多选
            select.style.position = "absolute";
            select.style.visibility = "hidden";
            select.style.width = "0";
            select.style.height = "0";
            Cyan.$(el).appendChild(select);
            this.extTree.getSelectionModel().on("selectionchange", function (model, nodes)
            {
                var select = Cyan.$(tree.name + ".selected");
                select.options.length = 0;
                if (nodes && nodes.length)
                {
                    for (var i = 0; i < nodes.length; i++)
                    {
                        var node = nodes[i];
                        if (node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                        {
                            var option = new Option("", nodes[i].id);
                            select.options[i] = option;
                            option.selected = true;
                        }
                    }
                }
            });
        }
        else
        {
            if (Cyan.$(el))
            {
                var input = document.createElement("INPUT");
                input.name = this.name + ".selected";
                input.id = input.name;
                input.type = "hidden";
                Cyan.$(el).appendChild(input);
                this.extTree.getSelectionModel().on("selectionchange", function (model, node)
                {
                    if (node && node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                        Cyan.$(tree.name + ".selected").value = node.id;
                });
            }
        }
    }

    if (this.autoRender)
        this.extTree.render();

    if (this.rootVisible)
    {
        if (this.autoRender)
        {
            if (!this.extTree.getRootNode().leaf)
                this.extTree.getRootNode().expand(false, false);
        }
        else
        {
            this.extTree.on('render', function ()
            {
                var root = this.extTree.getRootNode();
                if (!root.leaf && root.isLoaded && !root.isLoaded())
                {
                    root.expand(false, false);
                }
            }, this);
        }
    }
};

Cyan.Tree.prototype.render = function ()
{
    this.extTree.render();
};

Cyan.Tree.prototype.getExtComponent = function ()
{
    return this.extTree;
};

Cyan.Tree.prototype.getRoot = function ()
{
    if (this.loadedNodes)
        return this.loadedNodes[this.root.id];
    else
        return null;
};

/**
 * 根据节点id获得树节点
 * @param id
 */
Cyan.Tree.prototype.getNodeById = function (id)
{
    if (this.loadedNodes)
        return this.loadedNodes[id];
    else
        return null;
};

/**
 * 将node转化为Extjs能够识别的treenode
 * @param node node
 */
Cyan.Tree.prototype.toExtTreeNode = function (node)
{
    node.qtip = Cyan.escapeHtml(node.tip);
    var extTreeNode;
    var id = node.id;
    if (node.leaf)
    {
        extTreeNode = new Cyan.Extjs.TreeNode(node);
    }
    else if (node.children)
    {
        extTreeNode = new Cyan.Extjs.TreeNode(node);
        for (var i = 0; i < node.children.length; i++)
            extTreeNode.appendChild(this.toExtTreeNode(node.children[i]));
    }
    else
    {
        extTreeNode = new Cyan.Extjs.AsyncTreeNode(node);
    }

    if (!this.loadedNodes)
        this.loadedNodes = {};
    var treeNode = new Cyan.Tree.TreeNode(node, this, id);
    this.loadedNodes[node.id] = treeNode;

    return treeNode.extNode = extTreeNode;
};

/**
 * 获得被选中的节点，当多选时返回多个，当没有选中时返回空数组
 */
Cyan.Tree.prototype.getSelectedNodes = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var nodes = [];
            for (var i = 0; i < extNodes.length; i++)
            {
                var node = extNodes[i];
                if (node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                {
                    nodes[i] = this.getNodeById(node.id);
                }
            }
            return nodes;
        }
        return [];
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode && extNode.getOwnerTree().cyanTree.isSelectable(extNode.attributes) ?
                [this.getNodeById(extNode.id)] : [];
    }
};

/**
 * 获得选中的节点的ID，如果是多选，返回第一个节点的ID，如果没有选中任何节点返回null
 */
Cyan.Tree.prototype.getSelectedId = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();

        if (extNodes)
        {
            var n = extNodes.length;
            for (var i = 0; i < n; i++)
            {
                var node = extNodes[i];
                if (node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                {
                    return node.id;
                }
            }
        }

        return null;
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode && extNode.getOwnerTree().cyanTree.isSelectable(extNode.attributes) ? extNode.id : null;
    }
};

/**
 * 获得被选中的节点的id，当多选时返回多个，当没有选中时返回空数组
 */
Cyan.Tree.prototype.getSelectedIds = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var ids = [];
            for (var i = 0; i < extNodes.length; i++)
            {
                var node = extNodes[i];
                if (node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                    ids.push(extNodes[i].id);
            }
            return ids;
        }
        return [];
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode && extNode.getOwnerTree().cyanTree.isSelectable(extNode.attributes) ? [extNode.id] : [];
    }
};

/**
 * 获得选中的节点，如果是多选，返回第一个，如果没有选中任何节点返回null
 */
Cyan.Tree.prototype.getSelectedNode = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();

        if (extNodes)
        {
            var n = extNodes.length;
            for (var i = 0; i < n; i++)
            {
                var node = extNodes[i];
                if (node.getOwnerTree().cyanTree.isSelectable(node.attributes))
                {
                    return this.getNodeById(node.id);
                }
            }
        }

        return null;
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode && extNode.getOwnerTree().cyanTree.isSelectable(extNode.attributes) ?
                this.getNodeById(extNode.id) : null;
    }
};

Cyan.Tree.isTopSelected = function (node)
{
    while (node = node.parent)
    {
        if (node.isSelected())
            return false;
    }
    return true;
};

/**
 * 获得被选中的节点，当多选时返回多个，如果此节点的某层上级节点被选中则不计算此节点，当没有选中时返回空数组
 */
Cyan.Tree.prototype.getTopSelectedNodes = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var nodes = [];
            for (var i = 0; i < extNodes.length; i++)
            {
                if (Cyan.Tree.isTopSelected(extNodes[i]))
                    nodes.push(this.getNodeById(extNodes[i].id));
            }
            return nodes;
        }
        return [];
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode ? [this.getNodeById(extNode.id)] : [];
    }
};

/**
 * 获得被选中的节点的id，当多选时返回多个，如果此节点的某层上级节点被选中则不计算此节点，当没有选中时返回空数组
 */
Cyan.Tree.prototype.getTopSelectedIds = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var ids = [];
            for (var i = 0; i < extNodes.length; i++)
            {
                if (Cyan.Tree.isTopSelected(extNodes[i]))
                    ids.push(extNodes[i].id);
            }
            return ids;
        }
        return [];
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode ? [extNode.id] : [];
    }
};

Cyan.Tree.prototype.clearSelections = function ()
{
    this.extTree.getSelectionModel().clearSelections();
};

Cyan.Tree.TreeNode.prototype.getValue = function ()
{
    return this.extNode.attributes;
};

/**
 * 获得树节点对应的extjs树节点
 */
Cyan.Tree.TreeNode.prototype.getExtNode = function ()
{
    return this.extNode;
};

/**
 * 获得节点的文本
 */
Cyan.Tree.TreeNode.prototype.getText = function ()
{
    return this.extNode.text;
};

/**
 * 设置节点的文本
 */
Cyan.Tree.TreeNode.prototype.setText = function (text)
{
    this.extNode.setText(text);
};

/**
 * 获得父节点
 */
Cyan.Tree.TreeNode.prototype.getParent = function ()
{
    var parentNode = this.extNode.parentNode;
    return parentNode ? this.tree.getNodeById(parentNode.id) : null;
};

/**
 * 获得下一个节点
 */
Cyan.Tree.TreeNode.prototype.getNextSibling = function ()
{
    var nextSibling = this.extNode.nextSibling;
    return nextSibling ? this.tree.getNodeById(nextSibling.id) : null;
};

/**
 * 获得前一个节点
 */
Cyan.Tree.TreeNode.prototype.getPreviousSibling = function ()
{
    var previousSibling = this.extNode.previousSibling;
    return previousSibling ? this.tree.getNodeById(previousSibling.id) : null;
};

/**
 * 获得第一个子节点
 */
Cyan.Tree.TreeNode.prototype.getFirstChild = function ()
{
    var firstChild = this.extNode.firstChild;
    return firstChild ? this.tree.getNodeById(firstChild.id) : null;
};

/**
 * 获得子节点，只包括那些已经加载的子节点
 */
Cyan.Tree.TreeNode.prototype.getChildren = function ()
{
    var childNodes = this.extNode.childNodes;
    if (childNodes && childNodes.length)
    {
        var nodes = new Array(childNodes.length);
        for (var i = 0; i < childNodes.length; i++)
            nodes[i] = this.tree.getNodeById(childNodes[i].id);
        return nodes;
    }
    return [];
};

/**
 * 选中节点
 */
Cyan.Tree.TreeNode.prototype.select = function ()
{
    this.extNode.select();
};

/**
 * 判断一个节点是否已经加载子节点
 */
Cyan.Tree.TreeNode.prototype.isLoaded = function ()
{
    return !this.extNode.isLoaded || this.extNode.isLoaded();
};

/**
 * 判断一个节点是否是叶子节点
 */
Cyan.Tree.TreeNode.prototype.isLeaf = function ()
{
    return this.extNode.leaf;
};

/**
 * 判断一个节点是否被选中
 */
Cyan.Tree.TreeNode.prototype.isSelected = function ()
{
    return this.extNode.isSelected();
};

/**
 * 展开节点
 * @param deep 是否展开子节点，true为展开子节点，false为不展开子节点
 * @param callback 展开之后的动作，当deep为false时有效
 * @param anim 是否动画
 */
Cyan.Tree.TreeNode.prototype.expand = function (deep, anim, callback)
{
    this.extNode.expand(deep, anim, deep ? null : callback);
};

/**
 * 收起节点
 * @param deep 是否收起子节点，true为收起子节点，false为不收起子节点
 *  @param callback 收起来之后的动作，当deep为false时有效
 * @param anim 是否动画
 */
Cyan.Tree.TreeNode.prototype.collapse = function (deep, anim, callback)
{
    this.extNode.collapse(deep, anim, deep ? null : callback);
};

/**
 * 判断节点是否张开
 */
Cyan.Tree.TreeNode.prototype.isExpanded = function ()
{
    return this.extNode.isExpanded();
};

/**
 * 确保节点可见，将节点及节点的父节点展开，并将滚动条滚动到相应的位置
 */
Cyan.Tree.TreeNode.prototype.ensureVisible = function ()
{
    this.extNode.ensureVisible();
};

Cyan.Tree.TreeNode.prototype.setLoaded = function ()
{
    this.extNode.loaded = true;
};

/**
 * 添加一个子节点
 * @param node 要添加的子节点
 */
Cyan.Tree.TreeNode.prototype.appendChild = function (node)
{
    this.extNode.appendChild(this.tree.toExtTreeNode(node));
    if (this.extNode.leaf)
    {
        this.extNode.leaf = false;
        this.extNode.collapse(false, false);
    }
    return this.tree.loadedNodes[node.id];
};

/**
 * 在某个节点前面插入一个新节点
 * @param node 要添加的新节点
 * @param refNodeId 相关节点的id，在此节点前面插入
 */
Cyan.Tree.TreeNode.prototype.insertChildBefore = function (node, refNodeId)
{
    this.extNode.insertBefore(this.tree.toExtTreeNode(node), this.tree.extTree.getNodeById(refNodeId));
    return this.tree.loadedNodes[node.id];
};

/**
 * 删除此节点
 */
Cyan.Tree.TreeNode.prototype.remove = function ()
{
    var tree = this.tree;
    this.through(function ()
    {
        tree.loadedNodes[this.id] = null;
    }, true);

    var selected = this.extNode.isSelected();
    var selectedNode;
    if (selected && (!this.tree.multiple || this.tree.extTree.getSelectionModel().getSelectedNodes().length == 1))
    {
        if (this.extNode.previousSibling)
            selectedNode = this.extNode.previousSibling;
        else if (this.extNode.nextSibling)
            selectedNode = this.extNode.nextSibling;
        else if (this.extNode.parentNode)
            selectedNode = this.extNode.parentNode;
    }
    this.extNode.remove();
    if (selectedNode)
        selectedNode.select();
};

/**
 * 更新节点
 */
Cyan.Tree.TreeNode.prototype.update = function (node)
{
    this.extNode.value = node;
    this.extNode.setText(node.text);
};

/**
 * 移动本节点
 * @param offest 位移，<0表示往上移，>0表示往下移
 */
Cyan.Tree.TreeNode.prototype.move = function (offest)
{
    if (offest == 0)
        return false;
    var parentNode = this.extNode.parentNode;
    var index = parentNode.indexOf(this.extNode);
    var position = index + offest;

    if (position < 0)
        position = 0;

    if (position >= parentNode.childNodes.length)
        position = parentNode.childNodes.length - 1;

    if (position == index)
        return false;

    if (position == parentNode.childNodes.length - 1)
        parentNode.appendChild(this.extNode);
    else
        parentNode.insertBefore(this.extNode, parentNode.item(offest > 0 ? position + 1 : position));

    this.extNode.select();
    return true;
};

/**
 * 移动本节点到另外一个节点
 * @param parent 新的节点
 * @param afterNode 插入到这个节点前面
 */
Cyan.Tree.TreeNode.prototype.moveTo = function (parent, afterNode)
{
    var parentNode = parent.extNode;
    if (this.extNode == parentNode)
    {
        if (afterNode && this.extNode.nextSibling != afterNode.extNode)
            parentNode.insertBefore(this.extNode, afterNode.extNode);
    }
    else
    {
        if (afterNode)
            parentNode.insertBefore(this.extNode, afterNode.extNode);
        else
            parentNode.appendChild(this.extNode);
    }
};

Cyan.Tree.completeDrop = function (e)
{
    e.dropNode.ui.focus();
    e.target.ui.endDrop();
};

Cyan.Tree.prototype.getPosition = function ()
{
    var position = this.extTree.getPosition();
    return {x: position[0] + this.extTree.getFrameWidth(), y: position[1] + this.extTree.getFrameHeight()};
};

Cyan.Tree.prototype.filter = function (fn)
{
    if (!this.treeFilter)
    {
        if (!fn)
            return;
        this.treeFilter = new Ext.tree.TreeFilter(this.extTree, {autoClear: true});
    }
    if (fn)
    {
        var tree = this;
        this.treeFilter.filterBy(function (extNode)
        {
            return fn(tree.getNodeById(extNode.id));
        });
    }
    else
    {
        this.treeFilter.clear();
    }
};

Cyan.importJs("adapters/extjs/treeui.js");