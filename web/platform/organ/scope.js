var roleScopeDepts;
$.onload(function ()
{
    Cyan.$$$("scopeForm").disable();
    roleScopeDepts = Cyan.Arachne.form.entity.roleScopeDepts;
    if (!roleScopeDepts)
        roleScopeDepts = Cyan.Arachne.form.entity.roleScopeDepts = [];

    var save = window.save;
    window.save = function ()
    {
        saveItem();

        save({
            form: "form",
            callback: Cyan.Arachne.form.new$ ? addSuccess : updateSuccess
        });
    };

    Cyan.$$("#word").onkeyup(function ()
    {
        var scopes = Cyan.Arachne.form.entity.roleScopeDepts;
        Cyan.Arachne.form.entity.roleScopeDepts = null;
        scopeDeptTree.search(this.value);
        Cyan.Arachne.form.entity.roleScopeDepts = scopes;
    });

    Cyan.$$("#excluded").onclick(function ()
    {
        Cyan.$("includeSup").disabled = this.checked;
        Cyan.$("includeSelf").disabled = this.checked;
        Cyan.$("priority").disabled = !this.checked;
    });
});

var currentSopeDept;

function deptselect()
{
    saveItem();

    var node = scopeDeptTree.getSelectedNode();
    if (node)
    {
        var id = node.getValue().value.id;
        if (id)
        {
            if (Cyan.$$("#deptId").searchFirst(
                            function ()
                            {
                                return this.value == id;
                            }).checked)
            {
                setItem(id);
            }
            else
            {
                disableScopeForm();
            }
        }
        else
        {
            disableScopeForm();
        }
    }
}

function deptcheck(node)
{
    var treeNode = scopeDeptTree.getNodeById(node.id);
    if (Cyan.$$("#deptId").searchFirst(
                    function ()
                    {
                        return this.value == node.id;
                    }).checked)
    {
        if (treeNode.isSelected())
            setItem(node.id);
        else
            treeNode.select();

        if (!roleScopeDepts.searchFirst(function ()
                {
                    return this.deptId == node.id;
                }))
        {
            roleScopeDepts.push({
                deptId: node.id,
                includeSub: false,
                includeSup: false,
                includeSelf: true,
                excluded: false,
                priority: false,
                filter: ""
            });
        }
    }
    else
    {
        roleScopeDepts.removeCase(function ()
        {
            return this.deptId == node.id;
        });
        if (treeNode.isSelected())
            disableScopeForm();
    }
}

function disableScopeForm()
{
    Cyan.$$$("scopeForm").disable();
    Cyan.$("includeSub").checked = false;
    Cyan.$("includeSup").checked = false;
    Cyan.$("includeSelf").checked = true;
    Cyan.$("excluded").checked = false;
    Cyan.$("priority").checked = false;
    Cyan.$("filter").value = "";
    currentSopeDept = null;
}

function saveItem()
{
    if (currentSopeDept)
    {
        var scopeDept = roleScopeDepts.searchFirst(function ()
        {
            return this.deptId == currentSopeDept.deptId;
        });
        if (!scopeDept)
            scopeDept = {deptId: currentSopeDept.deptId};
        scopeDept.includeSub = Cyan.$("includeSub").checked;
        scopeDept.includeSup = Cyan.$("includeSup").checked;
        scopeDept.includeSelf = Cyan.$("includeSelf").checked;
        scopeDept.excluded = Cyan.$("excluded").checked;
        scopeDept.priority = Cyan.$("priority").checked;
        scopeDept.filter = Cyan.$("filter").value;
    }
}

function setItem(deptId)
{
    Cyan.$$$("scopeForm").enable();
    var scopeDept = roleScopeDepts.searchFirst(function ()
    {
        return this.deptId == deptId;
    });
    if (!scopeDept)
    {
        roleScopeDepts.push(scopeDept = {
            deptId: deptId,
            includeSub: false,
            includeSup: false,
            includeSelf: true,
            excluded: false,
            priority: false,
            filter: ""
        });
    }
    Cyan.$("filter").disabled = scopeDept.excluded || !scopeDept.includeSub;
    Cyan.$("includeSub").checked = !!scopeDept.includeSub;
    Cyan.$("includeSup").checked = !!scopeDept.includeSup;
    Cyan.$("includeSelf").checked = scopeDept.includeSelf == null || scopeDept.includeSelf;
    Cyan.$("excluded").checked = !!scopeDept.excluded;
    Cyan.$("priority").checked = !!scopeDept.priority;
    Cyan.$("filter").value = scopeDept.filter || '';

    if (scopeDept.excluded)
    {
        Cyan.$("includeSup").disabled = true;
        Cyan.$("includeSelf").disabled = true;
    }
    else
    {
        Cyan.$("priority").disabled = true;
    }

    currentSopeDept = scopeDept;
}

function includeSubClick()
{
    Cyan.$("filter").disabled = !Cyan.$("includeSub").checked;
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