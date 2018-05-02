(function ($, win) {
    var studentFaces = [];

    // function pushHistory() {
    //     var state = {
    //         title: "title",
    //         url: "/wx/personalCenter"
    //     };
    //     window.history.pushState(state, "title", "/wx/personalCenter");
    // }

    $(function () {
        jssdk(false)
        /**
         * 监听返回事件
         */
        // pushHistory();
        // window.addEventListener("popstate", function (e) {  //回调函数中实现需要的功能
        //     wx.closeWindow();
        // }, false);


        $(".cli").on("click", function () {
            $(".cli").find("h2").attr("class", "hs");
            $(".zdsqtb").css("display", "none");
            $(".lxls").css("display", "none");
            var obj = $(this);
            var id = obj.attr("id");
            obj.find("h2").attr("class", "");
            $("#list_" + id).css("display", "");
            $("#tel_" + id).css("display", "");
            $("#but_" + id).css("display", "");
        })

        /**
         * 打开导航页面
         */
        $(".sjhm_lb").on("click", function () {
            var pageurl = $(this).attr("url")
            window.location.href = pageurl;
        })

    })

    /**
     * 解绑
     */
    win.unBind = function () {
        pointOutMsg("确定要解决绑定吗?", function () {
            unbind(function (result) {
                $("#pointOut").remove();
                var html = template("jbcg", result);
                $("#psnbody").html(html)
            });
        })
    }

    win.openScorePage = function (studentId) {
        window.location.href = "/wx/scorePage/" + studentId
    }

    win.showPhotoPage = function (studentId) {
        $(".selectphoto").attr("state", "0");
        $("#saveFaceBut").attr("studentId", studentId);
        $(".photo").each(function () {
            var obj = $(this);
            var src = obj.attr("src")
            var type = obj.attr("type");
            obj.attr("src", "/wx/face/image/" + type + "/" + studentId);
        })
        $("#photoContext").css("display", "")
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

})(jQuery, window)