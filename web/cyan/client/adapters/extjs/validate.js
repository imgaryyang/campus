Cyan.onload(function ()
{
    //标识校验模式为校验所有控件，提示所有错误信息
    if (Cyan.Arachne)
        Cyan.Arachne.checkType = 1;
    setTimeout(function ()
    {
        Ext.QuickTips.init(true);
    }, 10);

    Cyan.attach(window, "resize", Cyan.Validator.resetPosition);
    Cyan.attach(window, "scroll", Cyan.Validator.resetPosition);
});

Cyan.Validator.resetPositionFunctions = [];

Cyan.Validator.resetPosition = function ()
{
    Cyan.Array.forEach(Cyan.Validator.resetPositionFunctions, function (f)
    {
        f();
    });
};

/**
 * 错误控件的style
 */
Cyan.Validator.INVALIDSTYLE =
        "background:#fff url(" + Cyan.getRealPath("../extjs/resources/images/default/grid/invalid_line.gif") +
        ") repeat-x bottom;border:1px solid #dd7870;";
Cyan.Validator.INVALIDSTYLE = new Cyan.Style(Cyan.Validator.INVALIDSTYLE);
Cyan.Validator.INVALIDICONCLASS = "x-form-invalid-icon";
Cyan.Validator.INVALIDTIPCLASS = "x-form-invalid-tip";
Cyan.Validator.INVALIDUNDERMESSAGECLASS = "x-form-invalid-msg";

/**
 * 标识一个控件为错误控件
 * @param element 控件
 * @param error 错误信息
 * @param callback 回调函数，标识完之后的动作
 */
Cyan.Validator.markInvalid = function (element, error, callback)
{
    element = Cyan.$(element);
    if (element)
    {
        Cyan.Validator.markInvalidStyle(element);
        var msgTarget = element.getAttribute("msgtype");
        if (!msgTarget)
            msgTarget = Ext.form.Field.prototype.msgTarget;
        if (msgTarget == "qtip")
            Cyan.Validator.markInvalidMessage(element, error);
        else if (msgTarget == "title")
        {
            if (element.originalTitle$ == null)
                element.originalTitle$ = element.title ? element.title : "";
            element.title = error;
        }
        else if (msgTarget == "under")
            Cyan.Validator.markInvalidUnderMessage(element, error);
        else if (msgTarget == "side")
            Cyan.Validator.markInvalidSideMessage(element, error);
        else
            Cyan.Validator.markInvalidIcon(element, error);
        if (callback)
            callback();
    }
    else
    {
        Cyan.error(error, callback);
    }
};

Cyan.Validator.markInvalidStyle = function (element)
{
    if (element.nodeName == "TEXTAREA" ||
            element.nodeName == "INPUT" && (element.type == "text" || element.type == "password" ))
    {
        if (element.originalCssText$ == null)
            element.originalCssText$ = element.style.cssText;
        Cyan.Validator.INVALIDSTYLE.set(element);
    }
};

Cyan.Validator.markInvalidIcon = function (element, error)
{
    var element0 = Cyan.Validator.getFirstComponent(element);
    if (element.parentNode && element.parentNode.nodeName == "SPAN")
        element = element.parentNode;
    else
        element = Cyan.Validator.getLastComponent(element);
    if (!element0.invalidIcon)
    {
        element0.invalidIcon = document.createElement("SPAN");
        element0.invalidIcon.className = Cyan.Validator.INVALIDICONCLASS;
        var position = Cyan.Elements.getPosition.apply(element.nodeType == 3 ? element.previousSibling : element);
        var width = element.nodeType == 3 ? Cyan.Elements.getComponentSize(element.previousSibling).width + 50 :
                Cyan.Elements.getComponentSize(element).width;
        element0.invalidIcon.style.left = (position.x + width + 4) + "px";
        element0.invalidIcon.style.top = position.y + "px";
        document.body.appendChild(element0.invalidIcon);
    }
    element0.invalidIcon.style.visibility = "visible";
    Cyan.Validator.markInvalidMessage(element0.invalidIcon, error);
};

Cyan.Validator.markInvalidMessage = function (element, error)
{
    element.qtip = error;
    if (!element.qclass)
    {
        element.qclass = Cyan.Validator.INVALIDTIPCLASS;
        Ext.QuickTips.enable();
    }
};

Cyan.Validator.markInvalidUnderMessage = function (element, error)
{
    var showErrorTo = element.showErrorTo || element.getAttribute && element.getAttribute("showErrorTo");
    var element0 = Cyan.Validator.getFirstComponent(element);
    element = Cyan.Validator.getLastComponent(element);

    if (showErrorTo)
        element = Cyan.$(showErrorTo);

    if (!element0.underMessage)
    {
        element0.underMessage = document.createElement("DIV");
        element0.underMessage.style.clear = "both";
        if (element.nextSibling)
            element.parentNode.insertBefore(element0.underMessage, element.nextSibling);
        else
            element.parentNode.appendChild(element0.underMessage);
        element0.underMessage.className = Cyan.Validator.INVALIDUNDERMESSAGECLASS;
        element0.underMessage.appendChild(document.createTextNode(""));
    }
    element0.underMessage.childNodes[0].nodeValue = error;
    element0.underMessage.style.display = "";
};

Cyan.Validator.markInvalidSideMessage = function (element, error)
{
    var element0 = Cyan.Validator.getFirstComponent(element);
    element = Cyan.Validator.getLastComponent(element);

    var showErrorTo = element.showErrorTo || element.getAttribute("showErrorTo");
    if (showErrorTo)
        element = Cyan.$(showErrorTo);

    if (!element0.sideMessage)
    {
        element0.sideMessage = document.createElement("DIV");
        element0.sideMessage.className = Cyan.Validator.INVALIDUNDERMESSAGECLASS;

        element0.sideMessage.style.position = "absolute";

        var f = function ()
        {
            var position = Cyan.Elements.getPosition(element.nodeType == 3 ? element.previousSibling : element);
            var width = element.nodeType == 3 ? Cyan.Elements.getComponentSize(element.previousSibling).width + 50 :
                    Cyan.Elements.getComponentSize(element).width;
            element0.sideMessage.style.left = (position.x + width + 4) + "px";
            element0.sideMessage.style.top = position.y + "px";
        };
        f();

        document.body.appendChild(element0.sideMessage);
        Cyan.Validator.resetPositionFunctions.push(f);
    }
    element0.sideMessage.innerHTML = error;
    element0.sideMessage.style.display = "";
};


Cyan.Validator.clearInvalid = function (element, callback)
{
    Cyan.Validator.clearInvalidStyle(element);
    Cyan.Validator.clearInvalidTip(element);
    if (element.originalTitle$ != null)
        element.title = element.originalTitle$;
    element = Cyan.Validator.getFirstComponent(element);
    if (element.invalidIcon)
        element.invalidIcon.style.visibility = "hidden";
    if (element.underMessage)
        element.underMessage.style.display = "none";
    if (element.sideMessage)
        element.sideMessage.style.display = "none";
    if (callback)
        callback();
};

Cyan.Validator.clearInvalidStyle = function (element)
{
    if (element.originalCssText$ != null)
        element.style.cssText = element.originalCssText$;
};

Cyan.Validator.clearInvalidTip = function (element)
{
    if (element.qtip)
        element.qtip = "";
};

Cyan.Validator.showInvalid = function (element, error, callback)
{
    if (element)
    {
        Cyan.Validator.markInvalidMessage(element, error);
        var position = Cyan.Elements.getPosition.apply(element);
        var tip = Ext.QuickTips.getQuickTip();
        tip.onTargetOver(new Cyan.Extjs.Event(element, position.x +
        Cyan.Elements.getComponentSize(element).width / 4 -
        5, position.y - 5));
        tip.show();
        if (callback)
            callback();
        Cyan.Validator.clearInvalidTip(element);
    }
    else
    {
        Cyan.error(error, callback);
    }
};

Cyan.Validator.isAutoValidate = function ()
{
    return true;
};

