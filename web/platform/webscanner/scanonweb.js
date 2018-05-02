System.Scanner.classId = "15D142CD-E529-4B01-9D62-22C9A6C00E9B";
System.Scanner.codebase = "/platform/webscanner/ScanOnWeb.cab#version=1,0,0,10";
System.Scanner.id = "webscaner";

System.Scanner.initActiveX = function ()
{
};

System.Scanner.setFormat = function (format)
{
    this.format = format;
};

System.Scanner.setDpi = function (width, height)
{
    System.Scanner.getActiveX().setDpi(width, height);
};

System.Scanner.scan = function ()
{
    try
    {
        System.Scanner.getActiveX().scan();
        return true;
    }
    catch (e)
    {
        return false;
    }
};

System.Scanner.saveToFile = function (path)
{
    System.Scanner.getActiveX().saveToFile(path);
};

System.Scanner.getImages = function ()
{
    var format = this.format;
    var base64;
    if (!format || format == "jpg")
        base64 = System.Scanner.getActiveX().jpegBase64Data();
    else if (format == "bmp")
        base64 = System.Scanner.getActiveX().bmpBase64Data();

    if (!base64)
    {
        alert("扫描错误");
        return null;
    }

    return [{
        getBase64: function ()
        {
            return base64;
        }
    }]
};