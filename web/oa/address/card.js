if (System.Crud)
{
    System.Crud.messages.confirmMoveRecord = "确定移动联系人到此组?";
    System.Crud.messages.confirmCopyRecord = "确定添加联系人到此组?";
    System.Crud.messages.moveRecord = "移动联系人到此组";
    System.Crud.messages.copyRecord = "添加联系人到此组";

    window.canMoveTo = function (cardIds, groupId)
    {
        return groupId != "0";
    };
}

Cyan.onload(function ()
{
    if (Cyan.FileUpload)
    {
        new Cyan.FileUpload("entity.headImg").bind(Cyan.$("head_upload_button")).onselect = function (file)
        {
            Cyan.Arachne.getImagePath(file, function (path)
            {
                Cyan.$("head_img").src = path;
            });
        };
    }

    window.afterCopy = function ()
    {
        left.reload();
    };

    window.afterMove = window.afterDelete = function ()
    {
        mainBody.reload();
        left.reload();
    };

    if (window.userGroupMenus)
    {
        var userGroupMenus = new Cyan.Menu(window.userGroupMenus);
        Cyan.attach(Cyan.$("showMenus"), "click", function ()
        {
            var position = Cyan.Elements.getPosition(this);
            var size = Cyan.Elements.getComponentSize(this);
            userGroupMenus.showAt(position.x, position.y - userGroupMenus.getHeight() - 2);
        });
    }
    else
    {
        Cyan.attach(Cyan.$("showMenus"), "click", function ()
        {
            copyToUser(null);
        });
    }
});

function delimg()
{
    Cyan.$("delHead").value = "true";
    Cyan.$("head_img").src = "/oa/address/images/head.gif";
}

function toSave()
{
    var name = Cyan.$("entity.cardName").value;
    if (Cyan.Arachne.form.new$ && name)
    {
        getCardsWithName(name, function (cards)
        {
            if (cards && cards.length)
            {
                var str = "";
                cards.each(function ()
                {
                    str += "姓名：" + this.cardName + "   昵称:" + (this.nick || "") +
                    "   手机:" + ((this.attributes ? this.attributes.mobilePhone : null) || "") +
                    "   单位:" + ((this.attributes ? this.attributes.office : null) || "") + "\n";
                });

                Cyan.confirm("已经存在" + cards.length + "个姓名相同的人，信息如下:\n" + str +
                "\n       是否继续添加?", function (ret)
                {
                    if (ret == 'ok')
                    {
                        save();
                    }
                });
            }
            else
            {
                save();
            }
        });
    }
    else
    {
        save();
    }
}

function afterSave()
{
    if (Cyan.List && left instanceof Cyan.List)
    {
        mainBody.reload();
        left.reload();
    }
    else
    {
        if (System.editingKey)
            reload(System.editingKey);
        else
            mainBody.reload();
    }
}

function exportCards()
{
    System.showModal(encodeUrl("/oa/address/export"));
}

function importCards()
{
    System.showModal(encodeUrl("/oa/address/import"), function (ret)
    {
        if (ret)
        {
            mainBody.reload();
            if (Cyan.List && left instanceof Cyan.List)
                left.reload();
        }
    });
}

function encodeUrl(url)
{
    if (url.indexOf("?") < 0)
        url += "?type=" + Cyan.Arachne.form.type;
    else
        url += "&type=" + Cyan.Arachne.form.type;

    var groupId = Cyan.Arachne.form.groupId;
    if (groupId && groupId > 0)
        url += "&groupId=" + groupId;

    var deptId = Cyan.Arachne.form.deptId;
    if (deptId && deptId > 0)
        url += "&deptId=" + deptId;
    return url;
}

function toImp()
{
    readImpFile({
        callback: function (ret)
        {
            if (ret)
            {
                System.showModal(encodeUrl("/oa/address/import/mapping"), function (ret)
                {
                    Cyan.Window.closeWindow(ret);
                });
            }
            else
            {
                $.message("上传的文件中没有内容", function ()
                {
                    Cyan.Window.closeWindow();
                });
            }
        },
        progress: true
    });
}

function imp()
{
    window.importCards({
        callback: function (count)
        {
            $.message("导入成功,共导入数据" + count + "条", function ()
            {
                Cyan.Window.closeWindow(true);
            });
        },
        error: function ()
        {
            $.message("导入数据错误", function ()
            {
                Cyan.Window.closeWindow(false);
            });
        },
        progress: true
    });
}

function copyToUser(groupId)
{
    if (Cyan.Arachne.form.entity)
    {
        copyCardToUser(Cyan.Arachne.form.entity.cardId, groupId, {
            callback: function ()
            {
                Cyan.message("复制成功");
            },
            form: false,
            obj: {}
        });
    }
    else
    {
        if (!Cyan.$$("#keys").checkeds().length)
        {
            Cyan.message("请选择要复制的通讯录");
            return;
        }

        copyCardsToUser(groupId, {
            form: Cyan.$$("form")[0],
            callback: function ()
            {
                Cyan.message("复制成功");
            }
        });
    }
}

function moveCards(groupId)
{
    var keys = $$("#keys").checkedValues();
    if (!keys.length)
    {
        Cyan.message("选择要移动的记录");
        return;
    }

    moveAllTo(keys, groupId);
}