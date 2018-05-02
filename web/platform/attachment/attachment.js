Cyan.importJs("/platform/commons/pdf.js");

if (!window.System)
    System = {};

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        System.FileStore.init(null);
    }, 100);
});

Cyan.importJs("widgets/fileupload.js");
if (System.userName)
    Cyan.importJs("/platform/commons/filestore/filestore.js");

System.AttachmentList = function (name, attachmentId, encodedId, items, editable)
{
    if (items == null)
        items = [];

    this.items = items;
    this.attachmentId = attachmentId;
    this.name = name;
    this.encodedId = encodedId;
    this.editable = editable;
};

System.AttachmentList.prototype.getUpload = function ()
{
    if (!this.upload)
    {
        var list = this;
        this.upload = new Cyan.FileUpload(this.name + ".files", true);
        this.upload.onselect = function (file)
        {
            var fileName = file.value;
            var pos = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("\\"));
            if (pos >= 0)
                fileName = fileName.substring(pos + 1);

            var item = {name: fileName, file: file};
            list.items.push(item);
            list.add(item);

            if (list.onselect)
                list.onselect(fileName);
        };
    }

    return this.upload;
};

System.AttachmentList.prototype.init = function (el)
{
    el = Cyan.$(el);

    this.div = document.createElement("DIV");
    this.div.className = "attachments";

    el.appendChild(this.div);

    if (this.items && this.items.length)
    {
        for (var i = 0; i < this.items.length; i++)
            this.add(this.items[i]);
    }

    this.deleteds = document.createElement("INPUT");
    this.deleteds.type = "hidden";
    this.deleteds.name = this.name + ".deleteds";
    el.appendChild(this.deleteds);
};

System.AttachmentList.prototype.add = function (item)
{
    var list = this;

    var div = document.createElement("DIV");
    div.className = "attachment_item";

    var label = document.createElement("SPAN");
    label.className = "attachment_name";
    div.appendChild(label);

    var url, url0;

    if (item.no)
    {
        url0 = "/attachment/" + this.encodedId + "/" + item.no;
        url = url0 + "/" + item.name;

        var href = document.createElement("A");
        label.appendChild(href);

        href.target = "_blank";
        href.href = url;

        if (href.appendChild)
            href.appendChild(document.createTextNode(item.name));
        else
            href.innerHTML = item.name;
    }
    else
    {
        label.appendChild(document.createTextNode(item.name));
    }

    label.title = item.name;

    if (item.deletable || !item.no)
    {
        var del = document.createElement("SPAN");
        del.className = "attachment_action";
        div.appendChild(del);
        var delHref = document.createElement("A");
        del.appendChild(delHref);
        delHref.href = "#";
        delHref.innerHTML = "删除";
        delHref.onclick = function ()
        {
            list.remove(item);
            return false;
        };
    }

    if (item.no)
    {
        var postfix;
        var index = item.name.lastIndexOf(".");
        if (index > 0)
            postfix = item.name.substring(index + 1).toLowerCase();

        if ((postfix == "doc" || postfix == "wps" || postfix == "docx" || postfix == "xls" ||
                postfix == "xlsx") && this.editable)
        {
            var edit = document.createElement("SPAN");
            edit.className = "attachment_action";
            div.appendChild(edit);
            var editHref = document.createElement("A");
            edit.appendChild(editHref);
            editHref.href = "#";
            editHref.innerHTML = "编辑";
            editHref.onclick = function ()
            {
                System.openPage(url0 + "/edit");
                return false;
            };
        }

        var showAction;
        if (postfix == "jpg" || postfix == "jpeg" || postfix == "gif" || postfix == "bmp" || postfix == "png")
        {
            showAction = function ()
            {
                System.showImage(url);
                return false;
            };
        }
        else if (postfix == "txt" || postfix == "doc" || postfix == "docx" || postfix == "xls" || postfix == "xlsx" ||
                postfix == "wps" || postfix == "zip" || postfix == "rar")
        {
            showAction = function ()
            {
                window.open(url0 + "/html");
                return false;
            };
        }
        else if (postfix == "pdf")
        {
            showAction = function ()
            {
                System.showPdf(url);
                return false;
            };
        }

        if (showAction)
        {
            var show = document.createElement("SPAN");
            show.className = "attachment_action";
            div.appendChild(show);
            var showHref = document.createElement("A");
            show.appendChild(showHref);
            showHref.href = "#";
            showHref.innerHTML = "查看";
            showHref.onclick = showAction;
        }
    }

    this.div.appendChild(div);
};

System.AttachmentList.prototype.remove = function (item)
{
    var index = Cyan.Array.indexOf(this.items, item);
    Cyan.Array.remove(this.items, index);

    if (item.no)
    {
        var s = this.deleteds.value;
        if (s.length > 0)
            s += ",";
        s += item.no;
        this.deleteds.value = s;
    }
    else
    {
        item.file.parentNode.removeChild(item.file);
    }

    this.div.removeChild(this.div.childNodes[index]);
};

System.AttachmentList.prototype.clear = function ()
{
    this.items = [];
    this.div.innerHTML = "";
    this.reset();
};

System.AttachmentList.prototype.reset = function ()
{
    this.deleteds.value = "";
    this.upload.reset();
    Cyan.$$(document.getElementsByName(this.name + ".files")).remove();
};

System.AttachmentList.prototype.setItems = function (items)
{
    this.clear();
    this.items = items || [];

    if (items && items.length)
    {
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            if (!this.attachmentId)
                this.attachmentId = item.id;
            if (!this.encodedId)
                this.encodedId = item.encodedId;
            this.add(item);
        }
    }
};

System.AttachmentList.prototype.bind = function (component)
{
    this.getUpload().bind(component);
};

System.AttachmentList.storeTo = function (encodedId, attachmentNo, target, source)
{
    var f = function ()
    {
        Cyan.Arachne.get("/attachment/" + encodedId + "/" + attachmentNo + "/storeTo/" + target + "?source=" +
        encodeURIComponent(source), null, function ()
        {
            Cyan.message("操作成功");
        });
    };

    if (target)
    {
        f();
    }
    else
    {
        System.FileStore.showCatalog(null, function (catalog)
        {
            target = catalog.id;
            f();
        });
    }
};

System.AttachmentList.prototype.storeTo = function (attachmentNo, target, source)
{
    System.AttachmentList.storeTo(this.encodedId, attachmentNo, target, source);
};

System.AttachmentList.storeAllTo = function (encodedId, target, source)
{
    var f = function ()
    {
        Cyan.Arachne.post("/attachment/" + encodedId + "/storeTo/" + target + "?source=" + encodeURIComponent(source),
                null, function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        System.FileStore.go(target);
                    });
                });
    };

    if (target)
    {
        f();
    }
    else
    {
        System.FileStore.showCatalog(null, function (catalog)
        {
            target = catalog.id;
            f();
        });
    }
};

System.AttachmentList.prototype.storeAllTo = function (target, source)
{
    System.AttachmentList.storeAllTo(this.encodedId, target, source);
};

System.AttachmentList.prototype.sort = function ()
{
    if (!this.attachmentId)
    {
        Cyan.message("文件未保存，不能排序，请先保存");
        return;
    }

    if (this.items && this.items.length)
    {
        for (var i = 0; i < this.items.length; i++)
        {
            if (!this.items[i].no)
            {
                Cyan.message("有文件未保存，不能排序，请先保存");
                return;
            }
        }
    }

    if (this.deleteds.value)
    {
        Cyan.message("有删除的文件未提交，不能排序，请先保存");
        return;
    }

    var attachmentList = this;
    System.showModal("/attachment/crud.sort?attachmentId=" + this.attachmentId, function (result)
    {
        if (result)
        {
            var n = result.length;
            if (n != attachmentList.div.childNodes.length)
                return;

            var hrefs = new Array(n);
            var items = new Array(n);
            var i, href, b;
            for (i = 0; i < n; i++)
            {
                var no = result[i];
                for (var j = 0; j < n; j++)
                {
                    var item = attachmentList.items[j];
                    if (item.no == no)
                    {
                        if (j != i)
                            b = true;
                        items[i] = item;
                        href = attachmentList.div.childNodes[j];
                        hrefs[i] = href;
                        break;
                    }
                }
            }

            if (b)
            {
                for (i = 0; i < n; i++)
                {
                    attachmentList.items[i] = items[i];
                    attachmentList.div.removeChild(hrefs[i]);
                }
                for (i = 0; i < n; i++)
                {
                    attachmentList.div.appendChild(hrefs[i]);
                }
            }
        }
    });
};