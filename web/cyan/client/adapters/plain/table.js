Cyan.Plain.importCss("table");
Cyan.Plain.importLanguage("table");
Cyan.importJs("widgets/mask.js");

Cyan.Table.parentClick = function (icon, id)
{
    var tableEl = icon;
    while (tableEl.className != "cyan-table" && tableEl.nodeName != "BODY")
    {
        tableEl = tableEl.parentNode;
    }

    if (tableEl.className == "cyan-table" && tableEl.table)
    {
        var table = tableEl.table;
        if (icon.className == "cyan-table-expander-icon-collapsed cyan-table-expander-icon")
        {
            icon.className = "cyan-table-expander-icon-expanded cyan-table-expander-icon";
            table.showChildren(id);
        }
        else
        {
            icon.className = "cyan-table-expander-icon-collapsed cyan-table-expander-icon";
            table.hideChildren(id);
        }
    }
};

Cyan.Table.remarkClick = function (icon, id)
{
    var trEl = icon;
    while (trEl.nodeName != "TR" && trEl.nodeName != "BODY")
    {
        trEl = trEl.parentNode;
    }

    if (trEl.nodeName == "TR")
    {
        var remarkTr = trEl.nextSibling;
        if (remarkTr.className == "cyan-table-remark")
        {
            if (icon.className == "cyan-table-expander-icon-collapsed cyan-table-expander-icon")
            {
                icon.className = "cyan-table-expander-icon-expanded cyan-table-expander-icon";
                remarkTr.style.display = "";
            }
            else
            {
                icon.className = "cyan-table-expander-icon-collapsed cyan-table-expander-icon";
                remarkTr.style.display = "none";
            }
        }
    }
};

Cyan.Table.prototype.defaultColumnWidth = 150;
Cyan.Table.prototype.scrollWidth = 16;
Cyan.Table.prototype.defaultRenderer = function (data, key)
{
    return data;
};
Cyan.Table.prototype.getRenderer0 = function (columnIndex)
{
    var renderer;
    if (!this.renderers0)
        this.renderers0 = {};
    else
        renderer = this.renderers0[columnIndex];

    if (renderer)
        return renderer;

    renderer = this.getRenderer(columnIndex);
    if (!renderer)
        renderer = this.defaultRenderer;

    if (columnIndex == this.autoExpandColumn)
    {
        var renderer0 = renderer;
        var table = this;
        renderer = function (data, key, expanded)
        {
            var record = table.getRecord(table.indexOf(key));
            if (!record)
                return renderer0(data, key);

            var s = renderer0(data, key), s1;
            if (record.hasChildren)
            {
                if (table.expandeds && Cyan.Array.contains(table.expandeds, key))
                    s1 = "cyan-table-expander-icon-expanded";
                else
                    s1 = "cyan-table-expander-icon-collapsed";

                s = "<div class='" + s1 + " cyan-table-expander-icon'" +
                        " onclick='Cyan.Table.parentClick(this,\"" + key + "\")'></div>" + s;
            }
            else if (record.remark)
            {
                if (expanded)
                    s1 = "cyan-table-expander-icon-expanded";
                else
                    s1 = "cyan-table-expander-icon-collapsed";

                s = "<div class='" + s1 + " cyan-table-expander-icon'" +
                        " onclick='Cyan.Table.remarkClick(this,\"" + key + "\")'></div>" + s;
            }

            if (record.level);
            {
                for (var i = 0; i < record.level; i++)
                    s = "<div class='cyan-table-expander-span'></div>" + s;
            }

            return s;
        };
    }

    this.renderers0[columnIndex] = renderer;
    return renderer;
};

Cyan.Table.prototype.init = function (el)
{
    this.selecteds = [];

    this.el = Cyan.$(el);
    var table = this;

    Cyan.onload(function ()
    {
        if (table.autoLoad)
            table.loadData();

        table.init0();
    });

    this.rowclick = function ()
    {
        var index = table.indexOf(this.key);
        table.click(table.getRecord(index), index);
    };

    this.rowdblclick = function ()
    {
        var index = table.indexOf(this.key);
        table.dblclick(table.getRecord(index), index);
    };
};

Cyan.Table.prototype.init0 = function ()
{
    this.inited = true;
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

Cyan.Table.prototype.loadData = function (callback)
{
    var table = this;
    var loader = this.getLoader();
    if (loader)
    {
        this.loading = true;
        loader.load(null, function (result)
        {
            table.clearHeaderCheckboxs();
            table.loading = false;
            table.totalCount = result.totalCount;
            var pageCount = Math.floor(result.totalCount / table.pageSize);
            if (result.totalCount % table.pageSize != 0)
                pageCount++;
            table.pageCount = pageCount;

            var start = (table.pageNo - 1) * table.pageSize + 1;
            var end = start + table.pageSize - 1;
            if (end > result.totalCount)
                end = result.totalCount;

            table.start = start;
            table.end = end;

            if (table.rendered)
            {
                table.stopLoading();
                table.renderRecords(result.records);
            }

            if (callback)
                callback();
        });
    }
};

Cyan.Table.prototype.resize = function ()
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
        var tableDiv = el.$(".cyan-table")[0];
        tableDiv.style.width = this.width + "px";
        tableDiv.style.hegith = this.height + "px";

        this.setBodyHeight();

        this.initWidths();

        var headerTable = el.$(".cyan-table-header table");
        var bodyTable = el.$(".cyan-table-body table");
        headerTable[0].style.width = this.tableWidth + "px";
        bodyTable[0].style.width = this.bodyWidth + "px";

        if (this.oldColumnWidths)
        {
            var n = this.columnWidths.length;
            for (var i = 0; i < n; i++)
            {
                var columnWidth = this.columnWidths[i];
                if (columnWidth != this.oldColumnWidths[i])
                {
                    columnWidth--;
                    el.$(".cyan-table-column" + i).each(function ()
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
            var emtpys = el.$("td.cyan-table-empty");
            if (this.emptyWidth)
                emtpys.css("width", this.emptyWidth + "px").show();
            else
                emtpys.css("width", "0").hide();
        }

        el.$(".cyan-table-body").css("overflow-x", this.tableWidth > this.width ? "auto" : "hidden")[0].scrollLeft = 0;
        headerTable[0].style.left = "0";
    }
};

Cyan.Table.prototype.initAutoExpandColumn = function ()
{
    var n = this.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];

        var columnWidth = column.width ? Cyan.toInt(column.width) : null;
        if (!columnWidth && column.checkboxName)
            column.width = columnWidth = 27;

        if (column.autoExpand ||
                this.autoExpandColumn == null && !columnWidth && !column.locked && column.align == "left")
        {
            this.autoExpandColumn = i;
            this.autoExpandMin = columnWidth || this.defaultColumnWidth;
        }
    }
};

Cyan.Table.prototype.initWidths = function ()
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

Cyan.Table.prototype.render = function ()
{
    if (!this.inited)
    {
        this.autoRender = true;
        return;
    }

    var tableDiv = document.createElement("div");
    tableDiv.className = "cyan-table";
    tableDiv.table = this;
    this.el.appendChild(tableDiv);

    tableDiv.style.width = this.width + "px";
    tableDiv.style.hegith = this.height + "px";

    this.initWidths();
    var headerDiv = this.renderHeader(tableDiv);
    var bodyDiv = this.renderBody(tableDiv);
    var pageBarDiv = this.renderPageBar(tableDiv);

    if (this.pageSize <= 0)
        pageBarDiv.style.display = "none";

    this.setBodyHeight();

    var headerTable = headerDiv.childNodes[0];
    Cyan.$$(bodyDiv).css("overflow-x", this.tableWidth > this.width ? "auto" : "hidden");
    bodyDiv.onscroll = function ()
    {
        headerTable.style.left = -this.scrollLeft + "px";
    };

    if (this.loading)
        this.showLoading();

    this.rendered = true;
};

Cyan.Table.prototype.setBodyHeight = function ()
{
    var el = Cyan.$$(this.el);
    var headerHeight = Cyan.toInt(el.$(".cyan-table-header tr:last td:first").css("height")) + 1;
    var bodyHeight = this.height - headerHeight * this.headerHeight - 1;

    if (!(this.pageSize <= 0))
        bodyHeight -= Cyan.toInt(el.$(".cyan-table-page").css("height")) + 1;

    if (Cyan.navigator.isEdge())
        bodyHeight -= 20;

    el.$(".cyan-table-body").css("height", bodyHeight + "px");
};

Cyan.Table.prototype.initHeader = function (column, headers, rowIndex)
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

Cyan.Table.prototype.renderHeader = function (tableDiv)
{
    var table = this;

    this.sortableClick = function ()
    {
        var sort = this.sort;
        var dir;
        if (table.sort == sort)
            dir = table.dir == "desc" ? "asc" : "desc";
        else
            dir = "asc";
        table.sort = sort;
        table.dir = dir;
        Cyan.$$(table.el).$(".cyan-table-sortable").each(function ()
        {
            var div = this.parentNode;
            if (div.sort == sort)
                this.className = "cyan-table-sortable cyan-table-sortable-" + dir;
            else
                this.className = "cyan-table-sortable";
        });
        table.loadData();
    };

    var headerDiv = document.createElement("div");
    headerDiv.className = "cyan-table-header";
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
                td.className = "cyan-table-header-column";
                td.style.textAlign = "center";
                td.innerHTML = column.title;
            }
            else
            {
                var align = column.align;
                if (!align)
                    align = column.checkboxName ? "center" : "left";

                var columnWidth = this.columnWidths[column.columnIndex];

                td.style.width = (columnWidth - 1) + "px";
                td.style.textAlign = align;
                var className = "cyan-table-header-column cyan-table-column" + column.columnIndex;
                if (column.sortable)
                    className += " cyan-table-header-sortable";

                td.className = className;

                var div = document.createElement("div");
                div.className = "cyan-table-content cyan-table-" + align;
                td.appendChild(div);
                div.style.width = (columnWidth - (align == "center" ? 3 : 5)) + "px";
                if (column.sortable && column.name)
                {
                    div.innerHTML = "<div class='cyan-table-sortable'>" + column.title + "</div>";
                    div.sort = column.name;
                    div.onclick = this.sortableClick;
                }
                else
                {
                    div.innerHTML = column.title;
                }
            }
        }

        if (j == 0)
        {
            td = document.createElement("td");
            td.style.width = this.emptyWidth ? ((this.emptyWidth - 1) + "px") : "0";
            td.className = "cyan-table-header-column cyan-table-empty";
            if (!this.emptyWidth)
                td.style.display = "none";
            if (this.headerHeight > 1)
                td.rowSpan = this.headerHeight;
            tr.appendChild(td);

            td = document.createElement("td");
            td.style.width = this.scrollWidth + "px";
            td.className = "cyan-table-header-scroll";
            if (this.headerHeight > 1)
                td.rowSpan = this.headerHeight;
            tr.appendChild(td);
        }
    }

    return headerDiv;
};

Cyan.Table.prototype.renderBody = function (tableDiv)
{
    var bodyDiv = document.createElement("div");
    bodyDiv.className = "cyan-table-body";
    bodyDiv.innerHTML = "<table><tbody></tbody></table>";
    tableDiv.appendChild(bodyDiv);

    Cyan.$$(bodyDiv).$("table").css("width", this.bodyWidth + "px");

    var table = this;
    bodyDiv.onclick = function (event)
    {
        event = event || window.event;
        var el = event.target || event.srcElement;

        while (el != bodyDiv && el.nodeName != "TD")
            el = el.parentNode;

        if (el.nodeName != "TD")
            return;

        var td = el;
        var tr = td.parentNode;

        var rowIndex = table.indexOf(tr.key);
        var columnIndex = td.cellIndex;

        if (table.cellSelectable)
        {
            if (table.multiple && event.ctrlKey)
            {
                if (Cyan.searchFirst(table.selecteds, function ()
                        {
                            return this.rowIndex == rowIndex && this.columnIndex == columnIndex;
                        }))
                {
                    table.unSelectCell(rowIndex, columnIndex, td);
                }
                else
                {
                    table.selectCell(rowIndex, columnIndex, true, td);
                }
            }
            else
            {
                table.selectCell(rowIndex, columnIndex, false, td);
            }
        }
        else
        {
            if (table.multiple && event.ctrlKey)
            {
                if (Cyan.Array.contains(table.selecteds, rowIndex))
                    table.unSelectRow(rowIndex, tr);
                else
                    table.selectRow(rowIndex, true, tr);
            }
            else
            {
                table.selectRow(rowIndex, false, tr);
            }
        }
    };

    return bodyDiv;
};

Cyan.Table.prototype.select = function (rowIndex, columnIndex)
{
    if (this.cellSelectable)
        this.selectCell(rowIndex, columnIndex, false);
    else
        this.selectRow(rowIndex, false);
};

Cyan.Table.prototype.getTr = function (rowIndex)
{
    return Cyan.$$(this.el).$(".cyan-table-body tr.cyan-table-record:" + rowIndex)[0];
};

Cyan.Table.prototype.getTd = function (rowIndex, colIndex)
{
    return Cyan.$$(this.el).$(".cyan-table-body tr.cyan-table-record:" + rowIndex + " td:" + colIndex)[0];
};

Cyan.Table.prototype.selectRow = function (rowIndex, append, el)
{
    if (append)
    {
        Cyan.Array.add(this.selecteds, rowIndex);
        Cyan.Elements.addClass(el || this.getTr(rowIndex), "cyan-table-selected");
    }
    else
    {
        this.selecteds = [rowIndex];
        Cyan.$$(this.el).$(".cyan-table-body tr.cyan-table-record").each(function (index)
        {
            if (index == rowIndex)
                Cyan.Elements.addClass(this, "cyan-table-selected");
            else
                Cyan.Elements.removeClass(this, "cyan-table-selected");
        });
    }
};

Cyan.Table.prototype.unSelectRow = function (rowIndex, el)
{
    Cyan.Array.removeElement(this.selecteds, rowIndex);
    Cyan.Elements.removeClass(el || this.getTr(rowIndex), "cyan-table-selected");
};

Cyan.Table.prototype.selectCell = function (rowIndex, columnIndex, append, el)
{
    if (append)
    {
        this.selecteds.push({rowIndex: rowIndex, columnIndex: columnIndex});
        Cyan.Elements.addClass(el || this.getTd(rowIndex, columnIndex), "cyan-table-selected");
    }
    else
    {
        this.selecteds = {rowIndex: rowIndex, columnIndex: columnIndex};
        Cyan.$$(this.el).$(".cyan-table-body tr.cyan-table-record").each(function (i)
        {
            var tds = this.childNodes, n = tds.length;
            for (var j = 0; j < n; j++)
            {
                if (i == rowIndex && j == columnIndex)
                    Cyan.Elements.addClass(tds[j], "cyan-table-selected");
                else
                    Cyan.Elements.removeClass(tds[j], "cyan-table-selected");
            }
        });
    }
};

Cyan.Table.prototype.unSelectCell = function (rowIndex, columnIndex, el)
{
    Cyan.Array.removeCase(this.selecteds, function ()
    {
        return this.rowIndex == rowIndex && this.columnIndex == columnIndex;
    });
    Cyan.Elements.removeClass(el || this.getTd(rowIndex, columnIndex), "cyan-table-selected");
};

Cyan.Table.prototype.showLoading = function (immediate)
{
    if (!this.mask)
    {
        this.mask = new Cyan.LoadingMask(this.el);
        this.mask.message = this.texts.loadingMsg;
    }
    this.mask.show(immediate);
};

Cyan.Table.prototype.hideLoading = function ()
{
    if (this.mask)
        this.mask.hide();
};

Cyan.Table.prototype.stopLoading = function ()
{
    if (this.mask)
        this.mask.hide();
};

Cyan.Table.prototype.renderRecords = function (records)
{
    var tbody = Cyan.$$(this.el).$(".cyan-table-body tbody");
    tbody.$("tr").remove();

    tbody = tbody[0];

    var n = records.length;
    for (var i = 0; i < n; i++)
    {
        var tr = document.createElement("tr");
        tbody.appendChild(tr);
        this.renderRecord(tr, records[i]);
    }

    this.refreshPageBar();
};

Cyan.Table.prototype.renderRecord = function (tr, record)
{
    var rowIndex = this.indexOf(record.key);

    var className = "cyan-table-record";
    if (!this.cellSelectable)
        className += " cyan-table-selectable";

    var rowClass = this.getRowClass(this.getRecord(rowIndex));
    if (rowClass)
        className += " " + rowClass;

    tr.className = className;
    tr.key = record.key;

    var rowStyle = this.getRowStyle(this.getRecord(rowIndex));
    if (rowStyle)
        tr.style.cssText = rowStyle;

    var td;
    var n = this.flatColumns.length;
    for (var i = 0; i < n; i++)
    {
        var column = this.flatColumns[i];

        var align = column.align;
        if (!align)
            align = column.checkboxName ? "center" : "left";

        var columnWidth = this.columnWidths[i];

        td = document.createElement("td");
        td.style.width = (columnWidth - 1) + "px";
        td.style.textAlign = align;
        className = "cyan-table-cell cyan-table-column" + i;
        if (this.cellSelectable)
            className += " cyan-table-selectable";
        if (column.wrap)
            className += " cyan-table-wrap";
        td.className = className;
        tr.appendChild(td);

        var div = document.createElement("div");
        div.className = "cyan-table-content cyan-table-" + align;
        td.appendChild(div);
        div.style.width = (columnWidth - (align == "center" ? 3 : 5)) + "px";
        div.innerHTML = this.getRenderer0(i)(record[i], record.key) || "";
    }

    td = document.createElement("td");
    td.style.width = this.emptyWidth ? ((this.emptyWidth - 1) + "px") : "0";
    td.className = "cyan-table-cell cyan-table-empty";
    if (!this.emptyWidth)
        td.style.display = "none";
    tr.appendChild(td);

    tr.onclick = this.rowclick;
    tr.ondblclick = this.rowdblclick;

    if (record.remark)
    {
        var remarkTr = document.createElement("tr");
        remarkTr.className = "cyan-table-remark";
        remarkTr.style.display = "none";
        if (tr.nextSibling)
            tr.parentNode.insertBefore(remarkTr, tr.nextSibling);
        else
            tr.parentNode.appendChild(remarkTr);

        var remarkTd = document.createElement("td");
        remarkTd.colSpan = n;
        remarkTr.appendChild(remarkTd);
        remarkTd.innerHTML = this.getRenderer0("remark")(record.remark, record.key) || "";
    }
};

Cyan.Table.prototype.renderPageBar = function (tableDiv)
{
    var pageBarDiv = document.createElement("div");
    pageBarDiv.className = "cyan-table-page";
    tableDiv.appendChild(pageBarDiv);

    this.initPageBar(pageBarDiv);

    return pageBarDiv;
};

Cyan.Table.prototype.initPageBar = function (pageBarDiv)
{
    var leftDiv = document.createElement("div");
    leftDiv.className = "cyan-table-page-left";
    pageBarDiv.appendChild(leftDiv);

    var rightDiv = document.createElement("div");
    rightDiv.className = "cyan-table-page-right";
    pageBarDiv.appendChild(rightDiv);

    this.initPageBarLeft(leftDiv);
    this.initPageBarRight(rightDiv);
};

Cyan.Table.prototype.initPageBarLeft = function (leftDiv)
{
    var texts = this.texts;
    var table = this;

    var firstDiv = document.createElement("div");
    firstDiv.className = "cyan-table-page-icon cyan-table-page-first";
    firstDiv.title = texts.firstText;
    leftDiv.appendChild(firstDiv);
    firstDiv.onclick = function ()
    {
        if (table.pageNo > 1)
            table.reload({pageNo: 1});
    };

    var preDiv = document.createElement("div");
    preDiv.className = "cyan-table-page-icon cyan-table-page-pre";
    preDiv.title = texts.prevText;
    leftDiv.appendChild(preDiv);
    preDiv.onclick = function ()
    {
        if (table.pageNo > 1)
            table.reload({pageNo: table.pageNo - 1});
    };

    var beforeTextDiv = document.createElement("div");
    beforeTextDiv.className = "cyan-table-page-text-before";
    beforeTextDiv.innerHTML = texts.beforePageText;
    leftDiv.appendChild(beforeTextDiv);

    var textDiv = document.createElement("div");
    textDiv.className = "cyan-table-page-text";
    textDiv.innerHTML = "<input value='" + (this.pageNo || 1) + "'>";
    leftDiv.appendChild(textDiv);
    Cyan.$$(textDiv).$("input").hotKey("#13", function ()
    {
        var value = Cyan.toInt(this.value);
        if (value <= 0)
            value = 1;
        this.value = value;
        table.reload({pageNo: value});
    });

    var afterTextDiv = document.createElement("div");
    afterTextDiv.className = "cyan-table-page-text-after";
    leftDiv.appendChild(afterTextDiv);

    var nextDiv = document.createElement("div");
    nextDiv.className = "cyan-table-page-icon cyan-table-page-next";
    nextDiv.title = texts.nextText;
    leftDiv.appendChild(nextDiv);
    nextDiv.onclick = function ()
    {
        if (table.pageNo < table.pageCount)
            table.reload({pageNo: table.pageNo + 1});
    };

    var lastDiv = document.createElement("div");
    lastDiv.className = "cyan-table-page-icon cyan-table-page-last";
    lastDiv.title = texts.lastText;
    leftDiv.appendChild(lastDiv);
    lastDiv.onclick = function ()
    {
        if (table.pageNo < table.pageCount)
            table.reload({pageNo: table.pageCount});
    };

    var refreshDiv = document.createElement("div");
    refreshDiv.className = "cyan-table-page-icon cyan-table-page-refresh";
    refreshDiv.title = texts.refreshText;
    leftDiv.appendChild(refreshDiv);
    refreshDiv.onclick = function ()
    {
        table.reload({showLoadingImmediate: true});
    };

    this.refreshPageBarLeft(leftDiv);
};

Cyan.Table.prototype.initPageBarRight = function (rightDiv)
{
    var table = this;
    this.pageClick = function ()
    {
        table.reload({pageNo: this.page});
    };

    var pagesDiv = document.createElement("div");
    pagesDiv.className = "cyan-table-page-pages";
    rightDiv.appendChild(pagesDiv);

    var msgDiv = document.createElement("div");
    msgDiv.className = "cyan-table-page-msg";
    rightDiv.appendChild(msgDiv);

    this.refreshPageBarRight(rightDiv);
};

Cyan.Table.prototype.refreshPageBar = function ()
{
    var pageBarDiv = Cyan.$$(this.el).$(".cyan-table-page")[0];
    this.refreshPageBarLeft(pageBarDiv.childNodes[0]);
    this.refreshPageBarRight(pageBarDiv.childNodes[1]);
};

Cyan.Table.prototype.refreshPageBarLeft = function (leftDiv)
{
    var texts = this.texts;
    var left = Cyan.$$(leftDiv);

    left.$(".cyan-table-page-text-after").html(texts.afterPageText.replace("{0}", this.pageCount || ""));
    left.$(".cyan-table-page-text input").val(this.pageNo);

    if (!this.pageNo || this.pageNo == 1)
    {
        left.$(".cyan-table-page-first").set("className", "cyan-table-page-icon cyan-table-page-first-disable");
        left.$(".cyan-table-page-pre").set("className", "cyan-table-page-icon cyan-table-page-pre-disable");
    }
    else
    {
        left.$(".cyan-table-page-first-disable").set("className", "cyan-table-page-icon cyan-table-page-first");
        left.$(".cyan-table-page-pre-disable").set("className", "cyan-table-page-icon cyan-table-page-pre");
    }

    if (!this.pageNo || !this.pageCount || this.pageNo == this.pageCount)
    {
        left.$(".cyan-table-page-next").set("className", "cyan-table-page-icon cyan-table-page-next-disable");
        left.$(".cyan-table-page-last").set("className", "cyan-table-page-icon cyan-table-page-last-disable");
    }
    else
    {
        left.$(".cyan-table-page-next-disable").set("className", "cyan-table-page-icon cyan-table-page-next");
        left.$(".cyan-table-page-last-disable").set("className", "cyan-table-page-icon cyan-table-page-last");
    }
};

Cyan.Table.prototype.createPageSpan = function (page, text)
{
    var span = document.createElement("SPAN");
    if (page == this.pageNo)
    {
        span.innerHTML = text;
        span.className = "cyan-table-page-selected";
    }
    else
    {
        var href = document.createElement("A");
        href.innerHTML = text;
        href.href = "#";
        href.page = page;
        href.onclick = this.pageClick;
        span.appendChild(href);
    }

    return span;
};

Cyan.Table.prototype.refreshPageBarRight = function (rightDiv)
{
    var texts = this.texts;
    var right = Cyan.$$(rightDiv);
    var pages = right.$(".cyan-table-page-pages")[0];
    pages.innerHTML = "";

    var msg;
    if (this.totalCount)
    {
        msg = texts.displayMsg.replace("{0}", this.start || "").replace("{1}", this.end || "").replace("{2}",
                this.totalCount || "");
    }
    else
    {
        msg = texts.emptyMsg;
    }
    right.$(".cyan-table-page-msg").html(msg);

    if (this.totalCount)
    {
        var startPage = Math.floor((this.pageNo - 1) / 10) * 10 + 1;
        var endPage = startPage + 9;

        if (endPage > this.pageCount)
            endPage = this.pageCount;

        if (startPage > 1)
            pages.appendChild(this.createPageSpan(1, "<<"));

        for (var i = startPage; i <= endPage; i++)
            pages.appendChild(this.createPageSpan(i, "[" + i + "]"));

        if (endPage < this.pageCount)
            pages.appendChild(this.createPageSpan(endPage + 1, ">>"));
    }
};

Cyan.Table.prototype.reload = function (params, callback)
{
    if (this.loading)
        return;

    if (params && params.pageNo != null)
    {
        this.pageNo = params.pageNo;
        this.sort = params.sort || this.sort;
        this.dir = params.dir || this.dir;
    }

    if (this.rendered)
        this.showLoading(params && params.showLoadingImmediate);

    this.loadData(callback);
};

Cyan.Table.prototype.updateRecord = function (rowIndex, record)
{
    var tr = Cyan.$$(this.el).$(".cyan-table-body .cyan-table-record:" + rowIndex);

    var remarkTr, expanded = false;
    if (tr.nextSibling && tr.nextSibling.className == "cyan-table-remark")
    {
        remarkTr = tr.nextSibling;
        expanded = remarkTr.style.display != "none";
    }

    var className = "cyan-table-record";
    if (!this.cellSelectable)
        className += " cyan-table-selectable";

    var rowClass = this.getRowClass(this.getRecord(rowIndex));
    if (rowClass)
        className += " " + rowClass;

    tr.set("className", className);

    var rowStyle = this.getRowStyle(this.getRecord(rowIndex));
    if (rowStyle)
        tr.cssText(rowStyle);

    var divs = tr.$(".cyan-table-content");
    var n = divs.length;
    for (var i = 0; i < n; i++)
        divs[i].innerHTML = this.getRenderer0(i)(record[i], record.key, expanded) || "";

    if (record.remark)
    {
        var remarkTd;
        if (remarkTr)
        {
            remarkTd = remarkTr.childNodes[0];
        }
        else
        {
            remarkTr = document.createElement("tr");
            remarkTr.style.display = "none";
            remarkTr.className = "cyan-table-remark";
            if (tr.nextSibling)
                tr.parentNode.insertBefore(remarkTr, tr.nextSibling);
            else
                tr.parentNode.appendChild(remarkTr);

            remarkTd = document.createElement("td");
            remarkTd.colSpan = n;
            remarkTr.appendChild(remarkTd);
        }

        remarkTd.innerHTML = this.getRenderer0("remark")(record.remark, record.key) || "";
    }
    else
    {
        remarkTr.parentNode.removeChild(remarkTr);
    }
};

Cyan.Table.prototype.addRecord = function (record, rowIndex)
{
    var tbody = Cyan.$$(this.el).$(".cyan-table-body tbody"), tr0;
    if (rowIndex != null)
        tr0 = tbody.$("tr.cyan-table-record")[rowIndex];

    tbody = tbody[0];

    var tr = document.createElement("tr");
    if (rowIndex == null || !tr0)
    {
        tbody.appendChild(tr);
        this.renderRecord(tr, record);
    }
    else
    {
        tbody.insertBefore(tr, tr0);
        this.renderRecord(tr, record);
    }
};
Cyan.Table.prototype.setHeader = function (index, title)
{
    this.columns[index].title = title;
    Cyan.$$(this.el).$(".cyan-table-header-column:" + index + " .cyan-table-content").html(title);
};

Cyan.Table.prototype.setRowVisible = function (rowIndex, visible)
{
    var tr = this.getTr(rowIndex);
    tr.style.display = visible ? "" : "none";
};

Cyan.Table.prototype.removeRecord = function (rowIndex)
{
    var tr = this.getTr(rowIndex);
    tr.parentNode.removeChild(tr);
};

Cyan.Table.prototype.getSelectedRowIndex = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.selecteds[0];
        return cell ? cell.rowIndex : null;
    }
    else
    {
        return this.selecteds[0];
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
        var result = [];
        this.selecteds.forEach(function (cell)
        {
            if (!Cyan.Array.contains(result, cell.rowIndex))
                result.push(cell.rowIndex);
        });
        return result;
    }
    else
    {
        return Cyan.clone(this.selecteds);
    }
};

Cyan.Table.prototype.getSelectedRows = function ()
{
    if (this.cellSelectable)
    {
        var result = [], rowIndexes = [];
        this.selecteds.forEach(function (cell)
        {
            var rowIndex = cell.rowIndex;
            if (!Cyan.Array.contains(rowIndexes, rowIndex))
            {
                rowIndexes.push(rowIndex);
                result.push(this.getRecord(rowIndex));
            }
        }, this);
        return result;
    }
    else
    {
        return this.selecteds.map(function (rowIndex)
        {
            return this.getRecord(rowIndex);
        }, this);
    }
};

Cyan.Table.prototype.getSelectedCell = function ()
{
    if (this.cellSelectable)
    {
        var cell = this.selected[0];
        if (cell)
        {
            var rowIndex = cell.rowIndex;
            var columnIndex = cell.columnIndex;
            var record = this.getRecord(rowIndex);
            return {rowIndex: rowIndex, columnIndex: columnIndex, record: record};
        }
    }
    return null;
};
