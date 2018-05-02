Cyan.importJs("/platform/flow/actions.js");
Cyan.importJs("/platform/group/member.js");
Cyan.importJs("/platform/commons/filestore/filestore.js");
Cyan.importJs("/platform/commons/pdf.js");
Cyan.importJs("/platform/fileupload/richupload.js");
var userSelectScopName = "流程用户选择";

Cyan.onload(function ()
{
    if (Cyan.Valmiki && Cyan.Valmiki.ParallelText)
    {
        Cyan.Valmiki.ParallelText.prototype.getThumbUrl = function (item)
        {
            var s = Cyan.Arachne.formURI + "/" + Cyan.Arachne.form.stepId + "/";
            if (this.formName)
                s += "form/" + this.formName + "/";
            else
                s += "component/";
            s += encodeURIComponent(this.fullName) + "/" + item.id + "/thumb";
            return s;
        };
        Cyan.Valmiki.ParallelText.prototype.onImageUpload = function ()
        {
            var parallelText = this;
            saveData(function ()
            {
                parallelText.reset();
                parallelText.loadImage();
            });
        };
        Cyan.Valmiki.ParallelText.prototype.showImage = function (item)
        {
            var s = Cyan.Arachne.formURI + "/" + Cyan.Arachne.form.stepId + "/";
            if (this.formName)
                s += "form/" + this.formName + "/";
            else
                s += "component/";
            s += encodeURIComponent(this.fullName) + "/" + item.id + "/image";

            System.showImage(Cyan.formatUrl(s));
        };
    }

    initExtensions();
    Cyan.importJs("/platform/opinion/opinion.js");

    Cyan.Class.overwrite(window, "save", function (options)
    {
        var filter0;

        if (options.nocheckRequired)
            filter0 = Cyan.Validator.filter;
        try
        {
            if (options.nocheckRequired)
            {
                Cyan.Validator.filter = function (validator)
                {
                    return validator.name != "require";
                };
            }
            this.inherited(options);
        }
        finally
        {
            if (options.nocheckRequired)
                Cyan.Validator.filter = filter0;
        }
    });

    window.save0 = window.save;
    window.save = function ()
    {
        save0({
            callback: function ()
            {
                Cyan.message("保存成功", exit);
            },
            progress: true,
            nocheckRequired: true,
            form: Cyan.$$("form")[0]
        });
    };

    window.saveData = function (callback, nocheckRequired, progress, error)
    {
        if (progress == null)
            progress = true;

        if (Cyan.Arachne.form.editable)
        {
            save0({
                callback: callback,
                error: error,
                progress: progress,
                wait: true,
                form: Cyan.$$("form")[0],
                nocheckRequired: nocheckRequired || false
            });
        }
        else
        {
            callback();
        }
    };

    Cyan.Class.overwrite(window, "execute", function (actionId, prompt)
    {
        var execute = this.inherited;

        var f = function ()
        {
            saveData(function ()
            {
                execute(actionId, {
                    callback: function (result)
                    {
                        if (result)
                        {
                            var receiverSelects = result.list;

                            if (receiverSelects && receiverSelects.length != null)
                            {
                                if (receiverSelects.length == 0)
                                {
                                    Cyan.message("下环节没有接收者，请联系管理员检查流程配置和用户权限");
                                }
                                else
                                {
                                    var selectable = false;
                                    if (receiverSelects.length > 1 ||
                                            receiverSelects[0].receiver &&
                                            receiverSelects[0].receiver.indexOf("&") == 0)
                                    {
                                        for (var i = 0; i < receiverSelects.length; i++)
                                        {
                                            var receiverSelect = receiverSelects[i];
                                            var selectType = receiverSelect.selectType;
                                            if (selectType && selectType != "no" ||
                                                    receiverSelect.receiver &&
                                                    receiverSelect.receiver.indexOf("&") == 0)
                                            {
                                                selectable = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (selectable)
                                    {
                                        selectReceiver(receiverSelects, actionId);
                                    }
                                    else
                                    {
                                        send(receiverSelects, actionId);
                                    }
                                }
                            }
                            else
                            {
                                disposeResult(result);
                            }
                        }
                    },
                    wait: true
                });
            });
        };

        if (prompt)
        {
            Cyan.confirm(prompt, function (ret)
            {
                if (ret == "ok")
                {
                    f();
                }
            });
        }
        else
        {
            f();
        }
    });

    Cyan.Class.overwrite(window, "send", function (receiverSelects, actionId)
    {
        for (var i = 0; i < receiverSelects.length; i++)
        {
            var receiverSelect = receiverSelects[i];
            receiverSelect.selectMode = null;
            receiverSelect.selectType = null;
        }
        this.inherited(receiverSelects, actionId, {
            callback: function (result)
            {
                disposeResult(result);
            },
            wait: true
        });
    });

    Cyan.Class.overwrite(window, "sendToAllNextNodes", function ()
    {
        if (checkData("sendToAllNextNodes"))
        {
            var sendToAllNextNodes = this.inherited;
            saveData(function ()
            {
                sendToAllNextNodes({
                    callback: function (result)
                    {
                        disposeResult(result);
                    },
                    wait: true
                });
            });
        }
    });

    Cyan.Class.overwrite(window, "turn", function ()
    {
        if (checkData("turn", true))
        {
            var turn = this.inherited;
            var selected = [];
            if (Cyan.Arachne.form.test)
                selected.push({type: "user", id: Cyan.Arachne.form.userId, name: System.userName});
            selectUser(selected, function (users)
            {
                if (users && users.length)
                {
                    var receivers = new Array(users.length);
                    for (var i = 0; i < users.length; i++)
                    {
                        var user = users[i];
                        receivers[i] = {receiver: user.id, properties: {deptId: user.deptId}};
                    }

                    saveData(function ()
                    {
                        turn(receivers, {
                            callback: function (result)
                            {
                                disposeResult(result);
                            },
                            wait: true
                        });
                    });
                }
            }, userSelectScopName);
        }
    });

    Cyan.Class.overwrite(window, "copy", function ()
    {
        if (checkData("copy", false))
        {
            var copy = this.inherited;
            var selected = [];
            if (Cyan.Arachne.form.test)
                selected.push({type: "user", id: Cyan.Arachne.form.userId, name: System.userName});
            selectUser(selected, function (users)
            {
                if (users && users.length)
                {
                    var receivers = new Array(users.length);
                    for (var i = 0; i < users.length; i++)
                    {
                        var user = users[i];
                        receivers[i] = {receiver: user.id, properties: {deptId: user.deptId}};
                    }

                    saveData(function ()
                    {
                        copy(receivers, {
                            callback: function (result)
                            {
                                disposeResult(result);
                            },
                            wait: true
                        });
                    });
                }
            }, userSelectScopName);
        }
    });

    Cyan.Class.overwrite(window, "pass", function ()
    {
        if (checkData("pass", false))
        {
            var pass = this.inherited;
            var selected = [];
            if (Cyan.Arachne.form.test)
                selected.push({type: "user", id: Cyan.Arachne.form.userId, name: System.userName});
            selectUser(selected, function (users)
            {
                if (users && users.length)
                {
                    var receivers = new Array(users.length);
                    for (var i = 0; i < users.length; i++)
                    {
                        var user = users[i];
                        receivers[i] = {receiver: user.id, properties: {deptId: user.deptId}};
                    }

                    saveData(function ()
                    {
                        pass(receivers, {
                            callback: function (result)
                            {
                                if (result)
                                {
                                    disposeResult(result);
                                }
                                else
                                {
                                    Cyan.message("传阅成功");
                                }
                            },
                            wait: true
                        });
                    });
                }
            }, userSelectScopName);
        }
    });

    Cyan.Class.overwrite(window, "cancelSend", function ()
    {
        this.inherited(function (result)
        {
            if (Cyan.isBoolean(result))
            {
                if (result)
                {
                    location.reload();
                }
                else
                {
                    Cyan.message("文件已被下环节接收，不能撤回");
                }
            }
            else
            {
                disposeResult(result);
            }
        });
    });

    Cyan.Class.overwrite(window, "end", function ()
    {
        if (checkData("end", false))
        {
            var end = this.inherited;
            saveData(function ()
            {
                end({
                    callback: function ()
                    {
                        Cyan.message("操作成功", function ()
                        {
                            exit();
                        });
                    },
                    wait: true
                });
            });
        }
    });

    Cyan.Class.overwrite(window, "stop", function ()
    {
        if (checkData("stop", true))
        {
            var stop = this.inherited;
            Cyan.confirm("确定要办结此事项?此操作将不能恢复。", function (ret)
            {
                if (ret == "ok")
                {
                    saveData(function ()
                    {
                        stop({
                            callback: function ()
                            {
                                Cyan.message("操作成功", function ()
                                {
                                    exit();
                                });
                            },
                            wait: true
                        });
                    });
                }
            });
        }
    });

    Cyan.Class.overwrite(window, "back", function (preStepIds, message)
    {
        var back = this.inherited;

        var f = function ()
        {
            if (message)
            {
                saveData(function ()
                {
                    back(preStepIds, message, {
                        callback: function (result)
                        {
                            disposeBackResult(result);
                        },
                        wait: true
                    });
                });
            }
            else
            {
                saveData(function ()
                {
                    System.Opinion.edit(null, function (result)
                    {
                        if (result != null)
                        {
                            back(preStepIds, result, function (result)
                            {
                                disposeBackResult(result);
                            });
                        }
                    });
                    System.Opinion.title = "输入打回意见";
                    System.Opinion.remark = "请输入打回意见";
                });
            }
        };

        if (preStepIds && preStepIds.length)
        {
            f();
        }
        else
        {
            getPreStepsCount({
                callback: function (preStepsCount)
                {
                    if (preStepsCount <= 1)
                    {
                        f();
                    }
                    else
                    {
                        window.showPreSteps({
                            target: "_modal",
                            callback: function (ret)
                            {
                                if (ret)
                                {
                                    preStepIds = ret;
                                    f();
                                }
                            }
                        });
                    }
                },
                form: "",
                obj: {stepId: Cyan.Arachne.form.stepId}
            });
        }
    });

    Cyan.Class.overwrite(window, "accept", function ()
    {
        this.inherited({
            callback: function (result)
            {
                if (result)
                    window.location.reload();
            },
            wait: true
        });
    });

    if (Cyan.Arachne.form.jsFiles)
    {
        Cyan.each(Cyan.Arachne.form.jsFiles, function ()
        {
            Cyan.importJs(this);
        });
    }

    if (Cyan.Valmiki && Cyan.Valmiki.FileList2)
    {
        Cyan.Valmiki.FileList2.prototype.onUpload = function ()
        {
            saveData();
        };
    }
    if (Cyan.Valmiki && Cyan.Valmiki.FileList)
    {
        Cyan.Valmiki.FileList.prototype.onUpload = function (item)
        {
            var fileList = this;
            saveData(null, false, true, function (error)
            {
                if (error)
                {
                    fileList.remove(item.id);
                    Cyan.Arachne.error(error.message);
                }
            });
        };

        Cyan.Valmiki.FileList.prototype.onDelete = function ()
        {
            saveData(null, false, false);
        };

        var htmlExtNames = ["txt", "doc", "xls", "docx", "xlsx", "csv", "wps", "zip", "rar"];
        var imgExtNames = ["jpg", "jpeg", "gif", "bmp", "png"];

        Cyan.Class.overwrite(Cyan.Valmiki.FileList, "getActions", function (item)
        {
            var actions = this.inherited(item);

            if (item.file)
                return;

            var fileName = item.fileName;
            var extName = Cyan.getExtName(fileName);
            if (extName)
            {
                extName = extName.toLowerCase();
                if (Cyan.Array.contains(htmlExtNames, extName))
                {
                    if (!actions)
                        actions = [];

                    actions.push({
                        text: "预览",
                        action: function ()
                        {
                            window.open(item.url + "/html");
                        }
                    })
                }
                else if (Cyan.Array.contains(imgExtNames, extName))
                {
                    if (!actions)
                        actions = [];

                    actions.push({
                        text: "预览",
                        action: function ()
                        {
                            System.showImage(item.url);
                        }
                    });
                }
                else if (extName == "pdf")
                {
                    if (!actions)
                        actions = [];

                    actions.push({
                        text: "预览",
                        action: function ()
                        {
                            System.showPdf(item.url);
                        }
                    });
                }
                if ((extName == "doc" || extName == "wps" || extName == "docx" || extName == "xls" ||
                        extName == "xlsx") && this.writable)
                {
                    if (!actions)
                        actions = [];

                    actions.push({
                        text: "编辑",
                        action: function ()
                        {
                            System.openPage(item.url + "/edit");
                        }
                    });
                }
            }

            return actions;
        });

        Cyan.Class.overwrite(Cyan.Valmiki.FileList, "getBottomActions", function ()
        {
            var actions = this.inherited();
            if (!actions)
                actions = [];

            var fileList = this;

            if (this.writable)
            {
                var batchId = Cyan.generateId("fileupload_batch");
                actions.push({
                    text: "批量上传",
                    id: batchId
                });

                setTimeout(function ()
                {
                    var upload = new System.RichUpload();
                    upload.autoUploadNextFile = false;
                    var button = Cyan.$(batchId);
                    upload.bindButton(button);
                    upload.displayProgressIn();
                    upload.addListener({
                        onselect: function (file)
                        {
                        },
                        onsuccess: function (file, result)
                        {
                            var percentageDiv = Cyan.$$$(file.id).$(".richupload_file_percentage");
                            percentageDiv.html("正在保存文件");
                            saveData(function ()
                            {
                                uploadFiles([result], fileList.formName, fileList.fullName, {
                                    progress: false,
                                    callback: function ()
                                    {
                                        percentageDiv.html("完成");
                                        upload.uploadNextFile();
                                    },
                                    error: function (error)
                                    {
                                        percentageDiv.html(error.message);
                                    }
                                });
                            }, true, false);
                        },
                        oncomplete: function (file)
                        {
                        },
                        onstop: function ()
                        {
                        },
                        onok: function ()
                        {
                        }
                    });
                }, 50);

                actions.push({
                    text: "从资料中心添加",
                    action: function ()
                    {
                        System.FileStore.selectFile(function (fileIds)
                        {
                            if (fileIds)
                            {
                                saveData(function ()
                                {
                                    addFiles(fileIds, fileList.formName, fileList.fullName);
                                });
                            }
                        });
                    }
                });

                actions.push({
                    text: "排序",
                    action: function ()
                    {
                        var url = fileList.items[0].url;
                        var index = url.lastIndexOf("/");
                        url = url.substring(0, index);
                        index = url.lastIndexOf("/");

                        var encodedId = url.substring(index + 1, url.length);
                        System.showModal("/attachment/crud.sort?encodedId=" + encodedId, function ()
                        {
                            refresh(fileList.formName, fileList.fullName);
                        });
                    }
                });
            }

            actions.push({
                text: "打包下载",
                action: function ()
                {
                    var name = fileList.name;
                    var index = name.lastIndexOf(".");
                    if (index > 0)
                        name = name.substring(index + 1);

                    var url = fileList.items[0].url;
                    index = url.lastIndexOf("/");
                    url = url.substring(0, index) + "?name=" + encodeURIComponent(name);

                    window.open(url);
                }
            });

            return actions;
        });

        Cyan.Class.overwrite(Cyan.Valmiki.FileList, "init", function ()
        {
            this.inherited();

            if (!this.items || this.items.length <= 1)
            {
                var fileList = this;
                Cyan.$$(this.getDiv()).$(".valmiki_filelist_bottom button").each(function ()
                {
                    if (this.innerHTML == "打包下载" && (!fileList.items || !fileList.items.length) ||
                            this.innerHTML == "排序")
                    {
                        this.style.display = "none";
                    }
                });
            }
        });

        Cyan.Class.overwrite(Cyan.Valmiki.FileList, "reset", function (items)
        {
            this.inherited(items);
            Cyan.$$(this.getDiv()).$(".valmiki_filelist_bottom button").each(function ()
            {
                if (this.innerHTML == "排序")
                {
                    if (items && items.length > 1)
                        this.style.display = "";
                    else
                        this.style.display = "none";
                }
                else if (this.innerHTML == "打包下载")
                {
                    if (items && items.length > 0)
                        this.style.display = "";
                    else
                        this.style.display = "none";
                }
            });
        });
    }
});

function initExtensions()
{
    for (var extensionName in Cyan.Arachne.form.extensions)
    {
        var extension = Cyan.Arachne.form.extensions[extensionName];
        for (var name in extension)
        {
            if (name != "$class$" && name != "jsFile")
                Cyan.Arachne.form[name] = extension[name];
        }
    }
}

function isEnd(receiverSelects)
{
    for (var i = 0; i < receiverSelects.length; i++)
    {
        if (receiverSelects[i].nodeId == "#end")
            return true;
    }

    return false;
}

function getReceiverNames(receiverList)
{
    var receiverNames = "";
    for (var i = 0; i < receiverList.length; i++)
    {
        var receiverName = receiverList[i].receiverName;
        receiverList[i].receiverName = null;
        if (receiverName)
        {
            if (receiverNames)
                receiverNames += ",";
            else
                receiverNames = "";
            receiverNames += receiverName;
        }
    }

    return receiverNames;
}

function disposeResult(result)
{
    if (result)
    {
        if (Cyan.isString(result))
        {
            Cyan.message(result);
        }
        else if (Cyan.isBoolean(result) && result)
        {
            window.location.reload();
        }
        else if (result.receiverList)
        {
            var receiverNames = getReceiverNames(result.receiverList);

            var s;
            if (receiverNames)
                s = "已经成功发送给" + receiverNames;
            else if (isEnd(result.receiverList))
                s = "办理结束";
            else
                s = "操作成功";

            Cyan.message(s, exit);
        }
    }
}

function disposeBackResult(result)
{
    if (result)
    {
        if (result.receiverList)
        {
            var receiverNames = getReceiverNames(result.receiverList);
            Cyan.message("已退回给" + receiverNames, exit);
        }
        else
        {
            disposeResult(result);
        }
    }
}

function selectReceiver(receiverSelects, actionId)
{
    window.receiverSelects = receiverSelects;

    var memberReceiverSelect = null;
    var memberReceiverSelectIndex = 0;
    for (var i = 0; i < receiverSelects.length; i++)
    {
        var select = receiverSelects[i];
        if (select.receiver && select.receiver.indexOf("&") == 0)
        {
            if (memberReceiverSelect)
            {
                memberReceiverSelect = null;
                break;
            }
            else
            {
                memberReceiverSelect = select;
                memberReceiverSelectIndex = i;
            }
        }
        else
        {
            var selectType = select.selectType;
            if (selectType && selectType != "no")
            {
                memberReceiverSelect = null;
                break;
            }
        }
    }

    if (memberReceiverSelect)
    {
        System.selectMembers({
            scopeId: memberReceiverSelect.properties.scopeId,
            scopeName: userSelectScopName,
            types: memberReceiverSelect.receiver.length == 1 ? "user" : "dept",
            deptId: memberReceiverSelect.properties.deptId || Cyan.Arachne.form.businessDeptId,
            callback: function (result)
            {
                if (result)
                {
                    if (result.length)
                    {
                        if (memberReceiverSelect.maxSelectCount > 0 &&
                                result.length > memberReceiverSelect.maxSelectCount)
                        {
                            Cyan.message("环节“" + memberReceiverSelect.nodeName + "”最多只能有" +
                            memberReceiverSelect.maxSelectCount + "个接收者");
                            return;
                        }

                        if (memberReceiverSelect.minSelectCount > 0 &&
                                result.length < memberReceiverSelect.minSelectCount)
                        {
                            Cyan.message("环节“" + memberReceiverSelect.nodeName + "”至少要选择" +
                            memberReceiverSelect.minSelectCount + "个接收者");
                            return;
                        }


                        for (var i = 0; i < result.length; i++)
                        {
                            var member = result[i];
                            var receiver = member.id;
                            if (memberReceiverSelect.receiver.length > 1)
                                receiver += memberReceiverSelect.receiver;
                            var select = {
                                nodeId: memberReceiverSelect.nodeId,
                                receiver: receiver, receiverName: member.name,
                                properties: {deptId: member.deptId}
                            };

                            if (i == 0)
                                receiverSelects[memberReceiverSelectIndex] = select;
                            else
                                Cyan.Array.insert(receiverSelects, memberReceiverSelectIndex + i, select);
                        }
                        send(receiverSelects, actionId);
                    }
                    else
                    {
                        Cyan.message("请至少选择一个接收者");
                    }
                }
            }
        });
    }
    else
    {
        window.receiverSelect(actionId, {
            target: "_modal",
            form: "",
            obj: {stepId: Cyan.Arachne.form.stepId},
            callback: function (receiverSelects)
            {
                if (receiverSelects)
                {
                    send(receiverSelects, actionId);
                }
            }
        });
    }
}

function copyReply()
{
    saveData(function ()
    {
        reply({
            callback: function ()
            {
                Cyan.message("已经回复给" + Cyan.Arachne.form.sourceName, exit);
            },
            wait: true
        });
    });
}

function passReply()
{
    saveData(function ()
    {
        reply({
            callback: function ()
            {
                Cyan.message("已经回复给" + Cyan.Arachne.form.sourceName, exit);
            },
            wait: true
        });
    });
}

function passEnd()
{
    end();
}

function getBusinessDeptId()
{
    return Cyan.Arachne.form.businessDeptId;
}

function exit()
{
    System.closePage();
}

function track()
{
    System.openPage("/flow/track/step/" + Cyan.Arachne.form.stepId);
}