System.Camera.classId = "8CAA584A-AC84-445B-89D6-D4BD455EAF96";
System.Camera.id = "iVideo";
//资源对象全局变量
var video;
//设备资源标志位，1为资源对象存在，0为已释放资源对象
var ref;

window.onunload = function () {
    if (ref) {
        video.CloseDevices();
    }
}

System.Camera.initActiveX = function (callback) {
    System.Camera.idx = 0;
    setTimeout(function () {
        try {
            //也可以用video = document.iVideo;由于兼容问题
            video=document.getElementById("iVideo");
            if (video) {
                video.CloseDevices();
                ref = video.OpenDevices();
            }
        }
        catch (e) {
        }

        if (callback)
            callback();

    },2000);
};

System.Camera.start = function () {
    //调节分辨率

    if (ref) {
        video.SetVideoResolution(1);
    } else {
        video.OpenDevices();
        video.SetVideoResolution(1);
    }

};

System.Camera.stop = function () {
    try {
        if (video) {
            document.iVideo.CloseDevices();
            ref = 0;
        }
    } catch (e) {

    }
};

System.Camera.fileIndex = 1;

System.Camera.cameraToBase64 = function () {
    //var activeX = System.Camera.getActiveX();
    var result = video.GetSnap();
    //if (result) {
    //    alert("拍照成功！");
    //}
    //var i = video.SaveSnap("d:\\123.jpg", 80); //保存路径和 压缩比例,图片的格式和后缀. 自行调节
    //if (i == 1) {
    //    alert("图片保存在D盘");
    //}
    //调用GetSnapBase64前先调用GetSnap，不可以调用saveSnap()不然内存中不存在对象
    return video.GetSnapBase64();
};

System.Camera.switchCamera = function () {
    if (System.Camera.idx)
        System.Camera.idx = 0;
    else
        System.Camera.idx = 1;

    //System.Camera.getActiveX().ReSetUpDevice(System.Camera.idx);
};
