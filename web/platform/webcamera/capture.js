System.Camera.classId = "49CBC347-34CD-4687-9D5C-C45E3D3314F0";
System.Camera.id = "captureOcx";

System.Camera.initActiveX = function (callback)
{
    if (!System.Camera.activeXInited)
    {
        try
        {
            System.Camera.getActiveX().Initialize();
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
    try
    {
        System.Camera.getActiveX().Initialize();
    }
    catch (e)
    {
    }
    System.Camera.getActiveX().Run()
};

System.Camera.stop = function ()
{
    System.Camera.getActiveX().Stop();
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