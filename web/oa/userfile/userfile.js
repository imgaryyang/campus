Cyan.importJs("/platform/fileupload/richupload.js");
Cyan.importJs("/platform/commons/pdf.js");

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        var batch = System.getButton("batch");
        if (batch)
        {
            var upload = new System.RichUpload();
            upload.autoUploadNextFile = false;
            upload.bindButton(batch);
            upload.displayProgressIn();

            upload.addListener({
                onselect: function (file)
                {
                },
                onsuccess: function (file, result)
                {
                    var percentageDiv = Cyan.$$$(file.id).$(".richupload_file_percentage");
                    percentageDiv.html("正在保存文件");
                    window.upload([result], {
                        callback: function ()
                        {
                            percentageDiv.html("完成");
                            upload.uploadNextFile();
                        },
                        error: function (error)
                        {
                            percentageDiv.html(error.message);
                        }
                    });
                },
                oncomplete: function (file)
                {
                },
                onstop: function ()
                {
                    refresh();
                },
                onok: function ()
                {
                    refresh();
                }
            });
        }
    }, 100);

    Cyan.$$("#file").attach("change", function ()
    {
        Cyan.$("entity.fileName").value = Cyan.getFileName(this.value);
    });

    if (window.getRemark)
    {
        window.afterSave = window.afterDelete = function ()
        {
            refreshRemark();
            refresh();
        };

        System.reload = function ()
        {
            refreshRemark();
            mainBody.reload();
            if (window.left)
                left.reload();
        };

        window.afterCopy = refreshRemark;
    }

    window.zipFolder = function ()
    {
        var folderId = Arachne.form.folderId;
        if (!(folderId > 0))
            Cyan.message("请选择目录");
        else
            window.open("/oa/userfile/folder/" + folderId + "/zip");
    };
});

function refreshRemark()
{
    getRemark(function (ret)
    {
        System.setRemark(ret);
    });
}

function batchUp()
{
    batchUpload({target: "_page"});
}

function display(fileId, editFile, ext)
{
    window.currentFileId = fileId;
    if (editFile)
    {
        window.open("/oa/userfile/" + fileId + "/show");
    }
    else if (ext)
    {
        ext = ext.toLowerCase();
        if (ext == "txt" || ext == "doc" || ext == "xls" || ext == "docx" || ext == "xlsx" || ext == "csv" ||
                ext == "wps" || ext == "zip" || ext == "rar")
        {
            window.open("/oa/userfile/" + fileId + "/html");
        }
        else if (ext == "jpg" || ext == "jpeg" || ext == "gif" || ext == "bmp" || ext == "png")
        {
            System.showImage(Cyan.formatUrl("/oa/userfile/" + fileId + "/down"));
        }
        else if (ext == "pdf")
        {
            System.showPdf("/oa/userfile/" + fileId + "/down");
        }
    }
}

function getNextImg(callback)
{
    getNextImgFile(window.currentFileId, function (fileId)
    {
        callback(fileId == null ? null : Cyan.formatUrl("/oa/userfile/" + fileId + "/down"));
        if (fileId)
            window.currentFileId = fileId;
    });
}

function getPreImg(callback)
{
    getPreImgFile(window.currentFileId, function (fileId)
    {
        callback(fileId == null ? null : Cyan.formatUrl("/oa/userfile/" + fileId + "/down"));
        if (fileId)
            window.currentFileId = fileId;
    });
}

function showBaks(fileId)
{
    System.showModal("/oa/userfile/file/" + fileId + "/baks");
}