if (!Cyan.$("ETScanFine"))
{
    document.writeln("<div style='left:0;top:0;height:0;width:0'><object style='left:0;top:0;height:0;width:0'" +
    " classid='clsid:6AAFF9FB-EDFA-4C7F-9F4D-CE7DDCC69682' id='ETScanFine' codebase='/platform/webcamera/ETScanFine.ocx'></object></div>");

    var scanFine = Cyan.$("ETScanFine");
    scanFine.HostUrl = location.protocol + "//" + location.hostname +
    ( location.port ? ( ":" + location.port) : "") + "/";

    scanFine.DocFormID = "R00014024A";
    scanFine.DocFormTitle = "拍照";
    scanFine.UserID = "106020";
    scanFine.DocFormType = "";
    scanFine.RefNo = "RefNo";
    scanFine.LicenseNo = "077A617E4D32AA22";
    scanFine.acceptNo = Cyan.Arachne.form.dealNumber ? Cyan.Arachne.form.dealNumber : "";
    scanFine.checkName = Cyan.Arachne.form.nodeId ? Cyan.Arachne.form.nodeId : "";

    Cyan.Class.overwrite(System, "showImage", function (url, win, attachmentId)
    {
        if (attachmentId)
        {
            scanFine.viewPack("$" + attachmentId.toString() + "$" + Math.random().toString().substr(2));
        }
        else
        {
            this.inherited(url, win);
        }
    });
}

System.Scanner = {
    scanAndStore: function (format, callback, attachmentId)
    {
        var scanFine = Cyan.$("ETScanFine");

        if (attachmentId)
            scanFine.updatePack("$" + attachmentId.toString() + "$" + Math.random().toString().substr(2));
        else
            scanFine.addPack("1");

        if (callback && !attachmentId)
        {
            var packId = scanFine.PackID;
            if (packId)
            {
                Cyan.Arachne.get("/ImageHostServlet/files?packId=" + packId, null, function (files)
                {
                    if (files)
                    {
                        callback(Cyan.get(files, function ()
                        {
                            return {id: this.toString(), path: "/richupload/thumb?path=" + this};
                        }));
                    }
                });
            }
        }
    }
};