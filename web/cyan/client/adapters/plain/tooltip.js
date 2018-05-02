Cyan.Plain.importCss("tooltip");

Cyan.ToolTip.prototype.create = function ()
{
    var tooltip = this;

    var el = document.createElement("div");
    this.el = Cyan.$$(el);

    el.className = "cyan-tooltip";
    document.body.appendChild(el);

    var header = document.createElement("div");
    header.className = "cyan-tooltip-header";
    el.appendChild(header);

    var titleEl = document.createElement("div");
    titleEl.className = "cyan-tooltip-title";
    header.appendChild(titleEl);
    if (this.title)
        titleEl.innerHTML = this.title;

    if (this.closable)
    {
        var closeEl = document.createElement("div");
        closeEl.className = "cyan-tooltip-close";
        header.appendChild(closeEl);

        closeEl.onclick = function ()
        {
            tooltip.hide();
        };
    }
    else if (!this.title)
    {
        header.style.display = "none";
    }

    var content = document.createElement("div");
    content.className = "cyan-tooltip-content";
    el.appendChild(content);
    if (this.html)
        content.innerHTML = this.html;
};

Cyan.ToolTip.prototype.showAt = function (x, y)
{
    var el = this.el;
    el.show();
    el.css("left", x + "px");
    el.css("top", y + "px");
};

Cyan.ToolTip.prototype.hide = function ()
{
    this.el.hide();
};

Cyan.ToolTip.prototype.setTitle = function (title)
{
    this.title = title;
    this.el.$(".cyan-tooltip-title").html(title);

    if (title)
        this.el.$("cyan-tooltip-header").show();
    else if (this.closable)
        this.el.$("cyan-tooltip-header").hide();
};

Cyan.ToolTip.prototype.setHtml = function (html)
{
    this.el.$(".cyan-tooltip-content").html(html);
};