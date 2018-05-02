Cyan.onload(function ()
{
    Cyan.$("ETScanFine").HostUrl = location.protocol + "//" + location.hostname +
            ( location.port ? ( ":" + location.port) : "") + "/";

    if (Cyan.Arachne.form.attachmentId)
    {
        refresh();
    }
});

function upload()
{
    var scanFine = Cyan.$("ETScanFine");
    if (Cyan.Arachne.form.attachmentId)
    {
        scanFine.updatePack(Cyan.Arachne.form.attachmentId);
    }
    else
    {
        scanFine.AddPack('1');
        alert(scanFine.PackID);
        Cyan.Arachne.form.attachmentId = scanFine.PackID;
    }

    refresh();
}

function refresh()
{
    Cyan.Arachne.get("/attachments/" + Cyan.Arachne.form.attachmentId, null, function (attachments)
    {
        var div = Cyan.$("images");
        div.innerHTML = "";
        for (var i = 0; i < attachments.length; i++)
        {
            var attachment = attachments[i];
            var img = document.createElement("IMG");
            img.src = "/attachment/" + attachment.attachmentId + "/" + attachment.attachmentNo + "/thumb";

            var href = document.createElement("A");
            href.appendChild(img);
            href.target = "_blank";
            href.href = "/attachment/" + attachment.attachmentId + "/" + attachment.attachmentNo;
            div.appendChild(href);
        }
    });
}
