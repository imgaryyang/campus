Cyan.importJs("widgets/fileupload.js");
Cyan.importCss("valmiki/filelist.css");

Cyan.Valmiki.FileList = function (items, name, writable, formName, fullName)
{
    Cyan.Valmiki.FileList.fileList[name] = this;

    this.items = items || [];
    this.name = name;
    this.writable = writable;
    this.formName = formName;
    this.fullName = fullName;

    var fileList = this;

    Cyan.onload(function ()
    {
        window.setTimeout(function ()
        {
            fileList.init();
        }, 200);
    })
};

Cyan.Valmiki.FileList.prototype.init = function ()
{
    var fileList = this;

    var name = this.name;
    var items = this.items;

    var topDiv = this.getTopDiv();

    var div = document.createElement("DIV");
    div.id = name + "$maindiv";
    div.className = "valmiki_filelist_main";
    topDiv.appendChild(div);

    var ul = document.createElement("UL");
    ul.id = name + "$list";
    ul.className = "valmiki_filelist";
    div.appendChild(ul);

    var bottom = document.createElement("DIV");
    bottom.id = name + "$bottom";
    bottom.className = "valmiki_filelist_bottom";
    div.appendChild(bottom);

    this.initBottom(bottom);

    Cyan.$$(bottom).$("button").each(function ()
    {
        if (this.innerHTML == fileList.addText)
        {
            fileList.getUpload().bind(this);
            return true;
        }
    });

    Cyan.Valmiki.resize(topDiv, div);

    var i;

    if (items)
    {
        for (i = 0; i < items.length; i++)
        {
            this.add(items[i]);
        }
    }
};

Cyan.Valmiki.FileList.fileList = {};

Cyan.Valmiki.FileList.get = function (name)
{
    return Cyan.Valmiki.FileList.fileList[name];
};

Cyan.Valmiki.FileList.prototype.getAddButton = function ()
{
    return Cyan.$(this.name + "$add");
};

Cyan.Valmiki.FileList.prototype.getUl = function ()
{
    return Cyan.$(this.name + "$list");
};

Cyan.Valmiki.FileList.prototype.getTopDiv = function ()
{
    return Cyan.$(this.name + "$div");
};

Cyan.Valmiki.FileList.prototype.getDiv = function ()
{
    return Cyan.$(this.name + "$maindiv");
};

Cyan.Valmiki.FileList.prototype.getItem = function (id)
{
    for (var i = 0; i < this.items.length; i++)
    {
        var item = this.items[i];
        if (item.id == id)
            return item;
    }

    return item;
};

Cyan.Valmiki.FileList.prototype.initBottom = function (bottom)
{
    var actions = this.getBottomActions();
    if (actions)
    {
        for (var i = 0; i < actions.length; i++)
        {
            var action = actions[i];

            var button = Cyan.Elements.createButton(action.text);
            if (action.action)
                button.onclick = action.action;
            if (action.id)
                button.id = action.id;

            bottom.appendChild(button);
        }
    }
};

Cyan.Valmiki.FileList.prototype.getBottomActions = function ()
{
    if (this.writable)
    {
        return [
            {text: this.addText, action: null}
        ];
    }
    else
    {
        return null;
    }
};

Cyan.Valmiki.FileList.prototype.add = function (item)
{
    var filelist = this;

    var div = this.getDiv();
    var ul = this.getUl();

    var li = document.createElement("LI");
    li.fileId = item.id;
    this.initFile(item, li);

    ul.appendChild(li);

    Cyan.Valmiki.resize(this.getTopDiv(), this.getDiv());
};

Cyan.Valmiki.FileList.prototype.initFile = function (item, li)
{
    this.initFileName(item, li);
    this.initActions(item, li);
};

Cyan.Valmiki.FileList.prototype.initFileName = function (item, li)
{
    var fileName = document.createElement("SPAN");
    fileName.className = "fileName";
    var html = "<a href='" + item.url + "' target='_blank'>" + item.fileName + "</a>";


    if (item.fileSize)
        html += "(" + Cyan.toBytesSize(item.fileSize) + ")";

    fileName.innerHTML = html;

    li.appendChild(fileName);
};

Cyan.Valmiki.FileList.prototype.initActions = function (item, li)
{
    var actions = this.getActions(item);
    if (actions)
    {
        for (var i = 0; i < actions.length; i++)
        {
            var action = actions[i];

            var span = document.createElement("SPAN");
            span.className = "action";
            span.innerHTML = "<a href='#' onclick='return false;'>" + action.text + "</a>";
            span.onclick = action.action;

            li.appendChild(span);
        }
    }
};

Cyan.Valmiki.FileList.prototype.getActions = function (item)
{
    if (this.writable && item.deletable)
    {
        var fileList = this;

        return [
            {
                text: this.deleteText,
                action: function ()
                {
                    fileList.remove(this.parentNode.fileId, true);
                }
            }
        ];
    }

    return null;
};

Cyan.Valmiki.FileList.prototype.down = function ()
{
    var item = this.getItem(this.getCombox().value);
    if (item.url)
        window.open(item.url);
};


Cyan.Valmiki.FileList.prototype.remove = function (id, invkeOnDelete)
{
    var item = this.getItem(id);

    var div = this.getDiv();
    if (item.file)
    {
        item.file.parentNode.removeChild(item.file);
    }
    else
    {
        var deleted = document.createElement("INPUT");
        deleted.type = "hidden";
        deleted.name = this.name + "$deleted";
        deleted.id = this.name + "$deleted";
        deleted.value = item.id;
        div.appendChild(deleted);
    }
    Cyan.Array.removeElement(this.items, item);

    var lis = Cyan.$$(this.getDiv()).$("li");
    for (var i = 0; i < lis.length; i++)
    {
        var li = lis[i];
        if (li.fileId == id)
        {
            li.parentNode.removeChild(li);

            Cyan.Valmiki.resize(this.getTopDiv(), this.getDiv());
        }
    }

    if (invkeOnDelete && this.onDelete)
        this.onDelete(item);
};

Cyan.Valmiki.FileList.prototype.getUpload = function ()
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
                fileName: fileName,
                file: file,
                id: "$#$" + list.items.length,
                deletable: true
            };
            list.items.push(item);
            list.add(item);
            if (list.onUpload)
                list.onUpload(item);
        };
    }

    return this.upload;
};

Cyan.Valmiki.FileList.prototype.reset = function (items)
{
    this.clear();
    if (items)
    {
        this.items = items;
        for (var i = 0; i < items.length; i++)
        {
            this.add(items[i]);
        }
    }
};

Cyan.Valmiki.FileList.prototype.clear = function ()
{
    while (this.items.length)
    {
        var item = this.items[0];
        this.remove(item.id, false);
    }
    this.items.length = 0;

    Cyan.$$(document.getElementsByName(this.name + "$deleted")).remove();
};

Cyan.importLanguage("valmiki_filelist");