Cyan.importJs("event.js");

Cyan.FileUpload = function (name, multiple)
{
    this.name = name;
    this.multiple = !!multiple;
};

Cyan.FileUpload.prototype.bind = function (component)
{
    var fileupload = this;
    Cyan.attach(document.body, "mousemove", function (event)
    {
        if (event.isOn(component))
        {
            fileupload.bindComponent = component;

            if (component.form)
            {
                fileupload.form = component.form;
            }
            else
            {
                var component1 = component;
                while (component1.nodeName != "FORM" && component1.nodeName != "BODY" && component1.parentNode)
                    component1 = component1.parentNode;

                if (component1.nodeName == "FORM")
                    fileupload.form = component1;
            }

            if (!fileupload.currentFile)
            {
                fileupload.currentFile = fileupload.createFileUpload();
                if (fileupload.form)
                    fileupload.form.appendChild(fileupload.currentFile);
                else
                    document.body.appendChild(fileupload.currentFile);
            }

            Cyan.Elements.setPosition(fileupload.currentFile, event.pageX - fileupload.currentFile.clientWidth / 2 - 12,
                    event.pageY - 4);
        }
        else if (fileupload.bindComponent == component)
        {
            fileupload.bindComponent = null;
            if (fileupload.currentFile)
            {
                fileupload.currentFile.style.top = "-100px";
                fileupload.currentFile.style.left = "-100px";
            }
        }
    });

    return this;
};

Cyan.FileUpload.prototype.createFileUpload = function ()
{
    var file = document.createElement("INPUT");
    file.type = "file";
    file.name = this.name;
    file.id = this.name;
    file.style.filter = "alpha(opacity=0)";
    file.style.opacity = "0";
    file.style.position = "absolute";
    file.style.cursor = "pointer";
    file.style.top = "-100px";
    file.style.left = "-100px";
    file.style.height = "20px";
    file.style.width = "40px";
    file.style.zIndex = 10000;
    file.onchange = this.getOnchange();

    if (this.accept)
        file.accept = this.accept;

    return file;
};

Cyan.FileUpload.prototype.getOnchange = function ()
{
    if (this.onchange == null)
    {
        var fileupload = this;
        this.onchange = function ()
        {
            if (this.value)
            {
                if (fileupload.onselect)
                    fileupload.onselect(this);

                if (fileupload.multiple)
                {
                    fileupload.currentFile.style.left = "-200px";
                    fileupload.currentFile.style.top = "-200px";
                    fileupload.currentFile = null;
                }
            }
        };
    }

    return this.onchange;
};

Cyan.FileUpload.prototype.reset = function ()
{
    if (this.currentFile != null)
    {
        this.currentFile.parentNode.removeChild(this.currentFile);
        this.currentFile = null;
    }
};