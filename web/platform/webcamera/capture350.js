System.Camera.classId = "3CA842C5-9B56-4329-A7CA-35CA77C7128D";
System.Camera.id = "captureOcx350";

System.Camera.initActiveX = function (callback)
{
    if (!System.Camera.activeXInited)
    {
        try
        {
            System.Camera.getActiveX().Initial();
        }
        catch (e)
        {
        }
    }

    if (callback)
    {
        setTimeout(callback, 600);
    }
};

System.Camera.start = function ()
{
    var deviceIndex
    try
    {
        deviceIndex = System.Camera.getActiveX().Initial();
    }
    catch (e)
    {
    }
    System.Camera.getActiveX().StartRun(deviceIndex);
};

System.Camera.stop = function ()
{
    try
    {
        System.Camera.getActiveX().Stop();
    }
    catch (e)
    {
    }
};

System.Camera.cameraToBase64 = function ()
{
    try
    {
        return System.Camera.getActiveX().CaptureToBase64();
    }
    catch (e)
    {
        return "";
    }
};