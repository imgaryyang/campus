Cyan.importJs("event.js");

/**
 * 创建TreeTable类
 * name 树型表格的id，必须保证在一个页面中唯一
 * root 树的根节点，为一个树节点，每个树节点包括的属性包括
 *              id 节点的id
 *              value 节点的值对象，是可扩展的自定义对象
 *              children 子节点的集合,children为null表示节点还未加载
 *              leaf 表示节点是否为子节点，true为字节点，false不是字节点
 */
Cyan.TreeTable = Cyan.Class.extend(function (name, root, columns)
{
    this.name = name;
    this.root = root;

    //树节点能否多选，false为单选，true为多选
    this.multiple = false;

    //展开模式，默认为false,表示可以同时展开多个节点，true表示只能展开一个节点
    this.singleExpand = false;

    //是否显示根节点，默认为显示
    this.rootVisible = true;

    //列定义
    this.columns = columns;

    this.flatColumns = [];

    this.headerHeight = 1;
    this.initColumns();
}, Cyan.Widget);

Cyan.TreeTable.prototype.reload = function (callback)
{
    this.getRoot().reloadDescendants(callback);
};

/**
 * 树节点对象
 * @param node 节点信息
 * @param table 树型表格
 */
Cyan.TreeTable.Node = function (node, table)
{
    this.id = node.id;
    this.table = table;
};

/**
 * 引入treetable的适配器
 */
Cyan.importAdapter("treetable");

Cyan.TreeTable.prototype.initColumns = function ()
{
    var n = this.columns.length, columnIndex = 0;
    for (var i = 0; i < n; i++)
    {
        var column = this.columns[i];
        this.initColumn(column, 0, columnIndex);
        columnIndex += column.colSpan;
    }

    for (i = 0; i < n; i++)
    {
        column = this.columns[i];
        column.rowSpan = this.headerHeight - column.rowCount + 1;
        column.rowCount = null;
    }
};


Cyan.TreeTable.prototype.initColumn = function (column, rowIndex, columnIndex)
{
    column.rowIndex = rowIndex;
    column.columnIndex = columnIndex;

    var height = rowIndex + 1;
    if (this.headerHeight < height)
        this.headerHeight = height;

    var rowCount = height;

    if (column.columns)
    {
        column.colSpan = 0;
        var n = column.columns.length;
        for (var i = 0; i < n; i++)
        {
            var subColumn = column.columns[i];
            var rowCount1 = this.initColumn(subColumn, rowIndex + 1, columnIndex + i);
            if (rowCount1 > rowCount)
                rowCount = rowCount1;
            column.colSpan += subColumn.colSpan;
            columnIndex += subColumn.colSpan;
        }
    }
    else
    {
        column.colSpan = 1;
        this.flatColumns.push(column);
        column.dataProvider = column.dataProvider ? Cyan.innerFunction(column.dataProvider) : null;
    }

    if (rowIndex == 0)
        column.rowCount = rowCount;

    return rowCount;
};

/**
 * 获得惰性加载器
 */
Cyan.TreeTable.prototype.getLoader = function ()
{
    if (!this.loader)
    {
        //默认的惰性加载器调用lazyLoadNode方法，将异步返回的结果当作子节点
        var table = this;
        this.loader = {
            load: function (node, callback)
            {
                table.lazyLoadNode(node.id, function (result)
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

Cyan.TreeTable.prototype.getCellDatas = function (node)
{
    var n = this.flatColumns.length;
    var cellDatas = new Array(n);
    for (var i = 0; i < n; i++)
    {
        var dataProvider = this.flatColumns[i].dataProvider;
        cellDatas[i] = dataProvider ? dataProvider.apply(node) : node[i];
    }
    return cellDatas;
};

/**
 * 循环调用节点的每个子节点，如果节点的子节点没有加载则先加载节点
 */
Cyan.TreeTable.Node.prototype.eachChild = function (callable, loadedOnly)
{
    if (!this.isLeaf())
    {
        if (this.isLoaded())
        {
            Cyan.each(this.getChildren(), callable);
        }
        else if (!loadedOnly)
        {
            var loader = this.table.getLoader();
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

/**
 * 遍历环节的所有子节点，包括下级节点
 */
Cyan.TreeTable.Node.prototype.through = function (callable, loadedOnly, finish)
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
            var loader = node.table.getLoader();
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

/**
 * 展开并遍历环节的所有子节点，包括下级节点
 */
Cyan.TreeTable.Node.prototype.expandAndThrough = function (callable, finish)
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

Cyan.TreeTable.Node.prototype.reloadDescendants = function (callback)
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

Cyan.TreeTable.Node.prototype.reloadChildren = function (replace, callback)
{
    if (this.isLeaf() || this.isLoaded())
    {
        var node = this;
        this.table.lazyLoadNode(node.id, function (result)
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
                            nextNode = node.insertChildBefore(newNode, nextNode.id);
                        else
                            nextNode = node.appendChild(newNode);
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

/**
 * 获得节点的某个属性
 * @param node 节点
 * @param name 属性名
 */
Cyan.TreeTable.prototype.getProperty = function (node, name)
{
    return node[name] ? node[name] : node.properties ? node.properties[name] : null;
};