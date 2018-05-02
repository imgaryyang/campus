(function ($, win) {
    var studentFaces = [];

    function pushHistory() {
        var state = {
            title: "title",
            url: "/wx/pageInfoPage"
        };
        window.history.pushState(state, "title", "/wx/pageInfoPage");
    }

    $(function () {

        //加载后显示第一个孩子照片
        var id = $("td[isfirt='true']").attr("id")
        showPhoto(id)

        /**
         * 监听返回事件
         */
        pushHistory();
        jssdk(false)
        window.addEventListener("popstate", function (e) {  //回调函数中实现需要的功能
            wx.closeWindow();
        }, false);


        /**
         * 选择照片 ，把照片对象塞进数组
         */
        $(".selectphoto").on("click", function () {
            var li = $(this);
            var type = li.find("img").attr("type");
            wx.chooseImage({
                count: 1, // 默认9
                sizeType: 'compressed', // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    var localId = res.localIds[0];
                    li.find("img").attr("src", localId);
                    wx.getLocalImgData({
                        localId: localId, // 图片的localID
                        success: function (res) {
                            var localData = res.localData;// localData是图片的base64数据，可以用img标签显示
                            for (var i; i < studentFaces.length; i++) {
                                if (studentFaces[i].faceType == type) {
                                    studentFaces.splice(i, 1);
                                }
                            }
                            studentFaces.add({faceType: type, base64: localData})
                        }
                    });
                }
            });
        })

        /**
         * 取消按钮
         */
        $("#closeBut").on("click", function () {
            wx.closeWindow();
        })

        /**
         * 保存人脸照片
         */
        $("#saveFaceBut").on("click", function () {
            var studentId = $(this).attr("studentId");
            if (studentFaces.length > 0) {
                showMsgOnWin()
                saveFaces(studentFaces, studentId, function (result) {
                    $("#loaddiv").remove();
                    if (result) {
                        pointOutMsg(result, function () {
                            $("#pointOut").remove();
                        })
                    }
                    studentFaces.splice(0, studentFaces.length)
                });
            }
        })
    })


    //居中弹窗
    var showMsgOnWin = function (msg, callback) {
        if ($(".zwyp-ptwz").size() > 0) {
            return;
        }
        var htmlstr = " <div class='tk' id='loaddiv' style='z-index: 999'>" +
            "<div  id='layer'  style='position: absolute; top: 0px;left: 0px; display: block;'>" +
            "\t<div class=\"loader-inner ball-clip-rotate-pulse\">\n" +
            "\t  <div></div>\n" +
            "\t  <div></div>\n" +
            "\t</div>\n" +
            "  </div>"
        "</div>";
        $(document.body).append(htmlstr);
        var pop = $("#layer");
        var top = ($(window).height() - pop.height()) / 2;
        var left = ($(window).width() - pop.width() - 30) / 2;
        var scrollTop = $(document).scrollTop();
        var scrollLeft = $(document).scrollLeft();
        pop.css({
            position: 'absolute',
            top: top,
            left: left,
            display: 'block',
            height: '80px'
        })
        $("#cancelBut").on("click", function () {
            pop.parent().remove();
        })
        $("#sureBut").on("click", function () {
            callback();
        })
    }


    //居中弹窗
    var pointOutMsg = function (msg, callback) {
        if ($(".zwyp-ptwz").size() > 0) {
            return;
        }
        var htmlstr = " <div class='tk' id='pointOut'>" +
            "<div id='layer'  style='position: absolute; top: 0px;left: 0px; display: block;'><div id='errorCont' style='background: rgb(255, 255, 255); color: black; font-size: 14px; border-radius: 3px; padding: 20px 30px; display: block;'>\n" +
            "<div style='float:left;height:36px;width: 40px ;'><img src='/safecampus/wx/image/ico-ts.png'>\n" +
            "</div>\n" +
            "<div style='float:left;padding-top: 10px;padding-left: 10px'><font style='font-weight: 600;color: gray'>" + msg + "</font>\n" +
            "</div>\n" +
            "<div style=' text-align: center;padding-top: 50px'>\n" +
            "<button id='cancelBut'\n" +
            "style='background: #fff;color: #555;border: solid 0.1rem #d1d1d1;padding: 5px 10px;border-radius: 3px;margin:0 5px'>取消\n" +
            "</button>\n" +
            "<button id='sureBut' style='margin:0 5px;background: #4fa495;border: none;color: #fff;padding: 5px 10px;border-radius: 3px;'>\n" +
            "确定\n" +
            "</button>\n" +
            "</div>\n" +
            "</div></div></div>";
        $(document.body).append(htmlstr);
        var pop = $("#layer");
        var top = ($(window).height() - pop.height()) / 2;
        var left = ($(window).width() - pop.width()) / 2;
        var scrollTop = $(document).scrollTop();
        var scrollLeft = $(document).scrollLeft();
        pop.css({
            position: 'absolute',
            top: top,
            left: left,
            display: 'block',
            height: '80px'
        })
        $("#cancelBut").on("click", function () {
            $("#pointOut").remove();
        })
        $("#sureBut").on("click", function () {
            callback();
        })
    }

    win.showPhoto = function (studentId) {
        $(".wxwq").attr("class","");
        $("#span_"+studentId).attr("class","wxwq");
        $(".selectphoto").attr("state", "0");
        $("#saveFaceBut").attr("studentId", studentId);
        $(".photo").each(function () {
            var obj = $(this);
            var src = obj.attr("src")
            var type = obj.attr("type");
            obj.attr("src", "/wx/face/image/" + type + "/" + studentId);
        })
    }


})(jQuery, window)