Cyan.importJs("widgets/fileupload.js");
Cyan.importJs("combox.js");

Cyan.Valmiki.FileList2 = function (items, name, writable)
{
    Cyan.Valmiki.FileList2.fileList[name] = this;

    this.items = items || [];
    this.name = name;
    this.writable = writable;

    var fileList = this;

    Cyan.onload(function ()
    {
        fileList.init();
    })
};

Cyan.Valmiki.FileList2.prototype.init = function ()
{
    var name = this.name;
    var items = this.items;
    var writable = this.writable;

    var div = Cyan.$(name + "$div");
    var size = Cyan.Elements.getComponentSize(div);

    var comboxWdith = size.width;
    comboxWdith -= this.downWidth + this.gap;

    if (writable)
    {
        comboxWdith -= this.addWidth + this.gap;
        comboxWdith -= this.deleteWidth + this.gap;
    }

    var combox = document.createElement("SELECT");
    combox.name = name + "$combox";
    combox.id = name + "$combox";
    combox.style.position = "absolute";
    combox.style.left = "0";
    combox.style.top = "0";
    combox.style.width = comboxWdith + "px";
    combox.style.height = size.height + "px";
    div.appendChild(combox);

    var left = comboxWdith;

    var downButton = Cyan.Elements.createButton(this.downText);
    downButton.name = name + "$down";
    downButton.id = name + "$down";
    downButton.style.position = "absolute";
    downButton.style.left = (left + this.gap) + "px";
    downButton.style.top = "0";
    downButton.style.width = this.downWidth + "px";
    downButton.style.height = size.height + "px";
    downButton.style.textAlign = "center";
    downButton.style.paddingLeft = 0;
    downButton.style.paddingRight = 0;
    div.appendChild(downButton);

    left += this.gap + this.downWidth;

    var addButton, deleteButton;

    if (writable)
    {
        addButton = Cyan.Elements.createButton(this.addText);
        addButton.name = name + "$add";
        addButton.id = name + "$add";
        addButton.style.position = "absolute";
        addButton.style.left = (left + this.gap) + "px";
        addButton.style.top = "0";
        addButton.style.width = this.addWidth + "px";
        addButton.style.height = size.height + "px";
        addButton.style.textAlign = "center";
        addButton.style.paddingLeft = 0;
        addButton.style.paddingRight = 0;
        div.appendChild(addButton);

        left += this.gap + this.addWidth;

        deleteButton = Cyan.Elements.createButton(this.deleteText);
        deleteButton.name = name + "$delete";
        deleteButton.id = name + "$add";
        deleteButton.style.position = "absolute";
        deleteButton.style.left = (left + this.gap) + "px";
        deleteButton.style.top = "0";
        deleteButton.style.width = this.deleteWidth + "px";
        deleteButton.style.height = size.height + "px";
        deleteButton.style.textAlign = "center";
        deleteButton.style.paddingLeft = 0;
        deleteButton.style.paddingRight = 0;
        div.appendChild(deleteButton);
    }

    var i;

    if (items)
    {
        for (i = 0; i < items.length; i++)
        {
            this.add(items[i], false);
        }
    }

    var fileList = this;

    if (addButton)
        this.getUpload().bind(addButton);

    if (!this.items.length)
    {
        downButton.disabled = true;
        if (deleteButton)
            deleteButton.disabled = true;
    }
    else if (deleteButton)
    {
        if (!items[0].deletable)
            deleteButton.disabled = true;
    }

    Cyan.attach(downButton, "click", function ()
    {
        fileList.down();
    });

    if (deleteButton)
    {
        Cyan.attach(deleteButton, "click", function ()
        {
            fileList.remove();
        });
    }

    Cyan.attach(this.getCombox(), "change", function ()
    {
        fileList.comboxChange(this);
    });
};

Cyan.Valmiki.FileList2.fileList = {};

Cyan.Valmiki.FileList2.get = function (name)
{
    return Cyan.Valmiki.FileList2.fileList[name];
};

Cyan.Valmiki.FileList2.prototype.getCombox = function ()
{
    return Cyan.$(this.name + "$combox");
};

Cyan.Valmiki.FileList2.prototype.getDownButton = function ()
{
    return Cyan.$(this.name + "$down");
};

Cyan.Valmiki.FileList2.prototype.getAddButton = function ()
{
    return Cyan.$(this.name + "$add");
};

Cyan.Valmiki.FileList2.prototype.getDeleteButton = function ()
{
    return Cyan.$(this.name + "$delete");
};

Cyan.Valmiki.FileList2.prototype.getItem = function (id)
{
    for (var i = 0; i < this.items.length; i++)
    {
        var item = this.items[i];
        if (item.id == id)
            return item;
    }

    return item;
};

Cyan.Valmiki.FileList2.prototype.comboxChange = function (combox)
{
    var item = this.getItem(combox.value);

    var deleteButton = this.getDeleteButton();
    if (deleteButton)
        deleteButton.disabled = !item.deletable;
    this.getDownButton().disabled = !item.url;
};

Cyan.Valmiki.FileList2.prototype.add = function (item, selected)
{
    var combox = this.getCombox();

    var option = new Option(item.fileName, item.id);
    combox.options[combox.options.length] = option;
    if (selected)
        option.selected = true;

    this.comboxChange(combox);
};

Cyan.Valmiki.FileList2.prototype.down = function ()
{
    var item = this.getItem(this.getCombox().value);
    if (item.url)
        window.open(item.url);
};


Cyan.Valmiki.FileList2.prototype.remove = function ()
{
    var combox = this.getCombox();
    var item = this.getItem(combox.value);

    if (item.deletable)
    {
        if (item.file)
        {
            item.file.parentNode.removeChild(item.file);
        }
        else
        {
            var form = combox.form;
            if (form)
            {
                var deleted = document.createElement("input");
                deleted.type = "hidden";
                deleted.name = this.name + "$deleted";
                deleted.id = this.name + "$deleted";
                deleted.value = item.id;
                form.appendChild(deleted);
            }
        }
        Cyan.Array.removeElement(this.items, item);
        combox.options[combox.selectedIndex] = null;

        if (this.onDelete)
            this.onDelete(item);
    }
};

Cyan.Valmiki.FileList2.prototype.getUpload = function ()
{
    if (!this.upload)
    {
        var list = this;
        this.upload = new Cyan.FileUpload(this.name, true);
        this.upload.onselect = function (file)
        {
            var fileName = file.value;
            var pos = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));
            if (pos >= 0)
                fileName = fileName.substring(pos + 1);

            var item = {
                fileName:fileName,
                file:file,
                id:"$#$" + list.items.length,
                deletable:true
            };
            list.items.push(item);
            list.add(item, true);
            if (list.onUpload)
                list.onUpload(item);
        };
    }

    return this.upload;
};

Cyan.Valmiki.FileList2.prototype.reset = function (items)
{
    this.clear();
    if (items)
    {
        this.items = items;
        for (var i = 0; i < items.length; i++)
        {
            this.add(items[i], false);
        }
    }
};

Cyan.Valmiki.FileList2.prototype.clear = function ()
{
    Cyan.$$(this.getCombox()).clearAll();

    for (var i = 0; i < this.items.length; i++)
    {
        var item = this.items[i];
        if (item.file)
        {
            item.file.parentNode.removeChild(item.file);
        }
    }
    this.items.length = 0;

    Cyan.$$(document.getElementsByName(this.name + "$deleted")).remove();
};

Cyan.Valmiki.FileList2.prototype.gap = 3;
Cyan.importLanguage("valmiki_filelist2");