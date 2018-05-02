Cyan.Plain.importCss("targetlist");

Cyan.TargetList.prototype.visible = false;
Cyan.TargetList.prototype.selectedIndex = 0;
Cyan.TargetList.prototype.getDiv = function ()
{
    if (!this.div)
    {
        var div = document.createElement("div");
        div.className = "cyan-targetlist";
        div.style.display = "none";
        div.style.width = this.width + "px";
        document.body.appendChild(div);
        this.div = div;

        var targetlist = this;
        this.itemClick = function ()
        {
            targetlist.selectedIndex = this.index;
            targetlist.updateText();
            targetlist.makeSelect();
        };

        this.itemMouseover = function ()
        {
            targetlist.selectedIndex = this.index;
            targetlist.updateText();
        };

        this.updateItems();

        Cyan.attach(document.body, "mousedown", function (event)
        {
            if (!event.isOn(div))
            {
                div.style.display = "none";
                targetlist.selectedIndex = 0;
                targetlist.updateText();
            }
        });
    }

    return this.div;
};

Cyan.TargetList.prototype.updateItems = function ()
{
    var div = this.div;
    if (div)
    {
        var items = this.items;
        div.innerHTML = "";
        var n = items.length;
        for (var i = 0; i < n; i++)
        {
            var item = items[i];
            var itemDiv = document.createElement("div");
            var className = "cyan-targetlist-item";
            if (i == this.selectedIndex)
                className += " cyan-targetlist-item-selected";
            itemDiv.className = className;
            itemDiv.innerHTML = this.render(item);
            itemDiv.value = Cyan.valueOf(item);
            itemDiv.onclick = this.itemClick;
            itemDiv.onmouseover = this.itemMouseover;
            itemDiv.index = i;
            div.appendChild(itemDiv);
        }
    }
};

Cyan.TargetList.prototype.updateText = function ()
{
    var div = this.div;
    if (div)
    {
        var n = this.items.length;
        for (var i = 0; i < n; i++)
        {
            var item = this.items[i];
            div.childNodes[i].className = i == this.selectedIndex ?
                    "cyan-targetlist-item cyan-targetlist-item-selected" : "cyan-targetlist-item";
        }
    }
};

Cyan.TargetList.prototype.load = function (items)
{
    this.items = items;
    this.updateItems();
};

Cyan.TargetList.prototype.isVisible = function ()
{
    return this.visible;
};

Cyan.TargetList.prototype.hide = function ()
{
    this.visible = false;

    if (this.div)
        this.div.style.display = "none";
};

Cyan.TargetList.prototype.showAt = function (x, y)
{
    var div = this.getDiv();
    this.visible = true;

    div.style.display = "";
    if (this.left != x || this.top != y)
    {
        div.style.left = x + "px";
        div.style.top = y + "px";
    }

    this.left = x;
    this.top = y;
};

Cyan.TargetList.prototype.getSelectedIndex = function ()
{
    return this.selectedIndex;
};

Cyan.TargetList.prototype.select = function (index)
{
    if (index < 0)
        index = 0;
    else if (index >= this.items.length)
        index = this.items.length - 1;
    this.selectedIndex = index;
    this.updateText();
};

Cyan.TargetList.prototype.makeSelect = function ()
{
    if (this.onselect)
        this.onselect(this.items[this.selectedIndex]);

    this.hide();
};