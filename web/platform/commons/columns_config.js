Cyan.onload(function ()
{
    setTimeout(function ()
    {
        if (Cyan.Table && window.mainBody instanceof Cyan.Table && mainBody.getExtComponent)
        {
            var url = location.pathname;
            if (location.search)
                url += location.search;
            url = System.clearMenuId(url);

            var columnsConfig = [];
            var grid = mainBody.grid;
            var columnModel = grid.getColumnModel();
            var n = columnModel.getColumnCount();
            var autoExpandColumn = grid.autoExpandColumn;
            var changedColumns = [];

            Cyan.Arachne.get("/userProperty", {propertyName: "columns_config:" + url}, function (s)
            {
                if (s)
                {
                    try
                    {
                        changedColumns = eval(s);
                    }
                    catch (e)
                    {
                        //
                    }
                    if (changedColumns && changedColumns.length)
                    {
                        Cyan.each(changedColumns, function (changedColumn)
                        {
                            if (changedColumn)
                            {
                                for (var i = 0; i < n; i++)
                                {
                                    var column = columnModel.getColumnAt(i);
                                    if (column.header == changedColumn.header)
                                    {
                                        if (changedColumn.width)
                                            columnModel.setColumnWidth(i, changedColumn.width);
                                        if (changedColumn.hidden != null)
                                            columnModel.setHidden(i, changedColumn.hidden);
                                    }
                                }
                            }
                        });
                    }
                }

                for (var i = 0; i < n; i++)
                {
                    var column = columnModel.getColumnAt(i);
                    if (column.id != "expander")
                    {
                        columnsConfig.push({
                            id: column.id,
                            width: autoExpandColumn == column.id ? null : column.width,
                            hidden: column.hidden
                        });
                    }
                }
            });

            Cyan.attach(window, "unload", function ()
            {
                var changed = false;
                for (var i = 0; i < n; i++)
                {
                    var column = columnModel.getColumnAt(i);
                    if (column.id != "expander")
                    {
                        var width = autoExpandColumn == column.id ? null : column.width;
                        var hidden = column.hidden;
                        var columnConfig;
                        for (var k = 0; k < columnsConfig.length; k++)
                        {
                            if (columnsConfig[k].id == column.id)
                            {
                                columnConfig = columnsConfig[k];
                                break;
                            }
                        }

                        if (columnConfig && (columnConfig.width != width || columnConfig.hidden != hidden))
                        {
                            changed = true;
                            var changedColumn;

                            for (var j = 0; j < changedColumns.length; j++)
                            {
                                if (changedColumns[j].header == column.header)
                                {
                                    changedColumn = changedColumns[j];
                                    break;
                                }
                            }

                            if (!changedColumn)
                                changedColumns.push(changedColumn = {});

                            changedColumn.header = column.header;
                            if (columnConfig.width != width)
                                changedColumn.width = width;
                            if (columnConfig.hidden != hidden)
                                changedColumn.hidden = hidden;
                        }
                    }
                }

                if (changed)
                {
                    var s = Cyan.Ajax.toJson(changedColumns);
                    Cyan.Arachne.post("/userProperty", {propertyName: "columns_config:" + url, propertyValue: s});
                }
            });
        }
    }, 1);
});