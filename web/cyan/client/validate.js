Cyan.Validator = {};

Cyan.Validator.getShowElement = function (element)
{
    return element.showElement || element.getAttribute("showElement");
};

Cyan.Validator.getShowName = function (element)
{
    if (!element)
        return "unknown";

    var elementName = element;
    element = Cyan.$(element);
    if (!element)
        return elementName;

    var showElement = Cyan.Validator.getShowElement(element);
    if (showElement)
        return Cyan.Validator.getShowName(showElement);

    var text, field = Cyan.isField(element);

    if (field)
        text = element.showName || element.getAttribute("showName");

    if (text == null)
    {
        //找前面的控件
        var node = element;
        while ((node = node.previousSibling) != null && text == null)
        {
            if (Cyan.Validator.isComponent(node))
                text = "";
            else if (node.nodeName != "SCRIPT")
                text = Cyan.Elements.eachElement(node, Cyan.Validator.getText);
        }
        if (text == null)
        {
            node = element.parentNode;
            if (node && node.nodeName != "BODY")
                text = Cyan.Validator.getShowName(node);
        }
    }
    if (field)
    {
        if (!text)
            text = "";
        element.showName = text;
    }
    return text;
};

Cyan.Validator.getText = function (element)
{
    if (!element)
        element = this;

    if (element.nodeType == 3 && element.parentNode.nodeName != "SCRIPT")
    {
        var text = element.nodeValue.replace(/\s|\t|\u00A0/g, "");
        if (Cyan.endsWith(text, ":") || Cyan.endsWith(text, "："))
            text = text.substring(0, text.length - 1).replace(/(\s|\t|\u00A0)*$/, "");

        if (text)
        {
            var n = text.length, b = false;
            for (var i = 0; i < n; i++)
            {
                var c = text.charCodeAt(i);
                if (c == 32 || c == 9 || c == 160)
                    b = true;
                else if (c < 256)
                {
                    b = false;
                    break;
                }
            }
            if (b)
                text = Cyan.replaceAll(Cyan.replaceAll(Cyan.replaceAll(text, " ", ""), "\t", ""), "\u00A0", "");
            return text;
        }
    }
    else if (Cyan.Validator.isComponent(element))
    {
        return "";
    }
};

Cyan.Validator.getLastComponent = function (element)
{
    if (Cyan.Validator.isButtonGroup(element))
    {
        var elements = element.form ? element.form[element.name] : element;
        if (elements.length)
        {
            Cyan.each(elements, function ()
            {
                if (this.nodeName == "INPUT" && this.type == element.type)
                    element = this;
            });
        }

        element = Cyan.Validator.getButtonAfterTextComponent(element);
    }
    return element;
};

Cyan.Validator.getButtonAfterTextComponent = function (element)
{
    var lastElement = null;
    var node = element;
    while ((node = node.nextSibling) != null && lastElement == null)
    {
        if (Cyan.Validator.isComponent(node))
        {
            lastElement = element;
        }
        else if (node.nodeName != "SCRIPT")
        {
            if (Cyan.Elements.eachElement(node, function ()
                    {
                        if (this.nodeType == 3 && this.nodeValue)
                            return true;
                    }))
            {
                lastElement = node;
            }
        }
    }
    if (lastElement == null)
    {
        node = element.parentNode;
        if (node && node.nodeName != "BODY")
        {
            lastElement = Cyan.Validator.getButtonAfterTextComponent(node);
        }
    }

    return lastElement;
};

Cyan.Validator.getFirstComponent = function (element)
{
    if (Cyan.Validator.isButtonGroup(element))
    {
        var elements = element.form ? element.form[element.name] : element;
        if (elements.length)
        {
            element = Cyan.searchFirst(elements, function ()
            {
                return this.nodeName == "INPUT" && this.type == element.type;
            });
        }
    }
    return element;
};

Cyan.Validator.isButtonGroup = function (element)
{
    return element.nodeName == "INPUT" && (element.type == "checkbox" || element.type == "radio");
};

Cyan.Validator.isComponent = function (element)
{
    return element.nodeName == "INPUT" || element.nodeName == "SELECT" || element.nodeName == "TEXTAREA" ||
            element.nodeName == "FORM";
};

Cyan.Validator.renderComponent = function (component)
{
    if (component.type == "checkbox" || component.type == "radio")
    {
        Cyan.attach(component, "click", Cyan.Validator.checkField);
        Cyan.attach(component, "change", Cyan.Validator.checkField);
    }
    else
    {
        var showElement = Cyan.Validator.getShowElement(component);
        var check;
        if (showElement)
        {
            showElement = Cyan.$(showElement);
            check = function ()
            {
                Cyan.Validator.checkField.call(component);
            };
        }
        else if (component.type != "hidden")
        {
            showElement = component;
            check = Cyan.Validator.checkField;
        }

        if (showElement)
        {
            Cyan.attach(showElement, "keyup", check);
            Cyan.attach(showElement, "change", check);
            Cyan.attach(showElement, "focus", check);
            Cyan.attach(showElement, "blur", check);
            component.oldValue = null;
        }
    }
};

Cyan.Validator.validate = function (element, type)
{
    element = element && element != document ? Cyan.$(element) : document.body;

    if (!type)
        type = 0;

    if (Cyan.isField(element))
    {
        if (element.name && !element.disabled)
        {
            var buttonGroup = Cyan.Validator.isButtonGroup(element);
            if (!buttonGroup)
            {
                var trim = element.getAttribute("trim");
                if (trim != null && trim.toLowerCase() != "false")
                {
                    var s = element.value.trim();
                    if (s != element.value)
                        element.value = s;
                }
            }
            else
            {
                element = Cyan.Validator.getFirstComponent(element);
            }

            var error;
            if (!Cyan.Validator.filter || Cyan.Validator.filter(Cyan.Validator.require))
            {
                error = Cyan.Validator.require.check(element);
                if (error)
                    return error;
            }

            if (!Cyan.Validator.filter || Cyan.Validator.filter(Cyan.Validator.notBlank))
            {
                error = Cyan.Validator.notBlank.check(element);
                if (error)
                    return error;
            }

            if (!buttonGroup && element.value == "")
                return null;

            var ajax = false;
            for (var i = 0; i < Cyan.Validator.validators.length; i++)
            {
                var validator = Cyan.Validator.validators[i];
                if (!Cyan.Validator.filter || Cyan.Validator.filter(validator))
                {
                    if (type == 2 || !(validator.isAjax && validator.isAjax()))
                    {
                        //只对即时校验作ajax校验
                        error = Cyan.Validator.validators[i].check(element);
                        if (error)
                            return error;

                        if (validator.isAjax && validator.isAjax() && validator.accept(element))
                            ajax = true;
                    }
                }
            }
            return ajax ? "ajax" : null;
        }
    }
    else
    {
        var result = true;
        var checkedNames = [];
        (element.nodeName == "FORM" ? Cyan.each : Cyan.Elements.eachElement)(element, function ()
        {
            if (Cyan.isField(this) && this.name)
            {
                if (type == 2)
                {
                    Cyan.Validator.renderComponent(this);
                }
                else
                {
                    if (Cyan.Validator.isButtonGroup(this))
                    {
                        if (Cyan.Array.contains(checkedNames, this.name))
                            return;
                        checkedNames.push(this.name);
                    }
                    var error = Cyan.Validator.validate(this, type);

                    Cyan.Validator.handleError(error, this, type, result);
                    if (error)
                    {
                        result = false;
                        if (type == 0)
                            return true;
                    }
                }
            }
        });
        return result;
    }
    return null;
};

Cyan.Validator.checkField = function ()
{
    if (this.type != "checkbox" && this.type != "radio")
    {
        if (this.oldValue == this.value)
            return;
        this.oldValue = this.value;
    }
    Cyan.Validator.handleError(Cyan.Validator.validate(this, 2), this, 2, false);
};

Cyan.Validator.handleError = function (error, element, type, focus)
{
    element = Cyan.$(element);
    if (element)
    {
        var showElement = Cyan.Validator.getShowElement(element);
        if (showElement)
            element = Cyan.$(showElement);
    }

    if (error == "ajax")
        return;
    var callback = element && focus && error ? function ()
    {
        Cyan.focus(element);
    } : null;


    if (error)
    {
        if (type == 0)
        {
            //在控件处显示错误信息
            Cyan.Validator.showInvalid(element, error, callback);
        }
        else
        {
            //将控件标记未错误控件
            Cyan.Validator.markInvalid(element, error);
            if (type == 1 && callback)
                Cyan.Validator.showInvalid(element, error, callback);
        }
    }
    else if (element != null)
    {
        //如果没有错误，清除错误标记
        Cyan.Validator.clearInvalid(element, callback);
    }
};

Cyan.Validator.showInvalid = function (element, error, callback)
{
    Cyan.error(error, callback);
};

Cyan.Validator.markInvalid = function (element, error, callback)
{
    Cyan.error(error, callback);
};

Cyan.Validator.clearInvalid = function (element, callback)
{
};

Cyan.Validator.errorHandler = function (error, element)
{
    Cyan.Validator.handleError(Cyan.Validator.changeMessage((error instanceof Error) ? error.message : error),
            element || error.component, 2, false);
};

Cyan.Validator.changeFileType = function (fileTypes)
{
    return fileTypes.replace("$image", "jpg jpeg gif bmp png").replace("$zip", "zip rar jar tar gz");
};

Cyan.Validator.checkFileType = function (fileName, fileTypes)
{
    var index = fileName.lastIndexOf(".");
    if (index > 0)
    {
        var ext = fileName.substring(index + 1).toLowerCase();
        fileTypes = Cyan.Validator.changeFileType(fileTypes).split(" ");
        for (var i = 0; i < fileTypes.length; i++)
        {
            if (ext == fileTypes[i].toLowerCase())
                return true;
        }
    }
    return false;
};

Cyan.Validator.compare = function (value1, value2)
{
    if (Cyan.Validator.isNumber(value1) && Cyan.Validator.isNumber(value2))
        return parseFloat(value1) - parseFloat(value2);
    else
        return value1 < value2 ? -1 : (value1 == value2 ? 0 : 1);
};

Cyan.Validator.isNumber = function (value)
{
    return /^[-\+]?[0-9]+(\.[0-9]+)?$/.test(value);
};

Cyan.Validator.isInteger = function (value)
{
    return /^[-\+]?[0-9]+$/.exec(value);
};

Cyan.Validator.isPositiveNumber = function (value)
{
    return /^[0-9]+(\.[0-9]+)?$/.exec(value);
};

Cyan.Validator.isPositiveInteger = function (value)
{
    return /^[0-9]+$/.exec(value);
};

Cyan.Validator.isEmail = function (value)
{
    return /^(\w|[\u4e00-\u9fa5])+((-(\w|[\u4e00-\u9fa5])+)|(\.(\w|[\u4e00-\u9fa5])+))*@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.exec(value);
};

Cyan.Validator.isIp = function (value)
{
    return /^(0|([1-9][0-9]{0,2}))(\.(0|([1-9][0-9]{0,2}))){3}$/.exec(value);
};

Cyan.Validator.checkDate = function (value, element, macther)
{
    var format;
    if (element)
        format = element.getAttribute("format");
    if (format)
        return Cyan.Date.match(value, format);
    else
        return macther(value);
};

Cyan.Validator.isDate = function (value, element)
{
    return Cyan.Validator.checkDate(value, element, Cyan.Date.matchDateFormat);
};

Cyan.Validator.isTime = function (value, element)
{
    return Cyan.Validator.checkDate(value, element, Cyan.Date.matchTimeFormat);
};

Cyan.Validator.isDateTime = function (value, element)
{
    return Cyan.Validator.checkDate(value, element, Cyan.Date.matchDateTimeFormat);
};
Cyan.Validator.isDatetime = function (value, element)
{
    return Cyan.Validator.isDateTime(value, element);
};

Cyan.Validator.isYearMonth = function (value, element)
{
    return Cyan.Validator.checkDate(value, element, Cyan.Date.matchYearMonthFormat);
};
Cyan.Validator.isYearmonth = function (value, element)
{
    return Cyan.Validator.isYearMonth(value, element);
};

Cyan.Validator.isMonthDay = function (value, element)
{
    return Cyan.Validator.checkDate(value, element, Cyan.Date.matchMonthDayFormat);
};
Cyan.Validator.isMonthday = function (value, element)
{
    return Cyan.Validator.isMonthDay(value, element);
};

Cyan.Validator.changeMessage = function (message)
{
    if (!message)
        return message;
    var index;
    while ((index = message.indexOf("${")) >= 0)
    {
        var s1 = message.substring(0, index);
        var s2 = message.substring(index + 2);
        index = s2.indexOf("}");
        if (index > 0)
        {
            var name = s2.substring(0, index);
            s2 = s2.substring(index + 1);
            message = s1 + Cyan.Validator.getShowName(name) + s2;
        }
        else
        {
            break;
        }
    }
    return message;
};


Cyan.Validator.registValidator = function (validator, name)
{
    if (validator instanceof Function && name)
        Cyan.Validator.validators.push(new Cyan.Validator.BaseValidator(name, validator));
    else if (validator.check instanceof Function)
        Cyan.Validator.validators.push(validator);
    else
        throw new Error("invalid validator");
};

Cyan.Validator.BaseValidator = function (name, checker, ajax)
{
    this.name = name;
    this.checker = checker;
    this.ajax = ajax;
};
Cyan.Validator.BaseValidator.prototype.check = function (element)
{
    var condition = this.getCondition(element);
    if (condition != null)
    {
        var r = this.checker(element.value, condition, element);
        if (r == null || Cyan.isString(r))
            return r;
        if (!r)
        {
            var error = element.getAttribute(this.name + "Error");
            if (!error)
                error = element.getAttribute("error");
            if (!error)
            {
                var f = Cyan.Validator["get" + this.name.charAt(0).toUpperCase() + this.name.substring(1) + "Error"] ||
                        Cyan.Validator["getError"];
                if (f)
                    error = f(Cyan.Validator.getShowName(element), element.value, condition, element);
                if (!error)
                    error = Cyan.Validator.getShowName(element) + " is invalid";
            }
            return error;
        }
    }
};

Cyan.Validator.BaseValidator.prototype.getCondition = function (element)
{
    var v = element[this.name];
    if (v != null)
        return v.toString();
    v = element[this.name.toLowerCase()];
    if (v != null)
        return v.toString();
    v = element.getAttribute(this.name);
    if (v != null)
        return v.toString();
    v = element.getAttribute(this.name.toLowerCase());
    if (v != null)
        return v.toString();
    return null;
};

Cyan.Validator.BaseValidator.prototype.accept = function (element)
{
    return this.getCondition(element) != null;
};

Cyan.Validator.BaseValidator.prototype.isAjax = function ()
{
    return this.ajax;
};

Cyan.Validator.require = new Cyan.Validator.BaseValidator("require", function (value, s, element)
{
    if (s.toLowerCase() != "false")
    {
        if (Cyan.Validator.isButtonGroup(element))
        {
            var elements = element.form ? element.form[element.name] : element;
            if (elements.length)
            {
                return Cyan.any(elements, function ()
                {
                    return this.nodeName == "INPUT" && this.type == element.type && this.checked;
                });
            }
            else
            {
                return element.checked;
            }
        }
        else
        {
            return value != "" && value != (element.emptyValue || element.getAttribute("emptyValue"));
        }
    }
});

Cyan.Validator.notBlank = new Cyan.Validator.BaseValidator("notBlank", function (value, s, element)
{
    return s.toLowerCase() == "false" || Cyan.trim(value) != "";
});

Cyan.Validator.validators = [
    new Cyan.Validator.BaseValidator("dataType", function (value, type, element)
    {
        if (element.type != "text")
            return true;
        type = Cyan.Validator["is" + type.charAt(0).toUpperCase() + type.substring(1)];
        return !type || !!type(value, element);
    }),
    new Cyan.Validator.BaseValidator("maxLen", function (value, len)
    {
        return !len || value.length <= Cyan.toInt(len);
    }),
    new Cyan.Validator.BaseValidator("minLen", function (value, len)
    {
        return !len || value.length >= Cyan.toInt(len);
    }),
    new Cyan.Validator.BaseValidator("len", function (value, len)
    {
        return !len || value.length == Cyan.toInt(len);
    }),
    new Cyan.Validator.BaseValidator("maxVal", function (value, max)
    {
        return !max || Cyan.Validator.compare(value, max) <= 0;
    }),
    new Cyan.Validator.BaseValidator("minVal", function (value, min)
    {
        return !min || Cyan.Validator.compare(value, min) >= 0;
    }),
    new Cyan.Validator.BaseValidator("range", function (value, range)
    {
        range = range.split(",");
        return range.length != 2 ||
                Cyan.Validator.compare(value, range[0]) >= 0 && Cyan.Validator.compare(value, range[1]) <= 0;
    }),
    new Cyan.Validator.BaseValidator("equal", function (value, equal)
    {
        return !(equal = Cyan.$(equal)) || equal.value == "" || value == equal.value;
    }),
    new Cyan.Validator.BaseValidator("lessThan", function (value, less)
    {
        return !(less = Cyan.$(less)) || less.value == "" || Cyan.Validator.compare(value, less.value) < 0;
    }),
    new Cyan.Validator.BaseValidator("lessEqual", function (value, less)
    {
        return !(less = Cyan.$(less)) || less.value == "" || Cyan.Validator.compare(value, less.value) <= 0;
    }),
    new Cyan.Validator.BaseValidator("largerThan", function (value, larger)
    {
        return !(larger = Cyan.$(larger)) || larger.value == "" || Cyan.Validator.compare(value, larger.value) > 0;
    }),
    new Cyan.Validator.BaseValidator("largerEqual", function (value, larger)
    {
        return !(larger = Cyan.$(larger)) || larger.value == "" || Cyan.Validator.compare(value, larger.value) >= 0;
    }),
    new Cyan.Validator.BaseValidator("maxCount", function (value, count, element)
    {
        if (element.nodeName != "INPUT" || element.type != "checkbox")
            return true;

        var maxCount = Cyan.toInt(count);
        if (!maxCount || maxCount < 1)
            return true;

        var elements = element.form ? element.form[element.name] : element;
        if (elements.length)
        {
            return Cyan.search(elements,
                            function ()
                            {
                                return this.nodeName == "INPUT" && this.type == element.type && this.checked;
                            }).length <= maxCount;
        }

    }),
    new Cyan.Validator.BaseValidator("minCount", function (value, count, element)
    {
        if (element.nodeName != "INPUT" || element.type != "checkbox")
            return true;

        var minCount = Cyan.toInt(count);
        if (!minCount || minCount < 1)
            return true;

        var elements = element.form ? element.form[element.name] : element;
        if (elements.length)
        {
            return Cyan.search(elements,
                            function ()
                            {
                                return this.nodeName == "INPUT" && this.type == element.type && this.checked;
                            }).length >= minCount;
        }
    }),
    new Cyan.Validator.BaseValidator("pattern", function (value, pattern)
    {
        return !!new RegExp(pattern).test(value);
    }),
    new Cyan.Validator.BaseValidator("fileType", function (value, fileTypes)
    {
        return Cyan.Validator.checkFileType(value, fileTypes);
    }),
    new Cyan.Validator.BaseValidator("validator", function (value, validator, element)
    {
        if (!element.validator_)
            element.validator_ = Cyan.innerFunction(validator, "this");
        return element.validator_(value);
    }),
    new Cyan.Validator.BaseValidator("ajax_validate", function (value, validator, element)
    {
        var callback = function (result)
        {
            if (window.Cyan && Cyan.Validator)
                Cyan.Validator.errorHandler(result, element);
        };
        if (validator == "#" || validator.indexOf("/") == 0)
            Cyan.Validator.ajaxCall(validator, element.name, element, callback);
        else
            eval(validator).apply(element, callback);
        return true;
    }, true)];

Cyan.Validator.ajaxCall = function (url, name, element, callable)
{
    if (Cyan.Ajax)
    {
        if (url == "#" && Cyan.Validator.getAjaxValidateUrl)
            url = Cyan.Validator.getAjaxValidateUrl(name, element);

        var ajax = new Cyan.Ajax();
        ajax.handleObject = callable;
        ajax.doGet(element, url);
    }
};

Cyan.Elements.prototype.validate = function (all)
{
    var result = true;
    this.each(function ()
    {
        if (!Cyan.Validator.validate(this, all ? 1 : 0))
        {
            result = false;
            if (!all)
                return true;
        }
    });
    return result;
};

Cyan.Validator.isAutoValidate = function (form)
{
    return form.getAttribute("validate") == "true";
};

Cyan.Validator.bind = function (element, validator)
{
    element = Cyan.$(element);
    if (element)
    {
        element.setAttribute("validator", "");
        element.validator_ = validator;
    }
};

Cyan.importLanguage("validate");
Cyan.importAdapter("validate");

Cyan.onload(function ()
{
    Cyan.each(Cyan.search(document.getElementsByTagName("FORM"), function ()
    {
        return Cyan.Validator.isAutoValidate(this);
    }), function ()
    {
        Cyan.Validator.validate(this, 2);
    });
});