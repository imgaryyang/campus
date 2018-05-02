Cyan.importJs("combox.js");
Cyan.importJs("/platform/group/member.js");

var roleApps;
Cyan.onload(function ()
{
    window.afterSave = function ()
    {
        mainBody.reload();
    };

    setTimeout(function ()
    {
        Cyan.Class.overwrite(window, "showSortList", function (forward)
        {
            if (forward == "parent")
            {
                var record = mainBody.getSelectedRow();
                var checked = false;
                if (record)
                {
                    if (record.hasChildren)
                    {
                        Cyan.Arachne.form.parentRoleId = record.key;
                        checked = true;
                    }
                }

                if (!checked)
                {
                    Cyan.message("请选择一个目录");
                    return;
                }
            }

            this.inherited(forward);
            Cyan.Arachne.form.parentRoleId = null;
        });
    }, 100);

    if (Cyan.Arachne.form.entity)
    {
        Cyan.$$$("appForm").disable();
        roleApps = Cyan.Arachne.form.entity.roleApps;
        if (!roleApps)
            roleApps = Cyan.Arachne.form.entity.roleApps = [];


        var save = window.save;
        window.save = function ()
        {
            saveItem();

            var roleApps = Cyan.Arachne.form.entity.roleApps;
            Cyan.Arachne.form.entity.roleApps = Cyan.Ajax.toJson(roleApps);

            save({
                form: "form",
                callback: Cyan.Arachne.form.new$ ? addSuccess : updateSuccess
            });

            Cyan.Arachne.form.entity.roleApps = roleApps;
        };

        var getAuths = window.getAuths;
        window.getAuths = function (menuId, callback)
        {
            if (menuId == "root")
            {
                callback([], false);
                return;
            }

            var auths = authsMap[menuId];
            if (auths)
            {
                callback(auths, false);
            }
            else
            {
                getAuths(menuId, {
                    obj: {}, wait: false, callback: function (auths)
                    {
                        authsMap[menuId] = auths;
                        callback(auths, true);
                    }
                });
            }
        };
    }

    Cyan.Class.overwrite(window, "addRoleToUser", function (roleId)
    {
        var addRoleToUser = this.inherited;

        System.selectUsers(null, null, function (ret)
        {
            if (ret)
            {
                addRoleToUser(roleId, Cyan.map(ret, "userId"), function ()
                {
                    Cyan.message("赋权成功");
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "moveCatalog", function (roleId)
    {
        if (!Cyan.$$("#keys").checkedValues().length)
        {
            Cyan.message("请选择要移动的记录");
            return;
        }

        var moveCatalog = this.inherited;

        selectCatalog({
            target: "_modal",
            callback: function (ret)
            {
                if (ret)
                {
                    moveCatalog(ret, {
                        form: Cyan.$$("form")[0],
                        callback: function ()
                        {
                            mainBody.reload();
                        }
                    })
                }
            }
        })
    });
});

var currentApp;
var authsMap = {};

function menuselect()
{
    saveItem();

    var node = menuTree.getSelectedNode();
    if (node)
    {
        var id = node.getValue().value.menuId;
        if (id)
        {
            refreshAuths(id);

            var checkbox = Cyan.$$("#appId").searchFirst(function ()
            {
                return this.value == id;
            });
            if (checkbox && checkbox.checked)
            {
                setItem(id);
            }
            else
            {
                disableAppForm();
            }
        }
        else
        {
            disableAppForm();
        }
    }
}

function disableAppForm()
{
    Cyan.$$("#auth").set("checked", false);
    Cyan.$$$("appForm").disable();
    currentApp = null;
}

function menucheck(node)
{
    var treeNode = menuTree.getNodeById(node.id);
    var checkbox = Cyan.$$("#appId").searchFirst(function ()
    {
        return this.value == node.id;
    });
    if (checkbox && checkbox.checked)
    {
        if (treeNode.isSelected())
            setItem(node.id);
        else
            treeNode.select();

        treeNode.expandAndThrough(function ()
        {
            var node = this;
            var checkbox = Cyan.$$("#appId").searchFirst(function ()
            {
                return this.value == node.id;
            });
            if (checkbox)
                checkbox.checked = true;

            var app = roleApps.searchFirst(function ()
            {
                return this.appId == node.id;
            });
            if (!app)
            {
                roleApps.push(app = {
                    appId: node.id,
                    scopeId: -1,
                    scopeName: '所有部门',
                    auths: "#all",
                    self: false
                });

                getAuths(node.id, function (auths)
                {
                    if (app.auths == null)
                    {
                        var codes = auths.gets("authCode");
                        codes.removeElement("default");
                        app.auths = codes.join(",");
                    }
                });
            }
        });
    }
    else
    {
        roleApps.removeCase(function ()
        {
            return this.appId == node.id;
        });
        if (treeNode.isSelected())
            disableAppForm();
    }
}

function refreshAuths(menuId)
{
    getAuths(menuId, function (auths, lazyLoad)
    {
        if (lazyLoad)
        {
            var node = menuTree.getSelectedNode();
            if (node)
            {
                var id = node.getValue().value.menuId;
                if (id && id != menuId)
                    return;
            }
        }

        var html = "";
        auths.each(function ()
        {
            if (this.authCode != "default")
                html += "<div class='checkbox'><input name='auth' type='checkbox' value='" + this.authCode +
                "' class='checkbox'><span>" + this.authName + "</span></div>";
        });
        Cyan.$("auths").innerHTML = html;

        if (lazyLoad)
        {
            if (currentApp)
            {
                if (currentApp.auths == "#all")
                {
                    Cyan.$$("#auth").set("checked", true);
                }
                else
                {
                    auths = (currentApp.auths || "").split(",");
                    Cyan.$$("#auth").each(function ()
                    {
                        this.checked = Cyan.Array.contains(auths, this.value);
                    });
                }
            }
            else
            {
                Cyan.$$("#auth").disable();
            }
        }
    });
}

function saveItem()
{
    if (currentApp)
    {
        var app = roleApps.searchFirst(function ()
        {
            return this.appId == currentApp.appId;
        });
        if (!app)
            app = {appId: currentApp.appId};
        app.scopeId = parseInt($("scopeId").value);
        app.scopeName = Cyan.$$$("scopeId").getText();
        app.auths = Cyan.$$("#auth").checkedValues().join(",");
        app.condition = $("condition").value;
        app.self = $("self").checked;
    }
}

function setItem(appId)
{
    Cyan.$$$("appForm").enable();
    var app = roleApps.searchFirst(function ()
    {
        return this.appId == appId;
    });
    if (!app)
    {
        roleApps.push(app = {
            appId: appId,
            scopeId: -1,
            scopeName: "所有部门",
            auths: "#all",
            self: false
        });
    }
    if (app.scopeId)
    {
        Cyan.$$$("scopeId").setValue(app.scopeId.toString());
        Cyan.$$$("scopeId").setText(app.scopeName);
    }

    if (app.auths == "#all")
    {
        Cyan.$$("#auth").set("checked", true);
    }
    else
    {
        var auths = (app.auths || "").split(",");
        Cyan.$$("#auth").each(function ()
        {
            this.checked = Cyan.Array.contains(auths, this.value);
        });
    }

    Cyan.$("condition").value = app.condition || "";
    Cyan.$("self").checked = !!app.self;

    currentApp = app;
}

function addSuccess()
{
    Cyan.message("添加成功", function ()
    {
        Cyan.Window.setReturnValue(true);
        if (Cyan.Arachne.form.duplicateKey)
            Cyan.Window.closeWindow();
        else
            reset();
    });
}

function updateSuccess()
{
    Cyan.message("修改成功", function ()
    {
        Cyan.Window.getOpener().afterSave();
        Cyan.Window.closeWindow();
    });
}

function reset()
{
    Cyan.$("form").reset();
}

function display(roleId)
{
    System.showModal("/RoleDisplay?roleIds=" + roleId);
}