System.Camera.activeXInited = true;

(function ()
{
    var test = function ()
    {
        var classId = "49CBC347-34CD-4687-9D5C-C45E3D3314F0";
        var id = "captureOcx";

        var s = "<div class='camera' style='display: none'><OBJECT style='left:0;top:0;height:100%;width:100%;display:none '" +
                " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> " + " </div>";

        if (document.body)
        {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);
        }

        try
        {
            Cyan.$(id).Initialize();
            var n = Cyan.$(id).GetDeviceCount();
            for (var i = 0; i < n; i++)
            {
                if (Cyan.$(id).GetDeviceIDName(i) != '')
                {
                    return true;
                }
            }

            return false;

        }
        catch (e)
        {
            return false;
        }
    };

    var testG350 = function ()
    {
        var classId = "3CA842C5-9B56-4329-A7CA-35CA77C7128D";
        var id = "captureOcx350";

        var s = "<div class='camera' style='display: none'><OBJECT style='left:0;top:0;height:100%;width:100%;display:none '" +
                " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> " + " </div>";

        if (document.body)
        {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);
        }

        try
        {
            var n = Cyan.$(id).Initial();
            if (n >= 0)
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (e)
        {
            return false;
        }
    };

    var testICapturevideo = function ()
    {
        var classId = "9A73DB73-2CA3-478D-9A3F-7E9D6A8D327C";
        var id = "capturevideo";

        var s = "<div class='camera' style='display: none'><OBJECT style='left:0;top:0;height:100%;width:100%;display:none '" +
                " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> " + " </div>";

        if (document.body)
        {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);
        }

        try
        {
            var n = Cyan.$(id).OpenDevice(0);
            if (n == 0)
            {
                Cyan.$(id).CloseDeviceEx();
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (e)
        {
            return false;
        }
    };

    var test1 = function ()
    {
        var classId = "52D1E686-D8D7-4DF2-9A74-8B8F4650BF73";
        var id = "EloamGlobal_ID";

        var s = "<div class='camera' style='display: none'><OBJECT style='height:100%;width:100%'  " +
                " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> </div>";

        if (document.body)
        {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);


        }

        try
        {
            document.getElementById("EloamGlobal_ID").DeinitDevs();
            document.getElementById("EloamGlobal_ID").InitDevs();
            //获取视频个数
            var count = document.getElementById("EloamGlobal_ID").GetDevCount(1);


            if (count)
            {
                return true;
            }
        }
        catch (e)
        {

            return false;
        }

    };

    var test2 = function ()
    {
        var classId = "8CAA584A-AC84-445B-89D6-D4BD455EAF96";
        var id = "iVideo";

        var s = "<div class='camera' style='display: none'><OBJECT style='left:0;top:0;height:100%;width:100%;display:none '" +
                " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> " + " </div>";

        if (document.body)
        {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);
        }

        try
        {
            var result = document.getElementById("iVideo").OpenDevices();
            if (result)
            {
                return true;
            }
        }
        catch (e)
        {
            return false;
        }
    };

    Cyan.onload(function ()
    {
        setTimeout(function ()
        {
            if (Cyan.navigator.isFF()){
                //核高基版（哲林）
                Cyan.importJs("/platform/webcamera/cameraservice.js");
            }else if (test1())
            {
                //连山德生高拍仪（开发：良田）
                Cyan.importJs("/platform/webcamera/eloamglobal.js");
            }
            else if (test2())
            {
                //云安高拍
                Cyan.importJs("/platform/webcamera/ivideo.js");
            }
            else if (testG350())
            {
                Cyan.importJs("/platform/webcamera/capture350.js");

            }
            else if (test())
            {
                Cyan.importJs("/platform/webcamera/capture.js");
            }
            else if (testICapturevideo())
            {
                Cyan.importJs("/platform/webcamera/icapturevideo.js");
            }
            else
            {
                Cyan.importJs("/platform/webcamera/videocap.js");
            }
        }, 1000);
    });

})();