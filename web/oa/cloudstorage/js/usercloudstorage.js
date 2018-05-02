/**
 * Created by gyw on 2017/7/26 0026.
 */
Cyan.importJs("/oa/cloudstorage/js/usercloudshare.js");
Cyan.onload(function () {
    var upload = new System.RichUpload();
    upload.autoUploadNextFile = false;
    upload.bindButton("upoladButton");
    upload.displayProgressIn("progress");
    upload.addListener({
        onselect: function (file) {
        },
        onsuccess: function (file, result) {
            var percentageDiv = $$$(file.id).$(".richupload_file_percentage");
            percentageDiv.html("正在保存文件");
            window.upload([result], {
                callback: function () {
                    percentageDiv.html("完成");
                    upload.uploadNextFile();
                },
                error: function (error) {
                    percentageDiv.html(error.message);
                }
            });
        },
        oncomplete: function (file) {
        },
        onok: function () {
            var typeName = $j("#typeName").text();
            toHomePage({
                target: "main1", callback: function () {
                    if (typeName) {
                        $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                    }
                    checkTheStyle()
                }
            });
        }
    });
});


$j(function () {
    $j("#searchVal").keydown(function (e) {
        if (e.which == 13) {
            searchByName()
        }
    });
    /**
     * 类型按钮绑定
     */
    var fileTypeBut = $j(".typeBut");
    fileTypeBut.bind("click", function () {
        var type = $j(this).attr("fileType");
        var typeName = $j(this).text();
        fileTypeBut.css("background", "");
        var folderId = $j("#floderId").val();
        $j(this).css("background", " #dfe5ea")
        if (!type) {
            folderId = 0;
        }
        Cyan.Arachne.form.nowfolderId = folderId;
        Cyan.Arachne.form.type = type;
        // Cyan.Arachne.form.searchName = ""
        Cyan.Arachne.form.shareId = null;
        toHomePage({
            target: "main1", callback: function () {
                if (type == "6") {
                    $j(".gncd").css("display", "none");

                } else {
                    $j(".gncd").css("display", "");
                }
                if (type) {
                    $j("#navList").append("<div id='typeName'>" + typeName +
                        "</div>");
                    $j("#newfolder").css("display", "none");
                    $j("#floderId").val("0");
                } else {
                    $j("#newfolder").css("display", "");

                }
                checkTheStyle()
            }
        });

    })

    $j('.yp_lt span').click(function () {

        $j(this).parent().removeClass().siblings().addClass('current')
    })


    //checkbox个数
    var checkNum = $j("input[type='checkbox']").length;
    //非全选按钮
    var singleCheck = $j("input[type='checkbox']").not("[id]");
    //重命名按钮
    var renameEle = $j("#rename");
    //新建文件夹按钮
    var newfolder = $j("#newfolder");
    //上传按钮
    var uploadFileBut = $j("#uploadBut");
    //删除按钮
    var deleteBut = $j("#deleteBut");
    //已选中的文件夹
    var checkedFolders = $j("#floderIds");
    //已选中的文件
    var checkedFiles = $j("#fileIds");
    //下载按钮
    var downFileBut = $j("#downFileBut");
    //复制到按钮
    var copyToBut = $j("#copyToBut");
    //分享按钮
    var shareBut = $j("#shareBut");
    //移动到按钮
    var moveToBut = $j("#moveToBut");

    moveToBut.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！")
        } else {
            // apeendCopyView(2);
            getFolderTreeChildren(2)
        }
    })

    /**
     * 重命名按钮绑定
     */
    renameEle.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！")
            return;
        }
        var selectedFile = $j("input[type='checkbox']:checked").not("[id]");

        if (selectedFile.length > 1 || selectedFile.length == 0) {
            return;
        }
        if ($j("#changeStyle").attr("styleTag") == "0") {
            var a = selectedFile.parent().parent().parent().find("a");
            a.css("display", "none");
            var td
            if(selectedFile.parent().parent()[0].tagName=="TD"){
                td=selectedFile.parent().parent();
            }else {
                td=selectedFile.parent().parent().parent();
            }
            td.append("<div  style='z-index: 2'   class='srk'>" +
                "<input id='renameInp' value='" + a.text() + "'/> " +
                "<div class='srk_dc'> " +
                "<a href='#' onclick=rename(" + selectedFile.val() + ",'" + selectedFile.attr("class") + "')></a> " +
                "<a href='#' onclick='removeRename(this)' class='cd'></a>" +
                "</div> ")
        } else {
            var style2selectedFile = $j("a[state][code='" + selectedFile.val() + "']");
            var a = style2selectedFile.parent().find("p").find("a");
            a.css("display", "none");
            var p = style2selectedFile.parent().find("p");
            p.append("<div style='z-index: 2' class='srk2'><input  id='renameInp' value='" + a.text() + "'/><div class='srk_dc'><a href='#' onclick=rename(" + selectedFile.val() + ",'" + selectedFile.attr("class") + "')></a><a href='#' onclick='removeRename(this)' class='cd'></a></div></div>");
            p.css("text-align", "left");
        }
        $j("#superDiv").prepend("<div class='hsbj2'></div>")
        $j("#renameInp").select();
    })


    /**
     * 新建文件夹按钮绑定
     */
    newfolder.bind("click", function () {
        if ($j("#newf").length > 0) {
            $j("#newf:text").select();
        } else {
            if ($j("#changeStyle").attr("styleTag") == "0") {
                $j("#tb1").prepend("<tr> " +
                    "<td class='td2'>" +
                    "<span><input type='checkbox' value=''/></span> " +
                    "<p><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/></p> " +
                    "<div  style='z-index: 2' class='srk'>" +
                    "<input id='newf' value='新建文件夹'/> " +
                    "<div class='srk_dc'> " +
                    "<a href='#' onclick='newfolder()'></a> " +
                    "<a href='#' onclick='removethis(this)' class='cd'></a>" +
                    "</div> " +
                    "</div> </td> <td class='td3'></td> </tr>");
            } else {
                $j("#table2").prepend("<li  > " +
                    "<h2 ><a href=''#'><img src='/oa/cloudstorage/image/dwjj_03.png' width='56' height='46' /></a></h2> " +
                    "<p><div style='z-index: 2' class='srk2'><input  id='newf' value='新建文件夹'/><div class='srk_dc'><a href='#' onclick='newfolder()'></a><a href='#' onclick='removeStyle2(this)' class='cd'></a></div></div></p> " +
                    "<a href='#' class='a2'></a> </li>");
            }
            $j("#superDiv").prepend("<div class='hsbj2'></div>")
            $j("#newf:text").select();
        }
    })

    /**
     * 上传文件按钮绑定
     */
    uploadFileBut.bind("click", function () {
        System.showModal("/oa/cloudstorage/usercloud/batch");
    })

    /**
     * 删除按钮绑定
     */
    deleteBut.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！");
        } else {
            deleteFile();
        }

    })

    /**
     * 下载按钮绑定
     */
    downFileBut.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！")
        } else {
            window.location.href = "/oa/cloudstorage/usercloud/zip?folderIds=" + folderIdsStr + "&fileIds=" + fileIds;
        }
    })


    /**
     * 复制到按钮绑定
     */
    copyToBut.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！")
        } else {
            // apeendCopyView(1);
            getFolderTreeChildren(1);
        }
    });

    /**
     * 分享按钮
     */
    shareBut.bind("click", function () {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "")) {
            errorMsg("请选择文件！")
        } else {
            System.showModal("/oa/cloudstorage/usercloud/showSharePage?fileIdsStr=" + fileIds + "&folderIdsStr=" + folderIdsStr, function (ret) {
                if (ret) {
                    errorMsg("分享成功！");
                }
            })
        }
    })

});

/**
 * 删除重命名框
 * @param obj
 */
function removeRename(obj) {

    if ($j("#changeStyle").attr("styleTag") == "0") {
        $j(obj).parent().parent().remove();
        $j("input[type='checkbox']").not("[id]").parent().parent().parent().find("a").css("display", "");
    } else {
        var p = $j(obj).parent().parent().parent();
        p.css("text-align", "center");
        p.find("a").css("display", "");
        $j(obj).parent().parent().remove();
    }
    $j(".hsbj2").remove();
}


//删除新建文件夹TR
function removethis(obj) {
    $j(obj).parent().parent().parent().parent().remove();
    $j(".hsbj2").remove();
}

function removeStyle2(obj) {
    $j(obj).parent().parent().parent().remove();
    $j(".hsbj2").remove();
}

/**
 *重命名
 */
function rename(id, type) {
    var typeName = $j("#typeName").text();
    var renameStr = $j("#renameInp").val();
    if (renameStr == null || renameStr == "") {
        errorMsg("标题不能为空！")
        return;
    }
    if (type == "file") {
        type = 1;
    } else {
        type = 2;
    }
    fileRename(id, renameStr, type, function (result) {
        if (result) {
            errorMsg("重命名成功！", function () {
                toHomePage({
                    target: "main1", callback: function () {
                        if (typeName) {
                            $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                        }
                        checkTheStyle()
                    }
                });
                $j(".hsbj2").remove();

            })
        } else {
            errorMsg("文件名已存在");
        }
    })

}

/**
 * 新建文件夹
 */
function newfolder() {
    var typeName = $j("#typeName").text();
    var parentId = $j("#floderId").val();
    var folderName = $j("#newf").val();
    createFolder(parentId, folderName, {
        obj: {},
        callback: function (result) {
            if (result) {
                toHomePage({
                    target: "main1", callback: function () {
                        if (typeName) {
                            $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                        }
                        checkTheStyle()
                    }
                });
                $j(".hsbj2").remove();
            } else {
                errorMsg("文件夹已存在！");
            }
        }
    })
}

/**
 * 文件复制
 */
function apeendCopyView(type) {
    var top = $j("body").height() / 2 - 353 / 2 + "px";
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var name = new String()
    if (type == 1) {
        name = "复制";
    } else {
        name = "移动";
    }
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (result) {
        $j("#superDiv").prepend("<div class='tk' style='top: " + top + "'><div class='tk_bt'>" + name + "到</div><div class='fxxs'>" + result +
            "</div><div class='anniu'> <div class='xjwj'> <button onclick='newFolderBySeled()'>新建文件夹</button> </div> <div class='cjlj'> " +
            "<button id='copy'>确定</button> <button class='btn2' onclick='cannelCopy()'>取消</button> </div> </div></div><input type='hidden' id='seledFolder' value=''> <div class='hsbj'></div>");

        var childButA = $j(".seled");
        childButA.bind("click", function () {
            $j(".seled").css("background", "");
            $j(this).css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");
            $j("#seledFolder").val($j(this).attr("code"))
        });
        var copy = $j("#copy");
        copy.bind("click", function () {
            var seledFolder = $j("#seledFolder").val();
            if (seledFolder == null || seledFolder == "") {
                errorMsg("请选择要" + name + "的文件夹!");
            } else if (type == 1) {
                copyFile(seledFolder);
            } else {
                moveFile(seledFolder);

            }
        });
    }
    ajax.call("/oa/cloudstorage/usercloud/createAllUserFolder?folderIds=" + folderIdsStr + "&fileIds=" + fileIds);
}

/**
 * 删除文件
 */
function deleteFile() {
    var typeName = $j("#typeName").text();
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (ret) {
        if (ret) {
            errorMsg("删除成功", function () {
                toHomePage({
                    target: "main1", callback: function () {
                        if (typeName) {
                            $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                        }
                        checkTheStyle()
                    }
                });
            })
        } else
            errorMsg("删除失败");
    }
    ajax.call("/oa/cloudstorage/usercloud/usercloudcrud/deleteFolderAndFiles?folderIds=" + folderIdsStr + "&fileIds=" + fileIds);
}

/**
 * 文件夹下拉列表
 * @param obj
 */
function spanToggle(obj) {
    if ($j(obj).parent().attr("class") == "current") {

        $j(obj).parent().removeClass();

    }
    else {
        $j(obj).parent().addClass('current');
    }

    if ($j(obj).attr("state") == "0") {
        var parenId = $j(obj).attr("code");
        var ajax = new Cyan.Ajax();
        ajax.setRequestHeader("Accept", "text/json");
        ajax.method = "GET";
        ajax.handleObject = function (ret) {
            $j(obj).parent().append(ret);
            $j(obj).attr("state", "1");
            var childButA = $j(".seled");
            childButA.bind("click", function () {
                $j(".seled").css("background", "");
                $j(this).css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");
                $j("#seledFolder").val($j(this).attr("code"))
            });
        }
        ajax.call("/oa/cloudstorage/usercloud/createFolderTreeChildren?parentId=" + parenId);
    }
}
/**
 * 关闭复制界面
 */
function cannelCopy() {
    $j(".tk").remove();
    $j(".hsbj").remove();
}

/**
 * 复制操作
 * @param seledFolderId
 */
function copyFile(seledFolderId) {
    var nowfolderId = $j("#floderId").val();
    if (nowfolderId == seledFolderId) {
        errorMsg("不能复制到同一目录下");
        return;
    }
    var typeName = $j("#typeName").text();
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (ret) {
        if (ret) {
            errorMsg("复制成功", function () {
                cannelCopy();
                toHomePage({
                    target: "main1", callback: function () {
                        if (typeName) {
                            $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                        }
                        checkTheStyle()
                    }
                });
            })
        } else
            errorMsg("复制失败");
    }
    ajax.call("/oa/cloudstorage/usercloud/copyToTargetFolder?folderIds=" + folderIdsStr + "&fileIds=" + fileIds + "&seledFolderId=" + seledFolderId);
}

/**
 * 复制到---新建文件夹
 */
function copyNewFolder() {
    if ($j("#newf").length > 0) {
        $j("#newf:text").select();
    } else {

        var root = $j("#root");
        root.removeClass();
        root.append("<ul><li style='line-height: 36px;padding-left: 40px'><a href='#'>" +
            "<img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" +
            "<input style='width: 152px;height: 22px;border: none;box-shadow: 0 0 6px #2db4e0' id='newf' value='新建文件夹'/>" +
            "<img onclick='createCopyFolder()' style='margin:10px 0 0 5px' class='copyimg' src='/oa/cloudstorage/image/dc_03.png'><img onclick='removeCopythis(this)' style='margin:10px 0 0 5px;'  class='copyimg' src='/oa/cloudstorage/image/dc_05.png'></a></li> " +
            "</ul>");
        $j("#newf").focus();
        $j("#newf:text").select();
    }
}

/**
 * 删除复制文件框
 * @param obj
 */
function copyNewFolder(obj, code, name) {
    if (name) {
        var ul = $j(obj).parent().parent().parent().parent().parent();
        ul.removeClass();
        ul.append(" <li class='childli'><a class='seled' code='" + code + "' href='#'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'>" + name + "</a></li>")
        var childButA = $j(".seled");
        childButA.bind("click", function () {
            $j(".seled").css("background", "");
            $j(this).css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");
            $j("#seledFolder").val($j(this).attr("code"))
        });
        $j(obj).parent().parent().parent().parent().remove();

    } else {
        $j(obj).parent().parent().parent().remove();
    }

}

/**
 * 复制页面创建文件夹
 */
function createCopyFolder(code) {
    var folderName = $j("#newf").val();
    var seled = $j(".seled[code=" + code + "]");
    createFolder(code, folderName, {
        obj: {},
        callback: function (result) {
            if (result) {
                $j(".seled").css("background", "");
                $j("#newf").parent().parent().parent().remove();
                seled.parent().append("<ul>" +
                    "<li class='childli'>" +
                    "<a style='background:#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center' class='seled' code=" + result.folderId + " href='#'>" +
                    "<img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'>" + result.folderName + "</a>" +
                    "</li> </ul>")

                $j("#seledFolder").val(result.folderId)
            } else {
                errorMsg("所创建的文件夹已存在！");
            }
        }
    })
}

/**
 * 根据文件夹ID到云盘页
 * @param folderId
 */
function gotoHomePage(folderId) {
    Cyan.Arachne.form.nowfolderId = folderId;
    Cyan.Arachne.form.searchName = null;
    // Cyan.Arachne.form.shareId = null;
    toHomePage({
        form: "", target: "main1", callback: function () {
            checkTheStyle()
        }
    });
}

/**
 * 展示分享页面
 */
function showShareView() {
    var top = $j("body").height() / 2 - 353 / 2 + "px";
    var shareName = "";
    var folderIds = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    if ((folderIds != null && folderIds != "")) {
        var folderIdsStr = folderIds.split(",");
        var folderName = $j("input[class='folder'][value='" + folderIdsStr[0] + "']").parent().parent().find("a").text();
        shareName = folderName;
    } else {
        var fileName = $j("input[class='file'][value='" + fileIds.split(",")[0] + "']").parent().parent().find("a").text().split(".")[0];
        shareName = fileName;
    }
    $j("#superDiv").prepend("" +
        "<div class='tk' style='top: " + top + ";'> " +
        "<div class='tk_bt'>分享</div> " +
        "<div class='fxxs'> " +
        "<div class='fxxs_jj'> " + "<div class='fxxs_fxxs'><div class='fxxs_fxxs_lt'>分享名称</div><input style='width: 300px' id='shareName' value='" + shareName + "'/></div>" +
        "<div class='fxxs_fxxs'> " +
        "<div class='fxxs_fxxs_lt'>分享形式</div> " +
        "<div class='fxxs_fxxs_rt'> " +
        "<div class='jmqx'> " +
        "<span> <input name='encrypt'  type='radio' value='true' checked='checked'/> </span> " +
        "<p>加密仅限拥有密码者可查看，更加隐私安全</p> " +
        "</div> " +
        "<div class='jmqx'> " +
        "<span> <input name='encrypt' type='radio' value='false' /> </span> " +
        "<p>公开任何人可查看或下载</p> " +
        "</div> " +
        "</div> " +
        "</div>" +
        "<div class='fxxs_fxxs'> " +
        "<div class='fxxs_fxxs_lt'>有效期</div> " +
        "<div class='fxxs_fxxs_rt'> " +
        "<div class='jmqx'> " +
        "<span> <input name='foerver' type='radio' value='true' checked='checked' /> </span> " +
        "<p>永久有效</p> " +
        "</div>" + "<div class='jmqx'> " +
        "<span> <input name='foerver' type='radio' value='false' /> </span> " +
        "<p><input style='width: 30px;text-align: center' type='text' id='validDay' disabled='disabled'> 天</p> " +
        "</div> </div> </div> </div> </div> " +
        "<div class='anniu'>" +
        "<div class='cjlj'><button id='createUrlBut'>创建连接</button><button class='btn2' onclick='cannelCopy()'>取消</button></div>" +
        "</div> </div> <div class='hsbj'> </div>")


    //绑定有效期按钮
    $j("input[name='foerver']").bind("change", function () {
        var foerverRadio = $j(('input:radio[name="foerver"]:checked')).val();
        if (foerverRadio == "true") {
            $j("#validDay").attr("disabled", "disabled");
        } else {
            $j("#validDay").removeAttr("disabled");
        }
    });

    /**
     * 创建连接按钮绑定  创建完成后返回分享连接及提取码
     */
    $j("#createUrlBut").bind("click", function (result) {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        //有效期单选
        var foerverRadio = $j(('input:radio[name="foerver"]:checked')).val();
        //天数
        var validay = $j("#validDay").val();
        //是否加密
        var encrypt = $j(('input:radio[name="encrypt"]:checked')).val();
        //分享的名字
        var shareName = $j("#shareName").val();

        if (foerverRadio == "false" && validay == "") {
            errorMsg("请输入有效的天数");
            return;
        } else {
            var ajax = new Cyan.Ajax();
            ajax.setRequestHeader("Accept", "text/json");
            ajax.method = "GET";
            ajax.handleObject = function (result) {
                if (result) {
                    var url = window.location.host + "/oa/cloudstorage/cloudsharecrud/" + result.shareId;
                    var str = "";
                    if (result.password) {
                        str = "<input value='" + result.password + "' type='text' style='height: 30px;width: 70px' readonly='true'/>    提取码 "
                    }
                    $j(".fxxs_jj").remove();
                    $j(".cjlj").remove();
                    $j(".fxxs").append("<div class='fxxs_jj'>" +
                        " <div class='fxxs_fxxs' style='height: 30px'> " +
                        "<div class='fxxs_fxxs_lt' style='width: 150px;text-align: left;color: #0b5cad'>" +
                        "<img style='padding-right: 5px' src='/oa/cloudstorage/image/share-ok_020f127.png'>成功创建连接 </div> " +
                        "<div class='fxxs_fxxs_rt'> <div class='jmqx'> </div> <div class='jmqx'> </div> </div> </div> " +
                        "<div class='fxxs_fxxs'> <div class='fxxs_fxxs_lt' style='width: 300px;padding-right: 10px;text-align: left'>" +
                        "<input value='" + url + "' readonly='true' style='height:30px;width: 300px' type='text'/></div> " +
                        "<div class='fxxs_fxxs_rt'> <div class='jmqx' > " +
                        "<button id='copyButton' style='border-radius: 5px;background: rgb(63, 116, 186);border-width: initial;border-style: none;border-color: initial;border-image: initial;height: 30px;width: 100px;color: white;cursor: pointer;' >" +
                        "复制到剪切板</button> </div> </div> " + str +
                        "</div> </div>")
                    $j(".anniu").append("<div class='cjlj'> <button onclick='cannelCopy()' class='btn2' style='float: right'>关闭</button> </div>")
                    $j("#copyButton").bind("click", function () {
                        window.clipboardData.setData("text", url);
                    })
                }
            }
            ajax.call("/oa/cloudstorage/usercloud/sharefiles?folderIds=" + folderIdsStr + "&fileIds=" + fileIds + "&foerverRadio=" + foerverRadio + "&validay=" + validay + "&encrypt=" + encrypt + "&shareName=" + shareName);
        }
    })

}

/**
 * 改变风格
 */
function changeStyle(obj) {
    if ($j(obj).attr("styleTag") == "0") {
        $j("#table1").css("display", "none");
        $j("#table2").css("display", "")
        $j(obj).attr("styleTag", "1");
        $j(".coltd").css("display", "none");
        $j("#allseltd").attr("colspan", $j(".coltd").length + 1);
        Cyan.Arachne.form.tag = 1;
    } else {
        $j("#table1").css("display", "");
        $j("#table2").css("display", "none")
        $j(obj).attr("styleTag", "0");
        $j(".coltd").css("display", "");
        $j("#allseltd").attr("colspan", "");
        Cyan.Arachne.form.tag = 0;
    }
}


/**
 * 风格一选择文件
 */
function filesSelect(obj) {
    //重命名按钮
    var renameEle = $j("#rename");
    var singleCheck = $j("input[type='checkbox']").not("[id]");
    //已选中的文件夹
    var checkedFolders = $j("#floderIds");
    //已选中的文件
    var checkedFiles = $j("#fileIds");
    //已选中的分享文件
    var checkedshares = $j("#shareIds");

    var style2Check = $j("a[code='" + $j(obj).val() + "']")
    if ($j(obj).attr("checked")) {
        //另外一个风格也选中
        style2Check.css("display", " block");
        style2Check.css("background", "url(/oa/cloudstorage/image/xmxz_03.png)");
        style2Check.attr("state", "1");
        style2Check.parent().css({"background": "#f1f5fa", "border-radius": "5px"})
    } else {
        style2Check.css({"background": "", "display": ""})
        style2Check.parent().css({"background": "", "border-radius": ""})
        style2Check.attr("state", "0");
    }

    var checkedBox = $j("input[type='checkbox']:checked").not("[id]");

    var checklen = checkedBox.length;
    if ($j("#typeNum").val() == "6") {
        if (checklen > 0) {
            $j("#allfiletr").css("display", "")
        } else {
            $j("#allfiletr").css("display", "none")
        }
    }

    if (checklen > 1) {
        renameEle.attr("disabled", "disabled");
        renameEle.css("color", "darkgray")
        //遍历已选中的

    } else {
        renameEle.removeAttr("disabled")
        renameEle.css("color", "#3f74ba")
    }

    if (singleCheck.length > checklen) {
        $j("#allcheck").removeAttr("checked");
    }

    if (singleCheck.length === checklen) {
        $j("#allcheck").attr("checked", "true");
    }

    var folderIds = new String();
    var fileIds = new String();
    var shareIds = new String();
    checkedBox.each(function () {
        var checkbox = $j(this);
        if (checkbox.attr("class") == "folder") {
            folderIds = folderIds + checkbox.val() + ",";
        } else if (checkbox.attr("class") == "file") {
            fileIds = fileIds + checkbox.val() + ",";
        } else {
            shareIds = shareIds + checkbox.val() + ",";
        }
    })

    folderIds = folderIds.substring(0, folderIds.length - 1);
    fileIds = fileIds.substring(0, fileIds.length - 1);
    shareIds = shareIds.substring(0, shareIds.length - 1);
    checkedFolders.val(folderIds);
    checkedFiles.val(fileIds);
    checkedshares.val(shareIds);
}


/**
 * 风格二选择文件
 * @param obj
 */
function selectFiles(obj) {
    var renameEle = $j("#rename");
    var a2 = $j("a[code]");
    //已选中的文件夹
    var checkedFolders = $j("#floderIds");
    //已选中的文件
    var checkedFiles = $j("#fileIds");
    //已选中的文件
    var checkedShares = $j("#shareIds");
    if ($j(obj).attr("state") == "0") {
        $j(obj).css( "display","block");
        $j(obj).css("background","url(/oa/cloudstorage/image/xmxz_03.png)");
        $j(obj).parent().css({"background": "#f1f5fa", "border-radius": "5px"})
        $j(obj).attr("state", "1");
        $j("input[type='checkbox'][value='" + $j(obj).attr("code") + "']").attr("checked", "true");
        if ($j("#typeNum").val() == "6") {
            $j("#allfiletr").css("display", "");
        }
    } else {
        $j(obj).css({"background": "", "display": ""})
        $j(obj).parent().css({"background": "", "border-radius": ""})
        $j(obj).attr("state", "0");
        $j("input[type='checkbox'][value='" + $j(obj).attr("code") + "']").removeAttr("checked");
        if ($j("#typeNum").val() == "6") {
            $j("#allfiletr").css("display", "none");
        }
    }
    var selectedA = $j("a[state='1']");
    var selectedLen = selectedA.length;
    if (selectedLen > 1) {
        renameEle.attr("disabled", "disabled");
        renameEle.css("color", "darkgray")
        //遍历已选中的

    } else {
        renameEle.removeAttr("disabled")
        renameEle.css("color", "#3f74ba")
    }

    if (a2.length > selectedLen) {
        $j("#allcheck").removeAttr("checked");
    }

    if (a2.length === selectedLen) {
        $j("#allcheck").attr("checked", "true");
    }

    var folderIds = new String();
    var fileIds = new String();
    var shareIds = new String();
    selectedA.each(function () {
        var selectedEle = $j(this);
        if (selectedEle.attr("class") == "a2 folder") {
            folderIds = folderIds + selectedEle.attr("code") + ",";
        } else if(selectedEle.attr("class") == "a2 file"){
            fileIds = fileIds + selectedEle.attr("code") + ",";
        }else{
            shareIds = shareIds + selectedEle.attr("code") + ",";
        }
    })

    folderIds = folderIds.substring(0, folderIds.length - 1);
    fileIds = fileIds.substring(0, fileIds.length - 1);
    shareIds = shareIds.substring(0, shareIds.length - 1);
    checkedFolders.val(folderIds);
    checkedFiles.val(fileIds);
    checkedShares.val(shareIds)
}

/**
 * 搜索
 */
function searchByName() {
    var typeName = $j("#typeName").text();
    var searchVal = $j("#searchVal");
    // Cyan.Arachne.form.folderId = $j("#floderId").val();
    Cyan.Arachne.form.searchName = searchVal.val();
    toHomePage({
        searchName: searchVal.val(), target: "main1", callback: function () {
            if (typeName) {
                $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
            }
            checkTheStyle()
        }
    });
}

/**
 * 全选按钮
 */
function allcheckSelect() {
    var renameEle = $j("#rename");
    //已选中的文件夹
    var checkedFolders = $j("#floderIds");
    //已选中的文件
    var checkedFiles = $j("#fileIds");
    //已选中的分享文件
    var checkShares = $j("#shareIds");

    var a2 = $j("a[state]");
    var folderIds = new String();
    var fileIds = new String();
    var shareIds = new String();
    if ($j("#allcheck").is(':checked')) {
        $j("input[type='checkbox']").attr("checked", "true");
        renameEle.attr("disabled", "disabled");
        renameEle.css("color", "lightgray");
        a2.parent().css({"background": "#f1f5fa", "border-radius": "5px"})
        a2.css("display", "block");
        a2.css("background", "url(/oa/cloudstorage/image/xmxz_03.png)");
        a2.attr("state", "1");
        if ($j("#typeNum").val() == "6") {
            $j("#allfiletr").css("display", "");
        }
    } else {
        $j("input[type='checkbox']").removeAttr("checked");
        renameEle.removeAttr("disabled")
        renameEle.css("color", "#3f74ba")
        a2.css({"background": "", "display": ""});
        a2.parent().css({"background": "", "border-radius": ""});
        a2.attr("state", "0");
        if ($j("#typeNum").val() == "6") {
            $j("#allfiletr").css("display", "none");
        }
    }
    var checkedBox = $j("input[type='checkbox']:checked").not("[id]");
    checkedBox.each(function () {
        var checkbox = $j(this);
        if (checkbox.attr("class") == "folder") {
            folderIds = folderIds + checkbox.val() + ",";
        } else if (checkbox.attr("class") == "file") {
            fileIds = fileIds + checkbox.val() + ",";
        } else {
            shareIds = shareIds + checkbox.val() + ",";
        }
    })

    folderIds = folderIds.substring(0, folderIds.length - 1);
    fileIds = fileIds.substring(0, fileIds.length - 1);
    shareIds = shareIds.substring(0, shareIds.length - 1);
    checkedFolders.val(folderIds);
    checkedFiles.val(fileIds);
    checkShares.val(shareIds);
}

/**
 * 取消分享
 */
function channelShare() {
    var typeName = $j("#typeName").text();
    var checkShares = $j("#shareIds").val();
    if (checkShares == null || "" == checkShares) {
        errorMsg("请选择文件");
    } else {
        var ajax = new Cyan.Ajax();
        ajax.setRequestHeader("Accept", "text/json");
        ajax.method = "GET";
        ajax.handleObject = function (ret) {
            if (ret) {
                errorMsg("取消分享成功", function () {
                    toHomePage({
                        target: "main1", callback: function () {
                            if (typeName) {
                                $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                            }
                            $j("#allfiletr").css("display", "");
                            checkTheStyle()
                        }

                    });
                })
            } else
                errorMsg("取消分享失败");
        }
        ajax.call("/oa/cloudstorage/usercloud/usercloudcrud/channelShareFiles?shareIds=" + checkShares);
    }
}


function moveFile(seledFolderId) {
    var nowfolderId = $j("#floderId").val();
    if (nowfolderId == seledFolderId) {
        errorMsg("不能移动到同一目录下");
        return;
    }
    var typeName = $j("#typeName").text();
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (ret) {
        if (ret) {
            errorMsg("已移动到目标文件夹", function () {
                cannelCopy();
                toHomePage({
                    target: "main1", callback: function () {
                        if (typeName) {
                            $j("#navList").append("<div id='typeName'>" + typeName + "</div>")
                        }
                        checkTheStyle()
                    }
                });
            })
        } else
            errorMsg("移动失败");
    }
    ajax.call("/oa/cloudstorage/usercloud/moveToTargetFolder?folderIds=" + folderIdsStr + "&fileIds=" + fileIds + "&seledFolderId=" + seledFolderId);
}

/**
 * 弹出框
 * @param msg
 * @param callback
 */
function errorMsg(msg, callback) {
    $j("#errorCont").text(msg);
    $j("#qxzwj").fadeIn(300, callback).delay(1000).fadeOut(300);
}

/**
 * 根据样式去变化
 */
function checkTheStyle() {
    var renameEle = $j("#rename");
    var styleTag = $j("#changeStyle").attr("styleTag")
    if (styleTag == "1") {
        $j(".coltd").css("display", "none");
        $j("#allseltd").attr("colspan", $j(".coltd").length + 1);
    } else {
        $j(".coltd").css("display", "");
        $j("#allseltd").attr("colspan", "");
    }
    renameEle.css("color", "#3f74ba")
}

/**
 * 展示分享信息
 * @param shareId
 */
function showShareById(shareId) {
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    Cyan.Arachne.form.fileIds = fileIds;
    Cyan.Arachne.form.folderIds = folderIdsStr;
    System.showModal("/oa/cloudstorage/usercloud/showSharePage?id=" + shareId, function (ret) {
            if (ret) {
                errorMsg("分享成功！");
            }
        }
    )
}

function displayImg(obj) {
    $j(obj).find("img[fxtp]").css("display", "");
}

function noDisplayImg(obj) {
    $j(obj).find("img[fxtp]").css("display", "none");
}

function gotoshowListPage(shareId) {
    var typeName = $j("#typeName").text();
    Cyan.Arachne.form.shareId = shareId;
    Cyan.Arachne.form.type = 6;
    toHomePage({
        target: "main1", callback: function () {
            Cyan.Arachne.form.type = null;
            checkTheStyle()
        }
    });
}

/**
 * 我的分享
 */
function gotoSharePage() {
    var fileTypeBut = $j(".typeBut[fileType=6]").click();
}

function newFolderBySeled() {

    var code = $j("#seledFolder").val();
    var seled = $j(".seled[code=" + code + "]");
    var name = seled.text();
    var seledParent = seled.parent();
    if(seled.length==0){
        errorMsg("请选择建立路径！")
    }
    if ($j("#newf").length > 0) {
        $j("#newf:text").select();
    } else {
        if (seledParent.attr("id") == "root") {
            seledParent.append("<ul><li style='line-height: 36px;padding-left: 40px'><a href='#'>" +
                "<img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" +
                "<input style='width: 152px;height: 22px;border: none;box-shadow: 0 0 6px #2db4e0' id='newf' value='新建文件夹'/>" +
                "<img onclick='createCopyFolder(" + code + ")' style='margin:10px 0 0 5px' class='copyimg' src='/oa/cloudstorage/image/dc_03.png'><img onclick='removeCopythis(this," + code + ",null)' style='margin:10px 0 0 5px;'  class='copyimg' src='/oa/cloudstorage/image/dc_05.png'></a></li> " +
                "</ul>");
            $j("#newf").focus();
            $j("#newf:text").select();
        } else {
            var parentUl = seledParent.parent();
            parentUl.attr("class", "fzd")
            seledParent.remove();
            parentUl.append(
                "<li id='root'>" +
                "<span class='seled' code=" + code + " onclick='spanToggle(this)'>" +
                "<img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'>" + seled.text() + "</span>" +
                "<ul><li style='line-height: 36px;padding-left: 40px'><a href='#'>" +
                "<img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" +
                "<input style='width: 152px;height: 22px;border: none;box-shadow: 0 0 6px #2db4e0' id='newf' value='新建文件夹'/>" +
                "<img onclick='createCopyFolder(" + code + ")' style='margin:10px 0 0 5px' class='copyimg' src='/oa/cloudstorage/image/dc_03.png'><img onclick=removeCopythis(this,'" + code + ",'" + null + ") style='margin:10px 0 0 5px;'  class='copyimg' src='/oa/cloudstorage/image/dc_05.png'></a></li> "
            )

            parentUl.find("span[code=" + code + "]").css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");

            var childButA = $j(".seled");
            childButA.bind("click", function () {
                $j(".seled").css("background", "");
                $j(this).css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");
                $j("#seledFolder").val($j(this).attr("code"))
            });
            $j("#newf").focus();
            $j("#newf:text").select();
        }
    }
}

/**
 * 异步加载树
 */
function getFolderTreeChildren(type) {
    var top = $j("body").height() / 2 - 353 / 2 + "px";
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var name = new String()
    if (type == 1) {
        name = "复制";
    } else {
        name = "移动";
    }
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (result) {
        $j("#superDiv").prepend("<div class='tk' style='top: " + top + ";'><div class='tk_bt'>" + name + "到</div><div class='fxxs'>" +
            "<ul class='fzd'><li  id='root'><span state='1' class='seled'code= '0' onclick='spanToggle(this)'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>全部文件</span>" + result + "</ul>" +
            "</div><div class='anniu'> <div class='xjwj'> <button onclick='newFolderBySeled()'>新建文件夹</button> </div> <div class='cjlj'> " +
            "<button id='copy'>确定</button> <button class='btn2' onclick='cannelCopy()'>取消</button> </div> </div></div><input type='hidden' id='seledFolder' value=''> <div class='hsbj'></div>");

        var childButA = $j(".seled");
        childButA.bind("click", function () {
            $j(".seled").css("background", "");
            $j(this).css("background", "#dfe5ea url(/oa/cloudstorage/image/sj_03.png) no-repeat 14px center");
            $j("#seledFolder").val($j(this).attr("code"))
        });
        var copy = $j("#copy");
        copy.bind("click", function () {
            var seledFolder = $j("#seledFolder").val();
            if (seledFolder == null || seledFolder == "") {
                errorMsg("请选择要" + name + "的文件夹!");
            } else if (type == 1) {
                copyFile(seledFolder);
            } else {
                moveFile(seledFolder);

            }
        });
    }
    ajax.call("/oa/cloudstorage/usercloud/createFolderTreeChildren?parentId=0&folderIds=" + folderIdsStr + "&fileIds=" + fileIds);
}