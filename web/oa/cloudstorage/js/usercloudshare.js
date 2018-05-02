function showFileAndFolderByShareId(shareId) {
    Cyan.Arachne.form.shareId = shareId;
    Cyan.Arachne.form.nowFolderId = null;
    toGetUrlPage({
        target: "main1", callback: function () {
        }
    })

}

function gotoFolderPage(folderId) {
    var shareId = $j("#shareId").val();
    Cyan.Arachne.form.shareId = shareId;
    Cyan.Arachne.form.nowFolderId = folderId;
    toGetUrlPage({
        target: "main1", callback: function () {
        }
    })
}


/**
 * 全选按钮
 */
function allcheckShareSelect() {
    var renameEle = $j("#rename");
    //已选中的文件夹
    var checkedFolders = $j("#floderIds");
    //已选中的文件
    var checkedFiles = $j("#fileIds");
    //已选中的分享文件
    var checkShares = $j("#shareIds");

    var a2 = $j("a[state]");
    if ($j("#allcheck").is(':checked')) {
        $j("input[type='checkbox']").attr("checked", "true");
        renameEle.attr("disabled", "disabled");
        renameEle.css("color", "lightgray");
        a2.css({"background": "url(/oa/cloudstorage/image/xmxz_03.png)", "display": " block"});
        a2.parent().css({"background": "#f1f5fa", "border-radius": "5px"})
        a2.attr("state", "1");
        $j("#btu").css("display","")
    } else {
        $j("input[type='checkbox']").removeAttr("checked");
        renameEle.removeAttr("disabled")
        renameEle.css("color", "#3f74ba")
        a2.css({"background": "", "display": ""});
        a2.parent().css({"background": "", "border-radius": ""});
        a2.attr("state", "0");
        $j("#btu").css("display","none")
    }
    var folderIds = new String();
    var fileIds = new String();
    var shareIds = new String();
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
 * 风格一选择文件
 */
function filesShareSelect(obj) {
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
        style2Check.css({"background": "url(/oa/cloudstorage/image/xmxz_03.png)", "display": " block"});
        style2Check.parent().css({"background": "#f1f5fa", "border-radius": "5px"})
        style2Check.attr("state", "1");

    } else {
        style2Check.css({"background": "", "display": ""})
        style2Check.parent().css({"background": "", "border-radius": ""})
        style2Check.attr("state", "0");
    }

    var checkedBox = $j("input[type='checkbox']:checked").not("[id]");

    var checklen = checkedBox.length;

    if(checklen>0 ){
        $j("#btu").css("display","")
    }else {
        $j("#btu").css("display","none")
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
 * 保存到云盘
 */
function saveToMyCloud() {
    if (checkFile()) {
        // apeendCopyView(1)
        getFolderTreeChildren(1);
    } else {

        errorMsg("请选择文件！")
    }
}

function checkFile() {
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var shareIds = $j("#shareIds").val();
    if ((folderIdsStr == null || folderIdsStr == "") && (fileIds == null || fileIds == "") && (shareIds == null || shareIds == "")) {
        return false;
    } else {
        return true;
    }
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
 * 异步加载树
 */
function getFolderTreeChildren(type) {
    var top = $j("body").height() / 2 - 353 / 2 + "px";
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var name = new String()
    if (type == 1) {
        name = "保存";
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
    // var nowfolderId = $j("#floderId").val();
    // if (nowfolderId == seledFolderId) {
    //     errorMsg("不能复制到同一目录下");
    //     return;
    // }
    var shareIds = $j("#shareIds").val();
    var typeName = $j("#typeName").text();
    var folderIdsStr = $j("#floderIds").val();
    var fileIds = $j("#fileIds").val();
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = function (ret) {
        if (ret) {
            errorMsg("保存成功", function () {
                cannelCopy();
            })
        } else
            errorMsg("保存失败");
    }
    ajax.call("/oa/cloudstorage/usercloud/copyToTargetFolder?folderIds=" + folderIdsStr + "&fileIds=" + fileIds + "&seledFolderId=" + seledFolderId + "&shareIds=" + shareIds);
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
function removeCopythis(obj, code, name) {
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


function createFolder(parentId, folderName, options) {
    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "text/json");
    ajax.method = "GET";
    ajax.handleObject = options.callback;
    ajax.call("/oa/cloudstorage/usercloud/usercloudcrud/createFolder?parentId=" + parentId + "&folderName=" + folderName);
}

function downFile() {
    if (checkFile()) {
        var folderIdsStr = $j("#floderIds").val();
        var fileIds = $j("#fileIds").val();
        var shareIds = $j("#shareIds").val();
        window.location.href = "/oa/cloudstorage/usercloud/zip?folderIds=" + folderIdsStr + "&fileIds=" + fileIds + "&shareIds=" + shareIds;
    } else {
        errorMsg("请选择文件！")
    }
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
                "<img onclick='createCopyFolder(" + code + ")' style='margin:10px 0 0 5px' class='copyimg' src='/oa/cloudstorage/image/dc_03.png'><img onclick=removeCopythis(this," + code + ",'" + name + "') style='margin:10px 0 0 5px;'  class='copyimg' src='/oa/cloudstorage/image/dc_05.png'></a></li> "
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
