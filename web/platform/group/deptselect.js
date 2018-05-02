Cyan.onload(function ()
{
    setTimeout(function ()
    {
        var word = Cyan.$("word");
        if (word)
        {
            deptSelector.from.bindSearch(word);
        }

        var opener = Cyan.Window.getOpener();
        if (opener && opener.System.Member)
        {
            var selected = opener.System.Member.selected;
            if (selected)
            {
                for (var i = 0; i < selected.length; i++)
                {
                    var item = selected[i];
                    deptSelector.addItem(item.deptId, item.deptName);
                }
            }
        }
    }, 500);
});

function ok()
{
    var items = deptSelector.selectedItems();
    var result = new Array(items.length);

    function toString()
    {
        return this.deptName;
    }

    function valueOf()
    {
        return this.deptId;
    }

    for (var i = 0; i < items.length; i++)
    {
        var item = items[i];
        result[i] = {deptId: item.value, deptName: item.text, toString: toString, valueOf: valueOf};
    }

    Cyan.Window.closeWindow(result);
}