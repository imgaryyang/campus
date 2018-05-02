Cyan.importJs("widgets/fileupload.js");
Cyan.importJs("/platform/commons/pdf.js");

Cyan.onload(function ()
{
    setTimeout(function ()
    {
        var fileUpload = new Cyan.FileUpload("file");
        fileUpload.form = $$("form")[0];
        fileUpload.bind(System.getButton("add"));
        fileUpload.onselect = function ()
        {
            upload({
                form: fileUpload.form,
                progress: true,
                callback: reload
            })
        };
    }, 100);
});

function showImage(url)
{
    if (window.System && window.System.getTopWin() != window)
    {
        System.showImage(url);
    }
    else
    {
        var win = window;
        while (win.parent != win && win.parent.Cyan)
            win = win.parent;

        win.showImage(url);
    }
}

function showHtml(attachmentNo, extName)
{
    if (extName.toLowerCase() == "pdf")
        System.showPdf("/attachment/" + Cyan.Arachne.form.encodedId + "/" + attachmentNo);
    else
        window.open("/attachment/" + Cyan.Arachne.form.encodedId + "/" + attachmentNo + "/html");
}

function display(attachmentNo)
{
    window.currentAttachmentNo = attachmentNo;
    showImage("/attachment/" + Cyan.Arachne.form.encodedId + "/" + attachmentNo);
}

function getNextImg(callback)
{
    getNextImgNo(currentAttachmentNo, function (attachmentNo)
    {
        callback(attachmentNo == null ? null : "/attachment/" + Cyan.Arachne.form.encodedId + "/" + attachmentNo);
        if (attachmentNo)
            window.currentAttachmentNo = attachmentNo;
    });
}

function getPreImg(callback)
{
    getPreImgNo(currentAttachmentNo, function (attachmentNo)
    {
        callback(attachmentNo == null ? null : "/attachment/" + Cyan.Arachne.form.encodedId + "/" + attachmentNo);
        if (attachmentNo)
            window.currentAttachmentNo = attachmentNo;
    });
}