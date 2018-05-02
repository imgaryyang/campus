Cyan.TargetList.prototype.getDiv = function ()
{
    if (!this.div)
    {
        this.div = document.createElement("DIV");
        this.div.style.position = "absolute";
        this.div.style.width = "0";
        this.div.style.height = "0";
        this.div.style.overflow = "hidden";

        document.body.appendChild(this.div);

        var innerDiv = document.createElement("DIV");
        this.div.appendChild(innerDiv);

        var store = new Ext.data.JsonStore({
            data: this.toOptions(),
            autoLoad: true,
            fields: ["value", "text"]
        });

        var list = this;
        var listeners = {
            select: function (combo, record, index)
            {
                if (list.onselect)
                    list.onselect(list.items[index]);
            }
        };

        this.combox = new Ext.form.ComboBox({
            hiddenName: "", hiddenId: "", editable: false,
            triggerAction: "all", mode: 'local', store: store, valueField: "value", displayField: "text",
            width: this.width, height: 20, value: "", listeners: listeners
        });
        this.combox.render(innerDiv);

        Cyan.Class.overwrite(this.combox, "onViewClick", function ()
        {
            this.inherited();
            if (list.bindComponent && list.bindComponent.focus)
                list.bindComponent.focus();
        });
    }
    return this.div;
};

Cyan.TargetList.prototype.toOptions = function (items)
{
    if (!items)
        items = this.items;

    var options = new Array(items.length);
    for (var i = 0; i < items.length; i++)
    {
        var item = items[i];
        options[i] = ({value: Cyan.valueOf(item), text: this.render(item)});
    }

    return options;
};

Cyan.TargetList.prototype.load = function (items)
{
    this.items = items;
    if (this.combox)
        this.combox.store.loadData(this.toOptions());
};

Cyan.TargetList.prototype.isVisible = function ()
{
    return this.combox && this.combox.isExpanded();
};

Cyan.TargetList.prototype.hide = function ()
{
    if (this.combox)
        this.combox.collapse();
};

Cyan.TargetList.prototype.showAt = function (x, y)
{
    this.getDiv();

    if (!this.combox.isExpanded())
    {
        this.div.style.left = x + "px";
        this.div.style.top = (y - 20) + "px";
        this.combox.onTriggerClick();
    }
    else if (this.left != x || this.top != y)
    {
        this.div.style.left = x + "px";
        this.div.style.top = (y - 20) + "px";
        this.combox.collapse();
        this.combox.onTriggerClick();
    }

    this.left = x;
    this.top = y;
};

Cyan.TargetList.prototype.getSelectedIndex = function ()
{
    var indexes = this.combox.view.getSelectedIndexes();
    return indexes ? indexes[0] : -1;
};

Cyan.TargetList.prototype.select = function (index)
{
    if (index >= 0 && index < this.items.length)
        this.combox.select(index, true);
};

Cyan.TargetList.prototype.makeSelect = function ()
{
    this.combox.onViewClick();
};