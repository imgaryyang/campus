System.Camera.camerabaseurl = "http://127.0.0.1:38088/?video=";
System.Camera.cameraDefaultUrl = System.Camera.camerabaseurl + "stream&camidx=0";
System.Camera.cameraUrl = System.Camera.camerabaseurl + "stream&camidx=";
System.Camera.fileIndex = 1;
System.Camera.phi=0;
System.Camera.id = "streamimage";
Cyan.importJs("/highcharts/jquery.min.js");
System.Camera.initActiveX = function (callback)
{
    System.Camera.idx = 0;
    Cyan.$(System.Camera.id).src = System.Camera.cameraUrl + System.Camera.idx;
    var activeX = System.Camera.getActiveX();
    if (callback)
        callback();
};

System.Camera.start = function ()
{
    Cyan.$(System.Camera.id).src = System.Camera.cameraUrl + System.Camera.idx;
};

System.Camera.stop = function ()
{
    Cyan.$(System.Camera.id).src = "";
};
System.Camera.cameraToBase64 = function (callback)
{
    try
    {
        var cutpage = document.getElementsByName("cutpage");
        var params = "{\"filepath\":\"base64\",\"rotate\":\""+System.Camera.phi.toString()+"\",\"camidx\":\"" + System.Camera.idx.toString() +
                "\",\"cutpage\":\"1\"}";//
        var url = System.Camera.camerabaseurl + "grabimage";
        var ajax = new Cyan.Ajax();
        ajax.method = "POST";
        ajax.setRequestHeader("Accept", "text/json");
        ajax.handleObject = function (data)
        {
            if (data.code == "0")
            {
                if(callback){
                    callback(data.photoBase64);
                }
            }else{
                allback("");
            }
        }
        ajax.call(url, params);
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

    Cyan.$(System.Camera.id).src = System.Camera.cameraUrl + System.Camera.idx;
};