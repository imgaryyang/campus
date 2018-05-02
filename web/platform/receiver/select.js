Cyan.importJs("render.js");

function getExpandCookieName()
{
    return "receiver_default_expend_" + Cyan.Arachne.form.type;
}

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        Cyan.Elements.promptInput(Cyan.$("word"), "输入姓名,全拼,简拼可以快速查找接收者", "color:gray");

        var selector = receiverSelector.getSelector();
        selector.bindQuery("word");

        var i, topNodes = selector.tree.getRoot().getChildren();
        var div = Cyan.$("default_expand");
        for (i = 0; i < topNodes.length; i++)
        {
            var topNode = topNodes[i];
            var nodeId = topNode.id;
            var span = document.createElement("span");
            span.id = "default_expand_" + nodeId;
            span.innerHTML = "<input type='radio' name='expand' value='" + nodeId + "'>" + topNode.getNodeText();
            div.appendChild(span);
        }


        var node, defaultExpand = Cyan.getCookie(getExpandCookieName());
        if (defaultExpand)
        {
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

        var receiverInput;
        var opener = Cyan.Window.getOpener();
        if (opener && opener.System.ReceiverInput)
            receiverInput = opener.System.ReceiverInput.currentInstance;

        if (receiverInput)
        {
            var items = receiverInput.items;
            for (i = 0; i < items.length; i++)
            {
                var item = items[i];
                selector.addSelected(item);
            }
        }
    }, 500);
});

function ok()
{
    Cyan.setCookie(getExpandCookieName(), Cyan.$$("#expand").checkedValues()[0]);
    Cyan.Window.closeWindow(receiverSelector.getSelector().selected);
}

function addUserGroup()
{
    System.showModal("/UserGroup.new", function (ret)
    {
        if (ret)
        {
            receiverSelector.getSelector().tree.getNodeById("#usergroup").reloadChildren();
        }
    });
}

function manageUserGroup()
{
    System.showModal("/UserGroup", function ()
    {
        receiverSelector.getSelector().tree.getNodeById("#usergroup").reloadChildren();
    }, {
        width: 800,
        height: 480
    });
}