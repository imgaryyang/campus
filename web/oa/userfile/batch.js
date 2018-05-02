Cyan.onload(function ()
{
    var upload = new System.RichUpload();
    upload.autoUploadNextFile = false;
    upload.bindButton("upoladButton");
    upload.displayProgressIn("progress");

    upload.addListener({
        onselect: function (file)
        {
        },
        onsuccess: function (file, result)
        {
            var percentageDiv = $$$(file.id).$(".richupload_file_percentage");
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
        }
    });
});
