System.RichUpload.prototype.create = function ()
{
    if (!this.input)
    {
        var input = document.createElement("INPUT");
        input.name = this.name;
        input.type = "file";
        input.multiple = true;
        input.style.filter = "alpha(opacity=0)";
        input.style.opacity = "0";
        input.style.position = "absolute";
        input.style.cursor = "pointer";
        input.style.top = -100 + "px";
        input.style.left = -100 + "px";
        input.style.height = "20px";
        input.style.width = "40px";
        input.style.zIndex = 10005;

        if (this.accept)
            input.accept = this.accept;

        document.body.appendChild(input);
        this.input = input;

        var richupload = this;
        Cyan.attach(input, "change", function ()
        {
            richupload.fileInfos = [];
            var files = this.files;
            for (var i = 0; i < files.length; i++)
            {
                var file = files[i];
                richupload.onselect(richupload.fileInfos[i] = {
                    name: file.name,
                    size: file.size,
                    id: Cyan.generateId("richupload_file")
                });
            }

            richupload.index = -1;
            richupload.onselectcomplete();
        });

        this.onAjaxLoad = function ()
        {
            var result = eval(this.responseText);
            var file = richupload.fileInfos[richupload.index];
            if (result instanceof Error)
                richupload.onerror(file, result.message);
            else
                richupload.onsuccess(file, result);
            richupload.oncomplete(file);
        };
        this.onAjaxError = function ()
        {
            var file = richupload.fileInfos[richupload.index];
            richupload.onerror(file);
            richupload.oncomplete(file);
        };
        this.onAjaxProgress = function (event)
        {
            if (event.lengthComputable)
            {
                var file = richupload.fileInfos[richupload.index];
                richupload.onprogress(file, event.loaded, event.total);
            }
        };
    }
};

System.RichUpload.prototype.startUpload = function (fileId)
{
    var files = this.input.files;
    if (this.index >= -1 && this.index < files.length - 1)
    {
        this.index++;
        this.onstart();

        var formData = new FormData();
        formData.append(this.name, files[this.index]);

        if (!this.xmlHttpRequest)
        {
            this.xmlHttpRequest = new XMLHttpRequest();

            this.xmlHttpRequest.addEventListener("load", this.onAjaxLoad, false);
            this.xmlHttpRequest.addEventListener("error", this.onAjaxError, false);
            this.xmlHttpRequest.upload.addEventListener("progress", this.onAjaxProgress, false);
        }

        this.xmlHttpRequest.open("POST", this.url, true);
        this.xmlHttpRequest.setRequestHeader("Accept", "application/json");
        this.xmlHttpRequest.send(formData);
    }
};

System.RichUpload.prototype.cancelUpload = function (fileId)
{
    if (this.swfupload)
        this.swfupload.cancelUpload(fileId);
};

System.RichUpload.prototype.stopUpload = function ()
{
    if (this.xmlHttpRequest)
    {
        this.xmlHttpRequest.abort();
        this.fileInfos = [];
        this.index = -2;
    }

    this.onstop();
};

System.RichUpload.prototype.makePosition = function (event)
{
    var input = this.input;
    input.style.left = (event.pageX - input.clientWidth / 2 - 12) + "px";
    input.style.top = (event.pageY - 4) + "px";
};

System.RichUpload.prototype.hide = function ()
{
    this.input.style.left = "-100px";
    this.input.style.top = "-100px";
};