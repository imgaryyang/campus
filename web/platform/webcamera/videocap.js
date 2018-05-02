System.Camera.classId = "6CA705D0-BB6E-46DF-BE44-64809B0B0E36";
System.Camera.id = "videocap";

System.Camera.initActiveX = function (callback)
{
    System.Camera.idx = 0;
    setTimeout(function ()
    {
        try
        {
            var activeX = System.Camera.getActiveX();
            activeX.SetOcrType(0x8000);
            activeX.BuildVideo();
        }
        catch (e)
        {
        }

        if (callback)
            callback();

    }, 200);
};

System.Camera.start = function ()
{
    System.Camera.getActiveX().StartPreview();
};

System.Camera.stop = function ()
{
    System.Camera.getActiveX().StopPreview();
};

System.Camera.fileIndex = 1;

System.Camera.cameraToBase64 = function ()
{
    try
    {
        var activeX = System.Camera.getActiveX();
        var directory = "C:\\currentDir\\1\\";
        var path = directory + (System.Camera.fileIndex++) + ".jpg";
        activeX.SetCurrentPicPath(directory);
        activeX.SetJpgQuality(75);
        activeX.SetPicDpi(290);
        activeX.TakePicture(path);

        return activeX.ChangePic2Base64(path, 75);
    }
    catch (e)
    {
        alert(e.message);
        return "";
    }
};

System.Camera.switchCamera = function ()
{
    if (System.Camera.idx)
        System.Camera.idx = 0;
    else
        System.Camera.idx = 1;

    System.Camera.getActiveX().ReSetUpDevice(System.Camera.idx);
};