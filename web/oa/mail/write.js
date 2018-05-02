Cyan.importJs("/platform/fileupload/richupload.js");

System.isEmail = function (s)
{
    return /^(\w|[\u4e00-\u9fa5])+((-(\w|[\u4e00-\u9fa5])+)|(\.(\w|[\u4e00-\u9fa5])+))*@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.exec(s);
};

function hasOutterMail()
{
    var b = Cyan.each(System.ReceiverInput.instances, function ()
    {
        var items = this.items;
        for (var i = 0; i < items.length; i++)
        {
            if (items[i].valid)
            {
                var address = items[i].simpleValue;
                if (!address.endsWith("@local"))
                    return true;
            }
        }
    });
    return !!b;
}

function displayMailFrom()
{
    var mailFromDiv = Cyan.$("mailFromDiv");
    if (mailFromDiv)
        mailFromDiv.style.display = hasOutterMail() ? "" : "none";
}

function isOutterMailSupported()
{
    return Cyan.$("mailFromDiv") != null || Cyan.Arachne.form.localServerSupported;
}

Cyan.onload(function ()
{
    Cyan.$$(".mail_receiver").each(function ()
    {
        var s;
        if (this.name == "body.mailTo")
            s = "收件人";
        else if (this.name == "body.cc")
            s = "抄送";
        else if (this.name == "body.sc")
            s = "密送";
        this.title = "输入用户姓名或者拼音可快速查找用户,点击左边的" + s + "可以在组织机构和通讯录中选择接收人";
        var receiverInput = new System.ReceiverInput("email");
        receiverInput.isValid = System.isEmail;
        receiverInput.create(this);
        receiverInput.onchange = displayMailFrom;
    });

    displayMailFrom();

    Cyan.$$(".ccbutton").onclick(function ()
    {
        var div = Cyan.$("ccdiv");
        if (div.style.display == "none")
        {
            div.style.display = "";
            Cyan.Elements.setText(this, "删除抄送");
        }
        else
        {
            div.style.display = "none";
            System.ReceiverInput.instances["body.cc"].clear();
            Cyan.Elements.setText(this, "添加抄送");
        }
    });

    Cyan.$$("#scbutton").onclick(function ()
    {
        var div = $("scdiv");
        if (div.style.display == "none")
        {
            div.style.display = "";
            Cyan.Elements.setText(this, "删除密送");
        }
        else
        {
            div.style.display = "none";
            System.ReceiverInput.instances["body.sc"].clear();
            Cyan.Elements.setText(this, "添加密送");
        }
    });

    window.save0 = window.save;
    Cyan.Class.overwrite(window, "save", function ()
    {
        this.inherited({
            callback: function ()
            {
                Cyan.message("保存成功", function ()
                {
                    System.getMenuByUrl("/oa/mail/list?type=draft").go();
                    System.closePage();
                });
            },
            progress: true
        });
    });

    Cyan.Class.overwrite(window, "send", function ()
    {
        var receiverInput = System.ReceiverInput.instances["body.mailTo"];
        if (!receiverInput.items.length)
        {
            Cyan.message("请填写收件人", function ()
            {
                receiverInput.focus();
            });
            return;
        }

        if (!Cyan.$("body.title").value)
        {
            Cyan.message("请填写标题", function ()
            {
                Cyan.focus("body.title");
            });
            return;
        }

        if (!checkReceiverInput(receiverInput))
            return;

        receiverInput = System.ReceiverInput.instances["body.cc"];
        if (receiverInput && !checkReceiverInput(receiverInput))
            return;

        receiverInput = System.ReceiverInput.instances["body.sc"];
        if (receiverInput && !checkReceiverInput(receiverInput))
            return;

        if (hasOutterMail())
        {
            if (!isOutterMailSupported())
            {
                Cyan.message("未开通外部邮件,收件人中不能包含外部邮箱");
                return;
            }

            if (!Cyan.Arachne.form.localServerSupported && !$("body.accountId").value)
            {
                Cyan.message("收件人中包含外部邮箱,请先定义一个支持SMTP功能的外部邮箱");
                return;
            }
        }

        this.inherited({
            callback: function ()
            {
                saveOutterMail(function ()
                {
                    Cyan.customConfirm(
                            [
                                {
                                    text: "返回收件箱",
                                    callback: function ()
                                    {
                                        var menu = System.getMenuByUrl("/oa/mail/list?type=received");
                                        if (!menu)
                                            menu = System.getMenuByUrl("/oa/mail/list?type=received&showReply=false");
                                        if (menu)
                                            menu.go();
                                        System.closePage();
                                    }
                                },
                                {
                                    text: "继续写信",
                                    callback: function ()
                                    {
                                        window.location.href = System.formatUrl("/oa/mail/new");
                                    }
                                }
                            ],
                            {
                                message: "发送成功",
                                width: 300,
                                close: function ()
                                {
                                    System.getMenuByUrl("/oa/mail/list?type=received").go();
                                    System.closePage();
                                }
                            }
                    );
                });
            },
            progress: true
        });
    });

    attachments.bind(Cyan.$("attachment_add"));
    attachments.onselect = function (fileName)
    {
        if (!Cyan.$("title").value)
        {
            var index = fileName.lastIndexOf(".");
            if (index > 0)
                fileName = fileName.substring(0, index);
            Cyan.$("title").value = fileName;
        }
    };

    var upload = new System.RichUpload();
    upload.autoUploadNextFile = false;
    upload.bindButton(Cyan.$("attachment_addbatch"));
    upload.displayProgressIn();
    upload.addListener({
        onselect: function (file)
        {
        },
        onsuccess: function (file, result)
        {
            var percentageDiv = Cyan.$$$(file.id).$(".richupload_file_percentage");
            percentageDiv.html("正在保存文件");

            uploadFiles([result], {
                callback: function (mailId)
                {
                    percentageDiv.html("完成");
                    if (!Cyan.Arachne.form.mailId)
                        Cyan.Arachne.form.mailId = mailId;
                    reloadAttachments(function ()
                    {
                        upload.uploadNextFile();
                    });
                },
                error: function (error)
                {
                    percentageDiv.html(error.message);
                },
                form: Cyan.$$("form")[0]
            });
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

    Cyan.imagePaste(function (file, extName)
    {
        var formData = new FormData();
        formData.append("body.title", Cyan.$("body.title").value);
        Cyan.prompt("请如入文件名", "", function (ret)
        {
            if (ret)
            {
                uploadFile(file, ret + "." + extName, {
                    form: formData,
                    callback: function (mailId)
                    {
                        if (!Cyan.Arachne.form.mailId)
                            Cyan.Arachne.form.mailId = mailId;
                        reloadAttachments();
                    }
                })
            }
        });
    });
});

function checkReceiverInput(receiverInput)
{
    var s = "";
    var errorAddressList = receiverInput.getErrorReceiverList();
    if (errorAddressList && errorAddressList.length)
    {
        for (var i = 0; i < errorAddressList.length; i++)
        {
            if (s)
                s += ",";
            s += errorAddressList[i];
        }

        Cyan.message("请正确填写以下地址\n" + s, function ()
        {
            receiverInput.focus();
        });
        return false;
    }
    else
    {
        return true;
    }
}

function openReceiverDialog(field)
{
    System.ReceiverInput.instances["body." + field].openSelectDialog();
}

function reloadAttachments(callback)
{
    getAttachmentItems(function (items)
    {
        attachments.setItems(items);
        if (callback)
            callback();
    });
}

function addAttachments()
{
    System.FileStore.selectFile(function (fileIds)
    {
        if (fileIds)
        {
            addFiles(fileIds, {
                callback: function (mailId)
                {
                    if (!Cyan.Arachne.form.mailId)
                        Cyan.Arachne.form.mailId = mailId;
                    reloadAttachments();
                },
                form: Cyan.$$("form")[0],
                wait: true
            });
        }
    });
}

function cameraAttachments()
{
    System.camera(function (images)
    {
        if (images && images.length)
        {
            uploadFiles(Cyan.get(images, "id"), {
                callback: function (mailId)
                {
                    Cyan.message("保存照片成功", function ()
                    {
                        if (!Cyan.Arachne.form.mailId)
                            Cyan.Arachne.form.mailId = mailId;
                        reloadAttachments();
                    });
                },
                form: Cyan.$$("form")[0],
                wait: true
            });
        }
    });
}

function getOutterMails()
{
    var outterMails = [];
    Cyan.each(System.ReceiverInput.instances, function ()
    {
        var items = this.items;
        for (var i = 0; i < items.length; i++)
        {
            if (items[i].valid)
            {
                var address = items[i].simpleValue;
                if (!address.endsWith("@local"))
                {
                    outterMails.add(items[i]);
                }
            }
        }
    });
    return outterMails;
}

function saveOutterMail(callback)
{
    var mails = getOutterMails();
    var notExistsMails = [];
    Cyan.lazyEach(mails, function (callback)
    {
        var mail = this;
        Cyan.Arachne.get("/oa/address/card.count", {
            type: "user",
            email: mail.simpleValue,
            pageSize: -1
        }, function (result)
        {
            if (!result)
                notExistsMails.push(mail);

            callback();
        });
    }, function ()
    {
        if (notExistsMails.length)
        {
            Cyan.confirm("将" + Cyan.map(notExistsMails, "value").join(",") + "保存到通讯录中？", function (ret)
            {
                if (ret == "ok")
                {
                    Cyan.lazyEach(notExistsMails, function (mail, callback)
                    {
                        var email = mail.simpleValue;
                        var name = mail.text || mail.value;

                        Cyan.Arachne.post("/oa/address/card", {
                            type: "user",
                            new$: true,
                            entity: {
                                cardName: name,
                                attributes: {
                                    email: email
                                }
                            }
                        }, callback);

                    }, function ()
                    {
                        callback();
                    });
                }
                else
                {
                    callback();
                }
            });
        }
        else
        {
            callback();
        }
    });
}

function preview()
{
    save0({
        callback: function (mailId)
        {
            if (!Cyan.Arachne.form.mailId)
                Cyan.Arachne.form.mailId = mailId;
            reloadAttachments(function ()
            {
                System.openPage("/oa/mail/show/" + mailId);
            });
        },
        progress: true
    });
}