Cyan.importCss("widgets/bubble.css");

Cyan.Bubble = function (direction)
{
    this.direction = direction;
};

Cyan.Bubble.prototype.showAt = function (x, y, text)
{
    if (text.length > 96)
        text = text.substring(0, 93) + "...";

    if (!this.div)
    {
        this.div = document.createElement("DIV");
        this.div.className = "cyan_bubble_" + this.direction;

        document.body.appendChild(this.div);
    }

    this.div.innerHTML = text;

    //添加关闭按钮
    var bubble = this;
    var closeBtn = document.createElement("DIV");
    closeBtn.className = "cyan_bubble_btn_close";
    closeBtn.onclick = function ()
    {
        bubble.hide();
        if (bubble.onclose)
            bubble.onclose();
    };
    this.div.appendChild(closeBtn);

    var left, top;

    if (this.direction == "top_left" || this.direction == "bottom_left")
        left = x;
    else
        left = x - 240;

    if (this.direction == "top_left" || this.direction == "top_right")
        top = y - 10;
    else
        top = y + 165;

    this.div.style.left = left + "px";
    this.div.style.top = top + "px";
    this.div.style.display = "";
};

Cyan.Bubble.prototype.showWith = function (component, text, onclose)
{
    this.onclose = onclose;

    var x, y;

    var position = Cyan.Elements.getPosition(component);
    var size = Cyan.Elements.getComponentSize(component);

    x = position.x + size.width / 2;
    if (this.direction == "top_left" || this.direction == "top_right")
        y = position.y + size.height;
    else
        y = position.y;

    this.showAt(x, y, text);
};

Cyan.Bubble.prototype.hide = function ()
{
    this.div.style.display = "none";
};