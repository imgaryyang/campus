(function ($) {
    $(function () {
        var countdown = 60;

        /**
         *验证码倒计时
         * @param obj
         */
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

        //选择学校
        $("#scl_sel").on("change", function () {
            var obj = $(this);
            var sid = obj.val();
            refreshSel(sid);
            validate(obj)
            $("select[id!=scl_sel]").attr("class", "kuang df")
        })


        //选择年级
        $("#grade_sel").on("change", function () {
            var obj = $(this)
            var gid = obj.val();
            gradeData(null, gid, function (data) {
                var classesSel = $("#classes_sel")
                var html = template('classItem', data);
                classesSel.html(html);
                classesSel.attr("class", "kuang df")
            })
            validate(obj)
        })

        //班级选择
        $("#classes_sel").on("change", function () {
            var obj = $(this)
            validate(obj)
        })

        //初始化控件
        var schoolId = $("#scl_sel :selected").val()

        /**
         * 确定按钮
         */
        // var mark = 0;
        $("#commitBut").on("click", function () {
            // if (mark != 0) return
            // var verifyCode = $("#verifyCode").val();
            // var vervali = (verifyCode != "" && verifyCode != null)
            if (validateAll()) {
                // mark++;
                registerWithVeriCode(null, {
                    form: 'form',
                    callback: function (result) {
                        // mark --;
                        if (result.status=='error') {
                            alert(result.msg)
                        }else{
                            Cyan.confirm(result.msg,function (reuslt) {
                                if(result){
                                    window.location.href=reuslt.url;
                                }
                            })
                        }
                    }
                })
            }
        })

        /**
         * 获取验证码
         */
        $("#verifCodeBut").on("click", function () {
            if (countdown > 0 && countdown < 60) return;
            var phone = $("#phone").val();
            var obj = $(this);
            if (validateAll()) {
                sendVerifCode(phone, function () {
                    settime(obj);
                })
            } else {
                return;
            }
        })

        /**
         * 失去焦点验证
         */
        $("input").on("blur", function () {
            validate($(this))
        })

    })

    /**
     * 验证全部信息
     */
    function validateAll() {
        var vali = true;
        $(".kuang[name!='verifyCode'] ").each(function () {
            vali = validate($(this)) && vali
        })
        return vali;
    }

    /**
     * 刷新控件
     * @param schoolId
     * @param gradeId
     */
    function refreshSel(schoolId, gradeId) {
        gradeData(schoolId, null, function (data) {
            var html = template('gradeItem', data);
            $("#grade_sel").html(html);
            var html2 = template('classItem', null);
            $("#classes_sel").html(html2);
        })
    }


})(jQuery)