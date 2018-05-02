Cyan.Plain.importCss("validate");

Cyan.onload(function ()
{
    if (!Cyan.Validator)
        return;

    if (Cyan.Arachne)
        Cyan.Arachne.checkType = 1;

    Cyan.Validator.markInvalid = function (element, error, callback)
    {
        element = Cyan.$(element);
        if (element)
        {
            Cyan.Validator.markInvalidStyle(element);
            Cyan.Validator.markInvalidUnderMessage(element, error);

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
            Cyan.$$(element).addClass("invalid");
        }
    };

    Cyan.Validator.markInvalidUnderMessage = function (element, error)
    {
        var element0 = Cyan.Validator.getFirstComponent(element);
        element = Cyan.Validator.getLastComponent(element);

        if (element.getAttribute)
        {
            var showErrorTo = element.showErrorTo || element.getAttribute("showErrorTo");
            if (showErrorTo)
                element = Cyan.$(showErrorTo);
        }

        if (!element0.underMessage)
        {
            element0.underMessage = document.createElement("DIV");
            if (element.nextSibling)
                element.parentNode.insertBefore(element0.underMessage, element.nextSibling);
            else
                element.parentNode.appendChild(element0.underMessage);
            element0.underMessage.className = "invalid_msg";
        }
        if (element0.underMessage.childNodes.length)
            element0.underMessage.childNodes[0].nodeValue = error;
        else
            element0.underMessage.appendChild(document.createTextNode(error));
        element0.underMessage.style.display = "";
    };

    Cyan.Validator.clearInvalid = function (element, callback)
    {
        Cyan.Validator.clearInvalidStyle(element);
        var element0 = Cyan.Validator.getFirstComponent(element);
        if (element0.underMessage)
            element0.underMessage.style.display = "none";

        element = Cyan.Validator.getLastComponent(element);

        if (!element)
            return;

        if (element.getAttribute)
        {
            var showErrorTo = element.showErrorTo || element.getAttribute("showErrorTo");
            if (showErrorTo)
                element = Cyan.$(showErrorTo);
        }

        if (callback)
            callback();
    };

    Cyan.Validator.clearInvalidStyle = function (element)
    {
        Cyan.$$(element).removeClass("invalid");
    };

    Cyan.Validator.showInvalid = function (element, error, callback)
    {
        Cyan.error(error, callback);
    };

    Cyan.Validator.isAutoValidate = function ()
    {
        return false;
    };

    Cyan.each(document.getElementsByTagName("FORM"), function ()
    {
        Cyan.Validator.validate(this, 2);
    });
});