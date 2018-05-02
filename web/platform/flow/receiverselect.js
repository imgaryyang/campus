Cyan.importJs("widgets/table.js");
Cyan.importJs("/platform/group/member.js");

var receiverSelects;

Cyan.onload(function ()
{
    receiverSelects = Cyan.Window.getOpener().receiverSelects;

    var list = Cyan.$("list");

    var table = new Cyan.Table("list", [
        {
            title: "环节",
            locked: true,
            dataProvider: "nodeName",
            name: "nodeName",
            sortable: false,
            align: "left",
            width: 180
        },
        {
            title: "接收者",
            locked: true,
            dataProvider: "receiverName",
            name: "receiverName",
            sortable: false,
            align: "left",
            width: 180
        },
        {
            title: "所属部门",
            locked: true,
            dataProvider: "properties.deptName",
            name: "deptName",
            sortable: false,
            align: "left",
            autoExpand: true
        },
        {
            title: "岗位",
            locked: true,
            dataProvider: "properties.stations",
            name: "stations",
            sortable: false,
            align: "left",
            width: 180
        },
        {
            title: "全选",
            name: 'checkbox$',
            locked: true,
            checkboxName: 'selected',
            valueProvider: 'index',
            checkedProvider: 'false',
            align: "center",
            width: 60
        }
    ]);
    table.autoRender = true;
    table.autoLoad = false;
    table.height = 320;

    table.init("list");

    for (var i = 0; i < receiverSelects.length; i++)
    {
        var receiverSelect = receiverSelects[i];
        receiverSelect.index = i;

        var selectable = receiverSelect.selectType && receiverSelect.selectType != "no";
        if (receiverSelect.receiver == "&")
        {
            receiverSelect.receiverName =
                    "<input readonly name='receiverName' class='receiverName unselected' value='请点击选择接收者' title='单击选择接收者' index='" +
                    i + "'>";

            selectable = true;
        }

        if (selectable)
            table.add(receiverSelect);
    }

    window.setTimeout(function ()
    {
        Cyan.$$(".receiverName").onclick(function ()
        {
            selectUsers(this);
        });
    }, 500);
});

function ok()
{
    var selecteds = Cyan.$$("#selected").checkedValues();

    var result = [];

    var i, j, receiverSelect, receiverSelect1;
    for (i = 0; i < receiverSelects.length; i++)
    {
        receiverSelect = receiverSelects[i];
        if (receiverSelect.receiver == "&")
        {
            if (receiverSelect.selected)
            {
                for (j = 0; j < receiverSelect.selected.length; j++)
                {
                    var member = receiverSelect.selected[j];
                    result.push({
                        nodeId: receiverSelect.nodeId, receiver: member.id, receiverName: member.name,
                        properties: {deptId: member.deptId}
                    });
                }
            }
        }
        else
        {
            var selectType = receiverSelect.selectType;
            if (!selectType || selectType == "no")
            {
                result.push(receiverSelect);
            }
            else if (Cyan.Array.contains(selecteds, i.toString()))
            {
                result.push({
                    nodeId: receiverSelect.nodeId,
                    receiver: receiverSelect.receiver,
                    receiverName: receiverSelect.receiverName,
                    properties: {
                        deptId: receiverSelect.properties.deptId
                    }
                });
            }
        }
    }

    for (i = 0; i < receiverSelects.length; i++)
    {
        receiverSelect = receiverSelects[i];
        var minSelectCount = receiverSelect.minSelectCount;
        var maxSelectCount = receiverSelect.maxSelectCount;
        if (minSelectCount > 0 || maxSelectCount > 0)
        {
            var count = 0;
            for (j = 0; j < result.length; j++)
            {
                receiverSelect1 = result[j];
                if (receiverSelect1.nodeId == receiverSelect.nodeId)
                {
                    count++;
                }
            }

            if (minSelectCount > 0 && count < minSelectCount)
            {
                Cyan.message("环节“" + receiverSelect.nodeName + "”至少要有" + minSelectCount + "个接收者");
                return;
            }

            if (maxSelectCount > 0 && count > maxSelectCount)
            {
                Cyan.message("环节“" + receiverSelect.nodeName + "”最多只能选择" + maxSelectCount + "个接收者");
                return;
            }
        }
    }

    if (result.length == 0)
    {
        Cyan.message("请选择接收者");
        return;
    }

    Cyan.Window.closeWindow(result);
}

function selectUsers(input)
{
    var receiverSelect = receiverSelects[parseInt(input.getAttribute("index"))];
    System.selectMembers({
        scopeId: receiverSelect.properties.scopeId,
        scopeName: "流程用户选择",
        types: "user",
        deptId: receiverSelect.properties.deptId || Cyan.Arachne.form.businessDeptId,
        selected: receiverSelect.selected,
        callback: function (result)
        {
            if (result)
            {
                receiverSelect.selected = result;
                input.value = result.join(",");

                input.className = "receiverName selected";
            }
        }
    });
}