function getExpandCookieName()
{
    return "member_default_expend_" + (Cyan.Arachne.form.type || "all");
}

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        var selector = memberSelector.getSelector();
        selector.bindQuery("word");

        var node, i;
        var defaultExpand = Cyan.getCookie(getExpandCookieName());
        if (defaultExpand)
        {
            var topNodes = selector.tree.getRoot().getChildren();
            for (i = 0; i < topNodes.length; i++)
            {
                if (topNodes[i].id == defaultExpand)
                {
                    node = topNodes[i];
                    break;
                }
            }
        }

        if (!node)
        {
            node = selector.tree.getRoot().getChildren()[0];
            defaultExpand = node.id;
        }

        if (node)
        {
            Cyan.$$("#expand").setValue(defaultExpand);
            node.expand(false, true, function ()
            {
                var children = node.getChildren();
                if (children.length == 1)
                {
                    var node1 = children[0];
                    if (node1)
                    {
                        node1.expand(false, true);
                    }
                }
            });
        }

        var opener = Cyan.Window.getOpener();
        if (opener && opener.System.Member)
        {
            var selected = opener.System.Member.selected;
            if (selected)
            {
                for (i = 0; i < selected.length; i++)
                {
                    var item = selected[i];
                    selector.addSelected(item);
                }
            }
        }
    }, 500);
});

function down()
{
    memberSelector.getSelector().down();
}

function up()
{
    memberSelector.getSelector().up();
}

function addCustom()
{
    var s = Cyan.$("customMember").value;
    if (s)
    {
        var f = function ()
        {
            memberSelector.getSelector().addSelected({type: "custom", id: 0, name: s});
            Cyan.$("customMember").value = "";
        };

        memberSelector.queryMember(s, function (members)
        {
            if (members && members.length)
            {
                if (members.length == 1)
                {
                    var member = members[0];

                    var name;
                    if (member.type == "dept")
                        name = "部门";
                    else if (member.type == "user")
                        name = "用户";
                    else if (member.type == "deptgroup")
                        name = "部门组";
                    else if (member.type == "usergroup")
                        name = "用户组";

                    name += "\"" + member.name + "\"";

                    Cyan.customConfirm([
                                {
                                    text: "添加系统中的对象",
                                    callback: function ()
                                    {
                                        memberSelector.getSelector().addSelected(member);
                                    }
                                },
                                {
                                    text: "添加自定义对象",
                                    callback: function ()
                                    {
                                        f();
                                    }
                                }
                            ],
                            {
                                message: "输入的内容和系统中的" + name + "匹配",
                                width: 400
                            });
                }
                else
                {
                    Cyan.confirm("输入的内容和系统中多个对象匹配，确定您要添加的对象系统中不存在", function (ret)
                    {
                        if (ret == "ok")
                            f();
                    });
                }
            }
            else
            {
                f();
            }
        });

    }
}

function ok()
{
    Cyan.setCookie(getExpandCookieName(), Cyan.$$("#expand").checkedValues()[0]);
    Cyan.Window.closeWindow(memberSelector.getSelector().selected);
}

function addDeptGroup()
{
    System.showModal("/DeptGroup.new?deptId=" + Cyan.Arachne.form.deptId, function (ret)
    {
        if (ret)
        {
            memberSelector.getSelector().tree.getNodeById("deptgroup").reloadChildren();
        }
    });
}

function manageDeptGroup()
{
    System.showModal("/DeptGroup?deptId=" + Cyan.Arachne.form.deptId, function (ret)
    {
        memberSelector.getSelector().tree.getNodeById("deptgroup").reloadChildren();
    }, {
        width: 800,
        height: 480
    });
}

function addUserGroup()
{
    System.showModal("/UserGroup.new", function (ret)
    {
        if (ret)
        {
            memberSelector.getSelector().tree.getNodeById("usergroup").reloadChildren();
        }
    });
}

function manageUserGroup()
{
    System.showModal("/UserGroup", function ()
    {
        memberSelector.getSelector().tree.getNodeById("usergroup").reloadChildren();
    }, {
        width: 800,
        height: 480
    });
}