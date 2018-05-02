Cyan.importJs("combox.js");

function menuChange()
{
    var title = Cyan.$("entity.title");
    if (!title.value)
    {
        title.value = Cyan.$$$("entity.appId").getText();
    }
}

Cyan.onload(function ()
{
    refreshAction();
    refreshLabels(Arachne.displayGroups);

    setTimeout(function ()
    {
        Cyan.Class.overwrite(window, "showSortList", function (forward)
        {
            if (Cyan.$$("#groupId").allItems().length > 1 && !Cyan.$("groupId").value)
            {
                Cyan.message("请选择一个分组");
                return;
            }

            this.inherited(forward);
        });
    }, 100);

    Cyan.Class.overwrite(window, "showSortList", function (forward)
    {
        if (Cyan.$$("#groupId").allItems().length > 1 && !Cyan.$("groupId").value)
        {
            Cyan.message("请选择一个分组");
            return;
        }

        this.inherited(forward);
    });

    window.afterSave = function (ret)
    {
        if (System.editingKey)
        {
            reload(System.editingKey, function ()
            {
                Arachne.refresh("groupId");
            });
        }
        else
        {
            Arachne.refresh("groupId", function ()
            {
                mainBody.reload();
            });
        }
    };

    System.reload = function (callback)
    {
        refreshLabels(null, function ()
        {
            mainList.reload(callback);
        });
    };

    Cyan.Arachne.wrap("saveMenus", function ()
    {
        Cyan.message(System.Crud.messages.addSuccess, function ()
        {
            Cyan.Window.setReturnValue(true);
            Cyan.Window.closeWindow();
        });
    });
});

function refreshLabels(groups, callback)
{
    if (!Cyan.$("top"))
        return;

    if (!groups)
    {
        getDisplayGroups(function (groups)
        {
            refreshLabels(groups, callback);
        });
    }
    else
    {
        var labelsDiv;
        if (groups && groups.length > 1)
        {
            labelsDiv = Cyan.$("labels");
            if (!labelsDiv)
            {
                labelsDiv = document.createElement("div");
                labelsDiv.id = "labels";
                Cyan.$("top").appendChild(labelsDiv);
            }
            else
            {
                labelsDiv.innerHTML = "";
            }

            var i, group;
            if (Arachne.form.groupId)
            {
                var b = false;
                for (i = 0; i < groups.length; i++)
                {
                    group = groups[i];
                    if (group.groupId == Arachne.form.groupId)
                    {
                        b = true;
                        break;
                    }
                }

                if (!b)
                    Arachne.form.groupId = null;
            }

            if (!Arachne.form.groupId)
            {
                Arachne.form.groupId = groups[0].groupId;
            }

            for (i = 0; i < groups.length; i++)
            {
                group = groups[i];

                var span = document.createElement("span");
                span.innerHTML = group.groupName;
                span.groupId = group.groupId;
                span.onclick = selectLabel;

                if (group.groupId == Arachne.form.groupId)
                    span.className = "label_selected";
                else
                    span.className = "label";

                labelsDiv.appendChild(span);
            }
        }
        else
        {
            Arachne.form.groupId = null;
            labelsDiv = Cyan.$("labels");
            if (labelsDiv)
                labelsDiv.parentNode.removeChild(labelsDiv);
        }

        if (callback)
            callback();
    }
}

System.config = function ()
{
    var url = "/desktop/userLink";
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
        System.openPage(url, "userlink_config", "常用连接");
};

function openHref(appId, url, title, target)
{
    if (appId)
    {
        System.goMenu(appId);
    }
    else if (target == "tab")
    {
        System.openPage(url, null, title);
    }
    else
    {
        window.open(url);
    }
}

function showAll()
{
    Arachne.form.type = 0;
    reloadList();
}

function showSelf()
{
    Arachne.form.type = 1;
    reloadList();
}

function refreshAction()
{
    var actions = Cyan.$$("#actions span");
    if (actions.length)
    {
        if (Arachne.form.type == 1)
        {
            actions[0].style.display = "block";
            actions[1].style.display = "none";
        }
        else
        {
            actions[0].style.display = "none";
            actions[1].style.display = "block";
        }
    }
}

function reloadList()
{
    refreshAction();
    System.reload();
}

function selectLabel()
{
    if (this.className == "label_selected")
        return;

    var labels = Cyan.$$("#labels span");

    for (var i = 0; i < labels.length; i++)
    {
        var label = labels[i];

        if (label == this)
            label.className = "label_selected";
        else
            label.className = "label";
    }

    Cyan.Arachne.form.groupId = this.groupId;
    mainList.reload();
}

function editGroups()
{
    var url = "/desktop/userLinkgroup";
    var menu = System.getMenuByUrl(url);
    if (menu)
        menu.go();
    else
        System.openPage(url, null, "快捷功能分组管理");
}