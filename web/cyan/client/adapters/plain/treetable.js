Cyan.Plain.importCss("treetable");
Cyan.importJs("render.js");
Cyan.importJs("event.js");

Cyan.TreeTable.prototype.defaultColumnWidth = 150;
Cyan.TreeTable.prototype.scrollWidth = 16;

Cyan.TreeTable.prototype.init = function (el)
{
    this.el = Cyan.$(el);

    if (this.multiple == "false" || this.multiple == null)
        this.multiple = false;

    if (this.singleExpand == "false" || this.singleExpand == null)
        this.singleExpand = false;

    if (this.rootVisible == "false" || this.rootVisible == null)
        this.rootVisible = false;

    var treetable = this;
    Cyan.onload(function ()
    {
        treetable.init0(el);
    });
};

Cyan.TreeTable.prototype.init0 = function (el)
{
    this.inited = true;
    this.rootNode = this.toTreeTableNode(this.root);
    this.selected = [];

    var treetable = this;

    var getNodeDiv = function (div)
    {
        while (div.className != "cyan-treetable-node" &&
        div.className != "cyan-treetable-node cyan-treetable-node-selected")
        {
            div = div.parentNode;
        }

        return div;
    };

    this.nodeExpanderClick = function (event)
    {
        var div = getNodeDiv(this);
        var node = treetable.getNodeById(div.nodeId);

        if (!node.leaf)
        {
            if (node.expanded)
                node.collapse(false, true);
            else
                node.expand(false, true);
        }

        event.stop();
    };

    this.nodeClick = function (event)
    {
        var div = getNodeDiv(this);
        var node = treetable.getNodeById(div.nodeId);

        if (treetable.multiple && event.ctrlKey)
        {
            if (node.isSelected())
                treetable.unselectNode(node);
            else
                treetable.selectNode(node, true);
        }
        else
        {
            treetable.selectNode(node, false);
        }
    };

    this.nodeDblClick = function (event)
    {
        var div = getNodeDiv(this);
        var node = treetable.getNodeById(div.nodeId);

        if (!node.leaf)
        {
            if (node.expanded)
                node.collapse(false, true);
            else
                node.expand(false, true);
        }
    };

    var size;
    if (!this.width)
    {
        var width = this.el.style.width;
        if (width && !Cyan.endsWith(width, "%"))
        {
            this.width = Cyan.toInt(width);
        }
        else
        {
            size = Cyan.Elements.getComponentSize(this.el);
            this.width = size.width || 300;
        }
    }
    if (!this.height)
    {
        var height = this.el.style.height;
        if (height && !Cyan.endsWith(height, "%"))
        {
            this.height = Cyan.toInt(height);
        }
        else
        {
            if (!size)
                size = Cyan.Elements.getComponentSize(this.el);
            this.height = size.height || 250;
        }
    }

    this.initAutoExpandColumn();

    if (this.autoRender)
        this.render();
};

Cyan.TreeTable.prototype.toTreeTableNode = function (node)
{
    if (!this.loadedNodes)
        this.loadedNodes = {};

    var treeNode = new Cyan.TreeTable.Node(node, this);
    treeNode.leaf = node.leaf;
    treeNode.value = node;
    if (!node.leaf)
        treeNode.expanded = false;

    this.loadedNodes[node.id] = treeNode;

    if (node.children && node.children.length)
    {
        var n = node.children.length;
        treeNode.children = new Array(n);
        for (var i = 0; i < n; i++)
        {
            var childNode = this.toTreeTableNode(node.children[i]);
            childNode.parentNode = treeNode;
            treeNode.children[i] = childNode;
        }
    }

    return treeNode;
};

Cyan.TreeTable.prototype.initAutoExpandColumn = function ()
{
    var n = this.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];

        var columnWidth = column.width ? Cyan.toInt(column.width) : null;
        if (column.autoExpand ||
                this.autoExpandColumn == null && !columnWidth && !column.locked && column.align == "left")
        {
            this.autoExpandColumn = i;
            this.autoExpandMin = columnWidth || this.defaultColumnWidth;
        }
    }
};

Cyan.TreeTable.prototype.initWidths = function ()
{
    var tableWidth = 0, autoExpandWidth, emptyWidth = 0;
    var n = this.flatColumns.length;

    this.oldColumnWidths = this.columnWidths;
    this.oldEmptyWidth = this.emptyWidth;
    this.columnWidths = new Array(n);

    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];

        var columnWidth = column.width ? Cyan.toInt(column.width) : null;
        if (this.autoExpandColumn != i)
        {
            if (!columnWidth && this.autoExpandColumn != null)
                columnWidth = this.defaultColumnWidth;

            if (columnWidth)
            {
                this.columnWidths[i] = columnWidth;
                tableWidth += columnWidth;
            }
        }
    }
    tableWidth += this.scrollWidth;
    if (tableWidth < this.width)
    {
        var restWidth = this.width - tableWidth;

        if (this.autoExpandColumn != null)
        {
            autoExpandWidth = restWidth;
            if (autoExpandWidth < this.autoExpandMin)
                autoExpandWidth = this.autoExpandMin;
            tableWidth += this.columnWidths[this.autoExpandColumn] = autoExpandWidth;
        }
        else
        {
            var m = 0;
            for (i = 0; i < n; i++)
            {
                if (!this.columnWidths[i])
                    m++;
            }

            if (m)
            {
                var w = Math.floor(restWidth / m), x = 0;
                if (w < this.defaultColumnWidth)
                {
                    w = this.defaultColumnWidth;
                }
                else
                {
                    x = restWidth - w * m;
                }

                var first = true;
                for (i = 0; i < n; i++)
                {
                    if (!this.columnWidths[i])
                        tableWidth += this.columnWidths[i] = (x-- > 0) ? w + 1 : w;
                }
            }
            else
            {
                emptyWidth = restWidth;
                tableWidth = this.width;
            }
        }
    }
    else
    {
        if (this.autoExpandColumn != null)
        {
            this.columnWidths[this.autoExpandColumn] = autoExpandWidth = this.autoExpandMin;
            tableWidth += autoExpandWidth;
        }
        else
        {
            for (i = 0; i < n; i++)
            {
                if (!this.columnWidths[i])
                    tableWidth += this.columnWidths[i] = this.defaultColumnWidth;
            }
        }
    }

    this.emptyWidth = emptyWidth;
    this.tableWidth = tableWidth;
    this.autoExpandWidth = autoExpandWidth;
    this.bodyWidth = tableWidth - this.scrollWidth;
};

Cyan.TreeTable.prototype.resize = function ()
{
    var size;
    var width = this.el.style.width;
    if (width && !Cyan.endsWith(width, "%"))
    {
        this.width = Cyan.toInt(width);
    }
    else
    {
        size = Cyan.Elements.getComponentSize(this.el);
        this.width = size.width || 300;
    }
    var height = this.el.style.height;
    if (height && !Cyan.endsWith(height, "%"))
    {
        this.height = Cyan.toInt(height);
    }
    else
    {
        if (!size)
            size = Cyan.Elements.getComponentSize(this.el);
        this.height = size.height || 250;
    }

    if (this.rendered)
    {
        var el = Cyan.$$(this.el);
        var tableDiv = el.$(".cyan-treetable")[0];
        tableDiv.style.width = this.width + "px";
        tableDiv.style.hegith = this.height + "px";

        this.setBodyHeight();

        this.initWidths();

        var headerTable = el.$(".cyan-treetable-header table");
        var bodyTable = el.$(".cyan-treetable-body table").css("width", this.bodyWidth + "px");
        headerTable[0].style.width = this.tableWidth + "px";

        if (this.oldColumnWidths)
        {
            var n = this.columnWidths.length;
            for (var i = 0; i < n; i++)
            {
                var columnWidth = this.columnWidths[i];
                if (columnWidth != this.oldColumnWidths[i])
                {
                    columnWidth--;
                    el.$(".cyan-treetable-column" + i).each(function ()
                    {
                        this.style.width = columnWidth + "px";
                        var div = this.childNodes[0];
                        div.style.width = (columnWidth - Cyan.toInt(Cyan.Elements.getCss(div, "padding-left")) -
                        Cyan.toInt(Cyan.Elements.getCss(div, "padding-right"))) + "px";
                    });
                }
            }
        }

        if (this.emptyWidth != this.oldEmptyWidth)
        {
            var emtpys = el.$("td.cyan-treetable-empty");
            if (this.emptyWidth)
                emtpys.css("width", this.emptyWidth + "px").show();
            else
                emtpys.css("width", "0").hide();
        }

        el.$(".cyan-treetable-body").
                css("overflow-x", this.tableWidth > this.width ? "auto" : "hidden")[0].scrollLeft = 0;
        headerTable[0].style.left = "0";
    }
};

Cyan.TreeTable.prototype.render = function ()
{
    if (!this.inited)
    {
        this.autoRender = true;
        return;
    }

    var tableDiv = document.createElement("div");
    tableDiv.className = "cyan-treetable";
    tableDiv.treetable = this;
    this.el.appendChild(tableDiv);

    tableDiv.style.width = this.width + "px";
    tableDiv.style.hegith = this.height + "px";

    this.initWidths();
    var headerDiv = this.renderHeader(tableDiv);
    var bodyDiv = this.renderBody(tableDiv);

    this.setBodyHeight();

    var headerTable = headerDiv.childNodes[0];
    Cyan.$$(bodyDiv).css("overflow-x", this.tableWidth > this.width ? "auto" : "hidden");
    bodyDiv.onscroll = function ()
    {
        headerTable.style.left = -this.scrollLeft + "px";
    };

    if (this.rootVisible)
    {
        this.getRoot().render(bodyDiv);
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
                children[i].render(bodyDiv);
            }
        }
        else
        {
            this.getLoader().load(root);
        }
    }

    if (this.rootVisible && !this.getRoot().leaf)
        this.getRoot().expand(false, false);

    this.rendered = true;
};

Cyan.TreeTable.prototype.getHeight = function (el)
{
    el = el || Cyan.$$(this.el).$(".cyan-treetable-body")[0];
    var height = 0;
    var childNodes = el.childNodes;
    var n = childNodes.length;
    for (var i = 0; i < n; i++)
    {
        var node = childNodes[i];
        if (node.className == "cyan-treetable-node" ||
                node.className == "cyan-treetable-node cyan-treetable-node-selected")
        {
            if (!this.nodeHeight)
                this.nodeHeight = Cyan.toInt(Cyan.$$(node).$("td").css("height"));

            height += this.nodeHeight;
        }
        else if (node.className == "cyan-treetable-sub" && node.style.display != "none")
        {
            height += this.getHeight(node);
        }
    }

    return height;
};

Cyan.TreeTable.prototype.setBodyHeight = function ()
{
    var el = Cyan.$$(this.el);
    var headerHeight = Cyan.toInt(el.$(".cyan-treetable-header tr:last td:first").css("height")) + 1;
    var bodyHeight = this.height - headerHeight * this.headerHeight - 1;

    el.$(".cyan-treetable-body").css("height", bodyHeight + "px");
};

Cyan.TreeTable.prototype.initHeader = function (column, headers, rowIndex)
{
    (headers[rowIndex] || (headers[rowIndex] = [])).push(column);

    if (column.columns)
    {
        for (var i = 0; i < column.columns.length; i++)
        {
            this.initHeader(column.columns[i], headers, rowIndex + 1);
        }
    }
};

Cyan.TreeTable.prototype.renderHeader = function (tableDiv)
{
    var headerDiv = document.createElement("div");
    headerDiv.className = "cyan-treetable-header";
    headerDiv.innerHTML = "<table><tbody></tbody></table>";
    tableDiv.appendChild(headerDiv);

    var tableEl = Cyan.$$(headerDiv).$("table")[0];
    var tbody = Cyan.$$(headerDiv).$("tbody")[0];

    var columns = this.columns, column;
    var headers = new Array(this.headerHeight - 1);
    for (i = 0; i < columns.length; i++)
    {
        column = columns[i];
        this.initHeader(column, headers, 0);
    }

    tableEl.style.width = this.tableWidth + "px";

    var m = headers.length;
    for (var j = 0; j < m; j++)
    {
        var tr = document.createElement("tr");
        tbody.appendChild(tr);

        columns = headers[j];

        var td;
        var n = columns.length;
        for (var i = 0; i < n; i++)
        {
            column = columns[i];

            td = document.createElement("td");
            tr.appendChild(td);

            if (column.rowSpan > 1)
                td.rowSpan = column.rowSpan;

            if (column.colSpan > 1)
                td.colSpan = column.colSpan;

            if (column.columns)
            {
                td.className = "cyan-treetable-header-column";
                td.style.textAlign = "center";
                td.innerHTML = column.title;
            }
            else
            {
                var align = column.align || "left";
                var columnWidth = this.columnWidths[column.columnIndex];

                td.style.width = (columnWidth - 1) + "px";
                td.style.textAlign = align;
                td.className = "cyan-treetable-header-column cyan-treetable-column" + column.columnIndex;

                var div = document.createElement("div");
                div.className = "cyan-treetable-content cyan-treetable-" + align;
                td.appendChild(div);
                div.style.width = (columnWidth - (align == "center" ? 3 : 5)) + "px";
                div.innerHTML = column.title;
            }
        }

        if (j == 0)
        {
            td = document.createElement("td");
            td.style.width = this.emptyWidth ? ((this.emptyWidth - 1) + "px") : "0";
            td.className = "cyan-treetable-header-column cyan-treetable-empty";
            if (!this.emptyWidth)
                td.style.display = "none";
            if (this.headerHeight > 1)
                td.rowSpan = this.headerHeight;
            tr.appendChild(td);

            td = document.createElement("td");
            td.style.width = this.scrollWidth + "px";
            td.className = "cyan-treetable-header-scroll";
            if (this.headerHeight > 1)
                td.rowSpan = this.headerHeight;
            tr.appendChild(td);
        }
    }

    return headerDiv;
};

Cyan.TreeTable.prototype.renderBody = function (tableDiv)
{
    var bodyDiv = document.createElement("div");
    bodyDiv.className = "cyan-treetable-body";
    tableDiv.appendChild(bodyDiv);

    return bodyDiv;
};

Cyan.TreeTable.prototype.getRoot = function ()
{
    return this.rootNode;
};

Cyan.TreeTable.prototype.getNodeById = function (id)
{
    return this.loadedNodes ? this.loadedNodes[id] : null;
};

Cyan.TreeTable.prototype.selectNode = function (node, append)
{
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
        Cyan.$$(this.el).$(".cyan-treetable-node-selected").each(function ()
        {
            if (this != nodeEl)
                this.className = "cyan-treetable-node";
        });
        this.selected = [id];
    }

    if (node.el)
    {
        if (nodeEl && nodeEl.className == "cyan-treetable-node")
        {
            nodeEl.className = "cyan-treetable-node cyan-treetable-node-selected";
        }
    }
};

Cyan.TreeTable.prototype.unselectNode = function (node)
{
    var id = node.id;
    Cyan.Array.removeElement(this.selected, id);

    if (node.el)
    {
        var nodeEl = Cyan.$(node.el);
        if (nodeEl.className == "cyan-treetable-node cyan-treetable-node-selected")
        {
            nodeEl.className = "cyan-treetable-node";
        }
    }
};

Cyan.TreeTable.prototype.clearSelections = function ()
{
    this.selected = [];
    Cyan.$$(this.el).$(".cyan-treetable-node-selected").each(function ()
    {
        this.className = "cyan-treetable-node";
    });
};

Cyan.TreeTable.prototype.getSelectedNodes = function ()
{
    return Cyan.Array.map(this.selected, function (id)
    {
        return this.getNodeById(id);
    }, this);
};

Cyan.TreeTable.prototype.getSelectedId = function ()
{
    return this.selected[0];
};

Cyan.TreeTable.prototype.getSelectedIds = function ()
{
    return Cyan.clone(this.selected);
};

Cyan.TreeTable.prototype.getSelectedNode = function ()
{
    var selectedId = this.selected[0];
    return selectedId ? this.getNodeById(selectedId) : null;
};

Cyan.TreeTable.Node.prototype.render = function (parentDiv, nextNodeDiv)
{
    var id = this.el || Cyan.generateId("cyan_treetable_node");
    if (!this.el)
        this.el = id;

    var div = document.createElement("div");
    div.className = "cyan-treetable-node";
    div.id = id;
    div.nodeId = this.id;

    if (nextNodeDiv)
        parentDiv.insertBefore(div, nextNodeDiv);
    else
        parentDiv.appendChild(div);

    Cyan.attach(div, "click", this.table.nodeClick);
    Cyan.attach(div, "dblclick", this.table.nodeDblClick);

    div.innerHTML = "<table><tbody><tr></tr></tbody></table>";
    Cyan.$$(div).$("table").css("width", this.table.bodyWidth + "px");
    var tr = Cyan.$$$(div).$("tr")[0];
    this.renderRecord(tr);

    if (!this.leaf && this.expanded)
        this.renderChildren(false, false);
};

Cyan.TreeTable.Node.prototype.renderRecord = function (tr)
{
    var record = this.table.getCellDatas(this.value);

    var td;
    var n = this.table.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.table.flatColumns[i];

        var align = i == 0 ? "left" : (column.align || "left");

        var columnWidth = this.table.columnWidths[i];

        td = document.createElement("td");
        td.style.width = (columnWidth - 1) + "px";
        td.style.textAlign = align;
        var className = "cyan-treetable-cell cyan-treetable-column" + i;
        if (column.wrap)
            className += " cyan-treetable-wrap";
        td.className = className;
        tr.appendChild(td);

        var div = document.createElement("div");
        div.className = "cyan-treetable-content cyan-treetable-" + align;
        td.appendChild(div);
        div.style.width = (columnWidth - (align == "center" ? 3 : 5)) + "px";

        var s = record[i] || "";
        if (i == 0)
        {
            var node = this.parentNode;
            while (node != null && (node != this.table.rootNode || this.table.rootVisible))
            {
                var indentDiv = document.createElement("div");
                indentDiv.className = "cyan-treetable-node-indent";
                div.appendChild(indentDiv);
                node = node.parentNode;
            }

            var expanderDiv = document.createElement("div");
            expanderDiv.className = "cyan-treetable-expander";
            if (!this.leaf)
            {
                expanderDiv.className +=
                        this.expanded ? " cyan-treetable-expander-expanded" : " cyan-treetable-expander-collapsed";
            }
            div.appendChild(expanderDiv);

            Cyan.attach(expanderDiv, "click", this.table.nodeExpanderClick);

            var textDiv = document.createElement("div");
            textDiv.className = "cyan-treetable-node-text";
            textDiv.innerHTML = s;
            div.appendChild(textDiv);
        }
        else
        {
            div.innerHTML = s;
        }
    }

    td = document.createElement("td");
    td.style.width = this.table.emptyWidth ? ((this.table.emptyWidth - 1) + "px") : "0";
    td.className = "cyan-treetable-cell cyan-treetable-empty";
    if (!this.table.emptyWidth)
        td.style.display = "none";
    tr.appendChild(td);
};

Cyan.TreeTable.Node.prototype.updateRecord = function (tr)
{
    var divs = Cyan.$$(tr).$(".cyan-treetable-content");
    var n = divs.length;
    var record = this.table.getCellDatas(this.value);
    for (var i = 0; i < n; i++)
    {
        var s = record[i] || "";
        if (i == 0)
        {
            var el = Cyan.$$(divs[i]);
            var className = "cyan-treetable-expander";
            if (!this.leaf)
                className += this.expanded ? " cyan-treetable-expander-expanded" : " cyan-treetable-expander-collapsed";
            el.$(".cyan-treetable-expander").set("className", className);
            el.$(".cyan-treetable-node-text").html(s);
        }
        else
        {
            divs[i].innerHTML = s || "";
        }
    }
};

Cyan.TreeTable.Node.prototype.appendChild = function (node)
{
    return this.insertChildBefore(node, null);
};

Cyan.TreeTable.Node.prototype.insertChildBefore = function (node, nextNodeId)
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

    var treeNode = this.table.toTreeTableNode(node);

    if (!this.children)
        this.children = [];

    treeNode.parentNode = this;
    if (nextNode)
    {
        Cyan.Array.insert(this.children, nextNodeIndex, treeNode);
    }
    else
    {
        this.children.push(treeNode);
    }

    if (this.childrenRendered)
        treeNode.render(this.getChildrenDiv(), nextNode ? Cyan.$(nextNode.el) : null);
    else if (this == this.table.rootNode && !this.table.rootVisible)
        treeNode.render(Cyan.$$(this.table.el).$(".cyan-treetable-body")[0], nextNode ? Cyan.$(nextNode.el) : null);


    if (this.leaf)
    {
        this.leaf = false;
        this.updateEl(false);
    }

    return treeNode;
};

Cyan.TreeTable.Node.prototype.setLoaded = function ()
{
    if (!this.leaf && !this.children)
        this.children = [];
};

Cyan.TreeTable.Node.prototype.isLeaf = function ()
{
    return this.leaf;
};

Cyan.TreeTable.Node.prototype.isLoaded = function ()
{
    return this.leaf || this.children;
};

Cyan.TreeTable.Node.prototype.isExpanded = function ()
{
    return this.expanded;
};

Cyan.TreeTable.Node.prototype.getParent = function ()
{
    return this.parentNode;
};

Cyan.TreeTable.Node.prototype.updateEl = function (descendant)
{
    if (this.el)
    {
        var el = Cyan.$$$(this.el);
        this.updateRecord(el.$("tr")[0]);

        if (this.leaf && this.childrenRendered)
        {
            var childrenDiv = this.getChildrenDiv();
            childrenDiv.parentNode.removeChild(childrenDiv);
            this.childrenRendered = false;
        }

        if (descendant && this.childrenRendered)
        {
            var n = this.children.length;
            for (var i = 0; i < n; i++)
                this.children[i].updateEl(true);
        }
    }
};

Cyan.TreeTable.Node.prototype.getChildrenDiv = function ()
{
    var el = Cyan.$(this.el);
    var nextSibling = el.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-treetable-sub")
        return nextSibling;
};

Cyan.TreeTable.Node.prototype.renderChildren = function ()
{
    var el = Cyan.$(this.el);
    var nextSibling = el.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-treetable-sub")
        return nextSibling;

    var childNodesDiv = document.createElement("div");
    childNodesDiv.className = "cyan-treetable-sub";

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

Cyan.TreeTable.Node.prototype.getChildren = function ()
{
    return Cyan.clone(this.children);
};

Cyan.TreeTable.Node.prototype.expand = function (deep, anim, callback)
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
        var el = Cyan.$$$(node.el);
        el.$(".cyan-treetable-expander").set("className", "cyan-treetable-expander cyan-treetable-expander-expanded");

        var subNodesDiv = node.renderChildren();
        subNodesDiv.style.display = "";
        if (anim)
        {
            subNodesDiv.style.display = "none";
            subNodesDiv.style.height = node.table.getHeight(subNodesDiv) + "px";
            subNodesDiv.style.overflowX = "hidden";
            var fadeIn = new Cyan.Render.PositionFade(subNodesDiv);
            fadeIn.onComplete = function ()
            {
                subNodesDiv.style.height = "";
                subNodesDiv.style.overflowY = "";
                subNodesDiv.style.overflowX = "";
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
        this.table.getLoader().load(this, function ()
        {
            f();
        });
    }
};

Cyan.TreeTable.Node.prototype.collapse = function (deep, anim, callback)
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

    var el = Cyan.$$$(node.el);
    el.$(".cyan-treetable-expander").set("className", "cyan-treetable-expander cyan-treetable-expander-collapsed");

    var nodeEl = Cyan.$(this.el);
    var nextSibling = nodeEl.nextSibling;
    if (nextSibling && nextSibling.className == "cyan-treetable-sub")
    {
        var subNodesDiv = nextSibling;
        if (anim)
        {
            subNodesDiv.style.height = node.table.getHeight(subNodesDiv) + "px";
            subNodesDiv.style.overflowX = "hidden";
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

Cyan.TreeTable.Node.prototype.update = function (node)
{
    this.value = node;
    this.leaf = node.leaf;
    if (this.leaf)
    {
        this.children = null;
        this.expanded = false;
    }

    this.updateEl(false);
};

Cyan.TreeTable.Node.prototype.select = function ()
{
    this.table.selectNode(this, false);
};

Cyan.TreeTable.Node.prototype.isSelected = function ()
{
    return Cyan.Array.contains(this.table.selected, this.id);
};

Cyan.TreeTable.Node.prototype.ensureVisible = function ()
{
    if (this.parentNode && (this.parentNode != this.tree.rootNode || this.tree.rootVisible))
    {
        this.parentNode.ensureVisible();
        this.parentNode.expand(false, false);
    }

    Cyan.Elements.scrollYTo(this.tree.el, Cyan.$(this.el));
};

Cyan.TreeTable.Node.prototype.getNextNode = function ()
{
    if (!this.parentNode)
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

Cyan.TreeTable.Node.prototype.getPreviousNode = function ()
{
    if (!this.parentNode)
        return null;

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

Cyan.TreeTable.Node.prototype.remove = function ()
{
    var treetable = this.table;
    if (this == treetable.rootNode)
        return;

    this.through(function ()
    {
        treetable.loadedNodes[this.id] = null;
    }, true);

    var selectedNode;
    var previousNode = this.getPreviousNode();
    if (treetable.selected.length == 1 && treetable.selected[0] == this.id)
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
        treetable.selected = [];
    }
    else
    {
        Cyan.Array.removeElement(treetable.selected, this.id);
    }

    var children = this.parentNode.children;
    Cyan.Array.removeElement(children, this);

    if (this.el)
    {
        var el = Cyan.$(this.el);
        var nextSibling = el.nextSibling;
        if (nextSibling && nextSibling.className == "cyan-treetable-sub")
            el.parentNode.removeChild(nextSibling);
        el.parentNode.removeChild(el);
    }

    if (children.length == 0)
    {
        this.parentNode.leaf = true;
        this.parentNode.updateEl(false);
    }

    if (selectedNode && (selectedNode != treetable.rootNode || treetable.rootVisible))
        selectedNode.select();
};