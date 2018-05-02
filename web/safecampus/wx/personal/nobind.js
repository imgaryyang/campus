(function ($, win) {

    var countdown = 60;

    function settime(obj) {
        if (countdown == 0) {
            obj.removeAttr("disabled", "");
            obj.text("获取验证码");
            countdown = 60;
            return;
        } else {
            obj.attr("disabled", true);
            obj.text("重新发送(" + countdown + ")");
            countdown--;
        }
        setTimeout(function () {
            settime(obj)
        }, 1000)
    }


    //居中弹窗
    var showMsgOnWin = function (msg, callback) {
        if ($(".zwyp-ptwz").size() > 0) {
            return;
        }
        var htmlstr = " <div class='tk' id='photoContext'>" +
            "<div id='layer'  style='position: absolute; top: 0px;left: 0px; display: block;'><div id='errorCont' style='background: rgb(255, 255, 255); color: black; font-size: 14px; border-radius: 3px; padding: 20px 30px; display: block;'>\n" +
            "<div style='float:left;height:36px;width: 40px ;'><img src='/safecampus/wx/image/ico-ts.png'>\n" +
            "</div>\n" +
            "<div style='float:left;padding-top: 10px;padding-left: 10px'><font style='font-weight: 600;color: gray'>您确定要解除绑定吗？</font>\n" +
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
            top: top + scrollTop,
            left: left + scrollLeft,
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

    $(function () {
        jssdk(false);
        /**
         * 获取验证码
         */
        $("#verifCodeBut").on("click", function () {
                var obj = $(this);
                if (countdown > 0 && countdown < 60) return;
                var vali = validate($("#phone"))
                if (vali) {
                    var phone = $("#phone").val();
                    sendVerifCode(phone, function () {
                        settime(obj);
                    })
                } else {
                    return;
                }
            }
        )
        //input校验
        $("input").on("blur", function () {
            validate($(this))
        })

        /**
         * 解除绑定
         */
        $("#unbinding").on("click", function () {
            showMsgOnWin("您确定要解除绑定吗？", function () {
                unbind(function (ret) {
                    $("#photoContext").remove();
                    var html = template("jbcg", ret);
                    $("#unbind").html(html)
                })
            })
        })

        /**
         * 修改电话号码
         */
        $("#confirmBut").on("click", function () {
            var vali = true;
            $("input").each(function () {
                vali = validate($(this)) && vali
            })
            if (!vali) return;
            var phone = $("#phone").val();
            var verifyCode = $("#verifyCode").val();
            changePhoneWithoutVeriCode(null,phone, function (reusult) {
                if (reusult.status == 'error') {
                    $("#errmsg").text(reusult.msg);
                } else {
                    Cyan.confirm(reusult.msg, function (ret) {
                        if (ret) {
                            window.location.href="/wx/pageInfoPage"
                        }
                    })
                }
            })
        })


        /**
         * 跳转修改电话号码页面
         */
        $("#changeBut").on("click", function () {
            window.location.href = "/wx/unbingpage"
        })
    })

})(jQuery, window)


