Cyan.importJs("adapters/extjs/GroupHeaderPlugin.js");
Cyan.importCss("adapters/extjs/GroupHeaderPlugin.css");

Cyan.Table.parentClick = function (icon, tableName, id)
{
    var table = Cyan.Table[tableName];
    if (icon.className == "x-grid3-row-expander-icon-collapsed x-grid3-row-expander-icon")
    {
        icon.className = "x-grid3-row-expander-icon-expanded x-grid3-row-expander-icon";

        table.showChildren(id);
    }
    else
    {
        icon.className = "x-grid3-row-expander-icon-collapsed x-grid3-row-expander-icon";
        table.hideChildren(id);
    }
};

Cyan.Table.prototype.init = function (el)
{
    var table = this;
    Cyan.onload(function ()
    {
        table.init0(el);
    });
};

Cyan.Table.wrapColumnRenderer = function (renderer)
{
    if (renderer)
    {
        return function (data, column, record)
        {
            Cyan.Table.wrapColumnRenderer0(data, column, record);
            return renderer(data, column, record);
        }
    }
    else
    {
        return Cyan.Table.wrapColumnRenderer0;
    }
};

Cyan.Table.wrapColumnRenderer0 = function (data, column, record)
{
    column.attr = "style=\"white-space:normal;\"";
    return data;
};

Cyan.Table.columnRenderer = function (data, column, record)
{
    if (this.renderer0)
        return this.renderer0(data, record.id);
    else
        return data;
};

Cyan.Table.prototype.init0 = function (el)
{
    var element = Cyan.$(el);
    if (!this.width)
    {
        if (element.style.width)
            this.width = Cyan.toInt(element.style.width);
        else
            this.width = element.clientWidth;
    }
    if (!this.height)
    {
        if (element.style.height)
            this.height = Cyan.toInt(element.style.height);
        else
            this.height = element.clientHeight;
    }

    var table = this;

    this.lockeds = [];

    var n = this.flatColumns.length;
    var fields = new Array(n);
    var columns = new Array(n);
    var autoExpandColumn;
    var autoExpandMin;
    var renderer, i, column;
    for (i = 0; i < n; i++)
    {
        column = this.flatColumns[i];

        fields[i] = {name: column.name, mapping: i};
        var width = column.width ? Cyan.toInt(column.width) : null;
        if (!width && column.checkboxName)
            width = 27;

        var id = "column" + i;

        if (column.autoExpand || !autoExpandColumn && !width && !column.locked && column.align == "left")
        {
            autoExpandColumn = id;
            autoExpandMin = column.width;
        }

        if (this.headerHeight > 1)
            column.locked = true;

        columns[i] = {
            id: id,
            header: column.title,
            dataIndex: column.name,
            width: width,
            sortable: column.sortable,
            align: column.align,
            fixed: !!column.checkboxName || (column.locked && width),
            menuDisabled: !!column.checkboxName || (column.locked),
            hidden: !!column.hidden,
            wrap: column.wrap
        };

        if (this.getRenderer || column.wrap)
        {
            renderer = this.getRenderer(i);
            if (column.wrap)
            {
                columns[i].renderer = Cyan.Table.wrapColumnRenderer(renderer);
            }
            else if (renderer)
            {
                columns[i].renderer = renderer;
            }
        }

        if (column.locked)
            this.lockeds.push("x-grid3-hd-inner x-grid3-hd-column" + i);
    }

    for (i = 0; i < n; i++)
    {
        column = columns[i];
        if (column.id == autoExpandColumn)
        {
            columns[i].renderer0 = function (s, id)
            {
                var record = table.getRecord(table.indexOf(id));
                if (!record)
                    return s;

                s = "<div class='x-grid3-row-expander-text'>" + s + "</div>";
                if (record.hasChildren)
                {
                    if (!Cyan.Table[table.name])
                        Cyan.Table[table.name] = table;

                    var s1;
                    if (table.expandeds && Cyan.Array.contains(table.expandeds, id))
                        s1 = "x-grid3-row-expander-icon-expanded";
                    else
                        s1 = "x-grid3-row-expander-icon-collapsed";

                    s = "<div class='" + s1 + " x-grid3-row-expander-icon' id='x-grid3-row-expander-icon-" + id + "'" +
                    " onclick='Cyan.Table.parentClick(this,\"" + table.name + "\",\"" + id +
                    "\")'></div>" + s;
                }

                if (record.level);
                {
                    for (var i = 0; i < record.level; i++)
                    {
                        s = "<span class='x-grid3-row-expander-span'></span>" + s;
                    }
                }

                return s;
            };
            columns[i].renderer = Cyan.Table.columnRenderer;
            if (columns[i].wrap)
                columns[i].renderer = Cyan.Table.wrapColumnRenderer(columns[i].renderer);
        }
    }

    var expander;
    if (this.hasRemark)
    {
        fields.push({name: "remark$", mapping: "remark"});
        expander = new Ext.grid.RowExpander();
        expander.table = this;
        if (this.getRenderer)
        {
            renderer = this.getRenderer("remark");
            if (renderer)
                expander.bodyRenderer = renderer;
        }
        Cyan.Array.insert(columns, 0, expander);
        this.lockeds.push("x-grid3-hd-inner x-grid3-hd-expander");
    }

    var proxy = new Ext.data.DataProxy();
    var f;
    proxy.load = function (params, reader, callback, scope, arg)
    {
        var loader = table.getLoader();
        if (loader)
        {
            if (!params.pageNo && params.limit)
                params.pageNo = params.start / params.limit + 1;
            loader.load(params, function (result)
            {
                table.clearHeaderCheckboxs();
                var records = reader.readRecords(result.records);
                records.totalRecords = result.totalCount;
                callback.call(scope, records, arg, true);
                if (this.pageBar && this.pageBar.updateBar)
                {
                    var pageCount = result.totalCount / result.pageSize;
                    if (result.totalCount % result.pageSize != 0)
                        pageCount++;
                    this.pageBar.updateBar({
                        totalCount: result.totalCount,
                        pageNo: result.pageNo,
                        pageSize: result.pageSize,
                        pageCount: pageCount
                    });
                }

                if (f)
                {
                    f();
                }
                else
                {
                    f = function ()
                    {
                        var n = table.getSize();
                        for (var i = 0; i < n; i++)
                        {
                            var style = table.getRowStyle(table.getRecord(i));
                            if (style)
                            {
                                var row = table.grid.getView().getRow(i);
                                if (row)
                                {
                                    row = row.childNodes[0];
                                    row = row.rows[0];
                                    var cells = row.cells;
                                    for (var j = 0; j < cells.length; j++)
                                    {
                                        var cssText = cells[j].childNodes[0].style.cssText;
                                        if (cssText)
                                            cssText += ";" + style;
                                        else
                                            cssText = style;
                                        cells[j].childNodes[0].style.cssText = cssText;
                                    }
                                }
                            }
                        }
                    };

                    if (table.getSize() > 0)
                    {
                        var handle = window.setInterval(function ()
                        {
                            try
                            {
                                table.grid.getView().getRow(0);

                                if (handle)
                                    window.clearInterval(handle);

                                window.setTimeout(f, 10);
                                handle = 0;
                            }
                            catch (e)
                            {
                            }
                        }, 10);
                    }
                }
            });
        }
        else
        {
            table.clearHeaderCheckboxs();
            callback.call(scope, reader.readRecords([]), arg, true);
        }
    };
    var reader = new Ext.data.ArrayReader({id: "key"}, fields);
    this.store = new Ext.data.Store({
        remoteSort: true,
        proxy: proxy,
        reader: reader
    });
    var gridConfig = this.getGridConfig();
    if (autoExpandColumn && !gridConfig.autoExpandColumn)
    {
        gridConfig.autoExpandColumn = autoExpandColumn;
        if (autoExpandMin && gridConfig.autoExpandMin == null)
            gridConfig.autoExpandMin = autoExpandMin;
    }
    gridConfig.el = el;

    if (this.headerHeight == 1)
    {
        gridConfig.cm = new Ext.grid.ColumnModel(columns);
    }
    else
    {
        var rows = new Array(this.headerHeight - 1);
        n = this.columns.length;
        for (i = 0; i < n; i++)
            this.initRows(rows, this.columns[i], 0, true);

        gridConfig.cm = new Ext.grid.ColumnModel({
            columns: columns,
            rows: rows
        });
        gridConfig.plugins = [new Ext.ux.plugins.GroupHeaderGrid()];
    }

    if (this.cellSelectable)
        gridConfig.sm = new Ext.grid.CellSelectionModel();

    this.grid = new Ext.grid.GridPanel(gridConfig);
    this.grid.on('render', function ()
    {
        Ext.fly(this.grid.view.innerHd).on('mousedown', function (e, t)
        {
            while (true)
            {
                if (Cyan.Array.contains(table.lockeds, t.className))
                    e.stopEvent();
                if (t.className.indexOf("x-grid3-hd-inner ") == 0)
                    return;
                t = t.parentNode;
                if (!t || t.nodeName == "BODY" || t.nodeName == "#document")
                    return;
            }
        });
        if (this.enableDrag && !this.cellSelectable)
        {
            this.grid.view.dragZone.onBeforeDrag = function (data, e)
            {
                //记录当前拖动上下文
                Cyan.Widget.DragContext.dragComponent = table;
                Cyan.Widget.DragContext.dragIds = Cyan.get(data.selections, "id");

                if (table.getStartDrag())
                    return table.startDrag(e.browserEvent);

                return true;
            };
            this.grid.view.dragZone.onEndDrag = function (data, e)
            {
                if (table.getEndDrag())
                    table.endDrag(e.browserEvent);

                Cyan.Widget.DragContext.dragComponent = null;
                Cyan.Widget.DragContext.dragIds = null;
            };
        }
        if (this.enableDrop)
        {
            new Ext.dd.DropTarget(table.grid.getView().el.dom.childNodes[0].childNodes[1], {
                ddGroup: this.dropGroup || this.ddGroup,
                notifyDrop: function (source, e, data)
                {
                    return !table.getOnDrop() || table.onDrop(e.browserEvent);
                }
            });
        }
    }, this);

    if (expander)
        expander.init(this.grid);

    if (!this.cellSelectable)
    {
        this.grid.on("rowdblclick", function (grid, rowIndex, e)
        {
            this.dblclick(this.getRecord(rowIndex), rowIndex);
        }, this);
        this.grid.on("rowclick", function (grid, rowIndex, e)
        {
            this.click(this.getRecord(rowIndex), rowIndex);
        }, this);
    }

    if (this.autoRender)
        this.grid.render();

    if (this.autoLoad)
    {
        var store = this.store;
        setTimeout(function ()
        {
            store.load();
        }, 10);
    }
};

Cyan.Table.prototype.initRows = function (rows, column, rowIndex, b)
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

Cyan.Table.prototype.render = function ()
{
    this.grid.render();
};

Cyan.Table.prototype.getExtComponent = function ()
{
    return this.grid;
};

Cyan.Table.prototype.getGridConfig = function ()
{
    var table = this;
    var gridConfig = {
        enableDrag: !!this.enableDrag,
        enableDrop: !!this.enableDrop,
        ddGroup: this.dragGroup || this.ddGroup,
        loadMask: true,
        width: Cyan.toInt(this.width),
        height: Cyan.toInt(this.height),
        title: this.title,
        store: this.store,
        stripeRows: this.stripeRows,
        listeners: {
            render: function (grid)
            {
                var v = grid.getView();
                v.scroller.on("scroll", function ()
                {
                    v.scrollTop = v.scroller.dom.scrollTop;
                    v.scrollHeight = v.scroller.dom.scrollHeight;
                });
            }
        },
        viewConfig: {
            forceFit: !!this.forceFit,
            getRowClass: function (record, rowIndex, rowParams, store)
            {
                return table.getRowClass(table.getRecord(rowIndex)) || "";
            },
            listeners: {
                refresh: function (v)
                {
                    var top = v.scrollTop;
                    var height = v.scrollHeight;
                    setTimeout(function ()
                    {
                        if (top)
                        {
                            v.scroller.dom.scrollTop =
                                    top + (v.scrollTop == 0 ? 0 : v.scroller.dom.scrollHeight - height);
                        }
                    }, 10);
                }

            }
        }
    };
    if (this.pageSize > 0)
    {
        this.pageBar = this.createPagingBar();
        if (this.pageBar)
            gridConfig.bbar = this.pageBar;
    }
    return gridConfig;
};

Cyan.Table.prototype.createPagingBar = function ()
{
    return new Ext.PagingToolbar({pageSize: this.pageSize, store: this.store, displayInfo: true});
};

Cyan.Table.prototype.reload = function (params, callback)
{
    if (params)
    {
        if (params.pageNo)
        {
            params.start = (params.pageNo - 1) * this.pageSize;
            params.limit = this.pageSize;
        }
    }
    else
    {
        params = {start: (this.pageNo - 1) * this.pageSize, limit: this.pageSize};
    }
    this.store.load({params: params, callback: callback});
};

Cyan.Table.prototype.updateRecord = function (rowIndex, record)
{
    this.store.remove(this.store.getAt(rowIndex));
    this.store.insert(rowIndex, this.toExtRecord(record));
};

Cyan.Table.prototype.addRecord = function (record, rowIndex)
{
    record = this.toExtRecord(record);
    if (rowIndex == null)
        this.store.add([record]);
    else
        this.store.insert(rowIndex, record);
};

Cyan.Table.prototype.toExtRecord = function (record)
{
    return this.store.reader.readRecords([record]).records[0];
};

Cyan.Table.prototype.removeRecord = function (rowIndex)
{
    this.store.remove(this.store.getAt(rowIndex));
};

Cyan.Table.prototype.getSelectedRowIndex = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.grid.getSelectionModel().getSelectedCell();
        return cell ? cell[0] : null;
    }
    else
    {
        var row = this.grid.getSelectionModel().getSelected();
        return row ? this.indexOf(row.id) : null;
    }
};

Cyan.Table.prototype.getSelectedRow = function ()
{
    var index = this.getSelectedRowIndex();
    return index == null ? null : this.getRecord(index);
};

Cyan.Table.prototype.getSelectedRowIndexes = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.grid.getSelectionModel().getSelectedCell();
        return cell ? [cell[0]] : null;
    }
    else
    {
        var rows = this.grid.getSelectionModel().getSelections();
        var tree = this;
        return rows ? Cyan.get(rows, function ()
        {
            return tree.indexOf(this.id);
        }) : null;
    }
};

Cyan.Table.prototype.getSelectedRows = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.grid.getSelectionModel().getSelectedCell();
        return cell ? [this.getRecord(cell[0])] : null;
    }
    else
    {
        var rows = this.grid.getSelectionModel().getSelections();
        var tree = this;
        return rows ? Cyan.get(rows, function ()
        {
            return this.getRecord(tree.indexOf(this.id));
        }) : null;
    }
};

Cyan.Table.prototype.getSelectedCell = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.grid.getSelectionModel().getSelectedCell();
        if (cell)
        {
            var rowIndex = cell[0];
            var columnIndex = cell[1];
            var record = this.getRecord(rowIndex);
            return {rowIndex: rowIndex, columnIndex: columnIndex, record: record};
        }
    }
    return null;
};

Cyan.Table.prototype.setHeader = function (columnIndex, title)
{
    this.grid.getColumnModel().setColumnHeader(columnIndex, title);
};

Cyan.Table.prototype.select = function (rowIndex, columnIndex)
{
    if (this.cellSelectable)
        this.grid.getSelectionModel().select(rowIndex, columnIndex);
    else
        this.grid.getSelectionModel().selectRow(rowIndex);
};

Cyan.Table.prototype.setRowVisible = function (rowIndex, visible)
{
    var row = this.grid.getView().getRow(rowIndex);
    row.style.display = visible ? "" : "none";
};

Cyan.importJs("adapters/extjs/RowExpander.js");

Cyan.Class.overwrite(Cyan.Table.prototype, "showChildren", function (key, callback)
{
    var icon = Cyan.$("x-grid3-row-expander-icon-" + key);
    icon.className = "x-grid3-row-expander-icon-expanded x-grid3-row-expander-icon";
    this.inherited(key, callback);
});

Cyan.Class.overwrite(Cyan.Table.prototype, "hideChildren", function (key)
{
    var icon = Cyan.$("x-grid3-row-expander-icon-" + key);
    icon.className = "x-grid3-row-expander-icon-collapsed x-grid3-row-expander-icon";
    this.inherited(key);
});