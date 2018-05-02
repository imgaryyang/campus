Cyan.ProgressBar.prototype.init = function(el)
{
    var progressbar = this;
    Cyan.onload(function()
    {
        progressbar.init0(el);
    });
};

Cyan.ProgressBar.prototype.init0 = function(el)
{
    el = Cyan.$(el);

    if (!this.width)
        this.width = Cyan.Elements.getComponentSize(el).width;

    this.bar = new Ext.ProgressBar({
        renderTo :el,
        width:this.width
    });
};

Cyan.ProgressBar.prototype.update = function(percentage, message)
{
    if (!message)
        message = Math.round(percentage * 100) + "%";
    this.bar.updateProgress(percentage, message);
};