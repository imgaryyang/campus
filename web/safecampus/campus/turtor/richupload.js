Cyan.importJs("widgets/progressbar.js");
Cyan.importCss("/platform/fileupload/richupload.css");

if (window.FormData)
    Cyan.importJs("/platform/fileupload/richupload_html5.js");
else
    Cyan.importJs("/platform/fileupload/richupload_swf.js");

if (!window.System) {
    window.System = {};
}

System.RichUpload = function (url, name) {
    if (!url)
        url = "/richupload/upload";

    var csrf_token = Cyan.getCookie("csrf_token");
    if (csrf_token) {
        url += (url.indexOf("?") >= 0 ? "&" : "?") + "csrf_token=" + csrf_token;
    }

    if (!name)
        name = "file";

    this.url = url;
    this.name = name;
    this.listeners = [];
};

System.RichUpload.prototype.autoUploadNextFile = true;

System.RichUpload.prototype.addListener = function (listener) {
    this.listeners.push(listener);
};

System.RichUpload.prototype.onselect = function (file) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onselect)
            this.listeners[i].onselect(file);
    }
};

System.RichUpload.prototype.onselectcomplete = function () {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onselectcomplete)
            this.listeners[i].onselectcomplete();
    }
};

System.RichUpload.prototype.onstart = function (file) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onstart)
            this.listeners[i].onstart(file);
    }
};

System.RichUpload.prototype.onprogress = function (file, completed, total) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onprogress)
            this.listeners[i].onprogress(file, completed, total);
    }
};

System.RichUpload.prototype.onsuccess = function (file, result) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onsuccess)
            this.listeners[i].onsuccess(file, result);
    }
};

System.RichUpload.prototype.onerror = function (file, result) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onerror)
            this.listeners[i].onerror(file, result);
    }
};

System.RichUpload.prototype.oncomplete = function (file) {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].oncomplete)
            this.listeners[i].oncomplete(file);
    }
};

System.RichUpload.prototype.onstop = function () {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onstop)
            this.listeners[i].onstop();
    }
};

System.RichUpload.prototype.onok = function () {
    for (var i = 0; i < this.listeners.length; i++) {
        if (this.listeners[i].onok)
            this.listeners[i].onok();
    }
};

System.RichUpload.prototype.bindButton = function (button) {
    button = Cyan.$(button);

    this.enable(button);
    var upload = this;

    Cyan.attach(document.body, "mousemove", function (event) {
        if (!button.disabled && event.isOn(button)) {
            upload.button = button;
            upload.makePosition(event);
        }
        else if (upload.button == button) {
            upload.hide();
        }
    });
};

System.RichUpload.prototype.enable = function (button) {
    if (button)
        this.button = button;

    this.create();
    this.button.disabled = false;
};

System.RichUpload.prototype.disable = function () {
    if (this.div) {
        if (this.button)
            this.button.disabled = true;
    }
};

System.RichUpload.prototype.displayProgressIn = function (div) {
    var richupload = this;
    div = Cyan.$(div);

    var mask, container, stopButton, okButton;
    if (!div) {
        var bodySize = Cyan.getBodySize();

        mask = document.createElement("DIV");
        mask.style.position = "absolute";
        mask.style.left = "0";
        mask.style.top = "0";
        mask.style.width = bodySize.width + "px";
        mask.style.height = bodySize.height + "px";
        mask.style.zIndex = 10000;
        mask.style.background = "#ffffff";
        mask.style.filter = "alpha(opacity=0)";
        mask.style.opacity = "0";
        mask.style.display = "none";
        document.body.appendChild(mask);

        container = document.createElement("DIV");
        container.style.position = "absolute";
        container.className = "richupload_container";
        container.style.zIndex = 10001;
        container.style.display = "none";
        document.body.appendChild(container);

        div = document.createElement("DIV");
        div.className = "richupload_display";
        container.appendChild(div);

        var bottom = document.createElement("DIV");
        bottom.className = "richupload_bottom";
        container.appendChild(bottom);

        stopButton = Cyan.Elements.createButton("停止");
        stopButton.onclick = function () {
            richupload.stopUpload();

            fileDivs = {};

            filesDiv.innerHTML = "";
            mask.style.display = "none";
            container.style.display = "none";
        };
        bottom.appendChild(stopButton);

        okButton = Cyan.Elements.createButton("确定");
        okButton.onclick = function () {
            fileDivs = {};
            totalSize = 0;
            uploadedSize = 0;
            fileCount = 0;
            fileIndex = 0;
            lastCompleted = 0;

            filesDiv.innerHTML = "";
            mask.style.display = "none";
            container.style.display = "none";

            richupload.onok();
        };
        bottom.appendChild(okButton);
    }


    var uploadDiv = document.createElement("DIV");
    div.appendChild(uploadDiv);
    uploadDiv.className = "richupload";

    var filesDiv = document.createElement("DIV");
    uploadDiv.appendChild(filesDiv);
    filesDiv.className = "richupload_files";

    var barDiv = document.createElement("DIV");
    uploadDiv.appendChild(barDiv);
    barDiv.className = "richupload_progressbar";

    var fileDivs = {};
    var totalSize = 0;
    var uploadedSize = 0;
    var fileCount = 0;
    var fileIndex = 0;
    var lastCompleted = 0;

    richupload.uploadNextFile = function () {
        if (fileIndex == fileCount) {
            richupload.progressBar.update(1, "完成");
            richupload.enable();

            totalSize = 0;
            uploadedSize = 0;
            fileCount = 0;
            fileIndex = 0;
            lastCompleted = 0;

            okButton.style.display = "";
            stopButton.style.display = "none";
        }
        else {
            richupload.startUpload();
        }
    };

    this.addListener({
        onselect: function (file) {
            if (mask) {
                if (container.style.display == "none") {


                    //隐藏mask

                    // mask.style.display = "";
                    // container.style.display = "";

                    Cyan.Elements.center(container);
                    okButton.style.display = "none";
                    stopButton.style.display = "";

                    if (richupload.progressBar)
                        richupload.progressBar.update(0, "总进度0%");
                }
            }

            var fileDiv = document.createElement("DIV");
            fileDiv.id = file.id;
            fileDiv.className = "richupload_file";
            filesDiv.appendChild(fileDiv);

            var fileNameDiv = document.createElement("DIV");
            fileNameDiv.className = "richupload_filename";
            fileDiv.appendChild(fileNameDiv);

            var percentageDiv = document.createElement("DIV");
            percentageDiv.className = "richupload_file_percentage";
            fileDiv.appendChild(percentageDiv);

            fileNameDiv.appendChild(document.createTextNode(file.name));

            fileDivs[file.id] = fileDiv;

            totalSize += file.size;
            fileCount++;
        },

        onselectcomplete: function () {
            richupload.disable();
            richupload.startUpload();

            if (!richupload.progressBar) {
                richupload.progressBar = new Cyan.ProgressBar();
                richupload.progressBar.init(barDiv);
                richupload.progressBar.update(0, "总进度0%");
            }
        },

        onstart: function (file) {
            fileIndex++;
            lastCompleted = 0;
        },

        onprogress: function (file, completed, total) {
            var fileDiv = fileDivs[file.id];
            var percentageDiv = Cyan.$$(fileDiv).$(".richupload_file_percentage");
            percentageDiv.html(Math.round((completed / total) * 100) + "%");

            uploadedSize += completed - lastCompleted;
            lastCompleted = completed;

            var p = uploadedSize / totalSize;
            console.log(file)

            richupload.progressBar.update(p, "总进度" + Math.round(p * 100) + "%&nbsp;&nbsp;正在上传第" + fileIndex + "个文件/共" +
                fileCount + "个");
        },

        onsuccess: function (file, result) {
            var fileDiv = fileDivs[file.id];
            var percentageDiv = Cyan.$$(fileDiv).$(".richupload_file_percentage");
            percentageDiv.html("result");

            //保存及回显
            $("#img").attr("src", "/richupload?path=" + result)

            $("#filePath").val(result)

            // $.get("/sc/stuPhoto/upload?filePath=" + result, function (d) {
            //     $("#img").attr("src","/richupload?path="+result)
            // })
        },
        onerror: function (file, result) {
            var fileDiv = fileDivs[file.id];
            var percentageDiv = Cyan.$$(fileDiv).$(".richupload_file_percentage");

            if (!result)
                result = "上传失败";
            percentageDiv.html(result);

            alert(result)
        },
        oncomplete: function (file) {
            if (richupload.autoUploadNextFile)
                richupload.uploadNextFile();

        },
        onstop: function () {
            totalSize = 0;
            uploadedSize = 0;
            fileCount = 0;
            fileIndex = 0;
            lastCompleted = 0;

            richupload.enable();
        }
    });
};



