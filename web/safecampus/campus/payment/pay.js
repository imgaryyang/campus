var haveBeenSend = 0;

//全选
$("#sendAll").click(function () {
    checkSelf($(this));
    //unionCheckChildren($(this), $("#selectTuition"))
    unionCheckChildren($(this), $(".showSession"))
    var ele = $(this)
    //全选：列统计
    $(".count").each(function () {
        if ($(this).data("count") == "") {
            //console.log(1)
            $(this).data("count", $(this).text())
        }
        if (ele.prop("checked")) {
            $(this).text($(this).next().text() + "/")
        }
        else {
            $(this).text($(this).data("count"))
        }
    })

});


//选择学生时，计算人数显示在头部
$("#selectSession").on("click", "input", function () {
    var count = 0;
    var countmsg = "0";
    var total = $(".showSession .stuList ul input").length;
    //
    // var clasId = $(".showSession .stuList ul").attr("id")
    // var clas = $("div[data-node=" + clasId + "]")
    // var graId = clas.parents("ul").attr("id");
    // var gra = $("div[data-node=" + graId + "]")

    $(".showSession .stuList ul input").each(function () {
        if ($(this).prop("checked"))
            count++;
    });
   // console.log(total + "/" + count)
    if (haveBeenSend > 0) {
        if (count == 0)
            countmsg = count + "(+" + haveBeenSend + ")"
        else if (count - haveBeenSend > 0)
            countmsg = count - haveBeenSend + "(+" + haveBeenSend + ")"
        else
            countmsg = 0 + "(+" + haveBeenSend + ")"
    } else {
        countmsg = count
    }

    $("#chooseCount strong#recent").text(countmsg);
    // console.log(total + ":" + count)
    //全选 input 样式
    if (count == total && count != 0) {
        $("#sendAll").prop("checked", true).removeClass().addClass("checkAll");
    } else if (count > 0) {
        $("#sendAll").prop("checked", true).removeClass().addClass("checkHalf");
    } else if ($("#total").text() == 0) {
        $("#sendAll").prop("checked", false).removeClass();
        Cyan.message("该缴费项目下无学生，请先添加")
    } else {
        $("#sendAll").prop("checked", false).removeClass();
    }
});

//年级
$("#grades").on("click", " input", function () {
    checkSelf($(this));

    var gradeUl = $("#" + $(this).parent().data("node"));//grade
    unionCheckChildren($(this), gradeUl);

    var ele = $(this);
    var fatherEle = ele.parent();
    var count = 0;
    var total = 0;
    gradeUl.find(".block").each(function () {
        unionCheckChildren(ele, $("#" + $(this).data("node")));//class

        var stuEle = $("#" + $(this).data("node"));
        stuEle.find("input").each(function () {
            if ($(this).prop("checked"))
                count++;
            total++;
        })
    });
    var msg = count + "/" //+ total;
    //console.log(fatherEle.find(".count").length + "-" + msg);
    if (fatherEle.find(".count").length == 0) {
        var countEle = $("<span class='count' data-count=''>" + msg + "</span>");
        fatherEle.append(countEle)
    }
    if (fatherEle.find(".total").length == 0) {
        var totalEle = $("<span class='total' data-total=''>" + total + "</span>");
        fatherEle.append(totalEle)
    }
    else {
        fatherEle.find(".count").text(msg)
    }


});

//班级
$("#classes").on("click", " input", function () {
    checkSelf($(this));

    //学生节点反选
    var stuUl = $("#" + $(this).parent().data("node"));//class
    unionCheckChildren($(this), stuUl);

    //年级节点反选
    var c = $(this).parents("ul").attr("id");
    var fatherCheckbox = $("#grades [data-node=" + c + "]");//grade/#grades
    unionCheckFather($(this), fatherCheckbox);
    //console.log(fatherCheckbox);
    showChildCount(stuUl, $("#grades [data-node=" + c + "]"));

});

//校巴
$("#bus").on("click", " input", function () {
    checkSelf($(this));

    //学生节点反选
    var stuUl = $("#" + $(this).parent().data("node"));//class
    unionCheckChildren($(this), stuUl);


    var ele = $(this);
    var fatherEle = ele.parent();
    var count = 0;
    var total = 0;

    stuUl.find("input").each(function () {
        if ($(this).prop("checked"))
            count++;
        total++;
    })
    var msg = count + "/";
    //console.log(fatherEle.find(".count").length + "-" + msg);
    if (fatherEle.find(".count").length == 0) {
        var countEle = $("<span class='count' data-count=''>" + msg + "</span>");
        fatherEle.append(countEle)
    }
    if (fatherEle.find(".total").length == 0) {
        var totalEle = $("<span class='total' data-total=''>" + total + "</span>");
        fatherEle.append(totalEle)
    }
    else {
        fatherEle.find(".count").text(msg)
    }


});

//托管室
$("#hostRoom").on("click", " input", function () {
    checkSelf($(this));

    //学生节点反选
    var stuUl = $("#" + $(this).parent().data("node"));//class
    unionCheckChildren($(this), stuUl);

});

//学生
$("#students").on("click", "ul input", function () {
    checkSelf($(this));

    //班级节点反选
    var c = $(this).parents("ul").attr("id");
    var fatherCheckbox = $("#classes [data-node=" + c + "]");
    unionCheckFather($(this), fatherCheckbox);

    //年级节点反选
    var g = $("#classes [data-node=" + c + "]").eq(0).parents("ul").attr("id");
    unionCheckFather($(this), $("#grades [data-node=" + g + "]"));

    showChildCount($(this).parents("ul"), $("#grades [data-node=" + g + "]"));
});

//学生
$("#busStudents").on("click", "ul input", function () {
    checkSelf($(this));
    //班级节点反选
    var c = $(this).parents("ul").attr("id");
    var fatherCheckbox = $("#bus [data-node=" + c + "]");
    unionCheckFather($(this), fatherCheckbox)

    showChildCount2($(this).parents("ul"), $("#bus [data-node=" + c + "]"));

});
//学生
$("#hostStudents").on("click", "ul input", function () {
    checkSelf($(this));
    //班级节点反选
    var c = $(this).parents("ul").attr("id");
    var fatherCheckbox = $("#host [data-node=" + c + "]");
    unionCheckFather($(this), fatherCheckbox)

    showChildCount2($(this).parents("ul"), $("#host [data-node=" + c + "]"));

});

//下拉显示子节点列表 - 年级
$("#grades ").on("click", "ul li .block i", function () {

    $("#classes ul").hide();
    $("#students").hide();

    var gradeClass = $("#" + $(this).parent().data("node"));//grade
    gradeClass.toggle();

    slideList($(this), $("#grades ul li .block i"), $("#classes"));

});

$("#classes ").on("click", "ul li .block i", function () {

    $("#students ul").hide();

    var classStudents = $("#" + $(this).parent().data("node"));//class
    classStudents.toggle();

    //动态计算高度,每行高50
    // line-height = height
    var n = parseInt($("#selectTuition").css("width")) / parseInt($(".block").css("width")) - 1;

    var cls = $(this).parent().data("node");//class
    var s = $("#" + cls).find("li").length;
    var h = 0;
    //+n border
    var num = Math.ceil(s / n);
    var border = num * 2 - 2;
    if (s > 0) {
        h = num * (parseInt($(".block").css("height"))) + border;
    } else {
        h = parseInt($(".block").css("height")) + border;
    }
    $(".stuAutoHide").css("height", h);
    $(".stuAutoHide").css("line-height", h + "px");

    //先生成高度再执行下拉动画
    slideList($(this), $("#classes ul li .block i"), $("#students"));

});

$("#hostRoom ").on("click", "ul li .block i", function () {

    $("#hostStudents ul").hide();

    var classStudents = $("#" + $(this).parent().data("node"));//class
    classStudents.toggle();

    //先生成高度再执行下拉动画
    slideList($(this), $("#hostRoom ul li .block i"), $("#hostStudents"));

});

$("#bus ").on("click", "ul li .block i", function () {

    $("#busStudents ul").hide();

    var classStudents = $("#" + $(this).parent().data("node"));//class
    classStudents.toggle();

    //先生成高度再执行下拉动画
    slideList($(this), $("#bus ul li .block i"), $("#busStudents"));

});

/**
 * 统计各列选择数量
 * @param stuEle
 * @param fatherEle
 */
function showChildCount(stuEle, fatherEle) {
    var count = 0;
    // stuEle.find("input").each(function () {
    //     if ($(this).prop("checked"))
    //         count++;
    // });

    var clasId = stuEle.attr("id") //$(".showSession .stuList ul").attr("id")
    var clas = $("div[data-node=" + clasId + "]")
    var graId = clas.parents("ul").attr("id");
    var clasess = $("#" + graId).find("li").find("div")
    clasess.each(function () {
        var cls = $(this).data("node")
        $("#" + cls).find("input").each(function () {
            if ($(this).prop("checked"))
                count++;
        })
    })
    console.log("s" + count)


    var msg = count + "/";
    if (fatherEle.find(".count").length == 0) {
        var countEle = $("<span class='count' data-count=''>" + msg + "</span>");
        fatherEle.append(countEle)
    }
    else {
        //
        // if (stuEle.parents(".session").attr("id")=="grades") {
        //     fatherEle.find(".count").text(msg + fatherEle.find(".count").text().split("/")[0])
        // } else
        //+ stuEle.find("input").length
        fatherEle.find(".count").text(msg)
    }


}

function showChildCount2(stuEle, fatherEle) {
    var count = 0;
    stuEle.find("input").each(function () {
        if ($(this).prop("checked"))
            count++;
    });

    var msg = count + "/";
    var total = stuEle.find("input").length;
   // console.log(fatherEle.find("input").val() )
    if (fatherEle.find(".count").length == 0) {
        var countEle = $("<span class='count' data-count=''>" + msg + "</span>");
        fatherEle.append(countEle)
    }
    if (fatherEle.find(".total").length == 0) {
        var totalEle = $("<span class='total' data-total=''>" + total + "</span>");
        fatherEle.append(totalEle)
    }
    else {
        fatherEle.find(".count").text(msg)
    }
    //console.log(msg+"/"+total)

}


function getFillNum() {

    //动态计算高度,每行高50
    // line-height = height
    //每行n个block
    var n = parseInt($("#selectTuition").css("width")) / parseInt($(".block").css("width")) - 1;

    var cls = $(this).parent().data("node");//class
    var s = $("#" + cls).find("li").length;
    var h = 0;
    //+n border
    var num = Math.ceil(s / n);
    var border = num * 2 - 2;

    var totalWidth = $(".session ul").eq(0).width();
    //console.log(totalWidth + " : totalWidth")

    //填充
    $(".session ul").each(function () {
        var n = $(this).find("li").length;
        var w = $(this).find("li").eq(0).width();
        //console.log(n + " - " + w)
        //if (n * w > totalWidth) {
        var fillNum = n * w % totalWidth * totalWidth / w;
        //console.log(fillNum + " :fillNum")
        for (var i = 0; i < fillNum; i++) {
            createList($(this), null, null, false);
        }
        // }

    })


}

/**
 *
 * @param payItemId 根据payItem的Id获取已发送账单的学生数据，为-1时获取全部学生，
 * @param show 0：不能选中 ，1：可选
 * @param p 回调
 */
function showTuition(payItemId, show, p) {

    $(".select").hide();
    $("#selectTuition").fadeIn(200).addClass("showSession");

//不为空时，获取数据填充
    if ($("#grades ul li").length == 0) {

        $.get(
            "/campus/pay/payItem/getStuMapList/0/" + payItemId + "/1",
            function (result) {
                //console.log(result)
                //动态生成年级列表节点
                for (i = 0; i < result.grade.length; i++) {
                    var grade = result.grade[i];
                    createList($("#grades ul"), grade, "grade", true);

                    //同时生成班级ul父节点
                    var clasUl = $("<ul id='grade" + grade.nodeId + "'></ul>");
                    $("#classes").append(clasUl);
                }
                //动态生成班级列表节点
                for (i = 0; i < result.class.length; i++) {
                    var clas = result.class[i];
                    createList($("#grade" + clas.parentId), clas, "class", true);

                    //同时生成学生ul父节点
                    var stuUl = $("<ul id='class" + clas.nodeId + "'></ul>");
                    $("#students").append(stuUl);
                }
                //动态生成学生列表节点
                for (i = 0; i < result.student.length; i++) {
                    var stu = result.student[i];
                    createList($("#class" + stu.parentId), stu, null, false);
                }

                //学生总数
                $("#chooseCount #total").text(result.student.length);
                // count()

                //console.log(result.student);

                $.get(
                    "/campus/pay/payItem/getStuMapList/0/" + payItemId + "/0",
                    function (result) {
                        //对已发送账单的学生进行标识，可选性设置
                        //console.log(result)
                        showStuList("Tuition", result.student, show)
                    });

                fiilColum()
                $("#grades input").click()
                $("#grades input").click()

            });
        if (p) {
            p();
        }
    }
}

/**
 *
 * @param payItemId 根据payItem的Id获取已发送账单的学生数据，为-1时获取全部学生，
 * @param show 0：不能选中 ，1：可选
 * @param p 回调
 */
function showHost(payItemId, show, p) {

    $(".select").hide();
    $("#selectHost").fadeIn(200).addClass("showSession");

    $.get(
        "/campus/pay/payItem/getStuMapList/1/" + payItemId + "/1",
        function (result) {
            //console.log(result.room);
            //动态生成年级列表节点
            if (result.room != undefined)
                for (i = 0; i < result.room.length; i++) {
                    var room = result.room[i];
                    createList($("#hostRoom ul"), room, "room", true);

                    //同时生成班级ul父节点
                    var stuUl = $("<ul id='room" + room.nodeId + "'></ul>");
                    $("#hostStudents").append(stuUl);
                }

            //动态生成学生列表节点
            for (i = 0; i < result.student.length; i++) {
                var stu = result.student[i];
                createList($("#room" + stu.parentId), stu, null, false);
            }

            //学生总数
            $("#chooseCount #total").text(result.student.length);
            //count()

            $.get(
                "/campus/pay/payItem/getStuMapList/1/" + payItemId + "/0",
                function (result) {
                    //对已发送账单的学生进行标识，可选性设置
                    // console.log(result)
                    showStuList("Host", result.student, show)
                });

            fiilColum()

            $("#hostRoom").next().find("input").click()
            $("#hostRoom").next().find("input").click()
        });
    if (p) {
        p();
    }

}

/**
 *
 * @param payItemId 根据payItem的Id获取已发送账单的学生数据，为-1时获取全部学生，
 * @param show 0：不能选中 ，1：可选
 * @param p 回调
 */
function showBus(payItemId, show, p) {

    $(".select").hide();
    $("#selectSchoolBus").fadeIn(200).addClass("showSession");

    $.get(
        "/campus/pay/payItem/getStuMapList/2/" + payItemId + "/1",
        function (result) {
            //console.log(result);
            //动态生成年级列表节点
            for (i = 0; i < result.bus.length; i++) {
                var bus = result.bus[i];
                createList($("#bus ul"), bus, "bus", true);

                //同时生成班级ul父节点
                var stuUl = $("<ul id='bus" + bus.nodeId + "'></ul>");
                $("#busStudents").append(stuUl);
            }

            //动态生成学生列表节点
            for (i = 0; i < result.student.length; i++) {
                var stu = result.student[i];
                createList($("#bus" + stu.parentId), stu, null, false);
            }

            //学生总数
            $("#chooseCount #total").text(result.student.length);


            $.get(
                "/campus/pay/payItem/getStuMapList/2/" + payItemId + "/0",
                function (result) {
                    //对已发送账单的学生进行标识，可选性设置
                    // console.log(result)
                    showStuList("SchoolBus", result.student, show)
                });

            fiilColum()
            $("#bus").next().find("input").click()
            $("#bus").next().find("input").click()
        });
    if (p) {
        p();
    }

}

/**
 * 不足一行的单元格，用空单元格填满一行
 */
function fiilColum() {

    var totalWidth = $(".showSession .session ul").eq(0).width();
    var w = $(".showSession .session ul").eq(0).find("li").eq(0).width();
    //填充，不足一行的数据，使用空格填满一行
    if (w != null)
        $(".showSession .session ul").each(function () {
            var n = $(this).find("li").length;
            //console.log(n + " :n-w: " + w);
            var mod = (n * w / totalWidth);
            var fillNum = Math.floor((1 - mod) * totalWidth / w);
            //console.log(mod+":mod-fill:"+fillNum+"-fillTo:"+Math.floor(fillNum))
            if (fillNum < 0) {
                fillNum = -fillNum;
                var nb = Math.floor(totalWidth / w);
                fillNum = nb - fillNum % nb;
            }
            for (var i = 0; i < fillNum; i++) {
                createList($(this), null, null, false);
            }
        })
}

/**
 *
 *  @param selectID selectXX 列表类型
 * @param students 需要勾选的学生JSON对象
 * @param show 是否可以勾选
 */
function showStuList(selectID, students, show) {
    haveBeenSend = students.length
    //勾选List里的学生
    $("#select" + selectID).find(".stuList").find("input").each(function () {
        var arr = [];
        for (var i = 0; i < students.length; i++) {
            arr.push(students[i].nodeId)
        }

        if (arr.indexOf($(this).val()) > -1) {
            $(this).click();
            if (show == "0") {
                $(this).attr("disabled", "disabled");
                $(this).parent().addClass("hasCheck");
            }
        }
    });

}


/**
 * 根据当前元素操作，将归属于他的子 input 选择
 * @param ele 当前元素
 * @param children 子 input 的父节点
 */
function unionCheckChildren(ele, children) {

    if (ele.prop("checked")) {
        children.find("input").prop("checked", true).removeClass().addClass("checkAll");
    }
    else {
        children.find("input").prop("checked", false).removeClass();
    }


}

/**
 * 将当前 input 的父 input 进行选择
 * @param ele
 * @param fatherCheckbox
 */
function unionCheckFather(ele, fatherCheckbox) {
    //判断该父节点的子节点（兄弟节点）是否全选
    var f = 0;
    var t = 0;
    ele.parents("ul").find("input").each(function () {
        if ($(this).prop("checked")) {
            t++;
        } else {
            f++;
        }
    });
    //console.log(t + " - " + f)
    //选中，判断是否全选
    var fatherCheckboxs = fatherCheckbox.find("input");
    if (ele.prop("checked")) {
        ele.removeClass().addClass("checkAll");
        //全选
        if (t > 0 && f == 0) {
            fatherCheckboxs.removeClass().addClass("checkAll")
        }
        else {
            fatherCheckboxs.removeClass().addClass("checkHalf")
        }
    }
    //取消选中，判断是否全取消
    else {
        ele.removeClass();
        //全取消
        if (f > 0 && t == 0) {
            fatherCheckboxs.removeClass();
        }
        else
            fatherCheckboxs.removeClass().addClass("checkHalf")
    }
}

/**
 * 选择自身 input , 更改样式
 * @param ele
 */
function checkSelf(ele) {
    if (ele.prop("checked")) {
        ele.removeClass().addClass("checkAll")
    }
    else {
        ele.removeClass()
    }
}

/**
 * 点击下拉箭头，其及其父箭头样式改变，显示 list
 * @param i
 * @param father
 * @param list
 */
function slideList(i, father, list) {

    if (i.hasClass("hide")) {
        i.removeClass("hide");
        i.parent().removeClass("chooseBlock")
    } else {
        father.removeClass("hide");
        father.parent().removeClass("chooseBlock");
        i.addClass("hide");
        i.parent().addClass("chooseBlock")
    }
    if (i.hasClass("hide"))
        list.slideDown();
    else
        list.slideUp()
}

/**
 * 在根节点下生成子节点
 * @param father 根节点对象
 * @param node 填充内容对象
 * @param dataSub data-dataSub
 * @param hasIcon 是否可下拉
 */
function createList(father, node, dataSub, hasIcon) {

    //console.log(isShow)
    var disable = "";

    var li = $("<li></li>");
    var attr = "";
    if (dataSub != null) {
        //attr = "data-" + dataSub + "='" + dataSub + node.nodeId + "'";
        attr = "data-node='" + dataSub + node.nodeId + "'";
    }
    var div = $("<div class='block' " + attr + " ></div>");
    var input;
    var span;
    if (node != null) {
        input = $("<input type='checkbox' value='" + node.nodeId + "'" + disable + ">");
        span = $("<span>" + node.nodeName + "</span>");
        div.append(input)
    } else {
        //span = $("<span> 空 </span>");
        span = $("<span>  </span>");

    }
    div.append(span);

    if (hasIcon) {
        var img = $("<i> </i>");
        div.append(img)
    }
    li.append(div);
    father.append(li)

}


function count() {

    var count = 0;
    $(".showSession .stuList ul input").each(function () {
        count++;
    });
    $("#chooseCount #total").text(count);
}

