Cyan.importJs("/swfupload/swfupload.js");

System.RichUpload.prototype.create = function ()
{
    if (!this.div)
    {
        this.div = document.createElement("DIV");

        var id = "swfupload" + Math.random().toString().substring(2);

        this.div.style.position = "absolute";
        this.div.style.overflow = "hidden";
        this.div.style.filter = "alpha(opacity=0)";
        this.div.style.opacity = "0";

        document.body.appendChild(this.div);

        var swfdiv = document.createElement("DIV");
        swfdiv.style.left = 50 + "px";
        swfdiv.style.width = 50 + "px";
        swfdiv.id = id;
        this.disable();
        this.div.appendChild(swfdiv);

        var richupload = this;

        this.swfupload = new SWFUpload({
            upload_url: this.url + (this.url.indexOf("?") > 0 ? "&" : "?") + "$Accept$=application/json&$ajax$=true",
            flash_url: "/swfupload/swfupload.swf",
            button_placeholder_id: id,
            button_width: 50,
            button_height: 20,
            button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
            file_post_name: this.name,
            file_queued_handler: function (file)
            {
                if (richupload.onselect)
                    richupload.onselect(file);
            },
            file_dialog_complete_handler: function (count)
            {
                if (count > 0 && richupload.onselectcomplete)
                    richupload.onselectcomplete();
            },
            upload_start_handler: function (file)
            {
                if (richupload.onstart)
                    richupload.onstart(file);
            },
            upload_progress_handler: function (file, completed, total)
            {
                if (richupload.onprogress)
                    richupload.onprogress(file, completed, total);
            },
            upload_success_handler: function (file, result)
            {
                result = eval(result);
                if (result instanceof Error)
                {
                    if (richupload.onerror)
                        richupload.onerror(file, result.message);
                }
                else
                {
                    if (richupload.onsuccess)
                        richupload.onsuccess(file, result);
                }
            },
            upload_error_handler: function (file, result)
            {
                if (richupload.onerror)
                    richupload.onerror(file);
            },
            upload_complete_handler: function (file)
            {
                if (richupload.oncomplete)
                    richupload.oncomplete(file);
            }
        });
    }
};

System.RichUpload.prototype.startUpload = function ()
{
    if (this.swfupload)
        this.swfupload.startUpload();
};

System.RichUpload.prototype.stopUpload = function ()
{
    if (this.swfupload)
    {
        while (true)
        {
            var stats = this.swfupload.getStats();
            if (stats.in_progress || stats.files_queued)
                this.swfupload.cancelUpload();
            else
                break;
        }

        this.onstop();
    }
};

System.RichUpload.prototype.makePosition = function ()
{
    var size = Cyan.Elements.getComponentSize(this.button);
    var position = Cyan.Elements.getPosition(this.button);

    this.div.style.left = position.x + "px";
    this.div.style.top = position.y + "px";
    this.div.style.width = (size.width + 4) + "px";
    this.div.style.height = (size.height + 4) + "px";
};

System.RichUpload.prototype.hide = function ()
{
    this.div.style.left = "-100px";
    this.div.style.top = "-100px";
    this.div.style.width = "1px";
    this.div.style.height = "1px";
};