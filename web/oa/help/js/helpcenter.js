/**
 * 帮助中心对象
 * @type {{open: Helper.open, toComPro: Helper.toComPro, refCom: Helper.refCom}}
 */
var Helper = {
    //打开帮助中心页面
    open: function () {
        window.open("/oa/help/compro")
    },
    //打开常见问题页面
    toComPro: function () {
        window.open("/oa/help/compro")
    },
    //控制搜索栏
    refCom: function (url) {
        var text = $("search").value;
        if (text && text != 'null') {
            if (text == '请输入关键字') {
                text = "";
            }
            if (!url) {
                url = "/oa/help/compro?search=" + text;
            } else {
                url += "&search=" + text;
            }
            Portal.redirect(encodeURI(url), "comproseg",
                function () {
                    $("search").value = text;
                });
        } else {
            $.error("搜索为空！");
        }
    }, 
    //初始化图标
    initIcon: function ()
    {
        System.addShortcut("help-center", "problem", "帮助中心", Helper.open);
    }
}