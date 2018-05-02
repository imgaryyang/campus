Cyan.importJs("event.js");

Cyan.Table = Cyan.Class.extend(function (name, columns)
{
    this.name = name;
//    this.stripeRows = true;
    this.columns = columns;
    this.headerHeight = 1;
    this.flatColumns = [];
    this.records = [];
    this.title = "";
    this.pageSize = -1;
    this.pageNo = 1;
    this.checkboxIds = [];

    this.initColumns();
}, Cyan.Widget);

Cyan.Table.prototype.autoLoad = true;
Cyan.Table.prototype.cellSelectable = false;

Cyan.Table.prototype.initColumns = function ()
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

Cyan.Table.prototype.initColumn = function (column, rowIndex, columnIndex)
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
            var rowCount1 = this.initColumn(subColumn, rowIndex + 1, columnIndex);
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

        if (column.checkboxName)
        {
            this.checkboxName = column.checkboxName;
            var id = "table_checkbox_header_" + Math.random().toString().substring(2);
            var className = "x-table-checkbox";
            var checkboxType = column.checkboxType;
            if (checkboxType != "radio")
                checkboxType = "checkbox";
            this.checkboxIds.push(id);
            if (checkboxType == "checkbox")
            {
                column.title =
                        "<input id='" + id + "' class='" + className + "' type='checkbox' onclick=\"Cyan.$$('#" +
                        column.checkboxName + "').check(this.checked);\">";
            }
            else
            {
                column.title = "";
            }
            column.valueProvider = column.valueProvider ? Cyan.innerFunction(column.valueProvider) : null;
            column.checkedProvider = column.checkedProvider ? Cyan.innerFunction(column.checkedProvider) : null;
            column.dataProvider = function ()
            {
                var checked = column.checkedProvider.apply(this);
                if (Cyan.isBoolean(checked))
                {
                    var s = "<input class='" + className + "' type='" + checkboxType + "' name='" +
                            column.checkboxName + "' value='" + column.valueProvider.apply(this) + "'";

                    if (checked)
                        s += " checked";

                    s += ">";

                    return s;
                }
                else
                {
                    return "<input class='" + className + "' type='checkbox' disabled>";
                }
            };
        }
        else
        {
            column.dataProvider = column.dataProvider ? Cyan.innerFunction(column.dataProvider) : null;
        }
    }

    if (rowIndex == 0)
        column.rowCount = rowCount;

    return rowCount;
};

Cyan.Table.prototype.loadChildren0 = function (id, callback)
{
    var rowIndex = this.indexOf(id);
    var record = this.getRecord(rowIndex);

    if (record.loaded)
        return;

    record.loaded = true;

    var table = this;
    if (this.loadChildren)
    {
        this.loadChildren(id, function (records)
        {
            var level = (record.level || 0) + 1;
            var toExpands = [];
            for (var i = 0; i < records.length; i++)
            {
                var record1 = records[i];
                record1.level = level;
                table.add(record1, ++rowIndex);

                if (table.expandeds && Cyan.Array.contains(table.expandeds, record1.key))
                    toExpands.push(record1.key);
            }

            record.expanded = true;
            if (!table.expandeds)
                table.expandeds = [];
            table.expandeds.push(record.key);

            if (callback)
                callback();

            if (toExpands.length)
            {
                var index = 0;
                var loadExpands = function ()
                {
                    if (index < toExpands.length)
                        table.loadChildren0(toExpands[index++], loadExpands);
                };

                loadExpands();
            }
        });
    }
};

Cyan.Table.prototype.getLoader = function ()
{
    if (!this.loader && this.load)
    {
        var table = this;
        this.loader = {
            load: function (params, callback)
            {
                if (!params)
                    params = {};

                if (params.pageNo == null)
                    params.pageNo = table.pageNo;
                else
                    table.pageNo = params.pageNo;

                if (params.sort)
                    table.sort = params.sort;
                else
                    params.sort = table.sort;

                if (params.dir)
                    table.dir = params.dir;
                else
                    params.dir = table.dir;

                table.load(params, function (result)
                {
                    table.records = result.records;

                    var toExpands = [];

                    var n = result.records.length;
                    var records = new Array(n);
                    var i, data;
                    for (i = 0; i < n; i++)
                    {
                        var record = result.records[i];
                        record.level = 0;
                        records[i] = table.getCellDatas(record);

                        if (table.expandeds && Cyan.Array.contains(table.expandeds, record.key))
                            toExpands.push(record.key);
                    }

                    this.totalCount = result.totalCount;
                    callback({
                        records: records, totalCount: result.totalCount, pageNo: this.pageNo, pageSize: this
                                .pageSize
                    });

                    if (toExpands.length)
                    {
                        var index = 0;
                        var loadExpands = function ()
                        {
                            if (index < toExpands.length)
                                table.loadChildren0(toExpands[index++], loadExpands);
                        };

                        loadExpands();
                    }
                });
            }
        };
    }
    return this.loader;
};

Cyan.Table.prototype.getCellDatas = function (record)
{
    var n = this.flatColumns.length;
    var cellDatas = new Array(n);
    for (var i = 0; i < n; i++)
    {
        var dataProvider = this.flatColumns[i].dataProvider;
        cellDatas[i] = dataProvider ? dataProvider.apply(record) : record[i];
    }
    cellDatas.key = record.key;
    cellDatas.remark = record.remark;
    cellDatas.hasChildren = record.hasChildren;
    return cellDatas;
};

Cyan.Table.prototype.indexOf = function (key)
{
    return Cyan.Array.indexOf(this.records, function ()
    {
        return this.key == key;
    });
};

Cyan.Table.prototype.update = function (rowIndex, record)
{
    if (this.records.length > rowIndex && rowIndex >= 0)
    {
        this.records[rowIndex] = record;
        this.updateRecord(rowIndex, this.getCellDatas(record));
    }
};

Cyan.Table.prototype.remove = function (rowIndex)
{
    if (this.records.length > rowIndex && rowIndex >= 0)
    {
        this.removeRecord(rowIndex);
        return Cyan.Array.remove(this.records, rowIndex);
    }
};

Cyan.Table.prototype.add = function (record, rowIndex)
{
    if (!record.level)
        record.level = 0;

    if (rowIndex == null)
    {
        this.records.push(record);
        this.addRecord(this.getCellDatas(record));
    }
    else
    {
        if (this.records.length > rowIndex && rowIndex >= 0)
        {
            Cyan.Array.insert(this.records, rowIndex, record);
            this.addRecord(this.getCellDatas(record), rowIndex);
        }
        else
        {
            this.records.push(record);
            this.addRecord(this.getCellDatas(record));
        }
    }
};

Cyan.Table.prototype.getSize = function ()
{
    return this.records.length;
};

Cyan.Table.prototype.getRecord = function (rowIndex)
{
    return this.records.length > rowIndex && rowIndex >= 0 ? this.records[rowIndex] : null;
};

Cyan.Table.prototype.getProperty = function (record, name)
{
    return record[name] ? record[name] : record.properties ? record.properties[name] : null;
};

Cyan.Table.prototype.getRowClass = function (record)
{
    return this.getProperty(record, "className");
};

Cyan.Table.prototype.getRowStyle = function (record)
{
    return this.getProperty(record, "style");
};

Cyan.Table.prototype.getAction = function (record, name)
{
    var action = this.getProperty(record, name);
    if (Cyan.isString(action))
    {
        action = new Function("", action);
        record[name] = action;
    }

    if (!action)
        action = this[name];

    if (Cyan.isString(action))
    {
        action = new Function("", action);
        this[name] = action;
    }

    return action;
};

Cyan.Table.prototype.invokeAction = function (record, rowIndex, name)
{
    var action = this.getAction(record, name);
    if (action)
        action.apply(record, [rowIndex]);
};

Cyan.Table.prototype.dblclick = function (record, rowIndex)
{
    this.invokeAction(record, rowIndex, "ondblclick");
};

Cyan.Table.prototype.click = function (record, rowIndex)
{
    this.invokeAction(record, rowIndex, "onclick");
};

Cyan.Table.prototype.getRenderer = function (columnIndex)
{
};

Cyan.Table.prototype.getStartDrag = function ()
{
    if (!this.startDrag && this.startdrag)
        this.startDrag = this.startdrag;

    if (this.startDrag && Cyan.isString(this.startDrag))
        this.startDrag = new Function("event", this.startDrag);

    if (this.startDrag)
        this.startDrag = Cyan.Event.action(this.startDrag);

    return this.startDrag;
};
Cyan.Table.prototype.getEndDrag = function ()
{
    if (!this.endDrag && this.enddrag)
        this.endDrag = this.enddrag;

    if (this.endDrag && Cyan.isString(this.endDrag))
        this.endDrag = new Function("event", this.endDrag);

    if (this.endDrag)
        this.endDrag = Cyan.Event.action(this.endDrag);

    return this.endDrag;
};
Cyan.Table.prototype.getOnDragOver = function ()
{
    if (!this.onDragOver && this.ondragover)
        this.onDragOver = this.ondragover;

    if (this.onDragOver && Cyan.isString(this.onDragOver))
        this.onDragOver = new Function("event", this.onDragOver);

    if (this.onDragOver)
        this.onDragOver = Cyan.Event.action(this.onDragOver);

    return this.onDragOver;
};
Cyan.Table.prototype.getOnDrop = function ()
{
    if (!this.onDrop && this.ondrop)
        this.onDrop = this.ondrop;

    if (this.onDrop && Cyan.isString(this.onDrop))
        this.onDrop = new Function("event", this.onDrop);

    if (this.onDrop)
        this.onDrop = Cyan.Event.action(this.onDrop);

    return this.onDrop;
};

Cyan.Table.prototype.clearHeaderCheckboxs = function ()
{
    for (var i = 0; i < this.checkboxIds.length; i++)
    {
        var checkbox = Cyan.$(this.checkboxIds[i]);
        if (checkbox)
            checkbox.checked = false;
    }
};

Cyan.Table.prototype.showRow = function (rowIndex)
{
    this.setRowVisible(rowIndex, true);
};

Cyan.Table.prototype.hideRow = function (rowIndex)
{
    this.setRowVisible(rowIndex, false);
};

Cyan.Table.prototype.setChildrenVisible = function (key, visible)
{
    if (visible)
        this.showChildren(key);
    else
        this.hideChildren(key);
};

Cyan.Table.prototype.showChildren = function (key, callback)
{
    var rowIndex = this.indexOf(key);
    var record = this.getRecord(rowIndex);

    if (!record.loaded)
    {
        this.loadChildren0(key, callback);
        return;
    }

    var level = record.level || 0;
    var n = this.getSize();

    for (var i = rowIndex + 1; i < n; i++)
    {
        var record2 = this.getRecord(i);
        if (record2.level == level + 1)
        {
            this.setRowVisible(i, true);
            if (record2.expanded)
            {
                this.showChildren(record2.key);
            }
        }
        else if ((record2.level || 0) <= level)
        {
            break;
        }
    }

    if (!this.expandeds)
        this.expandeds = [];
    this.expandeds.push(key);

    record.expanded = true;

    if (callback)
        callback();
};

Cyan.Table.prototype.hideChildren = function (key)
{
    var rowIndex = this.indexOf(key);
    var record = this.getRecord(rowIndex);

    var level = record.level || 0;
    var n = this.getSize();

    for (var i = rowIndex + 1; i < n; i++)
    {
        var record2 = this.getRecord(i);
        if (record2.level > level)
            this.setRowVisible(i, false);
        else
            break;
    }

    if (this.expandeds)
    {
        Cyan.Array.removeElement(this.expandeds, key);
    }

    record.expanded = false;
};

Cyan.Table.prototype.expandAll = function (callback)
{
    var records = Cyan.map(this.records, function ()
    {
        return {key: this.key, hasChildren: this.hasChildren};
    });

    var table = this;

    Cyan.lazyEach(records, function (callback)
    {
        if (this.hasChildren)
        {
            table.showChildren(this.key, callback);
        }
        else
        {
            callback();
        }
    }, callback);
};

Cyan.importAdapter("table");