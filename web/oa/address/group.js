$.importJs("widgets/menu.js");
$.onload(function ()
{
    Cyan.Class.overwrite(window, "moveOut", function (groupId)
    {
        var moveOut = this.inherited;
        $.confirm("确定移除组成员", function (ret)
        {
            if (ret == "ok")
            {
                moveOut(groupId, {
                    callback: function ()
                    {
                        refresh();
                    }
                });
            }
        });
    });

    window.copy = function (groupId, event)
    {
        showMenu(groupId, event, copyTo, "复制到");
    };

    window.move = function (groupId, event)
    {
        showMenu(groupId, event, moveTo, "移动到");
    };
});

function showMenu(groupId, event, fn, prefix)
{
    var callback = function ()
    {
        var newGroupId = this.groupId;
        $.confirm("确定要" + prefix + "到此组", function (ret)
        {
            if (ret == "ok")
            {
                fn(groupId, newGroupId, function (ret)
                {
                    refresh();
                });
            }
        });
    };

    var items = [];
    var n = mainBody.getSize();
    for (var i = 0; i < n; i++)
    {
        var record = mainBody.getRecord(i);
        var id = record.key;
        if (id != groupId)
        {
            items.push({
                text: prefix + "\"" + record.cells[0] + "\"",
                action: callback,
                groupId: id
            });
        }
    }

    if (items.length == 0)
    {
        Cyan.message("只有一个组，无法" + prefix);
        return;
    }

    var menu = new Cyan.Menu(items);
    event = new Cyan.Event(event);
    menu.showWith(event.srcElement);
}