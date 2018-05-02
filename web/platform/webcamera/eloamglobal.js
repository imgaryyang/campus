//System.Camera.classId = "26BA9E7F-78E9-4FB8-A05C-A4185D80D759";

System.Camera.classId = "26BA9E7F-78E9-4FB8-A05C-A4185D80D759";
System.Camera.id = "EloamView";
//调节分辨率 
System.Camera.videosize =5;

var EloamVideo;
var EloamGlobal;
var EloamDevice;
window.onunload = function () {
    CloseVideo();
    if (EloamDevice) {
        EloamDevice.Destroy();
        EloamDevice = null;
    }

    EloamGlobal.DeinitDevs();
    EloamGlobal = null;
}

System.Camera.initActiveX = function (callback) {
    System.Camera.idx = 0;
    setTimeout(function () {
        try {
            EloamGlobal = document.getElementById("EloamGlobal_ID");
            EloamGlobal.DeinitDevs();
            EloamGlobal.InitDevs();
            //alert(ret);
        }
        catch (e) {
            $.message("初始化失败！");
        }

        if (callback)
            callback();

    }, 500);
};


System.Camera.start = function () {
    ShowVideo()
};


System.Camera.stop = function () {
    CloseVideo();
};

System.Camera.fileIndex = 1;

System.Camera.cameraToBase64 = function () {
    try {
        if (EloamVideo) {
            //保存图片
            var image = null;
            image = EloamVideo.CreateImage(0, EloamView.GetView());
            if (image) {
                EloamView.PlayCaptureEffect();
                //保存图片到电脑

                var base64 = image.GetBase64(13, 0x0800);
                image.Destroy();
                image = null;
                alert("拍照成功！");
                return base64;
            }
            return false;
        }
    }
    catch (e) {
        alert(e.message);
        return "";
    }
};

System.Camera.switchCamera = function () {
    CloseVideo();
    if (System.Camera.idx) {
        System.Camera.idx = 0;
        System.Camera.videosize = 5;
    }
    else {
        System.Camera.idx = 1;
        System.Camera.videosize = 0;
    }
    ShowVideo();
}
;

function CloseVideo() {
    if (EloamVideo) {
        EloamView.SetText("", 0);
        EloamVideo.Destroy();
        EloamVideo = null;
    }
}

function ShowVideo() {

    //CreateDevice（type，idx）：获取设备 type:1：视频 2：音频  idx:0:向下摄像头，1：正面摄像头
    EloamDevice = EloamGlobal.CreateDevice(1, System.Camera.idx);

    //获取设备分辨率数组
    //var nResolution = System.Camera.height * System.Camera.width;
    if(System.Camera.idx==0){
        var EloamVideoText = EloamDevice.CreateVideo(7, 4);
         EloamVideoText.Destroy();
    }

    //创建视频对象
    // resolution 分辨率索引
    // subtype子类型，1 表示YUY2 ，2 表示MJPG ，4表示UYVY, 0表示自动选择一个子类型
    // 返回视频对象
    EloamVideo = EloamDevice.CreateVideo(System.Camera.videosize, 4);
    if (EloamVideo) {
        EloamView.SelectVideo(EloamVideo);
        EloamView.SetText("打开视频中，请等待...", 0);
    }
}

function Unload() {
    CloseVideo();
    if (EloamDevice) {
        EloamDevice.Destroy();
        EloamDevice = null;
    }

    EloamGlobal.DeinitDevs();
    EloamGlobal = null;
}


