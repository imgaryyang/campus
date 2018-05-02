Cyan.importJs("widgets/menu.js");
Cyan.importJs("widgets/menu.js");

Cyan.onload(function ()
{
    Cyan.attach(document.body, "mousemove", function (event)
    {
        var target = event.target;

        var repeatTable;
        var div;
        var element = target;
        while (true)
        {
            if (element.id && Cyan.endsWith(element.id, "$table"))
            {
                var name = element.id.substring(0, element.id.length - 6);
                repeatTable = Cyan.Valmiki.RepeatTable.tables[name];

                if (repeatTable)
                    break;
            }
            else if (element == document.body)
            {
                break;
            }

            div = element;
            element = element.parentNode;
        }

        if (repeatTable && repeatTable.editable)
        {
            var position = Cyan.Elements.getPosition(element);

            var index = Cyan.Array.indexOf(repeatTable.getRows(), div);

            Cyan.Valmiki.RepeatTable.currentTable = repeatTable;
            repeatTable.rowIndex = index;

            var rows = repeatTable.getRows();
            var x = position.x + repeatTable.bounds.left + repeatTable.bounds.width + 1;
            var y = Cyan.Elements.getPosition(rows[index].childNodes[0]).y;

            Cyan.Valmiki.RepeatTable.getMenu().showAt(x, y);
        }
        else
        {
            var menu = Cyan.Valmiki.RepeatTable.menu;
            if (menu)
            {
                var menuElement = menu.getElement();
                var p = event.getOffset(menuElement);
                if (!( p.x >= -1 && p.y >= -1 && p.x <= element.clientWidth + 1
                        && p.y <= element.clientHeight + 1))
                    menu.hide();
            }
        }
    });
});

Cyan.Valmiki.RepeatTable = function (name, bounds, html, javascript, editable)
{
    Cyan.Valmiki.RepeatTable.tables[name] = this;

    this.name = name;
    this.bounds = bounds;
    this.html = html;
    this.javascript = javascript;
    this.editable = editable;

    var repeateTable = this;
    var table = this.getTable();
    this.page = Cyan.Valmiki.getPage(table);

    var line = document.createElement("DIV");
    line.id = this.name + "$line";
    line.style.position = "absolute";
    line.style.left = bounds.left + "px";
    line.style.top = bounds.top + "px";
    line.style.height = "0px";

    table.parentNode.insertBefore(line, table);

    setTimeout(function ()
    {
        var rows = repeateTable.getRows();
        for (var i = 0; i < rows.length; i++)
        {
            repeateTable.initRow(rows[i], i, true);
        }
    }, 50);
};

Cyan.Valmiki.RepeatTable.tables = {};

Cyan.Valmiki.RepeatTable.get = function (name)
{
    var s;
    var tables = Cyan.Valmiki.RepeatTable.tables;
    if (name)
    {
        var repeatTable = tables[name];
        if (repeatTable)
            return repeatTable;

        name = "." + name;
        for (s in tables)
        {
            if (Cyan.endsWith(s, name))
                return tables[s];
        }
    }
    else
    {
        for (s in tables)
        {
            repeatTable = tables[s];
            if (repeatTable)
                return repeatTable;
        }
    }
};

Cyan.Valmiki.RepeatTable.getMenu = function ()
{
    if (!Cyan.Valmiki.RepeatTable.menu)
    {
        Cyan.Valmiki.RepeatTable.menu = new Cyan.Menu([
            {
                text: "添加一行",
                action: function ()
                {
                    if (Cyan.Valmiki.RepeatTable.currentTable)
                        Cyan.Valmiki.RepeatTable.currentTable.addRow();
                }
            },
            {
                text: "删除此行",
                action: function ()
                {
                    if (Cyan.Valmiki.RepeatTable.currentTable)
                        Cyan.Valmiki.RepeatTable.currentTable.deleteRow(null, true);
                }
            }
        ]);
    }

    return Cyan.Valmiki.RepeatTable.menu;
};

Cyan.Valmiki.RepeatTable.prototype.getTable = function ()
{
    return Cyan.$(this.name + "$table");
};

Cyan.Valmiki.RepeatTable.prototype.getLine = function ()
{
    return Cyan.$(this.name + "$line");
};

Cyan.Valmiki.RepeatTable.prototype.getRows = function ()
{
    var table = Cyan.$(this.name + "$table");

    var rows = [];

    Cyan.each(table.childNodes, function ()
    {
        if (this.nodeName == "DIV")
            rows.push(this);
    });

    return rows;
};

Cyan.Valmiki.RepeatTable.prototype.getRowCount = function ()
{
    return this.getRows().length;
};

Cyan.Valmiki.RepeatTable.prototype.getTableTop = function ()
{
    return Cyan.toInt(this.getLine().style.top);
};

Cyan.Valmiki.RepeatTable.prototype.getTableHeight = function ()
{
    var height = 0;
    var rows = this.getRows();
    for (var i = 0; i < rows.length; i++)
    {
        height += Cyan.toInt(rows[i].childNodes[0].style.height);
    }

    return height;
};

Cyan.Valmiki.RepeatTable.prototype.addRow = function (preRow)
{
    var table = this.getTable();

    var rowIndex = this.rowIndex;
    if (preRow)
    {
        rowIndex = Cyan.Array.indexOf(this.getRows(), preRow);
        if (rowIndex < 0)
            return;
    }
    else
    {
        var rows = this.getRows();
        preRow = rows[rowIndex];
        if (!preRow)
        {
            rowIndex = rows.length - 1;
            preRow = rows[rowIndex];
        }
    }

    var newRow = document.createElement("DIV");
    var id = new Date().getTime() + "" + Math.random().toString().substring(2);
    newRow.id = id;
    newRow.innerHTML = Cyan.replaceAll(this.html, "${id}", id);

    var input = document.createElement("input");
    input.type = "hidden";
    input.name = this.name;
    input.value = id;
    newRow.appendChild(input);

    this.initRow(newRow, rowIndex + 1, false);

    var nextRow;
    if (preRow)
        nextRow = preRow.nextSibling;
    if (nextRow)
        table.insertBefore(newRow, nextRow);
    else
        table.appendChild(newRow);

    if (this.javascript)
    {
        eval(Cyan.replaceAll(this.javascript, "${id}", id));
    }

    if (Cyan.renderField)
    {
        Cyan.$$(newRow).$(":field").each(function ()
        {
            Cyan.renderField(this);
        });
    }

    return new Cyan.Valmiki.RepeatTable.Row(this, id);
};

Cyan.Valmiki.RepeatTable.prototype.initRow = function (row, rowIndex, init)
{
    var table = this.getTable();
    var repeatTable = this;
    var page = Cyan.Valmiki.getPage(table);

    var height = repeatTable.bounds.height;
    var tableTop = repeatTable.getTableTop();
    var rowTop = tableTop;

    var rowDiff = init ? 0 : rowTop - repeatTable.bounds.top;

    var rows = this.getRows();
    for (var i = 0; i < rows.length; i++)
    {
        var row1 = rows[i];
        if (i == rowIndex)
        {
            var div = document.createElement("DIV");

            div.style.left = repeatTable.bounds.left + "px";
            div.style.width = repeatTable.bounds.width + "px";
            div.style.top = tableTop + "px";
            div.style.height = height + "px";
            div.style.position = "absolute";

            if (row1.childNodes.length)
                row1.insertBefore(div, row1.childNodes[0]);
            else
                row1.appendChild(div);

            break;
        }
        else
        {
            var height1 = Cyan.toInt(row1.childNodes[0].style.height);

            rowDiff += height1;
            rowTop += height1;
        }
    }

    if (rowIndex > 0)
    {
        page.style.height = (Cyan.toInt(page.style.height) + height) + "px";

        Cyan.$$(page).eachElement(function ()
        {
            if (this.style && this.style.position == "absolute" && this.style.top)
            {
                var top = Cyan.toInt(this.style.top);

                if (top >= rowTop)
                    this.style.top = (top + height) + "px";
            }
        });

        Cyan.$$(row).eachElement(function ()
        {
            if (this.style && this.style.position == "absolute" && this.style.top)
            {
                this.style.top = (Cyan.toInt(this.style.top) + rowDiff) + "px";
            }
        });
    }
};

Cyan.Valmiki.RepeatTable.prototype.deleteRow = function (row, manual)
{
    var table = this.getTable();
    var repeatTable = this;
    var page = Cyan.Valmiki.getPage(table);

    var rowIndex;
    var rows = this.getRows();
    if (row)
    {
        rowIndex = Cyan.Array.indexOf(this.getRows(), row);
        if (rowIndex < 0)
            return;
    }
    else
    {
        rowIndex = this.rowIndex;
        row = rows[rowIndex];
        if (!row)
            return;
    }

    if (manual && (rows.length == 1 || row.getAttribute("editable") == "false"))
        return;

    var bottom = repeatTable.getTableTop();
    var height;
    for (var i = 0; i <= rowIndex; i++)
    {
        var height1 = Cyan.toInt(rows[i].childNodes[0].style.height);
        bottom += height1;
        if (i == rowIndex)
            height = height1;
    }

    page.style.height = (Cyan.toInt(page.style.height) - height) + "px";

    Cyan.$$(page).eachElement(function ()
    {
        if (this.style && this.style.position == "absolute" && this.style.top)
        {
            var top = Cyan.toInt(this.style.top);

            if (top >= bottom)
                this.style.top = (top - height) + "px";
        }
    });

    var input = document.getElementsByName(this.name)[rowIndex];
    if (input.form)
    {
        var deleted = document.createElement("input");
        deleted.type = "hidden";
        deleted.name = this.name + "$deleted";
        deleted.value = input.value;
        input.form.appendChild(deleted);
    }

    table.removeChild(row);
};


Cyan.Valmiki.RepeatTable.prototype.getRow = function (index)
{
    var row = this.getRows()[index];
    if (row)
        return new Cyan.Valmiki.RepeatTable.Row(this, row.id);
    else
        return null;
};

Cyan.Valmiki.RepeatTable.Row = function (table, id)
{
    this.table = table;
    this.id = id;
};

Cyan.Valmiki.RepeatTable.prototype.getComponents = function (name)
{
    name = "." + name;
    var result = [];
    Cyan.$$(this.getTable()).eachElement(function ()
    {
        var id = this.name || this.id;
        if (id && Cyan.endsWith(id, name))
            result.push(this);
    });

    return result;
};

Cyan.Valmiki.RepeatTable.Row.prototype.getRow = function ()
{
    return Cyan.$(this.id);
};

Cyan.Valmiki.RepeatTable.Row.prototype.deleteRow = function ()
{
    this.table.deleteRow(this.getRow(), false);
};

Cyan.Valmiki.RepeatTable.Row.prototype.getComponent = function (name)
{
    name = "." + name;
    return Cyan.$$$(this.id).eachElement(function ()
    {
        var id = this.name || this.id;
        if (id && Cyan.endsWith(id, name))
            return this;
    });
};
