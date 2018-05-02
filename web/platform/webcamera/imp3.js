//连山德生高拍仪（开发：良田）
System.Camera.activeXInited = true;

(function () {
    var test = function () {
        var classId = "52D1E686-D8D7-4DF2-9A74-8B8F4650BF73";
        var id = "EloamGlobal_ID";

        var s = "<div class='camera' style='display: none'><OBJECT style='height:100%;width:100%'  " +
            " classid='clsid:" + classId + "' id='" + id + "'></OBJECT> " + " </div>";

        if (document.body) {
            var div = document.createElement("div");
            div.innerHTML = s;
            document.body.appendChild(div);
        }

        try {
            return true;
        }
        catch (e) {
            return false;
        }
    };

    if (test()) {
        Cyan.importJs("/platform/webcamera/eloamglobal.js");
    }

})();

