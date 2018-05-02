Cyan.importCss("adapters/extjs/treegrid/treegrid.css");
Cyan.importJs("adapters/extjs/treegrid/TreeGridSorter.js");
Cyan.importJs("adapters/extjs/treegrid/TreeGridColumnResizer.js");
Cyan.importJs("adapters/extjs/treegrid/TreeGridNodeUI.js");
Cyan.importJs("adapters/extjs/treegrid/TreeGridLoader.js");
Cyan.importJs("adapters/extjs/treegrid/TreeGridColumns.js");
Cyan.importJs("adapters/extjs/treegrid/TreeGrid.js");

Cyan.TreeTable.prototype.init = function (el)
{
    if (this.multiple == "false" || this.multiple == null)
        this.multiple = false;

    if (this.singleExpand == "false" || this.singleExpand == null)
        this.singleExpand = false;

    if (this.rootVisible == "false" || this.rootVisible == null)
        this.rootVisible = false;

    var tree = this;
    Cyan.onload(function ()
    {
        tree.init0(el);
    });
};
Cyan.TreeTable.prototype.init0 = function (el)
{
    if (!Cyan.Extjs.TreeTableNode)
    {
        Cyan.Extjs.TreeTableNode = Ext.extend(Ext.tree.TreeNode, {
            defaultUI: Ext.tree.ColumnNodeUI
        });

        Cyan.Extjs.AsyncTreeTableNode = Ext.extend(Ext.tree.AsyncTreeNode, {
            defaultUI: Ext.tree.ColumnNodeUI
        });
    }

    var table = this;

    var columns = [];

    var n = this.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];
        if (column.dataProvider && Cyan.isString(column.dataProvider))
            column.dataProvider = Cyan.innerFunction(column.dataProvider);

        if (!column.id)
            column.id = "column" + i;

        columns.push({
            header: column.title,
            width: parseInt(column.width),
            dataIndex: column.id,
            align: column.align
        });
    }

    var rows;
    if (this.headerHeight > 1)
    {
        rows = new Array(this.headerHeight - 1);
        n = this.columns.length;
        for (i = 0; i < n; i++)
            this.initRows(rows, this.columns[i], 0, true);
    }

    var config = {
        columns: columns,
        border: false,
        enableSort: false,
        enableHdMenu: false,
        columnResize: false,
        autoScroll: false,
        bodyStyle: "overflow-y:auto;overflow-x:hidden",
        rootVisible: this.rootVisible,
        root: this.toExtTreeNode(this.root),
        el: el,
        selModel: this.multiple ? new Ext.tree.MultiSelectionModel() : new Ext.tree.DefaultSelectionModel(),
        singleExpand: this.singleExpand,
        cyanTreeTable: this,
        rows: rows,
        loader: new Ext.tree.TreeLoader({
            load: function (node, callback)
            {
                var loader = table.getLoader();
                if (loader)
                    loader.load(table.loadedNodes[node.id], callback);
                else
                    callback();
            }
        })
    };
    this.extTree = new Ext.ux.tree.TreeGrid(config);
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
                if (!this.extTree.getRootNode().leaf)
                    this.extTree.getRootNode().expand(false, false);
            }, this);
        }
    }
};

Cyan.TreeTable.prototype.render = function ()
{
    this.extTree.render();
};

Cyan.TreeTable.prototype.getExtComponent = function ()
{
    return this.extTree;
};

Cyan.TreeTable.prototype.getRoot = function ()
{
    return this.loadedNodes[this.root.id];
};

/**
 * 根据节点id获得树节点
 * @param id
 */
Cyan.TreeTable.prototype.getNodeById = function (id)
{
    return this.loadedNodes[id];
};

/**
 * 将node转化为Extjs能够识别的treenode
 * @param node node
 */
Cyan.TreeTable.prototype.toExtTreeNode = function (node)
{
    this.initNode(node);

    if (this.loadedNodes && this.loadedNodes[node.id])
    {
        this.loadedNodes[node.id].update(node);

        return this.loadedNodes[node.id].extNode;
    }

    var extTreeNode;
    if (node.leaf)
    {
        extTreeNode = new Cyan.Extjs.TreeTableNode(node);
    }
    else if (node.children)
    {
        extTreeNode = new Cyan.Extjs.TreeTableNode(node);
        for (var i = 0; i < node.children.length; i++)
            extTreeNode.appendChild(this.toExtTreeNode(node.children[i]));
    }
    else
    {
        extTreeNode = new Cyan.Extjs.AsyncTreeTableNode(node);
    }

    if (!this.loadedNodes)
        this.loadedNodes = {};
    var treeNode = new Cyan.TreeTable.Node(node, this);
    this.loadedNodes[node.id] = treeNode;

    return treeNode.extNode = extTreeNode;
};

/**
 * 获得被选中的节点，当多选时返回多个，当没有选中时返回空数组
 */
Cyan.TreeTable.prototype.getSelectedNodes = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var nodes = new Array(extNodes.length);
            for (var i = 0; i < extNodes.length; i++)
                nodes[i] = this.getNodeById(extNodes[i].id);
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
 * 获得选中的节点的ID，如果是多选，返回第一个节点的ID，如果没有选中任何节点返回null
 */
Cyan.TreeTable.prototype.getSelectedId = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        return extNodes && extNodes.length ? extNodes[0].id : null;
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode ? extNode.id : null;
    }
};

/**
 * 获得被选中的节点的id，当多选时返回多个，当没有选中时返回空数组
 */
Cyan.TreeTable.prototype.getSelectedIds = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        if (extNodes && extNodes.length)
        {
            var ids = new Array(extNodes.length);
            for (var i = 0; i < extNodes.length; i++)
                ids[i] = extNodes[i].id;
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

/**
 * 获得选中的节点，如果是多选，返回第一个，如果没有选中任何节点返回null
 */
Cyan.TreeTable.prototype.getSelectedNode = function ()
{
    if (this.multiple)
    {
        //多选，extjs支持getSelectedNodes
        var extNodes = this.extTree.getSelectionModel().getSelectedNodes();
        return extNodes && extNodes.length ? this.getNodeById(extNodes[0].id) : null;
    }
    else
    {
        //单选
        var extNode = this.extTree.getSelectionModel().getSelectedNode();
        return extNode ? this.getNodeById(extNode.id) : null;
    }
};

Cyan.TreeTable.Node.prototype.getValue = function ()
{
    return this.extNode.attributes;
};

/**
 * 获得树节点对应的extjs树节点
 */
Cyan.TreeTable.Node.prototype.getExtNode = function ()
{
    return this.extNode;
};

/**
 * 获得父节点
 */
Cyan.TreeTable.Node.prototype.getParent = function ()
{
    var parentNode = this.extNode.parentNode;
    return parentNode ? this.table.getNodeById(parentNode.id) : null;
};

/**
 * 获得下一个节点
 */
Cyan.TreeTable.Node.prototype.getNextSibling = function ()
{
    var nextSibling = this.extNode.nextSibling;
    return nextSibling ? this.table.getNodeById(nextSibling.id) : null;
};

/**
 * 获得前一个节点
 */
Cyan.TreeTable.Node.prototype.getPreviousSibling = function ()
{
    var previousSibling = this.extNode.previousSibling;
    return previousSibling ? this.table.getNodeById(previousSibling.id) : null;
};

/**
 * 获得第一个子节点
 */
Cyan.TreeTable.Node.prototype.getFirstChild = function ()
{
    var firstChild = this.extNode.firstChild;
    return firstChild ? this.table.getNodeById(firstChild.id) : null;
};

/**
 * 获得子节点，只包括那些已经加载的子节点
 */
Cyan.TreeTable.Node.prototype.getChildren = function ()
{
    var childNodes = this.extNode.childNodes;
    if (childNodes && childNodes.length)
    {
        var nodes = new Array(childNodes.length);
        for (var i = 0; i < childNodes.length; i++)
            nodes[i] = this.table.getNodeById(childNodes[i].id);
        return nodes;
    }
    return [];
};

/**
 * 选中节点
 */
Cyan.TreeTable.Node.prototype.select = function ()
{
    this.extNode.select();
};

/**
 * 判断一个节点是否已经加载子节点
 */
Cyan.TreeTable.Node.prototype.isLoaded = function ()
{
    return !this.extNode.isLoaded || this.extNode.isLoaded();
};

/**
 * 判断一个节点是否是叶子节点
 */
Cyan.TreeTable.Node.prototype.isLeaf = function ()
{
    return this.extNode.leaf;
};

/**
 * 判断一个节点是否被选中
 */
Cyan.TreeTable.Node.prototype.isSelected = function ()
{
    return this.extNode.isSelected();
};

/**
 * 展开节点
 * @param deep 是否展开子节点，true为展开子节点，false为不展开子节点
 * @param callback 展开之后的动作，当deep为false时有效
 */
Cyan.TreeTable.Node.prototype.expand = function (deep, anim, callback)
{
    this.extNode.expand(deep, anim, deep ? null : callback);
};

/**
 * 收起节点
 * @param deep 是否收起子节点，true为收起子节点，false为不收起子节点
 */
Cyan.TreeTable.Node.prototype.collapse = function (deep, anim)
{
    this.extNode.collapse(deep, anim);
};

/**
 * 判断节点是否张开
 */
Cyan.TreeTable.Node.prototype.isExpanded = function ()
{
    return this.extNode.isExpanded();
};

/**
 * 确保节点可见，将节点及节点的父节点展开，并将滚动条滚动到相应的位置
 */
Cyan.TreeTable.Node.prototype.ensureVisible = function ()
{
    this.extNode.ensureVisible();
};

Cyan.TreeTable.Node.prototype.setLoaded = function ()
{
    this.extNode.loaded = true;
};

/**
 * 添加一个子节点
 * @param node 要添加的子节点
 */
Cyan.TreeTable.Node.prototype.appendChild = function (node)
{
    this.extNode.appendChild(this.table.toExtTreeNode(node));
    if (this.extNode.leaf)
    {
        this.extNode.leaf = false;
        this.extNode.collapse(false, false);
    }
    return this.table.loadedNodes[node.id];
};

/**
 * 在某个节点前面插入一个新节点
 * @param node 要添加的新节点
 * @param refNodeId 相关节点的id，在此节点前面插入
 */
Cyan.TreeTable.Node.prototype.insertChildBefore = function (node, refNodeId)
{
    this.extNode.insertBefore(this.table.toExtTreeNode(node), this.table.extTree.getNodeById(refNodeId));
    return this.table.loadedNodes[node.id];
};

/**
 * 删除此节点
 */
Cyan.TreeTable.Node.prototype.remove = function ()
{
    var table = this.table;
    this.through(function ()
    {
        table.loadedNodes[this.id] = null;
    }, true);

    var selected = this.extNode.isSelected();
    var selectedNode;
    if (selected && (!this.table.multiple || this.table.extTree.getSelectionModel().getSelectedNodes().length == 1))
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
Cyan.TreeTable.Node.prototype.update = function (node)
{
    this.extNode.value = node = this.table.initNode(node);

    var tree = this.table.extTree;
    var cols = tree.columns;
    var cs = this.extNode.ui.wrap.childNodes[0].childNodes;
    for (var i = 0; i < cols.length; i++)
    {
        var col = cols[i];
        ( i ? cs[i].childNodes[0] : this.extNode.ui.textNode).innerHTML =
                col.renderer ? col.renderer(node[col.dataIndex], this.extNode, node) : node[col.dataIndex];
    }
};

Cyan.TreeTable.prototype.initNode = function (node)
{
    var n = this.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];
        var dataProvider = column.dataProvider;
        if (dataProvider)
            node[column.id] = dataProvider.apply(node);
        else
            node[column.id] = node[i];
    }
    return node;
};

Cyan.TreeTable.prototype.initRows = function (rows, column, rowIndex, b)
{
    var row = rows[rowIndex];
    if (!row)
        rows[rowIndex] = row = [];

    if (column.columns)
    {
        var n = column.columns.length;
        b = false;
        var subColumn, i;
        for (i = 0; i < n; i++)
        {
            subColumn = column.columns[i];
            b |= subColumn.columns != null;
        }

        for (i = 0; i < n; i++)
        {
            subColumn = column.columns[i];
            this.initRows(rows, subColumn, rowIndex + 1, b);
        }

        row.push({
            header: column.title, colspan: column.colSpan, rowspan: b ? 1 :
            this.headerHeight - column.rowIndex - 1, align: 'center'
        });
    }
    else if (b && column.rowIndex < this.headerHeight - 1)
    {
        row.push({header: "&nbsp;", colspan: 1, rowspan: this.headerHeight - 1 - column.rowIndex, align: 'center'});
    }
};