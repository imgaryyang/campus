(function ()
{
    var User = function (userId, userName)
    {
        this.userId = userId;
        this.userName = userName;
    };

    User.prototype.toString = function ()
    {
        return this.userName;
    };

    User.prototype.valueOf = function ()
    {
        return this.userId;
    };

    Cyan.onload(function ()
    {
        setTimeout(function ()
        {
            var word = Cyan.$("word");
            if (word)
            {
                userSelector.bindQuery(word);
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
                        userSelector.addSelected(new User(item.userId, item.userName));
                    }
                }
            }
        }, 1000);
    });

    window.ok = function ()
    {
        var items = userSelector.itemSelector.selectedItems();
        var result = new Array(items.length);

        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            result[i] = new User(item.value, item.text);
        }

        Cyan.Window.closeWindow(result);
    }
})();