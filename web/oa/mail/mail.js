Cyan.importJs("widgets/tooltip.js");

var tip;
var currentMail;

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "catalog", function (catalogId, mailId)
    {
        if (mailId)
        {
            this.inherited(catalogId, mailId, {
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        System.getMenuByUrl("/oa/mail/list").goWith({catalogId: catalogId}, catalogId);
                        System.closePage();
                    });
                }
            });
        }
        else if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要归档的邮件");
        }
        else
        {
            catalogAll(catalogId, {
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        refresh();
                    });
                }
            });
        }
    });

    Cyan.Class.overwrite(window, "mark", function (markId, mailId)
    {
        if (mailId)
        {
            this.inherited(markId, mailId, {
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        var mail = Cyan.Arachne.form.mail;
                        var url = "/oa/mail/list";
                        if (mail.deleted)
                            System.getMenuByUrl(url + "?deleted=true").go();
                        else if (mail.catalogId)
                            System.getMenuByUrl(url).goWith({catalogId: mail.catalogId}, mail.catalogId);
                        else
                            System.getMenuByUrl(url + "?type=" + mail.type).go();
                    });
                }
            });
        }
        else if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要标记的邮件");
        }
        else
        {
            markAll(markId, {
                form: $$("form")[0],
                callback: function ()
                {
                    refresh();
                }
            });
        }
    });

    Cyan.Class.overwrite(window, "markDeleted", function (mailId)
    {
        if (mailId)
        {
            this.inherited(mailId, {
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    System.getMenuByUrl("/oa/mail/list?deleted=true").go();
                    System.closePage();
                }
            });
        }
        else if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要删除的邮件");
        }
        else
        {
            Cyan.confirm("确定要删除所选择的邮件", function (ret)
            {
                if (ret == "ok")
                {
                    markDeletedAll({
                        form: Cyan.$$("form")[0],
                        callback: function ()
                        {
                            refresh();
                        }
                    });
                }
            });
        }
    });

    Cyan.Class.overwrite(window, "restore", function (mailId)
    {
        if (mailId)
        {
            this.inherited(mailId, {
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    Cyan.message("操作成功", function ()
                    {
                        var mail = Cyan.Arachne.form.mail;
                        var url = "/oa/mail/list";
                        if (mail.catalogId)
                            System.getMenuByUrl(url).goWith({catalogId: mail.catalogId}, mail.catalogId);
                        else
                            System.getMenuByUrl(url + "?type=" + mail.type).go();

                        System.closePage();
                    });
                }
            });
        }
        else if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要还原的邮件");
        }
        else
        {
            restoreAll({
                form: Cyan.$$("form")[0],
                callback: function ()
                {
                    refresh();
                }
            });
        }
    });

    Cyan.Class.overwrite(window, "remove", function (mailId)
    {
        this.inherited(mailId, {
            form: Cyan.$$("form")[0],
            callback: function ()
            {
                Cyan.message("操作成功", function ()
                {
                    System.closePage();
                });
            }
        });
    });

    Cyan.Class.overwrite(window, "replyQuickly", function ()
    {
        this.inherited({
            form: Cyan.$$("form")[0],
            callback: function ()
            {
                var content = Cyan.$("reply_content");
                content.style.display = "none";
                Cyan.$("reply_result").style.display = "block";
                Cyan.$("reply").value = "";
            }
        });
    });

    Cyan.Class.overwrite(window, "forward", function (bodyId)
    {
        openWritePage("/oa/mail/" + bodyId + "/forward");
    });

    Cyan.Class.overwrite(window, "back", function ()
    {
        var back = this.inherited;
        if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要撤回的邮件");
        }
        else
        {
            Cyan.confirm("确定要撤回所选择的邮件", function (ret)
            {
                if (ret == "ok")
                {
                    back({
                        form: Cyan.$$("form")[0],
                        callback: function ()
                        {
                            refresh();
                        }
                    });
                }
            });
        }
    });

    initEmails("from");
    initEmails("to");
    initEmails("cc");
    initEmails("sc");

    initBlockQuote();
    initMailTos();

    Cyan.Class.overwrite(window, "setReaded", function ()
    {
        if (Cyan.$$("#keys").checkeds().length == 0)
        {
            Cyan.message("请选择要设置为已读的邮件");
            return;
        }

        this.inherited(function ()
        {
            refresh();
        });
    });
});

function showMail(mailId)
{
    System.openPage("/oa/mail/show/" + mailId);
    return false;
}

function writeMail(mailId)
{
    openWritePage("/oa/mail/write/" + mailId);
    return false;
}

function displayReply()
{
    Cyan.$("reply_result").style.display = "none";
    Cyan.$("reply_content").style.display = "block";
}

function replyTo(mailId, mailTo)
{
    if (!mailTo && currentMail)
        mailTo = currentMail.email;
    reply(mailId, mailTo);
}

function reply(mailId, mailTo)
{
    if (!mailId)
        mailId = Cyan.Arachne.form.mail.mailId;

    var url = "/oa/mail/" + mailId + "/reply";
    if (mailTo)
        url += "?mailTo=" + encodeURIComponent(mailTo);
    openWritePage(url);
}

function replyAll(mailId)
{
    openWritePage("/oa/mail/" + mailId + "/replyAll");
}

function resend(bodyId)
{
    openWritePage("/oa/mail/" + bodyId + "/resend");
}

function trace(bodyId)
{
    System.openPage("/oa/mail/trace/" + bodyId);
}

function mailTo(mailTo)
{
    if (!mailTo && currentMail)
        mailTo = currentMail.email;

    openWritePage("/oa/mail/new?mailTo=" + encodeURIComponent(mailTo));
}

function refMail(ref)
{
    if (!ref && currentMail)
        ref = currentMail.address;

    var pageId = "mail_relationShip";
    var page = System.getPage(pageId);
    if (page)
    {
        var win = page.getWindow();
        win.Cyan.Arachne.form.ref = ref;
        page.show();
    }
    else
    {
        System.openPage("/oa/mail/list?ref=" + encodeURIComponent(ref), pageId);
    }
}

function showReplies(bodyId)
{
    var pageId = "mail_replies";
    var page = System.getPage(pageId);
    if (page)
    {
        var win = page.getWindow();
        win.Cyan.Arachne.form.refBodyId = bodyId;
        page.show();
    }
    else
    {
        System.openPage("/oa/mail/list?refBodyId=" + encodeURIComponent(bodyId) + "&type=received", pageId);
    }
}

function addToAddress()
{
    System.showModal("/oa/address/card.new?type=user&entity.cardName=" + encodeURIComponent(currentMail.name) +
    "&entity.attributes.email=" + encodeURIComponent(currentMail.address));
}

function openWritePage(url)
{
    System.openPage(System.getMenuByUrl("/oa/mail/new").formatUrl(url));
}

function initEmails(div)
{
    if (!div)
        return;

    div = Cyan.$(div);

    if (!div)
        return;

    var emails = div.childNodes[0].nodeValue;
    div.innerHTML = "";

    parseEmailList(emails).each(function ()
    {
        var span = document.createElement("SPAN");
        span.className = "email";

        span.appendChild(document.createTextNode(this.name));

        div.appendChild(span);

        var email = this;
        $$(span).attach("mouseover", function ()
        {
            if ((email.server == "local" || email.server == Cyan.Arachne.form.domain) && !email.deptName)
            {
                hideTip();
                getDeptNameForUser(email.address, {
                    callback: function (deptName)
                    {
                        email.deptName = deptName;
                        showTip(email, span);
                    },
                    error: function ()
                    {
                    },
                    obj: {}
                });
            }
            else
            {
                showTip(email, span);
            }
        });
        $$(span).attach("click", Cyan.Event.stop);
    });
}

function showTip(email, span)
{
    currentMail = email;

    if (!tip)
    {
        tip = new Cyan.ToolTip();
        var html = "<div id='tip'>";

        html += "<div id='email_name'></div>";
        html += "<div id='email_remark'></div>";

        html += "<div id='email_operators'>";
        html += "<span>[<a href='#' onclick='replyTo();hideTip();'>回复此人</a>]</span>";
        html += "<span>[<a href='#' onclick='hideTip();mailTo();'>写信</a>]</span>";
        html += "<span>[<a href='#' onclick='hideTip();refMail();'>往来邮件</a>]</span>";
        html += "<span id='addToAddress'>[<a href='#' onclick='hideTip();addToAddress();'>添加到通讯录</a>]</span>";

        html += "</div></div>";

        tip.html = html;

        tip.create();

        $$("#tip").attach("click", Cyan.Event.stop);
        $$(document.body).attach("click", hideTip);
    }

    $("email_name").innerHTML = email.name;
    $("email_remark").innerHTML = email.deptName || email.address;

    $("addToAddress").style.display = email.server == "local" ? "none" : "";

    var position = Cyan.Elements.getPosition(span);
    var size = Cyan.Elements.getComponentSize(span);
    var x = position.x + size.width + 20, y = position.y + 20;

    tip.showAt(x, y);
}

function hideTip()
{
    if (tip)
        tip.hide();
}

function initBlockQuote()
{
    var blockQuote = $$("BLOCKQUOTE").first;

    if (blockQuote)
    {
        var div = document.createElement("DIV");

        div.innerHTML = "- 显示引用文字 -";
        div.className = "quote_handler";

        blockQuote.parentNode.insertBefore(div, blockQuote);
        blockQuote.style.display = "none";

        div.onclick = function ()
        {
            if (blockQuote.style.display == "none")
            {
                div.innerHTML = "- 隐藏引用文字 -";
                blockQuote.style.display = "block";
            }
            else
            {
                div.innerHTML = "- 显示引用文字 -";
                blockQuote.style.display = "none";
            }
        };
    }
}

function initMailTos()
{
    $$("A").each(function ()
    {
        var href = this.href;
        if (href.startsWith("mailto:"))
        {
            var mailto = decodeURI(href.substring(7).trim());
            this.onclick = function ()
            {
                mailTo(mailto);
                return false;
            };
        }
    });
}