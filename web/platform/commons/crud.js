Cyan.importJs("/platform/weboffice/office.js");

System.Crud = {
    messages: {}
};

System.importLanguage("crud");

function query(callback)
{
    if (window.subBody && subBody.reload)
    {
        if (callback)
        {
            var callback0 = callback;
            callback = function ()
            {
                subBody.reload(callback0);
            };
        }
        else
        {
            callback = function ()
            {
                subBody.reload();
            }
        }
    }

    if (mainBody && mainBody.search)
    {
        mainBody.search(Cyan.$("text").value, callback);
    }
    else if (mainBody && mainBody.reload)
    {
        if (Cyan.Table && mainBody instanceof Cyan.Table)
            mainBody.reload({pageNo: 1}, callback);
        else
            mainBody.reload(callback);
    }
    else if (window.chart && chart.reload)
    {
        chart.reload(callback);
    }
}

function queryBy(args)
{
    if (args)
    {
        var form = Cyan.Arachne.form;
        for (var name in args)
        {
            var value = args[name];
            if (value != null)
            {
                var component = Cyan.$$$(name);
                if (component.length)
                    component.setValue(value);
                Cyan.setValue(form, name, value);
            }
        }

        query();
    }
}

function moreQuery(forward)
{
    window.forward(forward, {
        target: "_modal",
        callback: function (result)
        {
            if (result)
            {
                this.moreQueryArgs = result;
                queryBy(result);
            }
        }
    });
}

function moreQuery2(forward)
{
    window.forward(forward, {
        target: "_page"
    });
}

function getSelectedKey()
{
    return mainBody.getSelectedId();
}

function requireSelectedKey()
{
    var key = mainBody.getSelectedId();
    if (key == null)
        Cyan.message(System.Crud.messages.requireSelectedKey);

    return key;
}

function getSelectedKeys()
{
    return mainBody.getSelectedIds();
}

function reload(key, callback)
{
    if (window.mainBody)
    {
        if (Cyan.Table && mainBody instanceof Cyan.Table)
        {
            if (key)
            {
                load(key, function (result)
                {
                    mainBody.update(mainBody.indexOf(key), result);
                    if (callback)
                        callback();
                });
            }
            else
            {
                mainBody.reload(callback);
            }
        }
        else
        {
            if (key)
            {
                load(key, function (result)
                {
                    mainBody.getNodeById(key).update(result);
                    if (callback)
                        callback();
                });
            }
        }
    }
    else if (Cyan.Arachne.form.entity)
    {
        load(key, {
            callback: function (result)
            {
                Cyan.Arachne.form.entity = result;
                updateData(result, callback);
            },
            validate: false
        });
    }
}

function updateData(entity, callback)
{
    updateForm(entity);
    if (callback != null)
        callback();
}

function updateForm(entity)
{
    Cyan.$$("form").setValue(entity, "entity");
}

function reloadChildren(key)
{
    var node = mainBody.getNodeById(key);
    node.reloadChildren(true, function ()
    {
        if (!node.isExpanded())
            node.expand();
    });
}

function reloadDescendants(key)
{
    var node = mainBody.getNodeById(key);
    load(key, function (result)
    {
        node.update(result);
        node.reloadDescendants();
    });
}

function afterSave(ret)
{
    if (System.editingKey)
    {
        reload(System.editingKey);
    }
    else if (Cyan.Table && mainBody instanceof Cyan.Table)
    {
        mainBody.reload();
    }
    else if (System.parentKey != null)
    {
        reloadChildren(System.parentKey);
    }
}

function afterDelete()
{
    reload();
}

function afterSort()
{
    reload();
}

function afterMove()
{
    reload();
}

function afterCopy()
{
    reload();
}

function refresh(callback)
{
    if (Cyan.Table && mainBody instanceof Cyan.Table)
        mainBody.reload(null, callback);
}

function reset()
{
    Cyan.$$("form").reset();
}

var returnValue;

function addSuccess(key)
{
    Cyan.message(System.Crud.messages.addSuccess, function ()
    {
        var close = Cyan.Arachne.form.duplicateKey || window.closeAfterAdd;
        if (close)
        {
            Cyan.Window.setReturnValue(key);
            Cyan.Window.closeWindow();
        }
        else
        {
            if (key)
            {
                if (!returnValue)
                    returnValue = [key];
                else
                    returnValue.push(key);
                Cyan.Window.setReturnValue(returnValue);
            }
            reset();
        }
    });
}

function updateSuccess()
{
    Cyan.message(System.Crud.messages.updateSuccess, function ()
    {
        Cyan.Window.setReturnValue(true);
        Cyan.Window.closeWindow();
    });
}

function isNew()
{
    return Cyan.Arachne.form.new$;
}

Cyan.onload(function ()
{
    var add = window.add0 = window.add;
    window.add = function (forward)
    {
        System.editingKey = null;
        if (mainBody.getSelectedNode)
        {
            var node = mainBody.getSelectedNode();
            if (!node)
            {
                Cyan.message(System.Crud.messages.requireSelectedParentNode);
                return;
            }

            System.parentKey = node.id;
            add(System.parentKey, forward, {
                target: "_modal",
                callback: function (ret)
                {
                    if (ret)
                        afterSave(ret);
                }
            });
        }
        else
        {
            add(forward, {
                target: "_modal",
                callback: function (ret)
                {
                    if (ret)
                        afterSave(ret);
                }
            });
        }
    };

    var show = window.show0 = window.show;
    window.show = function (key, forward)
    {
        if (!key && mainBody.getSelectedNode)
        {
            var node = mainBody.getSelectedNode();
            if (!node)
            {
                Cyan.message(System.Crud.messages.requireSelectedEditNode);
                return;
            }
            key = node.id;
        }

        System.editingKey = key;
        show(key, forward, {
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                    afterSave(System.editingKey);
            }
        });
    };

    var duplicate = window.duplicate0 = window.duplicate;
    window.duplicate = function (key, forward)
    {
        System.editingKey = null;
        if (!key && mainBody.getSelectedNode)
        {
            var node = mainBody.getSelectedNode();
            if (!node)
            {
                Cyan.message(System.Crud.messages.requireSelectedDuplicateNode);
                return;
            }

            var parentNode = node.getParent();
            if (!parentNode)
            {
                Cyan.message(System.Crud.messages.mayNotDuplicateRoot);
                return;
            }

            key = node.id;
            System.parentKey = parentNode.id;
        }

        duplicate(key, forward, {
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                    afterSave(ret);
            }
        });
    };

    var save = window.save0 = window.save;
    window.save = function ()
    {
        if (window.beforeSave)
        {
            if (!beforeSave())
                return;
        }
        save({
            callback: Cyan.Arachne.form.new$ ? addSuccess : updateSuccess,
            form: Cyan.$$("form")[0],
            validate: true
        });
    };

    var exp = window.exp0 = window.exp;
    window.exp = function ()
    {
        if (arguments.length == 1)
        {
            exp(arguments[0], {form: false});
        }
        else
        {
            var url = "/platform/commons/export.ptl";
            if (arguments.length > 0)
            {
                for (var i = 0; i < arguments.length; i++)
                    url += (i == 0 ? "?" : "&") + "type=" + arguments[i];
            }
            Cyan.Window.showModal(url, function (type)
            {
                if (type)
                    exp(type, {form: false});
            });
        }
    };

    var showImp = window.showImp;
    window.showImp = function ()
    {
        if (mainBody.getSelectedNode)
        {
            var node = mainBody.getSelectedNode();
            if (!node)
            {
                Cyan.message(System.Crud.messages.requireSelectedParentNode);
                return;
            }

            var parentKey = node.id;
            showImp(parentKey, {
                target: "_modal",
                callback: function (ret)
                {
                    if (ret)
                        reloadChildren(parentKey);
                }
            });
        }
        else
        {
            showImp({
                target: "_modal",
                callback: function (ret)
                {
                    if (ret)
                        refresh();
                }
            });
        }
    };

    var exportEntity = window.exportEntity0 = window.exportEntity;
    window.exportEntity = function ()
    {
        exportEntity(arguments[0], arguments[1] || "doc", {form: false, obj: {}});
    };

    var exportEntitys = window.exportEntitys0 = window.exportEntitys;
    window.exportEntitys = function ()
    {
        exportEntitys(arguments[0] || "doc", {form: false});
    };

    var showSortList = window.showSortList;
    window.showSortList = function (forward)
    {
        showSortList(forward, {
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                    afterSort();
            }
        });
    };

    if (window.move && window.copy && !window.left && Cyan.Tree && window.mainBody instanceof Cyan.Tree)
    {
        var move = window.move;

        var move0 = function (dragNode, dropNode, point, type)
        {
            var oldParent = dragNode.getParent();
            var newParent;
            var afterNode;
            if (point == "append")
            {
                newParent = dropNode;
            }
            else
            {
                newParent = dropNode.getParent();
                if (point == "above")
                    afterNode = dropNode;
                else if (point)
                    afterNode = dropNode.getNextSibling();
            }

            if (type == 1)
            {
                dragNode.moveTo(newParent, afterNode);

                if (oldParent != newParent)
                {
                    move(dragNode.id, newParent.id, {
                        wait: true, form: false, callback: function ()
                        {
                            sortChildren(newParent);
                            if (window.afterTreeMove)
                                afterTreeMove(dragNode.id, oldParent.id, newParent.id)
                        }
                    });
                }
                else
                {
                    sortChildren(newParent);
                }
            }
            else if (type == 2)
            {
                copy(dragNode.id, newParent.id, {
                    wait: true, form: false, callback: function (newKey)
                    {
                        var callback;
                        if (point != "append")
                        {
                            callback = function ()
                            {
                                dragNode.tree.getNodeById(newKey).moveTo(newParent, afterNode);
                                sortChildren(newParent);
                            };
                        }
                        newParent.reloadChildren(true, callback);
                    }
                });
            }
        };

        window.move = function (type)
        {
            var dragNode = Cyan.Widget.DragContext.dragNode;
            var dropNode = Cyan.Widget.DragContext.dropNode;
            var point = Cyan.Widget.DragContext.dropPoint;

            if (type == 1 || (point != "append" && dragNode.getParent() == dropNode.getParent()))
            {
                Cyan.confirm(System.Crud.messages.confirmMoveNode, function (ret)
                {
                    if (ret == "ok")
                        move0(dragNode, dropNode, point, 1);
                });
            }
            else if (type == 2)
            {
                if (point == "append")
                {
                    Cyan.confirm(System.Crud.messages.confirmCopyNode, function (ret)
                    {
                        if (ret == "ok")
                            move0(dragNode, dropNode, point, 2);
                    });
                }
            }
            else
            {
                Cyan.customConfirm([
                            {
                                text: System.Crud.messages.moveNode,
                                callback: function ()
                                {
                                    move0(dragNode, dropNode, point, 1);
                                }
                            },
                            {
                                text: System.Crud.messages.copyNode,
                                callback: function ()
                                {
                                    move0(dragNode, dropNode, point, 2);
                                }
                            },
                            {
                                text: System.Crud.messages.cancel
                            }
                        ],
                        {
                            message: System.Crud.messages.selectOperation,
                            width: 300
                        });
            }
        };

        var copy = window.copy;
        window.copy = function (key, parentKey)
        {
            if (isChildOf(parentKey, key))
            {
                Cyan.message(System.Crud.messages.mayNotDuplicateToChild);
                return;
            }
            copy(key, parentKey, {
                form: false, callback: function (ret)
                {
                    reloadChildren(parentKey);
                }
            });
        };
    }

    if (Cyan.Arachne.form.ownerField)
    {
        if (window.moveTo)
        {
            var moveTo = window.moveTo;
            window.moveTo = function (key, ownerId)
            {
                moveTo(key, ownerId, Cyan.Arachne.form[Cyan.Arachne.form.ownerField],
                        {wait: true, form: false, callback: afterMove});
            };
        }

        if (window.moveAllTo)
        {
            var moveAllTo = window.moveAllTo;
            window.moveAllTo = function (keys, ownerId)
            {
                if (!keys)
                {
                    keys = Cyan.$$("#keys").checkedValues()
                }

                if (!keys.length)
                {
                    Cyan.message(System.Crud.messages.requireSelectedCopyRecord);
                    return;
                }

                moveAllTo(keys, ownerId, Cyan.Arachne.form[Cyan.Arachne.form.ownerField],
                        {wait: true, form: false, callback: afterMove});
            };
        }

        if (window.copyTo)
        {
            var copyTo = window.copyTo;
            window.copyTo = function (key, ownerId)
            {
                copyTo(key, ownerId, Cyan.Arachne.form[Cyan.Arachne.form.ownerField],
                        {wait: true, form: false, callback: afterCopy});
            };
        }

        if (window.copyAllTo)
        {
            var copyAllTo = window.copyAllTo;
            window.copyAllTo = function (keys, ownerId)
            {
                if (!keys)
                {
                    keys = Cyan.$$("#keys").checkedValues()
                }

                if (!keys.length)
                {
                    Cyan.message(System.Crud.messages.requireSelectedCopyRecord);
                    return;
                }

                copyAllTo(keys, ownerId, Cyan.Arachne.form[Cyan.Arachne.form.ownerField],
                        {wait: true, form: false, callback: afterCopy});
            };
        }
    }

    if (Cyan.Arachne.form.ownerField && window.left && window.left.getItemById)
    {
        var ownerId = Cyan.Arachne.form[Cyan.Arachne.form.ownerField];
        if (ownerId)
        {
            Cyan.run(function ()
            {
                return left.loaded;
            }, function ()
            {
                var item = left.getItemById(ownerId);
                if (item && item.select)
                    item.select();
            });
        }
    }

    if (window.subBody)
    {
        Cyan.importJs("render.js");
    }
});

function isChildOf(key, parentKey)
{
    if (key == parentKey)
        return true;
    var node = mainBody.getNodeById(key).getParent();
    if (node)
        return isChildOf(node.id, parentKey);
    return false;
}

function del(key)
{
    if (mainBody.getSelectedNode)
    {
        if (!key)
        {
            if (mainBody.getRoot().isSelected())
            {
                Cyan.message(System.Crud.messages.mayNotDeleteRoot);
                return;
            }

            key = mainBody.getTopSelectedIds();
            if (!key)
            {
                Cyan.message(System.Crud.messages.requireSelectedDeleteNode);
                return;
            }
        }

        Cyan.confirm(System.Crud.messages.deleteNodeConfirm, function (ret)
        {
            if (ret == "ok")
            {
                removeAll(key, function (ret)
                {
                    if (Cyan.isArray(key))
                    {
                        for (var i = 0; i < key.length; i++)
                            mainBody.getNodeById(key[i]).remove();
                    }
                    else
                    {
                        mainBody.getNodeById(key).remove();
                    }
                });
            }
        });
    }
    else
    {
        if (key)
        {
            Cyan.confirm(System.Crud.messages.deleteRecordConfirm, function (ret)
            {
                if (ret == "ok")
                {
                    remove(key, function ()
                    {
                        if (Cyan.Table && mainBody instanceof Cyan.Table)
                            mainBody.reload();
                    });
                }
            });
        }
        else
        {
            if (!Cyan.$$("#keys").checkeds().length)
            {
                Cyan.message(System.Crud.messages.requireSelectedDeleteRecord);
                return;
            }

            Cyan.confirm(System.Crud.messages.deleteRecordConfirm, function (ret)
            {
                if (ret == "ok")
                {
                    removeAll(null, function (ret)
                    {
                        if (ret)
                        {
                            Cyan.Arachne.form.keys = null;
                            afterDelete();
                        }
                    });
                }
            });
        }
    }
}

function up()
{
    var nodes = mainBody.getSelectedNodes();
    if (nodes.length == 0)
    {
        Cyan.message(System.Crud.messages.requireSelectedUpNode);
        return;
    }

    if (nodes.length > 1)
    {
        Cyan.message(System.Crud.messages.canOnlyUpOneNode);
        return;
    }

    var node = nodes[0];

    if (node.move(-1))
        sortChildren(node.getParent());
}

function down()
{
    var nodes = mainBody.getSelectedNodes();
    if (nodes.length == 0)
    {
        Cyan.message(System.Crud.messages.requireSelectedDownNode);
        return;
    }

    if (nodes.length > 1)
    {
        Cyan.message(System.Crud.messages.canOnlyDownOneNode);
        return;
    }

    var node = nodes[0];

    if (node.move(1))
        sortChildren(node.getParent());
}

function sortChildren(node)
{
    var ids = node.getChildren().gets("id");
    if (ids.length > 1)
        sort(ids, {wait: false, form: false});
}

function leftSelect(field, id)
{
    if (!id)
    {
        if (left.getSelectedId)
            id = left.getSelectedId();
    }

    if (id)
    {
        Cyan.Arachne.form[field] = id;
        query();
    }
}

function clone(key, name)
{
    if (!name)
        name = Cyan.Arachne.formURI;

    if (!key)
    {
        var node = mainBody.getSelectedNode();
        if (node)
            key = node.id;
    }

    if (key)
    {
        cloneForCopy(key, {
            wait: false, form: false, callback: function (result)
            {
                System.getMainWindow().System.putToClipboard({
                    type: name, content: Cyan.Ajax
                            .toJson(result), key: key
                });
                Cyan.message(System.Crud.messages.putToClipboard);
            }
        });
    }
}

function paste(parentKey, name)
{
    if (!name)
        name = Cyan.Arachne.formURI;

    if (!parentKey)
    {
        var node = mainBody.getSelectedNode();
        if (node)
            parentKey = node.id;
    }

    if (parentKey)
    {
        var data = System.getMainWindow().System.getClipboardData(name);
        if (data)
        {
            if (data.obj)
            {
                copy(data.obj.key, parentKey, {
                    form: false, callback: function (ret)
                    {
                        reloadChildren(parentKey);
                    }
                });
            }
            else if (data.content)
            {
                saveEntity(data.content, true, parentKey, {
                    callback: function (ret)
                    {
                        reloadChildren(parentKey);
                    }
                });
            }
        }
    }
}

function enableCP()
{
    if (window.cloneForCopy)
    {
        Cyan.$$(document.body).hotKey(
                {
                    "ctrl c": function ()
                    {
                        clone(null);
                    },
                    "ctrl v": function ()
                    {
                        paste(null);
                    }
                });
    }
}

function moveToLeft(type)
{
    var point = Cyan.Widget.DragContext.dropPoint;

    var dragIds = Cyan.Widget.DragContext.dragIds;
    var dropId = Cyan.Widget.DragContext.dropId;

    if ((point == null || point == "append") && canMoveTo(dragIds, dropId))
    {
        if (type == 1)
        {
            Cyan.confirm(System.Crud.messages.confirmMoveRecord, function (ret)
            {
                if (ret == "ok")
                    moveToLeft0(dragIds, dropId, 1);
            });
        }
        else if (type == 2)
        {
            Cyan.confirm(System.Crud.messages.confirmCopyRecord, function (ret)
            {
                if (ret == "ok")
                    moveToLeft0(dragIds, dropId, 2);
            });
        }
        else
        {
            Cyan.customConfirm([
                        {
                            text: System.Crud.messages.moveRecord,
                            callback: function ()
                            {
                                moveToLeft0(dragIds, dropId, 1);
                            }
                        },
                        {
                            text: System.Crud.messages.copyRecord,
                            callback: function ()
                            {
                                moveToLeft0(dragIds, dropId, 2);
                            }
                        },
                        {
                            text: System.Crud.messages.cancel
                        }
                    ],
                    {
                        message: System.Crud.messages.selectOperation,
                        width: 400
                    });
        }
    }
}

function canMoveTo(dragIds, dropId)
{
    return true;
}

function moveToLeft0(dragIds, dropId, type)
{
    if (type == 1)
    {
        moveAllTo(dragIds, dropId);
    }
    else if (type == 2)
    {
        copyAllTo(dragIds, dropId);
    }
}

function print(type)
{
    if (!type)
        type = "doc";

    System.Office.getHiddenOffice(function (office)
    {
        office.fileType = type;
        exp0(type, {
            form: false,
            target: office,
            ajax: {
                fileType: type
            },
            callback: function ()
            {
                office.print();
            }
        });
    });
}

function showSubView(name)
{
    var subBody = Cyan.$("subBody");
    if (subBody)
    {
        if (!subBody.fade)
            subBody.fade = new Cyan.Render.PositionFade(subBody);

        Cyan.Elements.setText(event$.srcElement, System.Crud.messages[
                subBody.style.display == "none" ? "hideSubView" : "showSubView"](name));
        subBody.fade.fade(null, 1);
    }
}

function getEntity()
{
    return Cyan.Arachne.form.entity;
}
