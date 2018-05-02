Cyan.importJs("widgets/tooltip.js");
Cyan.importCss("/platform/agent/agent.css");

System.Agent = function(path, position)
{
    this.path = path;
    this.position = position || "right_bottom";
    this.showDisplayTime = 2500;
    this.hideDisplayTime = 2000;
};

System.Agent.load = function(name)
{
    return new System.Agent("/platform/agent/" + name);
};

System.Agent.prototype.getImg = function()
{
    return this.img;
};

System.Agent.prototype.getImgX = function()
{
    if (this.position == "left_top" || this.position == "left_bottom")
        return 50;
    else
        return Cyan.getBodyWidth() - 120;
};

System.Agent.prototype.getImgY = function()
{
    if (this.position == "left_top" || this.position == "right_top")
        return 50;
    else
        return Cyan.getBodyHeight() - 140;
};

System.Agent.prototype.getTipStyle = function()
{
    return "agent_tip";
};

System.Agent.prototype.getTipX = function()
{
    if (this.position == "left_top" || this.position == "left_bottom")
        return 70;
    else
        return Cyan.getBodyWidth() - 290;
};

System.Agent.prototype.getTipY = function()
{
    if (this.position == "left_top" || this.position == "right_top")
        return 10;
    else
        return Cyan.getBodyHeight() - 180;
};

System.Agent.prototype.isVisible = function()
{
    return this.visible;
};

System.Agent.prototype.getGif = function(name)
{
    return this.path + "/" + name + ".gif";
};

System.Agent.prototype.playGif = function(name, time)
{
    if (this.img)
        this.img.src = this.getGif(name);

    if (time > 0)
    {
        var agent = this;
        var timeout = window.setTimeout(function()
        {
            window.clearTimeout(timeout);
            agent.playGif("sit");
        }, time);
    }
};

System.Agent.prototype.show = function(callback)
{
    if (!this.img)
    {
        this.img = document.createElement("IMG");
        this.img.style.position = "absolute";
        this.img.style.zIndex = 10000;
        document.body.appendChild(this.img);
    }

    this.img.style.left = this.getImgX() + "px";
    this.img.style.top = this.getImgY() + "px";

    this.playGif("show");
    this.img.style.display = "";

    var agent = this;
    var timeout = window.setTimeout(function()
    {
        window.clearTimeout(timeout);
        agent.playGif("sit");
        if (callback)
            callback();
    }, this.showDisplayTime);

    this.visible = true;
};

System.Agent.prototype.hide = function(callback)
{
    if (this.img)
    {
        this.visible = false;
        this.playGif("hide");
        var agent = this;
        var timeout = window.setTimeout(function()
        {
            window.clearTimeout(timeout);
            agent.img.style.display = "none";
            if (callback)
                callback();
        }, this.hideDisplayTime);
    }
};

System.Agent.prototype.jump = function()
{
    this.playGif("jump", 2000);
};

System.Agent.prototype.speak = function(content, time)
{
    var agent = this;
    if (this.visible)
    {
        if (this.closeSpeakTimeout)
            window.clearTimeout(this.closeSpeakTimeout);
        this.playGif("speak", 1000);

        var timeout = setTimeout(function()
        {
            window.clearTimeout(timeout);
            agent.speak0(content, time);
        }, 500);
    }
    else
    {
        this.show(function()
        {
            agent.speak(content, time);
        });
    }
};

System.Agent.prototype.speak0 = function(content, time)
{
    if (!this.tip)
    {
        this.tip = new Cyan.ToolTip();
        this.tip.closable = true;
        this.tip.draggable = false;
        this.tipDivId = "agent_speak_" + Math.random().toString().substring(2);
        this.tip.html = "<div id=\"" + this.tipDivId + "\" class=\"" + this.getTipStyle() + "\"></div>";
    }
    else
    {
        if (this.closeSpeakTimeout)
            window.clearTimeout(this.closeSpeakTimeout);
    }

    this.tip.showAt(this.getTipX(), this.getTipY());
    $(this.tipDivId).innerHTML = content;

    if (time > 0)
    {
        var agent = this;
        this.closeSpeakTimeout = setTimeout(function()
        {
            window.clearTimeout(agent.closeSpeakTimeout);
            agent.closeSpeakTimeout = null;
            agent.closeSpeak();
        }, time);
    }
};

System.Agent.prototype.closeSpeak = function()
{
    if (this.tip)
        this.tip.hide();
};