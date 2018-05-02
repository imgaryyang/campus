System.Camera.classId = "9A73DB73-2CA3-478D-9A3F-7E9D6A8D327C";
System.Camera.id = "capturevideo";
System.Camera.codebase="CaptureVideo.cab";

System.Camera.initActiveX = function (callback)
{
    System.Camera.idx = 0;
    setTimeout(function ()
    {
        try
        {
            var activeX = System.Camera.getActiveX();
            activeX.SetColorMode(0);//0彩色，1黑白
            activeX.SetDeviceRotate(0);//旋转角度，0 恢复默认
            activeX.SetGrabbedDPIEx(300);//DPI
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
    var activeX = System.Camera.getActiveX();
    var value = activeX.OpenDevice(System.Camera.idx);
    if (value == 0)
    {
        activeX.SetCameraExposure(System.Camera.idx, 10);
    }
    else
    {
        setTimeout(function ()
        {
            System.Camera.start();
        }, 200);
    }


};

System.Camera.stop = function ()
{
    System.Camera.getActiveX().CloseDeviceEx();
};

System.Camera.fileIndex = 1;

System.Camera.cameraToBase64 = function ()
{
    try
    {
        var activeX = System.Camera.getActiveX();
        activeX.SetJPGQuality(75);//取值在 0~100 之间
        return activeX.GetBase64String();
    }
    catch (e)
    {
        alert(e.message);
        return "";
    }
};

System.Camera.setRotate = function (value)
{
    if (value == 0)
    {
        System.Camera.getActiveX().SetDeviceRotate(System.Camera.idx, 180);
    }
    else if (value == 1)
    {
        System.Camera.getActiveX().SetDeviceRotate(System.Camera.idx, 90);
    }
    else
    {
        System.Camera.getActiveX().SetDeviceRotate(System.Camera.idx, 270);
    }

}

System.Camera.switchCamera = function ()
{
    if (System.Camera.idx)
        System.Camera.idx = 0;
    else
        System.Camera.idx = 1;

    System.Camera.getActiveX().OpenDevice(System.Camera.idx);
};