Cyan.importJs("/platform/commons/pdf.js");

var backs = [];
var forwards = [];
var iconWidth;
var iconHeight;
var currentImg;
var currentFile;

Cyan.onload(function ()
{
    Cyan.overwrite(window, "list", function (path)
    {
        Arachne.form.path = path;

        this.inherited(function (files)
        {
            Cyan.$("actions").style.display = "none";
            currentFile = null;

            initPath(path);
            Cyan.$("files").innerHTML = "";
            var n = files.length;
            for (var i = 0; i < n; i++)
            {
                renderFile(files[i]);
            }
        });
    });

    list(Arachne.form.path || "");
});

function initPath(path)
{
    if (backs.length || forwards.length || path)
    {
        var index;
        Cyan.$$("#navigator").show();

        var navigator = Cyan.$("navigator");

        Cyan.$$("#back").display(backs.length > 0);
        Cyan.$$("#forward").display(forwards.length > 0);
        Cyan.$$("#up").display(!!path);

        if (backs.length)
        {
            var back = backs[backs.length - 1];
            index = back.lastIndexOf("/");
            if (index > 0)
                back = back.substring(index + 1);
            if (Cyan.endsWith(back, "!"))
                back = back.substring(0, back.length - 1);
            Cyan.$("back").title = "返回到 " + (back || Arachne.form.name);
        }

        if (forwards.length)
        {
            var forward = forwards[forwards.length - 1];
            index = forward.lastIndexOf("/");
            if (index > 0)
                forward = forward.substring(index + 1);
            if (Cyan.endsWith(forward, "!"))
                forward = forward.substring(0, forward.length - 1);
            Cyan.$("forward").title = "前进到 " + (forward || Arachne.form.name);
        }

        var paths = Cyan.$("paths");
        paths.innerHTML = "";

        if (path)
        {
            index = path.lastIndexOf("/");
            if (index > 0)
            {
                var path1 = path.substring(0, index);

                index = path1.lastIndexOf("/");
                if (index > 0)
                    path1 = path1.substring(index + 1);
                if (Cyan.endsWith(path1, "!"))
                    path1 = path1.substring(0, path1.length - 1);
                Cyan.$("up").title = "上升到 " + path1;
            }
            else
            {
                Cyan.$("up").title = "上升到 " + Arachne.form.name;
            }

            var pathDiv = document.createElement("div");
            pathDiv.className = "path";
            var a = document.createElement("a");
            a.innerHTML = Arachne.form.name;
            a.href = "#";
            a.path = "";
            a.onclick = goPath;
            pathDiv.appendChild(a);
            paths.appendChild(pathDiv);

            var split = document.createElement("div");
            split.className = "split";
            paths.appendChild(split);

            var index1 = 0;
            while ((index = path.indexOf("/", index1)) > 0)
            {
                pathDiv = document.createElement("div");
                pathDiv.className = "path";
                a = document.createElement("a");
                var s1 = path.substring(index1, index);
                if (Cyan.endsWith(s1, "!"))
                    s1 = s1.substring(0, s1.length - 1);
                a.innerHTML = s1;
                a.href = "#";
                a.path = path.substring(0, index);
                a.onclick = goPath;
                pathDiv.appendChild(a);
                paths.appendChild(pathDiv);

                index1 = index + 1;

                split = document.createElement("div");
                split.className = "split";
                paths.appendChild(split);
            }

            pathDiv = document.createElement("div");
            pathDiv.className = "path";
            s1 = path.substring(index1);
            if (Cyan.endsWith(s1, "!"))
                s1 = s1.substring(0, s1.length - 1);
            pathDiv.innerHTML = s1;
            paths.appendChild(pathDiv);
        }
    }
    else
    {
        Cyan.$$("#navigator").hide();
    }
}

function renderFile(file)
{
    var fileDiv = document.createElement("div");
    Cyan.$("files").appendChild(fileDiv);

    var name = file.name;

    fileDiv.className = "file";
    fileDiv.fileName = name;
    fileDiv.directory = file.directory;

    var iconClass = getIconClass(file);
    var icon = document.createElement("div");
    if (iconClass)
        icon.className = "icon " + iconClass;
    else
        icon.className = "icon";
    fileDiv.appendChild(icon);

    if (!iconClass)
    {
        if (!iconWidth)
            iconWidth = Cyan.toInt(Cyan.Elements.getCss(icon, "width"));
        if (!iconHeight)
            iconHeight = Cyan.toInt(Cyan.Elements.getCss(icon, "height"));

        var path = name;
        if (Arachne.form.path)
            path = Arachne.form.path + "/" + path;
        var image = document.createElement("IMG");
        image.src = "/archive/thumb?file=" + Arachne.form.file + "&path=" + encodeURIComponent(path) + "&width=" +
        iconWidth + "&height=" + iconHeight;

        icon.appendChild(image);
    }

    var fileName = document.createElement("div");
    fileName.className = "name";
    fileName.innerHTML = name;

    fileDiv.appendChild(fileName);

    if (file.directory)
    {
        fileDiv.onclick = directoryClick;
    }
    else
    {
        fileDiv.onclick = fileClick;
        var sizeDiv = document.createElement("div");
        sizeDiv.className = "size";
        sizeDiv.innerHTML = file.size;
        fileDiv.appendChild(sizeDiv);
    }

    Cyan.attach(fileDiv, "mouseover", fileMouseOver);
    Cyan.attach(fileDiv, "mouseout", fileMouseOut);
}

function getIconClass(file)
{
    if (file.directory)
        return "folder";

    var name = file.name;
    var ext = Cyan.getExtName(name);
    if (!ext)
        return "unknown";

    ext = ext.toLowerCase();
    if (ext == "jpg" || ext == "jpge" || ext == "gif" || ext == "png")
    {
        return null;
    }
    else if (ext == "bmp" || ext == "tif" || ext == "tiff")
    {
        return "image";
    }
    else if (ext == "txt" || ext == "css" || ext == "js" || ext == "xml" || ext == "jsp" || ext == "php" ||
            ext == "asp" || ext == "aspx" || ext == "properties" || ext == "ini")
    {
        return "text";
    }
    else if (ext == "html" || ext == "htm" || ext == "xhtml")
    {
        return "html";
    }
    else if (ext == "doc" || ext == "docx" || ext == "wps" || ext == "dot" || ext == "rtf")
    {
        return "doc";
    }
    else if (ext == "xls" || ext == "xlsx")
    {
        return "xls";
    }
    else if (ext == "ppt" || ext == "pptx")
    {
        return "ppt";
    }
    else if (ext == "ppt" || ext == "pptx")
    {
        return "ppt";
    }
    else if (ext == "pdf")
    {
        return "pdf";
    }
    else if (ext == "ps")
    {
        return "ps";
    }
    else if (ext == "flv" || ext == "swf")
    {
        return "flv";
    }
    else if (ext == "zip" || ext == "rar" || ext == "jar")
    {
        return "ar";
    }
    else if (ext == "au" || ext == "mid" || ext == "ram" || ext == "mp3" || ext == "wav" || ext == "wma")
    {
        return "audio";
    }
    else if (ext == "avi" || ext == "asf" || ext == "mp4" || ext == "mpg" || ext == "wmv")
    {
        return "video";
    }
    else
    {
        return "unknown";
    }
}

function directoryClick()
{
    var fileName = this.fileName;
    if (Arachne.form.path)
        fileName = Arachne.form.path + "/" + fileName;
    selectPath(fileName);
}

function fileClick()
{
    var fileName = this.fileName;
    var extName = Cyan.getExtName(fileName), s;
    if (extName == "txt" || extName == "doc" || extName == "docx" || extName == "xls" || extName == "xlsx" ||
            extName == "wps" || extName == "zip" || extName == "rar" || extName == "pdf")
    {
        preview(fileName)
    }
    else if (extName == "jpg" || extName == "jpeg" || extName == "gif" || extName == "png")
    {
        var img = Cyan.$$(this).$("img")[0];
        if (img.width < iconWidth && img.height < iconHeight)
            download(fileName);
        else
            preview(fileName)
    }
    else
    {
        download(fileName);
    }
}

function preview(fileName)
{
    var extName = Cyan.getExtName(fileName), s;
    if (extName == "zip" || extName == "rar")
    {
        s = fileName + "!";
        if (Arachne.form.path)
            s = Arachne.form.path + "/" + s;
        selectPath(s);
    }
    else
    {
        s = fileName;
        if (Arachne.form.path)
            s = encodeURIComponent(Arachne.form.path + "/" + s);

        if (extName == "jpg" || extName == "jpeg" || extName == "gif" || extName == "png")
        {
            currentImg = fileName;
            System.showImage("/archive/img?file=" + Arachne.form.file + "&path=" + s);
        }
        else if (extName == "txt" || extName == "doc" || extName == "docx" || extName == "xls" || extName == "xlsx" ||
                extName == "wps")
        {
            window.open("/archive/html?file=" + Arachne.form.file + "&path=" + s)
        }
        else if (extName == "pdf")
        {
            System.showPdf("/archive/down?file=" + Arachne.form.file + "&path=" + s);
        }
    }
}

function download(fileName)
{
    if (Arachne.form.path)
        fileName = Arachne.form.path + "/" + fileName;
    window.location.href = "/archive/down?file=" + Arachne.form.file + "&path=" + encodeURIComponent(fileName);
}

function getNextImg(callback)
{
    var imgs = Cyan.$$(".file .icon img");
    var n = imgs.length;
    for (var i = 0; i < n; i++)
    {
        var img = imgs[i];

        var fileName = img.parentNode.parentNode.fileName;
        if (fileName == currentImg)
        {
            if (i == n - 1)
            {
                callback(null)
            }
            else
            {
                fileName = imgs[i + 1].parentNode.parentNode.fileName;
                currentImg = fileName;
                var s = fileName;
                if (Arachne.form.path)
                    s = Arachne.form.path + "/" + s;
                callback("/archive/img?file=" + Arachne.form.file + "&path=" + encodeURIComponent(s));
            }
            break;
        }
    }
}

function getPreImg(callback)
{
    var imgs = Cyan.$$(".file .icon img");
    var n = imgs.length;
    for (var i = 0; i < n; i++)
    {
        var img = imgs[i];

        var fileName = img.parentNode.parentNode.fileName;
        if (fileName == currentImg)
        {
            if (i == 0)
            {
                callback(null)
            }
            else
            {
                fileName = imgs[i - 1].parentNode.parentNode.fileName;
                currentImg = fileName;
                var s = fileName;
                if (Arachne.form.path)
                    s = Arachne.form.path + "/" + s;
                callback("/archive/img?file=" + Arachne.form.file + "&path=" + encodeURIComponent(s));

            }
            break;
        }
    }
}

function selectPath(path)
{
    backs.push(Arachne.form.path);
    forwards.length = 0;
    list(path);

    return false;
}

function goBack()
{
    var path = backs.pop();
    forwards.push(Arachne.form.path);

    list(path);
}

function goForward()
{
    var path = forwards.pop();
    backs.push(Arachne.form.path);

    list(path);
}

function goUp()
{
    var path = Arachne.form.path;
    var index = path.lastIndexOf("/");
    if (index > 0)
        path = path.substring(0, index);
    else
        path = "";

    selectPath(path);
}

function goPath()
{
    selectPath(this.path);
}

function download0()
{
    if (currentFile)
        download(currentFile.fileName);

    Cyan.$("actions").style.display = "none";
}

function preview0()
{
    if (currentFile)
        preview(currentFile.fileName);

    Cyan.$("actions").style.display = "none";
}

function fileMouseOver()
{
    if (currentFile && currentFile != this)
        currentFile.className = "file";

    this.className = "file file_hover";
    currentFile = this;

    if (!this.directory)
    {
        var actions = Cyan.$("actions");

        if (event.fromElement != actions && event.fromElement != this)
        {
            var fileName = this.fileName;
            var extName = Cyan.getExtName(fileName), s;
            var b = false;
            if (extName == "txt" || extName == "doc" || extName == "docx" || extName == "xls" || extName == "xlsx" ||
                    extName == "wps" || extName == "zip" || extName == "rar" || extName == "pdf")
            {
                b = true;
            }
            else if (extName == "jpg" || extName == "jpeg" || extName == "gif" || extName == "png")
            {
                var img = Cyan.$$(this).$("img")[0];
                if (img.width >= iconWidth || img.height >= iconHeight)
                    b = true;
            }

            if (b)
            {
                if (extName == "zip" || extName == "rar")
                    Cyan.$("preview").innerHTML = "打开";
                else
                    Cyan.$("preview").innerHTML = "预览";

                actions.style.display = "";

                var position = Cyan.Elements.getPosition(this);
                actions.style.left = (position.x + 75) + "px";
                actions.style.top = (position.y + 90) + "px";
            }
        }
    }
}

function fileMouseOut(event)
{
    var actions = Cyan.$("actions");
    if (!event.isOn(this))
    {
        actions.style.display = "none";
        this.className = "file";
    }
}