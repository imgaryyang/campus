System.SimpleList = function (el)
{
    this.el = el;
};

System.SimpleList.prototype.reload = function (callback)
{
    var list = this;
    this.load(function (result)
    {
        list.refresh(result);
        list = null;
        if (callback)
            callback();
        callback = null;
    });
};

System.SimpleList.prototype.refresh = function (result)
{
    if (!this.element)
        this.element = Cyan.$$$(this.el);

    this.element.$("LI").remove();
    if (result && result.length)
    {
        for (var i = 0; i < result.length; i++)
        {
            var text = this.render(result[i]);
            var li = document.createElement("LI");
            this.element.first.appendChild(li);
            li.innerHTML = text;
        }
    }
};

System.SimpleList.prototype.render = function (item)
{
    var text;
    if (Cyan.isString(item))
        text = item;
    else
        text = item.text;
    return text;
};