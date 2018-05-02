if (!System.SubList)
{
    System.SubList = {
        fields: {},
        getParentId: function (field)
        {
            return Cyan.Arachne.form.entity[field];
        },
        init: function (id, field)
        {
            System.SubList.fields[id] = field;
            window.setTimeout(function ()
            {
                var parentId = System.SubList.getParentId(field);
                if (!Cyan.Arachne.form.new$ || parentId)
                {

                    System.SubList.loadList(id);

                }
            }, 50);
        },
        saveForNew: function (id, callback)
        {
            if (Cyan.Arachne.form.new$)
            {
                save0({
                    form: Cyan.$$("form")[0],
                    validate: false,
                    callback: function (key)
                    {
                        if (key)
                        {
                            if (!window.returnValue)
                                window.returnValue = [key];
                            else
                                window.returnValue.push(key);
                            Cyan.Window.setReturnValue(returnValue);
                        }

                        if (Cyan.Arachne.form.entity)
                        {
                            var field = System.SubList.fields[id];
                            Cyan.Arachne.form.entity[field] = key;
                            Cyan.Arachne.form.new$ = false;
                        }
                        callback();
                    }
                });
            }
            else
            {
                callback();
            }
        },
        loadList: function (id, callback)
        {
            var field = System.SubList.fields[id];
            window[id + "$crud"][field] = System.SubList.getParentId(field);
            window[id + "$crud"].loadPage(0, {
                callback: function (result)
                {
                    Cyan.$$$(id).$("tbody tr").remove();

                    var records = result.records;
                    for (var i = 0; i < records.length; i++)
                        System.SubList.addRow(records[i], id);

                    if (callback)
                        callback();
                },
                wait: false,
                check: false,
                headers: {
                    "pre-action": "showList"
                }
            });
        },
        refresh: function (id, callback)
        {
            System.SubList.loadList(id, callback);
        },
        add: function (id, callback)
        {
            System.SubList.saveForNew(id, function ()
            {
                var crud = window[id + "$crud"];
                var field = System.SubList.fields[id];
                crud[field] = System.SubList.getParentId(field);
                crud.add("", {
                    target: "_modal",
                    callback: function (ret)
                    {
                        if (ret == true)
                        {
                            System.SubList.refresh(id);
                            callback(ret);
                            return;
                        }

                        var keys = ret;
                        if (keys && keys.length)
                        {
                            if (Cyan.$(id).getAttribute("orderable") == "true")
                            {
                                for (var i = 0; i < keys.length; i++)
                                {
                                    crud.load(keys[i], {
                                        callback: function (record)
                                        {
                                            System.SubList.addRow(record, id);
                                        },
                                        wait: false,
                                        check: false,
                                        headers: {
                                            "pre-action": "showList"
                                        }
                                    });
                                }
                            }
                            else
                            {
                                System.SubList.refresh(id);
                            }
                        }
                    }
                });
            });
        },
        edit: function (id, key)
        {
            var crud = window[id + "$crud"];
            crud.show(key, null, {
                target: "_modal",
                callback: function (ret)
                {
                    if (ret)
                    {
                        if (Cyan.$(id).getAttribute("orderable") == "true")
                        {
                            crud.load(key, {
                                callback: function (record)
                                {
                                    System.SubList.updateRow(record, id);
                                },
                                wait: false,
                                check: false,
                                headers: {
                                    "pre-action": "showList"
                                }
                            });
                        }
                        else
                        {
                            System.SubList.refresh(id);
                        }
                    }
                }
            });
        },
        remove: function (id, key)
        {
            Cyan.confirm("确定删除记录?", function (ret)
            {
                if (ret == "ok")
                {
                    var crud = window[id + "$crud"];
                    crud.remove(key, {
                        callback: function (ret)
                        {
                            if (ret)
                                System.SubList.removeRow(id, key);
                        }
                    });
                }
            });
        },
        up: function (id, key)
        {
            var rows = Cyan.$$$(id).$("tbody tr");
            var n = rows.length;
            for (var i = 0; i < n; i++)
            {
                var row = rows[i];
                if (row.key == key)
                {
                    if (i > 0)
                    {
                        var preRow = rows[i - 1];
                        row.parentNode.insertBefore(row, preRow);
                        rows[i - 1] = row;
                        rows[i] = preRow;
                        System.SubList.updateSort(id, rows);
                        break;
                    }
                }
            }
        },
        down: function (id, key)
        {
            var rows = Cyan.$$$(id).$("tbody tr");
            var n = rows.length;
            for (var i = 0; i < n; i++)
            {
                var row = rows[i];
                if (row.key == key)
                {
                    if (i < n - 1)
                    {
                        var nextRow = rows[i + 1];
                        nextRow.parentNode.insertBefore(nextRow, row);
                        rows[i + 1] = row;
                        rows[i] = nextRow;
                        System.SubList.updateSort(id, rows);
                        break;
                    }
                }
            }
        },
        updateSort: function (id, rows)
        {
            if (!rows)
                rows = Cyan.$$$(id).$("tbody tr");
            var n = rows.length;
            var keys = new Array(n);
            for (var i = 0; i < n; i++)
                keys[i] = rows[i].key;
            window[id + "$crud"].sort(keys);
        },
        addRow: function (record, id)
        {
            var key = record.key;
            var table = Cyan.$(id);
            var tbody = Cyan.$$(table).$("tbody")[0];
            var row = document.createElement("tr");
            tbody.appendChild(row);

            var cells = record.cells;
            row.key = key;

            for (var i = 0; i < cells.length; i++)
                System.SubList.addCell(row, cells[i], null);

            if (table.getAttribute("readOnly") != "true")
            {
                if (table.getAttribute("orderable") == "true")
                {
                    System.SubList.addAction(row, "up", id, key);
                    System.SubList.addAction(row, "down", id, key);
                }
                System.SubList.addAction(row, "remove", id, key);
                var editable = table.getAttribute("editable");
                if(editable == "true")
                {
                    System.SubList.addAction(row, "edit", id, key);
                }
            }
        },
        addCell: function (row, html, className)
        {
            var cell = document.createElement("td");

            if (className)
                cell.className = className;
            row.appendChild(cell);
            cell.innerHTML = html || "";
        },
        addAction: function (row, action, id, key)
        {
            System.SubList.addCell(row,
                    "<span class='" + action + "' onclick=' System.SubList." + action + "(\"" + id + "\",\"" + key +
                    "\")'></span>", action);
        },
        updateRow: function (record, id)
        {
            var key = record.key;
            var rows = Cyan.$$$(id).$("tbody tr");
            for (var i = 0; i < rows.length; i++)
            {
                var row = rows[i];
                if (row.key == key)
                {
                    var cells = record.cells;
                    var tds = Cyan.$$(row).$("td");

                    for (var j = 0; j < cells.length; j++)
                        tds[j].innerHTML = cells[j];

                    return;
                }
            }
        },
        removeRow: function (id, key)
        {
            var rows = Cyan.$$$(id).$("tbody tr");
            for (var i = 0; i < rows.length; i++)
            {
                var row = rows[i];
                if (row.key == key)
                {
                    row.parentNode.removeChild(row);
                    return;
                }
            }
        }
    };

    Cyan.Class.overwrite(window, "updateData", function (entity, callback)
    {
        this.inherited(entity, function ()
        {
            var ids = [];
            for (var id in System.SubList.fields)
            {
                ids.push(id);
            }

            var f = function ()
            {
                if (ids.length)
                    System.SubList.refresh(ids.pop(), f);
                else if (callback)
                    callback();
            };

            f();
        });
    });
}