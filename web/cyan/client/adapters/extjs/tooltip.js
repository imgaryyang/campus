Cyan.onload(function ()
{
    Ext.QuickTips.init();
});

Cyan.ToolTip.prototype.getTip = function ()
{
    if (!this.tip)
    {
        var config = {};
        if (this.width)
            config.width = this.width;
        if (this.height)
            config.height = this.height;
        config.title = this.title || " ";
        if (this.html)
            config.html = this.html;
        config.closable = !!this.closable;
        config.draggable = !!this.draggable;

        this.tip = new Ext.Tip(config);
    }

    return this.tip;
};

Cyan.ToolTip.prototype.create = function ()
{
    this.getTip().showAt([0, 0]);
    this.getTip().hide();
};

Cyan.ToolTip.prototype.showAt = function (x, y)
{
    this.getTip().showAt([x, y]);
};

Cyan.ToolTip.prototype.hide = function ()
{
    if (this.tip)
        this.tip.hide();
};

Cyan.ToolTip.prototype.setTitle = function (title)
{
    this.title = title;
    if (this.tip)
        this.tip.setTitle(title);
};

Cyan.ToolTip.prototype.setHtml = function (html)
{
    Cyan.$$(this.tip.el.dom).$(".x-tip-body").html(html);
};