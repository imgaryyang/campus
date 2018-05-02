System.Scanner.classId = "25540CEC-FCB7-42B4-B636-3533F2ED7868";
System.Scanner.id = "ScanSDK_OCX";

System.Scanner.initActiveX = function ()
{
    var activeX = System.Scanner.getActiveX();
    activeX.ScanImageFormat = 1;
    activeX.ScanDPI = 200;
};

System.Scanner.setFormat = function (format)
{
    this.format = format;
    var activeX = System.Scanner.getActiveX();
    if (!format || format == "jpg")
        activeX.ScanImageFormat = 1;
    else if (format == "bmp")
        activeX.ScanImageFormat = 0;
};

System.Scanner.setDpi = function (width, height)
{
    System.Scanner.getActiveX().ScanDPI = width;
};

System.Scanner.scan = function ()
{
    try
    {
        var activeX = System.Scanner.getActiveX();
        activeX.StartSN = activeX.StartSN + activeX.ScanCount;
        activeX.ClearScanFiles(0);
        activeX.Scan();
        return true;
    }
    catch (e)
    {
        return false;
    }
};

System.Scanner.getImages = function ()
{
    var activeX = System.Scanner.getActiveX();
    if (activeX.ScanCount)
    {
        var images = [];
        var count = activeX.ScanCount;
        for (var i = 0; i < count; i++)
        {
            images.push(new System.Scanner.Image(activeX.GetScanFileName(i)));
        }

        return images;
    }
    else
    {
        return null;
    }
};

System.Scanner.Image = function (fileName)
{
    this.fileName = fileName
};
System.Scanner.Image.prototype.getBase64 = function ()
{
    return System.Scanner.getActiveX().GetFileBase64(this.fileName);

};
System.Scanner.Image.prototype.getPath = function ()
{
    return this.fileName;
};