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


    /**
     * 选择认证身份
     * @param obj
     */
    function selectIdentify(obj) {
        $("input").attr("class", "kuang df")
        obj.siblings().attr("state", "0");
        obj.siblings().css({background: "#FFF", color: "#969696"})
        obj.attr("state", "1");
        obj.css({background: "#87b831", color: "#FFF"})
    }


    $(function () {
        jssdk(false);
        $("#patriarch").click(function () {
            var obj = $(this);
            selectIdentify(obj)
            $(".inpdiv").css("display", "none");
            $("#" + obj.attr("id") + "_div").css("display", "");
        })

        $("#teacher").on("click", function () {
            var obj = $(this);
            selectIdentify(obj)
            $(".inpdiv").css("display", "none");
            $("#" + obj.attr("id") + "_div").css("display", "");
        })


        /**
         * 获取验证码
         */
        $("#verifCodeBut").on("click", function () {
                var obj = $(this);
                if (countdown > 0 && countdown < 60) return;
                //拿到身份
                var identifyName = $("a[state =1]").attr("id");
                var vali = true;
                $("div[id=" + identifyName + "_div] input").each(function () {
                    vali = validate($(this)) && vali;
                })
                if (vali) {
                    var phone = $("div[id=" + identifyName + "_div] input[id=phone]").val();
                    sendVerifCode(phone, function () {
                        settime(obj);
                    })
                } else {
                    return;
                }

            }
        )


        /**
         * 认证
         */
        // var mark = 0;
        $("#authed").on("click", function () {
            $("#errmsg").text("");
            var identify = $("a[state =1]")
            var identifyName = identify.attr("id");
            var identifyType = identify.attr("idtype")
            var phone = $("div[id=" + identifyName + "_div] input[id=phone]").val();
            var name = $("div[id=" + identifyName + "_div] input[id=userName]").val();
            var idCard = $("#idCard").val();
            var vali = true;
            $("div[id=" + identifyName + "_div] input").each(function () {
                vali = validate($(this)) && vali;
            })
            if (vali) {
                checkWxAuthWithVeriCode(null,phone,identifyType,name,idCard, function (result) {
                    // mark--;
                    if (result.status == 'success') {
                        if(result.url){
                            window.location.href = result.url;
                        }else{
                            wx.closeWindow();
                        }
                    } else {
                        $("#errmsg").text(result.msg);
                    }
                })
            }else{
                return;
            }

        })

        $("input").on("blur", function () {
            validate($(this))
        })
    })

})(jQuery, window)


