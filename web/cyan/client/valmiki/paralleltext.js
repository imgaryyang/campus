Cyan.importJs("widgets/fileupload.js");
Cyan.importCss("valmiki/paralleltext.css");
Cyan.importJs("event.js");

Cyan.Valmiki.ParallelText = function (items, name, operationId, formName, fullName, signUrl)
{
    if (!Cyan.Valmiki.ParallelText.texts)
        Cyan.Valmiki.ParallelText.texts = {};

    Cyan.Valmiki.ParallelText.texts[fullName] = this;

    var parallelText = this;

    this.disabled = false;
    this.items = items || [];
    this.name = name;
    this.operationId = operationId;
    this.formName = formName;
    this.fullName = fullName;
    this.signUrl = signUrl;

    var topDiv = this.getTopDiv();
    this.borderColor = topDiv.style.borderColor;

    var div = document.createElement("DIV");
    div.id = name + "$maindiv";
    div.className = "valmiki_paralleltext_main";
    topDiv.appendChild(div);

    var itemsDiv = document.createElement("DIV");
    itemsDiv.id = name + "$itemsdiv";
    itemsDiv.className = "valmiki_paralleltext_items";
    div.appendChild(itemsDiv);

    var editable = this.editable = Cyan.$(this.name) != null;

    if (editable)
    {
        Cyan.attach(topDiv, "click", function ()
        {
            if (!parallelText.disabled)
            {
                var text, selection = document.selection || document.getSelection();
                if (selection.createRange)
                {
                    text = selection.createRange().text;
                }
                else
                {
                    text = selection.toString();
                }
                if (!text)
                    parallelText.editText();
            }
        });
        topDiv.style.cursor = "pointer";

        var operstions = this.getOperations();
        if (operstions && operstions.length)
        {
            var operationsDiv = document.createElement("div");
            operationsDiv.className = "valmiki_paralleltext_operations";
            div.appendChild(operationsDiv);

            Cyan.each(operstions, function ()
            {
                var button = document.createElement("button");
                try
                {
                    button.type = "button";
                }
                catch (e)
                {
                }
                if (this.text)
                    Cyan.Elements.setText(button, this.text);

                if (this.className)
                    button.className = this.className;

                if (this.id)
                    button.id = this.id;

                var action = this.action;
                Cyan.attach(button, "click", function (event)
                {
                    if (!parallelText.disabled)
                    {
                        event.stop();
                        if (action)
                            action();
                    }
                });

                operationsDiv.appendChild(button);

                if (this.className == "valmiki_paralleltext_image")
                {
                    parallelText.getUpload().bind(button);
                }
            });

            operationsDiv.appendChild(document.createElement("SPAN"));
        }
    }

    var paralleltext = this;
    setTimeout(function ()
    {
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            if (item.text == null)
                item.text = "";
            var text = item.text;

            if (item.operationId == paralleltext.operationId)
            {
                var input = Cyan.$(name);
                if (input)
                {
                    input.value = text;

                    var id = document.createElement("input");
                    id.type = "hidden";
                    id.name = paralleltext.name + "$id";
                    id.value = item.id;
                    input.parentNode.appendChild(id);

                    var imageDeleted = document.createElement("input");
                    imageDeleted.type = "hidden";
                    imageDeleted.name = paralleltext.name + "$imageDeleted";
                    imageDeleted.value = "false";
                    input.parentNode.appendChild(imageDeleted);
                }
            }

            if (text || item.imageExists)
                paralleltext.createItemDiv(item, true);
        }

        if (paralleltext.resizable)
        {
            setTimeout(function ()
            {
                parallelText.resize();
            }, 200);
        }
    }, 5);
};

Cyan.Valmiki.ParallelText.getText = function (fullName)
{
    if (Cyan.Valmiki.ParallelText.texts)
        return Cyan.Valmiki.ParallelText.texts[fullName];
};

Cyan.Valmiki.ParallelText.prototype.resizable = true;

Cyan.Valmiki.ParallelText.prototype.resize = function ()
{
    if (this.resizable)
        Cyan.Valmiki.resize(this.getTopDiv(), this.getDiv());
};

Cyan.Valmiki.ParallelText.prototype.getDiv = function ()
{
    return Cyan.$(this.name + "$maindiv");
};

Cyan.Valmiki.ParallelText.prototype.getItemsDiv = function ()
{
    return Cyan.$(this.name + "$itemsdiv");
};

Cyan.Valmiki.ParallelText.prototype.getTopDiv = function ()
{
    return Cyan.$(this.name + "$div");
};

Cyan.Valmiki.ParallelText.prototype.createItemDiv = function (item, inited)
{
    var itemDiv = document.createElement("DIV");
    itemDiv.className = "valmiki_paralleltext_item";
    itemDiv.id = this.name + "$" + item.id;
    this.initStyles(itemDiv);
    this.getItemsDiv().appendChild(itemDiv);

    this.renderItem(item, itemDiv);

    if (!inited)
        this.resize();
};

Cyan.Valmiki.ParallelText.prototype.getCurrentItem = function ()
{
    for (var i = 0; i < this.items.length; i++)
    {
        var item = this.items[i];
        if (item.operationId == this.operationId)
            return item;
    }
};

Cyan.Valmiki.ParallelText.prototype.editText = function (html)
{
    var parallelText = this;
    var currentItem = this.getCurrentItem();
    var text = Cyan.$(this.name).value;

    if (html == null)
    {
        html = currentItem.html;
    }
    else if (html && !currentItem.html)
    {
        text = Cyan.escapeHtml(text);
    }

    this.getText(text, function (text)
    {
        parallelText.setText(text, html);
    }, html);
};

Cyan.Valmiki.ParallelText.prototype.setText = function (text, html)
{
    if (text != null)
    {
        var item = this.getCurrentItem();
        if (item.text != text)
        {
            item.text = text;
            item.html = html;
            item.time = new Date();
            Cyan.$(this.name).value = text;
            Cyan.$(this.name + "$html").value = html;

            var itemDiv = Cyan.$(this.name + "$" + item.id);
            if (text)
            {
                if (itemDiv)
                    this.updateText(itemDiv, text, html);
                else
                    this.createItemDiv(item);
            }
            else if (itemDiv && !item.imageExists)
            {
                itemDiv.parentNode.removeChild(itemDiv);
            }
        }
    }

    this.resize();
};

Cyan.Valmiki.ParallelText.prototype.loadImage = function ()
{
    var item = this.getCurrentItem();
    item.imageExists = true;
    item.time = new Date();

    var itemDiv = Cyan.$(this.name + "$" + item.id);
    if (itemDiv)
    {
        var el = Cyan.$$(itemDiv);
        var imageDiv = el.$(".valmiki_paralleltext_item_image");
        imageDiv.display(true);
        var img = imageDiv.$("img")[0];
        img.src = Cyan.formatUrl(this.getThumbUrl(item));
        el.$(".valmiki_paralleltext_item_time").html(this.formatTime(new Date()));
    }
    else
    {
        this.createItemDiv(item);
    }

    this.resize();
};

Cyan.Valmiki.ParallelText.prototype.getText = function (text, callback, html)
{
    if (!html)
        Cyan.prompt("", text, callback);
    else
        callback(text);
};

Cyan.Valmiki.ParallelText.prototype.renderItem = function (item, div)
{
    var textDiv = document.createElement("DIV");
    textDiv.className = "valmiki_paralleltext_item_text";
    if (item.text)
        textDiv.innerHTML = item.html ? item.text : Cyan.escapeHtml(item.text);
    else
        textDiv.style.display = "none";
    this.initStyles(textDiv);
    div.appendChild(textDiv);

    var imageDiv = document.createElement("DIV");
    imageDiv.className = "valmiki_paralleltext_item_image";
    var img = document.createElement("IMG");
    imageDiv.appendChild(img);
    if (item.imageExists)
        img.src = this.getThumbUrl(item);
    else
        imageDiv.style.display = "none";

    var parallelText = this;
    Cyan.attach(img, "click", function (event)
    {
        event.stop();
        parallelText.showImage(item);
    });

    this.initStyles(imageDiv);
    div.appendChild(imageDiv);

    var signDiv = document.createElement("DIV");
    signDiv.className = "valmiki_paralleltext_item_sign";
    this.initStyles(signDiv);
    div.appendChild(signDiv);

    this.renderSign(item, signDiv);
};

Cyan.Valmiki.ParallelText.prototype.renderSign = function (item, signDiv)
{
    if (item.signUrl)
    {
        var img = document.createElement("IMG");
        img.src = item.signUrl;
        signDiv.appendChild(img);
    }
    else
    {
        var nameSpan = document.createElement("SPAN");
        nameSpan.className = "valmiki_paralleltext_item_name";
        nameSpan.innerHTML = item.operatorName;
        this.initStyles(nameSpan);
        signDiv.appendChild(nameSpan);
    }

    var timeSpan = document.createElement("SPAN");
    timeSpan.className = "valmiki_paralleltext_item_time";
    timeSpan.innerHTML = this.formatTime(item.time);
    this.initStyles(timeSpan);
    signDiv.appendChild(timeSpan);
};

Cyan.Valmiki.ParallelText.prototype.updateText = function (div, text, html)
{
    var el = Cyan.$$(div);
    var textDiv = el.$(".valmiki_paralleltext_item_text");
    textDiv.display(true);
    textDiv.html(html ? text : Cyan.escapeHtml(text));
    el.$(".valmiki_paralleltext_item_time").html(this.formatTime(new Date()));
};

Cyan.Valmiki.ParallelText.prototype.initStyles = function (element)
{
    var div = Cyan.$(this.name + "$div");

    element.style.fontFamily = div.style.fontFamily;
    element.style.fontSize = div.style.fontSize;
    element.style.color = div.style.color;
    element.style.fontWeight = div.style.fontWeight;
    element.style.fontStyle = div.style.fontStyle;
    element.style.textDecoration = div.style.textDecoration;
};

Cyan.Valmiki.ParallelText.prototype.getThumbUrl = function (item)
{
    return "";
};

Cyan.Valmiki.ParallelText.prototype.formatTime = function (time)
{
    return Cyan.Date.format(time, "yyyy-MM-dd HH:mm");
};

Cyan.Valmiki.ParallelText.prototype.getOperations = function ()
{
    var parallelText = this;

    return [
        {
            className: "valmiki_paralleltext_edit",
            action: function ()
            {
                parallelText.editText(true);
            }
        },
        {
            className: "valmiki_paralleltext_image"
        },
        {
            className: "valmiki_paralleltext_clear",
            action: function ()
            {
                parallelText.clear();
            }
        }
    ];
};

Cyan.Valmiki.ParallelText.prototype.getUpload = function ()
{
    if (!this.upload)
    {
        var parallelText = this;
        this.upload = new Cyan.FileUpload(this.name + "$imageFile", false);
        this.upload.onselect = function (file)
        {
            Cyan.$(parallelText.name + "$imageDeleted").value = "false";
            if (parallelText.onImageUpload)
                parallelText.onImageUpload();
        };
    }

    return this.upload;
};

Cyan.Valmiki.ParallelText.prototype.reset = function ()
{
    this.getUpload().reset();
    Cyan.$(this.name + "$imageDeleted").value = "false";
};

Cyan.Valmiki.ParallelText.prototype.clear = function ()
{
    this.getUpload().reset();
    Cyan.$(this.name + "$imageDeleted").value = "true";

    var item = this.getCurrentItem();
    item.imageExists = false;
    var itemDiv = Cyan.$(this.name + "$" + item.id);
    if (itemDiv)
    {
        var el = Cyan.$$(itemDiv);
        var imageDiv = el.$(".valmiki_paralleltext_item_image");
        imageDiv.display(false);
    }
};

Cyan.Valmiki.ParallelText.prototype.showImage = function (item)
{
};

Cyan.Valmiki.ParallelText.prototype.disable = function ()
{
    this.disabled = true;
    this.getTopDiv().style.borderColor = "gray";

    Cyan.$$(this.getDiv()).$(".valmiki_paralleltext_operations").hide();
    this.resize();
};

Cyan.Valmiki.ParallelText.prototype.enable = function ()
{
    this.disabled = false;
    this.getTopDiv().style.borderColor = this.borderColor;

    Cyan.$$(this.getDiv()).$(".valmiki_paralleltext_operations").show();
    this.resize();
};


Cyan.Valmiki.ParallelText.prototype.displayOrgName = false;