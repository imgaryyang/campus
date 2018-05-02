Cyan.importJs("event.js");


Cyan.QuickInput = function(label)
{
    if (!label)
        label = "";
    this.label = label;
};

Cyan.QuickInput.prototype.init = function()
{
    var quickInput = this;
    Cyan.attach(Cyan.navigator.isIE() ? document.body : window, "keydown", function(event)
    {
        var nodeName = event.srcElement.nodeName;
        if (nodeName == "INPUT" || nodeName == "TEXTAREA" || nodeName == "SELECT")
        {
            if (event.srcElement != quickInput.input)
                quickInput.hide();
        }
        else if (!event.ctrlKey && !event.altKey)
        {
            var keyCode = event.keyCode;
            if (keyCode != 16 && keyCode != 37 && (keyCode < 33 || keyCode > 40) && keyCode != 8 && keyCode != 9 &&
                    keyCode != 27 && keyCode != 45 && keyCode != 46 && keyCode != 167 &&
                    (keyCode < 112 || keyCode > 123))
            {
                quickInput.show(event);
            }
        }
    });
};

Cyan.QuickInput.prototype.setText = function(label)
{
    this.label = label;
    if (this.labelComponent)
        this.labelComponent.innerHTML = label;
};

Cyan.QuickInput.prototype.show = function(event)
{
    this.createInput();
    if (this.div.style.display == "none")
    {
        this.input.value = "";
        this.div.style.display = "block";
        var position = this.getPosition(event);
        this.div.style.left = position.x + "px";
        this.div.style.top = position.y + "px";
    }
    this.input.focus();
};

Cyan.QuickInput.prototype.hide = function()
{
    if (this.input)
    {
        this.input.value = "";
        this.div.style.display = "none";

        if (this.process)
            this.process("");
    }
};

Cyan.QuickInput.prototype.createInput = function()
{
    if (!this.input)
    {
        this.div = document.createElement("DIV");
        this.div.style.position = "absolute";
        this.div.style.display = "none";
        this.div.className = "cyan-quickinput-div";

        this.labelComponent = document.createElement("SPAN");
        this.div.appendChild(this.labelComponent);
        this.labelComponent.innerHTML = this.label;
        this.labelComponent.className = "cyan-quickinput-label";

        this.input = document.createElement("INPUT");
        this.input.className = "cyan-quickinput-input";

        this.div.appendChild(this.input);
        document.body.appendChild(this.div);

        var quickInput = this;
        Cyan.attach(this.input, "keyup", function()
        {
            if (quickInput.process)
                quickInput.process(this.value);
        });

        Cyan.attach(this.input, "keypress", function(event)
        {
            if (event.keyCode == 13 || event.keyCode == 27)
                quickInput.hide();
        });
    }
};

Cyan.QuickInput.prototype.getPosition = function(event)
{
    return {x:10,y:10};
};

Cyan.onload(function()
{
    Cyan.importCss("widgets/quickinput.css");
});