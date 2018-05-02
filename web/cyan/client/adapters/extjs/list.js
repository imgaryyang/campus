Cyan.List.prototype.init = function (el)
{
    if (this.checkMode != "checkbox" && this.checkMode != "radio")
        this.checkMode = "checkbox";

    var list = this;
    Cyan.onload(function ()
    {
        list.init0(el);
    });
};
Cyan.List.prototype.init0 = function (el)
{
    if (!Cyan.Extjs.ListNodeUI)
    {
        Cyan.Extjs.ListNodeUI = Ext.extend(Ext.ux.CyanTreeNodeUI, {
            isBoxChecked: function (n, a)
            {
                return n.getOwnerTree().cyanList.isChecked(a);
            },
            getIcon: function (n, a)
            {
                var list = n.getOwnerTree().cyanList;
                return list.getIcon(a) || list.icon;
            },
            getIconCls: function (n, a)
            {
                var list = n.getOwnerTree().cyanList;
                return list.getIconCls(a) || list.iconCls;
            },
            getHref: function (n, a)
            {
                return n.getOwnerTree().cyanList.getHref(a);
            },
            getHrefTarget: function (n, a)
            {
                var list = n.getOwnerTree().cyanList;
                return list.getHrefTarget(a) || list.hrefTarget;
            },
            getTextCls: function (n, a)
            {
                var list = n.getOwnerTree().cyanList;
                return list.getTextCls(a) || list.textCls;
            },
            getTextStyle: function (n, a)
            {
                var list = n.getOwnerTree().cyanList;
                return list.getTextStyle(a) || list.textStyle;
            }
        });

        Cyan.Extjs.ListNode = Ext.extend(Ext.tree.TreeNode, {
            defaultUI: Cyan.Extjs.ListNodeUI
        });

        Cyan.Extjs.AsyncListNode = Ext.extend(Ext.tree.AsyncTreeNode, {
            defaultUI: Cyan.Extjs.ListNodeUI
        });
    }


    var list = this;
    var config = {
        border: false,
        autoScroll: true,
        rootVisible: false,
        root: this.getExtRoot(),
        selModel: new Ext.tree.DefaultSelectionModel(),
        cyanList: this,
        checkboxName: this.checkboxName,
        checkMode: this.checkMode,
        enableDrag: !!this.enableDrag,
        enableDrop: !!this.enableDrop,
        ddGroup: this.dropGroup || this.ddGroup,
        loader: new Ext.tree.TreeLoader({
            load: function (node, callback)
            {
                var loader = list.getLoader();
                if (loader)
                    loader.load(callback);
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
                Cyan.Widget.DragContext.dragComponent = list;
                Cyan.Widget.DragContext.dragItem = list.getItemById(node.id);
                Cyan.Widget.DragContext.dragIds = [node.id];

                if (list.getStartDrag())
                    return list.startDrag(e.browserEvent);

                return true;
            }
        };
    }
    this.extTree = new Ext.tree.TreePanel(config);
    this.extTree.on("click", function (node)
    {
        return list.click(node.attributes);
    });
    this.extTree.on("dblclick", function (node)
    {
        return list.dblclick(node.attributes);
    });
    this.extTree.oncheck = function (node)
    {
        return list.check(node.attributes);
    };
    this.extTree.selModel.on("selectionchange", function (model, node)
    {
        if (node)
            return list.select(node.attributes);
    });
    if (this.enableDrag)
    {
        this.extTree.on("enddrag", function (extTree, node, e)
        {
            if (list.getEndDrag())
                list.endDrag(e.browserEvent);

            Cyan.Widget.DragContext.dragComponent = null;
            Cyan.Widget.DragContext.dragItem = null;
            Cyan.Widget.DragContext.dragIds = null;
        });
    }
    if (this.enableDrop)
    {
        this.extTree.on("nodedragover", function (e)
        {
            var point = e.point;
            if (Cyan.Widget.DragContext.dragComponent == list && Cyan.Widget.DragContext.dragItem)
            {
                var dragItem = Cyan.Widget.DragContext.dragItem;
                if (point == "above")
                {
                    if (e.target == dragItem.extNode.nextSibling)
                        return false;
                }
                else if (point == "below")
                {
                    if (e.target == dragItem.extNode.previousSibling)
                        return false;
                }
            }

            //设置子节点可以添加
            var n = e.target;
            if (n.leaf)
                n.leaf = false;

            Cyan.Widget.DragContext.dropItem = list.getItemById(e.target.id);
            Cyan.Widget.DragContext.dropId = e.target.id;
            Cyan.Widget.DragContext.dropPoint = point;

            var ret = !list.getOnDragOver() || list.onDragOver(e.rawEvent.browserEvent);

            Cyan.Widget.DragContext.dropItem = null;
            Cyan.Widget.DragContext.dropId = null;
            Cyan.Widget.DragContext.dropPoint = null;

            return ret;
        });
        this.extTree.on("beforenodedrop", function (e)
        {
            if (list.extTree.dropZone.completeDrop != Cyan.List.completeDrop)
                list.extTree.dropZone.completeDrop = Cyan.List.completeDrop;

            Cyan.Widget.DragContext.dropItem = list.getItemById(e.target.id);
            Cyan.Widget.DragContext.dropId = e.target.id;
            Cyan.Widget.DragContext.dropPoint = e.point;

            var ret = !list.getOnDrop() || list.onDrop(e.rawEvent.browserEvent);

            Cyan.Widget.DragContext.dropItem = null;
            Cyan.Widget.DragContext.dropId = null;
            Cyan.Widget.DragContext.dropPoint = null;

            return ret;
        });
    }

    if (this.selectedNodeId)
    {
        setTimeout(function ()
        {
            var node = list.extTree.getNodeById(list.selectedNodeId);
            if (node)
            {
                list.loading = true;
                node.select();
                list.loading = false;
            }
        }, 50);
    }

    this.loaded = true;
};

Cyan.List.prototype.render = function ()
{
    this.extTree.render();
};

Cyan.List.prototype.getExtComponent = function ()
{
    return this.extTree;
};

Cyan.List.prototype.getItems = function ()
{
    var childNodes = this.extTree.getRootNode().childNodes;
    if (childNodes && childNodes.length)
    {
        var items = new Array(childNodes.length);
        for (var i = 0; i < childNodes.length; i++)
            items[i] = this.getItemById(childNodes[i].id);
        return items;
    }
    return [];
};

Cyan.List.prototype.clear = function ()
{
    var childNodes = this.extTree.getRootNode().childNodes;
    if (childNodes && childNodes.length)
    {
        while (childNodes.length > 0)
            childNodes[0].remove();
    }
    for (var name in this.loadedItems)
        this.loadedItems[name] = null;
};

/**
 * 添加一个item
 * @param item 要添加的item
 */
Cyan.List.prototype.addItem = function (item)
{
    this.extTree.getRootNode().appendChild(this.toExtTreeNode(item));
    return this.loadedItems[item.id];
};

/**
 * 在某个节点前面插入一个新节点
 * @param item 要添加的新节点
 * @param itemId 相关节点的id，在此节点前面插入
 */
Cyan.List.prototype.insertItemBefore = function (item, itemId)
{
    this.extTree.getRootNode().insertBefore(this.toExtTreeNode(item), this.extTree.getNodeById(itemId));
    return this.loadedItems[item.id];
};

/**
 * 根据节点id获得树节点
 * @param id
 */
Cyan.List.prototype.getItemById = function (id)
{
    return this.loadedItems ? this.loadedItems[id] : null;
};

/**
 * 生成Extjs能够识别的根节点
 */
Cyan.List.prototype.getExtRoot = function ()
{
    var extRoot;
    extRoot = new Cyan.Extjs.ListNode({id: "", text: ""});
    for (var i = 0; i < this.items.length; i++)
        extRoot.appendChild(this.toExtTreeNode(this.items[i]));

    return extRoot;
};

/**
 * 将node转化为Extjs能够识别的treenode
 * @param item node
 */
Cyan.List.prototype.toExtTreeNode = function (item)
{
    item.qtip = Cyan.escapeHtml(item.tip);
    var extTreeNode = new Cyan.Extjs.ListNode(item);

    if (!this.loadedItems)
        this.loadedItems = {};
    var listItem = new Cyan.List.ListItem(item, this);
    this.loadedItems[item.id] = listItem;

    return listItem.extNode = extTreeNode;
};

/**
 * 获得选中的节点的ID，如果没有选中任何节点返回null
 */
Cyan.List.prototype.getSelectedId = function ()
{
    var extNode = this.extTree.getSelectionModel().getSelectedNode();
    return extNode ? extNode.id : null;
};

/**
 * 获得选中的节点，如果没有选中任何节点返回null
 */
Cyan.List.prototype.getSelectedItem = function ()
{
    var extNode = this.extTree.getSelectionModel().getSelectedNode();
    return extNode ? this.getItemById(extNode.id) : null;
};

Cyan.List.ListItem.prototype.getValue = function ()
{
    return this.extNode.attributes;
};

/**
 * 获得item对应的extjs树节点
 */
Cyan.List.ListItem.prototype.getExtNode = function ()
{
    return this.extNode;
};

/**
 * 获得节点的文本
 */
Cyan.List.ListItem.prototype.getText = function ()
{
    return this.extNode.text;
};

/**
 * 设置节点的文本
 */
Cyan.List.ListItem.prototype.setText = function (text)
{
    this.extNode.setText(text);
};

/**
 * 获得下一个节点
 */
Cyan.List.ListItem.prototype.getNextSibling = function ()
{
    var nextSibling = this.extNode.nextSibling;
    return nextSibling ? this.tree.getItemById(nextSibling.id) : null;
};

/**
 * 获得前一个节点
 */
Cyan.List.ListItem.prototype.getPreviousSibling = function ()
{
    var previousSibling = this.extNode.previousSibling;
    return previousSibling ? this.tree.getItemById(previousSibling.id) : null;
};

/**
 * 选中节点
 */
Cyan.List.ListItem.prototype.select = function ()
{
    this.list.loading = true;
    this.extNode.select();
    this.list.loading = false;
};

/**
 * 判断一个节点是否被选中
 */
Cyan.List.ListItem.prototype.isSelected = function ()
{
    return this.extNode.isSelected();
};

/**
 * 删除此节点
 */
Cyan.List.ListItem.prototype.remove = function ()
{
    var selected = this.extNode.isSelected();
    var selectedNode;
    if (selected)
    {
        if (this.extNode.previousSibling)
            selectedNode = this.extNode.previousSibling;
        else if (this.extNode.nextSibling)
            selectedNode = this.extNode.nextSibling;
        else if (this.extNode.parentNode)
            selectedNode = this.extNode.parentNode;
    }
    this.extNode.remove();
    this.list.loadedItems[this.id] = null;
    if (selectedNode)
    {
        this.list.loading = true;
        selectedNode.select();
        this.list.loading = false;
    }
};

/**
 * 更新节点
 */
Cyan.List.ListItem.prototype.update = function (item)
{
    this.extNode.value = item;
    this.extNode.setText(item.text);
};

/**
 * 移动本节点
 * @param offest 位移，<0表示往上移，>0表示往下移
 */
Cyan.List.ListItem.prototype.move = function (offest)
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

    this.list.loading = true;
    this.extNode.select();
    this.list.loading = false;
    return true;
};

Cyan.List.completeDrop = function (e)
{
    e.dropNode.ui.focus();
    e.target.ui.endDrop();
};

Cyan.List.prototype.getPosition = function ()
{
    var position = this.extTree.getPosition();
    return {x: position[0] + this.extTree.getFrameWidth(), y: position[1] + this.extTree.getFrameHeight()};
};

Cyan.List.prototype.filter = function (fn)
{
    if (!this.treeFilter)
    {
        if (!fn)
            return;
        this.treeFilter = new Ext.tree.TreeFilter(this.extTree, {autoClear: true});
    }
    if (fn)
    {
        var list = this;
        this.treeFilter.filterBy(function (extNode)
        {
            return fn(list.getItemById(extNode.id));
        });
    }
    else
    {
        this.treeFilter.clear();
    }
};

Cyan.List.prototype.clearSelections = function ()
{
    this.extTree.getSelectionModel().clearSelections();
};

Cyan.Class.overwrite(Cyan.List, "setSelectedValues", function (values)
{
    this.inherited(values);
    if (values.length && values[0])
    {
        var nodeId = values[0].toString();
        if (this.extTree)
        {
            var node = this.extTree.getNodeById(nodeId);
            if (node)
            {
                this.loading = true;
                node.select();
                this.loading = false;
            }
        }
        else
        {
            this.selectedNodeId = nodeId;
        }
    }
});

Cyan.importJs("adapters/extjs/treeui.js");