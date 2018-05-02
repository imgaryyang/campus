Cyan.importCss("widgets/mask.css");

Cyan.Mask = function (bindEl)
{
    this.bindEl = bindEl;
    this.zIndex = Cyan.Mask.zIndex++;
};

Cyan.Mask.prototype.init = function ()
{
    if (!this.inited)
    {
        this.el = document.createElement("div");
        this.el.style.zIndex = this.zIndex;
        this.el.style.display = "none";
        this.el.className = "cyan-mask";

        document.body.appendChild(this.el);
        this.inited = true;
    }
};

Cyan.Mask.prototype.show = function ()
{
    this.init();

    var position = this.bindEl == document.body ? {x: 0, y: 0} : Cyan.Elements.getPosition(this.bindEl);
    var size = this.bindEl == document.body ? Cyan.getBodySize() : Cyan.Elements.getComponentSize(this.bindEl);

    this.el.style.left = (this.left = position.x) + "px";
    this.el.style.top = (this.top = position.y) + "px";
    this.el.style.width = (this.width = size.width) + "px";
    this.el.style.height = (this.height = size.height) + "px";
    this.el.style.display = "";
};

Cyan.Mask.prototype.active = function ()
{
    if (this.inited)
    {
        this.zIndex = Cyan.Mask.zIndex++;
        this.el.style.zIndex = this.zIndex;
    }
};

Cyan.Mask.prototype.hide = function ()
{
    if (this.inited)
        this.el.style.display = "none";
};

Cyan.Mask.prototype.destory = function ()
{
    if (this.inited)
    {
        document.body.removeChild(this.el);
        this.el = null;
    }
};

Cyan.LoadingMask = Cyan.Class.extend(function (bindEl)
{
    this.inherited(bindEl);
    this.loadingZIndex = Cyan.Mask.zIndex++;

    var mask = this;
    this.show1 = function ()
    {
        if (mask.loadingVisible)
            mask.show0();
    };

}, Cyan.Mask);

Cyan.overwrite(Cyan.LoadingMask, "init", function ()
{
    this.inherited();

    this.loading = document.createElement("div");
    this.loading.className = "cyan-mask-loading";
    this.loading.zIndex = this.loadingZIndex;
    this.loading.style.display = "none";
    document.body.appendChild(this.loading);
});

Cyan.overwrite(Cyan.LoadingMask, "show", function (el)
{
    this.inherited(el);

    this.loading.innerHTML = this.message;
    var width = Cyan.toInt(Cyan.Elements.getCss(this.loading, "width"));
    var height = Cyan.toInt(Cyan.Elements.getCss(this.loading, "height"));

    var left = this.left + Math.floor((this.width - width) / 2);
    var top = this.top + Math.floor((this.height / 2 - height));

    this.loading.style.left = left + "px";
    this.loading.style.top = top + "px";
    this.loading.style.display = "";
});

Cyan.LoadingMask.prototype.show0 = Cyan.LoadingMask.prototype.show;

Cyan.LoadingMask.prototype.show = function (immediate)
{
    if (immediate)
    {
        this.show0();
    }
    else
    {
        this.loadingVisible = true;
        setTimeout(this.show1, 400);
    }
};

Cyan.overwrite(Cyan.LoadingMask, "hide", function ()
{
    if (this.inited)
    {
        this.inherited();
        this.loading.style.display = "none";
    }
    this.loadingVisible = false;
});


Cyan.overwrite(Cyan.LoadingMask, "destory", function ()
{
    this.inherited();

    if (this.inited)
    {
        document.body.removeChild(this.loading);
        this.loading = null;
    }
});

Cyan.LoadingMask.maskBody = function (visible)
{
    var mask = Cyan.LoadingMask.bodyMask;
    if (visible)
    {
        if (!mask)
        {
            mask = Cyan.LoadingMask.bodyMask = new Cyan.LoadingMask(document.body);
            mask.message = Cyan.titles["waiting"] + "...";
        }

        mask.show();
    }
    else
    {
        if (mask)
            mask.hide();
    }
};

Cyan.Mask.zIndex = 2000;