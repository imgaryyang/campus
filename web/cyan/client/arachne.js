Cyan.importJs("ajax.js");
Cyan.importJs("event.js");
Cyan.importJs("encrypt.js");

Cyan.Arachne = {};

window.Arachne = Cyan.Arachne;

Cyan.Arachne.uriSeparator = "!";
Cyan.Arachne.postfix = "page";
Cyan.Arachne.formURI = null;
Cyan.Arachne.form = {};
Cyan.Arachne.contextPath = null;
Cyan.Arachne.referer = null;

Cyan.Arachne.progressFinish = true;

Cyan.Arachne.getPath = function (url)
{
    return Cyan.Arachne.contextPath ? Cyan.Arachne.contextPath + url : url;
};

Cyan.Arachne.call = function (url, args, method, type, component, obj)
{
    if (method == "post")
    {
        var event = window.event$;
        Cyan.Arachne.formatCsrfTokenToUrl(url, function (url)
        {
            Cyan.Arachne.call0(url, args, method, type, component, obj, event);
        });
    }
    else
    {
        Cyan.Arachne.call0(url, args, method, type, component, obj);
    }
};

Cyan.Arachne.call0 = function (url, args, method, type, component, obj, event)
{
    if (type == null)
        type = 3;

    if (Cyan.Arachne.contextPath)
        url = Cyan.Arachne.contextPath + url;

    var params;
    if (args)
    {
        if (args.length > args.callee.length)
            params = args[args.callee.length];
    }

    var args1 = {};
    if (args)
    {
        var parameterNames = Cyan.getFunctionParameters(args.callee);
        for (var i = 0; i < parameterNames.length; i++)
        {
            var parameterName = parameterNames[i];
            if ("$" == parameterName)
                parameterName = "";

            var parameterValue = parameterName ? Cyan.Arachne.encodeArg(args[i]) : args[i];
            var parameterName1 = "$" + i;

            if (parameterName == parameterName1)
                args1[parameterName] = parameterValue;
            else
                args1[parameterName] = {
                    value: parameterValue,
                    $$aliasName$$: [parameterName1]
                }
        }
        args = args1;
    }

    var check, form, checkName, wait, progressId, target, validate;
    if (params)
    {
        target = params.target;

        if (!component)
            component = params.component;

        check = params.check;
        wait = params.wait;
        if (params.event)
            event = params.event;
        validate = params.validate;
        form = Cyan.$(params.form);
        if (params.obj)
            obj = params.obj;

        if (params.progress)
            progressId = Math.random();
        else if (typeof params.progress != "undefined")
            progressId = " ";
    }

    if (!obj)
        obj = Cyan.Arachne.form;

    if (obj != Cyan.Arachne.form && !form)
        form = false;

    if (!event)
        event = window.event$;

    if (event)
    {
        if (!component)
            component = event.target;
        if (!check)
            checkName = event.type + "check";
    }

    while (component && component.nodeName && component.nodeName != "BODY" && component.nodeName != "#document")
    {
        if (form == null && component.nodeName == "FORM")
        {
            if (method != "redirect")
                form = component;
            break;
        }

        if (type == 0 && !target)
        {
            target = component.target || component.getAttribute("target");
            if (method == "redirect" || form != null)
                break;
        }

        if (method != "redirect")
        {
            if (check == null && checkName)
            {
                check = component[checkName] || component.getAttribute(checkName);
                if (Cyan.isString(check))
                    component[checkName] = check = new Function(check);
            }

            if (!progressId)
            {
                if (component.getAttribute("progress") == "true")
                    progressId = Math.random();
            }

            if (wait == null)
            {
                var s = component.getAttribute("wait");
                if (s)
                    wait = s == "true";
            }
        }

        component = component.parentNode;
    }

    if (!form)
        form = null;

    if (progressId == " ")
        progressId = null;

    var isSubmitAction = event && (event.type == "click" || event.type == "dblclick" ||
            (event.type == "keypress" && event.keyCode == 13));

    if (wait == null)
        wait = isSubmitAction && (type > 1 || type == 0 && (!target || target == "_self"));

    if (wait && Cyan.Arachne.submiting)
    {
        Cyan.Arachne.message(Cyan.titles.waitForSubmit, null);
        return;
    }

    if (check && check != true)
    {
        if (!check())
            return;
    }
    else if ((check == null || check) && form && (validate || validate == null && isSubmitAction) &&
            Cyan.Arachne.validate && !Cyan.Arachne.validate(form))
    {
        return;
    }

    var callback;
    var errorHandle;
    if (params && (type > 1 || (target && target != "_self" && target != "_blank")))
    {
        callback = params.callback ? params.callback : params instanceof Function ? params : null;
        errorHandle = params.error ? params.error : Cyan.Arachne.errorHandler;
    }

    if (!target)
    {
        if (type == 0)
            target = "_self";
        else if (type == 1)
            target = "_blank";
    }

    var ajax = new Cyan.Ajax();
    if (params)
    {
        if (params.headers)
        {
            for (var headerName in params.headers)
            {
                ajax.setRequestHeader(headerName, params.headers[headerName]);
            }
        }

        if (params.ajax)
        {
            for (var ajaxName in params.ajax)
            {
                ajax[ajaxName] = params.ajax[ajaxName];
            }
        }
    }
    var waitdisplayer;
    if (wait)
        waitdisplayer = wait.show ? wait : Cyan.Arachne.waitDisplayer;

    var resultType;
    if (params)
        resultType = params.resultType;
    var handler = wait || progressId ? function (result)
    {
        if (wait)
        {
            waitdisplayer.close();
            Cyan.Arachne.submiting = false;
        }
        if (progressId)
            progressDisplayer.close();
        if (callback)
            callback(result);
    } : callback;

    if (resultType == "text")
        ajax.handleText = handler;
    else if (resultType == "xml")
        ajax.handleXml = handler;
    else
        ajax.handleObject = handler;

    var progressDisplayer = params && params.progress && params.progress.display ? params.progress :
            Cyan.Arachne.progressDisplayer;

    ajax.errorHandle = wait || progressId ? function (result)
    {
        if (wait)
        {
            waitdisplayer.close();
            Cyan.Arachne.submiting = false;
        }
        if (progressId)
            progressDisplayer.close();
        if (errorHandle)
            errorHandle(result);
    } : errorHandle;

    if (target && Cyan.isString(target))
    {
        var targetObject = Cyan.$(target);
        if (targetObject && targetObject.getAttribute("type") == "segment")
        {
            ajax.setRequestHeader("segment", target);
            ajax.callback = callback;
            if (params)
                ajax.loadingHTML = params.loadingHTML;
        }
    }
    else
    {
        ajax.setRequestHeader("ajax", "true");
        if (resultType == "xml")
            ajax.setRequestHeader("Accept", "application/xml");
        else if (type == 2)
            ajax.setRequestHeader("Accept", "text/javascript");
        else
            ajax.setRequestHeader("Accept", "application/json");
    }

    if (progressId)
        url += (url.indexOf("?") >= 0 ? "&" : "?") + "$progressId$=" + progressId;

    if (obj)
    {
        var map = {};
        for (var name in obj)
        {
            if (!(obj[name] instanceof Function))
                map[name] = Cyan.Arachne.encodeFormObj(obj[name], name, form);
        }
        obj = map;
    }

    var f = function ()
    {
        var b;
        if (method == "post")
            b = ajax.doPost(form, url, target, args ? [obj, args] : [obj]);
        else if (method == "put")
            b = ajax.doPut(form, url, args ? [obj, args] : [obj]);
        else if (method == "delete")
            b = ajax.doDelete(form, url, args ? [obj, args] : [obj]);
        else
            b = ajax.doGet(form, url, target, method != "redirect" && args ? [obj, args] : [obj]);

        if (b)
        {
            if (wait)
                Cyan.Arachne.submiting = true;

            //显示进度对话框
            if (progressId)
            {
                Cyan.Arachne.showProgress(progressId, progressDisplayer);
            }
            else if (wait)
            {
                setTimeout(function ()
                {
                    if (Cyan.Arachne.submiting)
                        waitdisplayer.show();
                }, 500);
            }

            if (form && (type == 3 || type == 2))
                Cyan.Arachne.updateForm(form);
        }
        else if (wait)
        {
            Cyan.Arachne.submiting = false;
        }
    };

    if (params && params.encrypteds && location.protocol == "http:" &&
            navigator.platform.toLowerCase().startsWith("win") &&
            (!Cyan.navigator.isIE() || Cyan.navigator.version > 9))
    {
        var encrypteds = params.encrypteds;
        if (wait)
            Cyan.Arachne.submiting = true;

        Cyan.Arachne.getEncrypt(function (encrypt)
        {
            ajax.setRequestHeader("encrypt-modulus", Cyan.Arachne.encryptModulus);
            ajax.setRequestHeader("encrypteds", encrypteds.join(","));
            ajax.encrypt = function (name, value)
            {
                if (Cyan.Array.contains(encrypteds, name))
                    return encrypt(value);
                else
                    return value;
            };
            f();
        });
    }
    else
    {
        f();
    }
};

Cyan.Arachne.updateForm = function (form)
{
    var valuesMap = Cyan.$$(form).getValuesMap();
    for (var name in valuesMap)
    {
        var values = valuesMap[name];
        if (values.length <= 50 && (!values.length || values[0].length <= 200))
            Cyan.Arachne.updateFormValue(name, values);
    }
};
Cyan.Arachne.updateFormValue = function (name, values)
{
    name = name.split(".");
    var obj = Cyan.Arachne.form, n = name.length - 1, v;
    for (var i = 0; i < n; i++)
    {
        var s = name[i];
        v = obj[s];
        if (typeof v == "undefined")
            return;
        if (v == null)
            obj[s] = v = {};
        obj = v;
    }
    name = name[n];
    v = obj[name];
    if (typeof v != "undefined")
    {
        if (v == null)
        {
            if (values.length == 1)
                v = values[0];
            else if (values.length > 1)
                v = values;
        }
        else if (Cyan.isArray(v))
        {
            v = values;
        }
        else if (!values.length)
        {
            v = null;
        }
        else
        {
            v = values[0];
            if (v)
            {
                if (Cyan.isNumber(v))
                    v = parseFloat(v);
                else if (v instanceof Date)
                    v = Cyan.Date.parse(v);
            }
        }
        obj[name] = v;
    }
};

Cyan.Arachne.encodeArg = function (value)
{
    if (value == null || Cyan.isBaseType(value) || window.Blob && value instanceof Blob)
        return value;
    if (Cyan.isArray(value))
    {
        var n = value.length;
        for (var i = 0; i < n; i++)
        {
            if (value && !Cyan.isBaseType(value[i]))
                return Cyan.Ajax.toJson(value);
        }
        return value;
    }
    return Cyan.Ajax.toJson(value);
};

Cyan.Arachne.encodeParameters = function (parameters)
{
    if (parameters == null)
        return null;

    var result = {};
    if (Cyan.isArray(parameters))
    {
        for (var i = 0; i < parameters.length; i++)
            result["$" + i] = Cyan.Arachne.encodeArg(parameters[i]);
    }
    else
    {
        for (var name in parameters)
        {
            var value = parameters[name];
            if (!(value instanceof Function))
                result[name] = Cyan.Arachne.encodeArg(value);
        }
    }

    return result;
};

Cyan.Arachne.encodeFormObj = function (obj, expression, form)
{
    if (obj == null || Cyan.isBaseType(obj))
        return obj;

    if (!form || !Cyan.Arachne.checkFormObj(obj, expression, form))
        return Cyan.Arachne.encodeArg(obj);

    if (Cyan.isArray(obj))
        return obj;

    var result = {};
    for (var name in obj)
    {
        if (!(obj[name] instanceof Function))
            result[name] = Cyan.Arachne.encodeFormObj(obj[name], expression + "." + name, form);
    }

    return result;
};

Cyan.Arachne.checkFormObj = function (obj, expression, form)
{
    return Cyan.searchFirst(form, function ()
    {
        var name = this.name;
        return name && (name == expression || Cyan.startsWith(name, expression + "."));
    });
};

Cyan.Arachne.doPost = function (url, args, type, obj)
{
    Cyan.Arachne.call(url, args, "post", type, null, obj);
};

Cyan.Arachne.doGet = function (url, args, type, obj)
{
    Cyan.Arachne.call(url, args, "get", type, null, obj);
};

Cyan.Arachne.doPut = function (url, args, type, obj)
{
    Cyan.Arachne.call(url, args, "put", type, null, obj);
};

Cyan.Arachne.doDelete = function (url, args, type, obj)
{
    Cyan.Arachne.call(url, args, "delete", type, null, obj);
};

Cyan.Arachne.redirect = function (url, target, args)
{
    var segment = Cyan.$(target);
    var ajax = new Cyan.Ajax();
    if (segment && segment.getAttribute("type") == "segment")
    {
        ajax.setRequestHeader("segment", target);
    }

    var form, obj;
    if (args)
    {
        if (args instanceof Function)
        {
            ajax.callback = args;
        }
        else
        {
            ajax.callback = args.callback;
            ajax.loadingHTML = args.loadingHTML;
            form = args.form;
            obj = args.obj;
        }
    }
    ajax.doGet(form, url, target, obj ? [obj] : null);
};

Cyan.Arachne.back = function ()
{
    if (Cyan.Arachne.referer)
    {
        var ajax = new Cyan.Ajax();
        ajax.doGet(null, Cyan.Arachne.referer, "_self", null);
    }
};

Cyan.Arachne.comet = function (url, args, obj)
{
    if (!obj)
        obj = Cyan.Arachne.form;

    Cyan.Arachne.initComet();
    if (Cyan.Arachne.contextPath)
        url = Cyan.Arachne.contextPath + url;

    var callback;
    var errorHandle;
    if (args.length > args.callee.length)
    {
        var callable = args[args.callee.length];
        callback = callable.callback ? callable.callback : callable;
        errorHandle = callable.error ? callable.error : Cyan.Arachne.errorHandler;
    }

    var ajax = new Cyan.Ajax();
    ajax.handleComet = callback;
    ajax.errorHandle = errorHandle;
    ajax.comet(ajax.toParameters(obj).toUrl(ajax.toParameters(args).toUrl(url)));

    return ajax;
};

Cyan.Arachne.getToolsUrl = function (method, form)
{
    var s = form ? Cyan.Arachne.formURI + Cyan.Arachne.uriSeparator : "/net/cyan/arachne/PageUtils";
    s += Cyan.Arachne.uriSeparator + method + "." + Cyan.Arachne.postfix;
    if (Cyan.Arachne.contextPath)
        s = Cyan.Arachne.contextPath + s;
    return s;
};

Cyan.Arachne.ajaxValidate = function (url, name, element, callable)
{
    if (url == "#")
        url = Cyan.Arachne.getToolsUrl("validate", true);

    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    ajax.handleObject = callable;
    ajax.doGet(element.form, url, null, [
        {
            $0: name
        },
        Cyan.Arachne.form
    ]);
};

Cyan.Arachne.getEncrypt = function (callback)
{
    if (!Cyan.Arachne.encrypt)
    {
        var url = Cyan.Arachne.getToolsUrl("getEncryptModulus", false);

        var ajax = new Cyan.Ajax();
        ajax.setRequestHeader("Accept", "application/json");
        ajax.setRequestHeader("ajax", "true");
        ajax.handleObject = function (modulus)
        {
            Cyan.Arachne.encryptModulus = modulus;
            modulus = Cyan.Encrypt.base64ToByteArray(modulus);
            Cyan.Arachne.encrypt = function (s)
            {
                return Cyan.Encrypt.rsaEncryptToBase64(s, [1, 0, 1], modulus);
            };

            Cyan.Arachne.decryptWithPublicKey = function (s)
            {
                return Cyan.Encrypt.rsaDecryptToString(s, [1, 0, 1], modulus);
            };

            if (callback)
                callback(Cyan.Arachne.encrypt);
        };
        ajax.doGet(null, url);
    }
    else if (callback)
    {
        callback(Cyan.Arachne.encrypt);
    }
};

Cyan.Arachne.getCookie = function (name, callback)
{
    var url = Cyan.Arachne.getToolsUrl("getCookie", false);

    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    ajax.handleObject = function (value)
    {
        if (callback)
            callback(value);
    };
    ajax.doGet(null, url, null, [{
        $0: name
    }]);
};

Cyan.Arachne.getCookies = function (names, callback)
{
    var url = Cyan.Arachne.getToolsUrl("getCookies", false);

    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    ajax.handleObject = function (values)
    {
        if (callback)
            callback(values);
    };
    ajax.doGet(null, url, null, [{
        $0: names
    }]);
};


Cyan.Arachne.setCookie = function (name, value, callback)
{
    var url = Cyan.Arachne.getToolsUrl("setCookie", false);

    var ajax = new Cyan.Ajax();
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    ajax.handleObject = function ()
    {
        if (callback)
            callback();
    };
    ajax.doPost(null, url, null, [{
        $0: name,
        $1: value
    }]);
};

Cyan.Arachne.getSessionId = function (callback)
{
    if (Cyan.Arachne.sessionCookieHttpOnly)
    {
        Cyan.Arachne.getCookie(Cyan.Arachne.sessionCookieName, callback);
    }
    else if (callback)
    {
        callback(Cyan.getCookie(Cyan.Arachne.sessionCookieName));
    }
};

Cyan.Arachne.getCsrfToken = function (callback)
{
    if (Cyan.Arachne.checkCsrf)
    {
        if (Cyan.Arachne.csrf_token)
        {
            if (callback)
                callback(Cyan.Arachne.csrf_token);
        }
        else
        {
            Cyan.Arachne.getCookie("csrf_token", function (csrf_token)
            {
                Cyan.Arachne.csrf_token = csrf_token;
                if (callback)
                    callback(csrf_token);
            });
        }
    }
    else if (callback)
    {
        callback(null);
    }
};

Cyan.Arachne.getSessionIdAndCsrfToken = function (callback)
{
    if (Cyan.Arachne.checkCsrf || Cyan.Arachne.csrf_token)
    {
        Cyan.Arachne.getSessionId(function (sessionId)
        {
            if (callback)
                callback(sessionId, Cyan.Arachne.csrf_token);
        });
    }
    else
    {
        Cyan.Arachne.getCookies(["sessionId", "csrf_token"], function (values)
        {
            if (callback)
                callback(values[0], values[1])
        });
    }
};

Cyan.Arachne.formatSessionIdToUrl = function (url, callback)
{
    Cyan.Arachne.getSessionId(function (sessionId)
    {
        if (sessionId)
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "$" + Cyan.Arachne.sessionCookieName + "$=" + sessionId;
        callback(url);
    });
};

Cyan.Arachne.formatCsrfTokenToUrl = function (url, callback)
{
    Cyan.Arachne.getCsrfToken(function (csrf_token)
    {
        if (csrf_token)
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "csrf_token=" + csrf_token;
        callback(url);
    });
};

Cyan.Arachne.formatSessionIdAndCsrfTokenToUrl = function (url, callback)
{
    Cyan.Arachne.getSessionIdAndCsrfToken(function (sessionId, csrf_token)
    {
        if (sessionId)
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "$" + Cyan.Arachne.sessionCookieName + "$=" + sessionId;
        if (csrf_token)
            url += (url.indexOf("?") >= 0 ? "&" : "?") + "csrf_token=" + csrf_token;
        callback(url);
    });
};

Cyan.Arachne.waitDisplayer = {
    show: function ()
    {
        Cyan.Arachne.showWait(Cyan.titles.waitForSubmit);
    },
    close: function ()
    {
        Cyan.Arachne.showWait("");
    }
};

Cyan.Arachne.showWait = function (message)
{
    if (Cyan.wait)
        Cyan.wait(message);
};

Cyan.Arachne.showProgress = function (progressId, progressDisplayer)
{
    if (!Cyan.Arachne.progressFinish)
        return;

    Cyan.Arachne.progressFinish = false;
    var timeout = function ()
    {
        if (!Cyan.Arachne.progressFinish)
        {
            var ajax = new Cyan.Ajax();
            ajax.setRequestHeader("Accept", "application/json");
            ajax.setRequestHeader("ajax", "true");
            ajax.handleObject = callable;
            ajax.call(url, null);
        }
    };
    var url = Cyan.Arachne.getToolsUrl("getProgressInfo", null) + "?$0=" + progressId;
    var callable = function (progress)
    {
        if (!Cyan.Arachne.progressFinish)
        {
            if (progress && progress.progressName)
            {
                progressDisplayer.display(progress);
                if (progress.progressName == "finish")
                    Cyan.Arachne.progressFinish = true;
            }
            window.setTimeout(timeout, 500);
        }
    };
    progressDisplayer.display({progressName: Cyan.titles.submiting, percentage: 0, desniption: ""});
    window.setTimeout(timeout, 500);
};

Cyan.Arachne.progressDisplayer = {
    display: function (progress)
    {
        if (Cyan.displayProgress)
            Cyan.displayProgress(progress.progressName == "finish" ? -1 :
                    progress.percentage, progress.progressName, progress.description);
        else
            window.status = progress.progressName == "finish" ? "" :
            progress.progressName + " " + Math.round(progress.percentage * 100) + "% " +
            progress.description;
    },
    close: function ()
    {
        Cyan.Arachne.progressFinish = true;
        if (Cyan.closeProgress)
            Cyan.closeProgress();
    }
};

Cyan.Arachne.createError = function (message, component)
{
    var error = new Error(message);
    error.component = component;
    return error;
};

Cyan.Arachne.message = function (message, component)
{
    Cyan.message(message, component ? function ()
    {
        Cyan.focus(component);
    } : null);
};

Cyan.Arachne.error = function (message, component)
{
    Cyan.error(message, component ? function ()
    {
        Cyan.focus(component);
    } : null);
};

Cyan.Arachne.errorHandler = function (error)
{
    if (error)
        Cyan.Arachne.error(error.message, error.component);
};
Cyan.Arachne.refreshSelectable = function ($0)
{
    var component = $0;
    $0 = component.name;
    Cyan.Arachne.call(Cyan.Arachne.getToolsUrl("refreshSelectable", true), arguments, "get", 3, component, null);
};

Cyan.Arachne.refresh = function (component, callable)
{
    var callback, form;
    if (callable)
    {
        if (callable instanceof Function)
        {
            callback = callable;
            form = null;
        }
        else
        {
            callback = callable.callback;
            form = callable.form;
        }
    }
    component = Cyan.$(component, null, true);
    if (!component)
        return;
    Cyan.Arachne.beforeRefresh(component);

    if (component.refresh)
    {
        component.refresh(callback);
    }
    else
    {
        Cyan.Arachne.refreshSelectable(component, {
            callback: function (seletable)
            {
                Cyan.Arachne.afterRefresh(component, seletable);
                if (callback)
                    callback();
            },
            check: false,
            wait: false,
            form: form
        });
    }
};
Cyan.Arachne.beforeRefresh = function (component)
{
    if (component.onBeforeRefresh)
    {
        component.onBeforeRefresh();
    }
    else if (component.nodeName == "SELECT")
    {
        component.oldValue = component.value;
        var options = component.options;
        var length = options.length;
        for (var i = 0; i < length; i++)
        {
            var option = options[i];
            if (option.value != "")
            {
                options[i] = null;
                i--;
                length--;
            }
        }
    }
};
Cyan.Arachne.afterRefresh = function (component, selectable)
{
    if (component.onAfterRefresh)
    {
        component.onAfterRefresh(selectable);
    }
    else if (component.nodeName == "SELECT")
    {
        if (selectable && selectable.length)
        {
            var options = component.options;
            var b = false;
            if (options.length && options[0].value == "")
                b = true;
            for (var i = 0; i < selectable.length; i++)
            {
                var option = selectable[i];
                var value = Cyan.valueOf(option);
                if (!b || value)
                {
                    var item = new Option(Cyan.toString(option), value);
                    if (option.value == component.oldValue)
                        item.selected = true;
                    options[options.length] = item;
                }
            }
        }
    }
};
Cyan.onload(function ()
{
    if (Cyan.Validator)
    {
        Cyan.Validator.getAjaxValidateUrl = Cyan.Arachne.getAjaxValidateUrl;
        if (!Cyan.Arachne.checkType)
            Cyan.Arachne.checkType = 0;
        Cyan.Arachne.validate = function (form)
        {
            return Cyan.Validator.validate(form, Cyan.Arachne.checkType);
        };
        Cyan.Arachne.error = function (message, component)
        {
            Cyan.Validator.handleError(Cyan.Validator.changeMessage(message), Cyan.$(component), 0, false);
        };
        Cyan.Validator.ajaxCall = Cyan.Arachne.ajaxValidate;
    }
});

Cyan.Arachne.initComet = function ()
{
    if (!Cyan.Ajax.prototype.comet)
    {
        Cyan.Ajax.prototype.comet = function (url)
        {
            var ajax = this;
            if (window.EventSource instanceof Function)
            {
                url += (url.indexOf("?") >= 0 ? "&" : "?") + "$$eventsource$$=true";
                var source = new EventSource(url);
                source.onmessage = function (event)
                {
                    var data = event.data;
                    if (data == "end")
                    {
                        source.close();
                        if (ajax.handleComet)
                            ajax.handleComet(null, 1);
                    }
                    else
                    {
                        var result = eval(event.data);
                        if (ajax.handleComet && typeof result != "undefined")
                            ajax.handleComet(result, 0);
                    }
                };
                source.onerror = function (event)
                {
                    source.close();
                    ajax.handleComet(null, 1);
                };
            }
            else if (window.addEventStream instanceof Function)
            {
                url += (url.indexOf("?") >= 0 ? "&" : "?") + "$$eventsource$$=true";
                var eventSrc = document.createElement("event-source");
                eventSrc.setAttribute("src", url);
                document.body.appendChild(eventSrc);
                eventSrc.addEventListener("script", function (event)
                {
                    var result = eval(event.data);
                    if (ajax.handleComet && typeof result != "undefined")
                        ajax.handleComet(result, 0);
                }, false);
                eventSrc.addEventListener("end", function (event)
                {
                    eventSrc.setAttribute("src", "");
                    document.body.removeChild(eventSrc);
                    if (ajax.handleComet)
                        ajax.handleComet(null, 1);
                }, false);
            }
            else if (Cyan.navigator.isIE() && Cyan.navigator.version <= 9)
            {
                var postMessage = Cyan.navigator.version >= 9;
                var doc = new ActiveXObject("htmlfile");
                doc.open();
                doc.write("<html>");
                doc.write("<body></body></html>");
                doc.close();
                doc.parentWindow.callback = this.handleComet;
                if (!doc.parentWindow.callback)
                    doc.parentWindow.callback = Cyan.void$;
                doc.parentWindow.evalScript = eval;

                if (postMessage)
                {
                    doc.parentWindow.onmessage = function (event)
                    {
                        if (!event)
                            event = window.event;
                        this.callback(this.evalScript("(" + event.data + ")"), false);
                    };
                }
                var frame = doc.createElement("IFRAME");
                doc.body.appendChild(frame);
                if (Cyan.navigator.version >= 9)
                {
                    frame.contentWindow.onload = function ()
                    {
                        try
                        {
                            doc.parentWindow.callback(null, true);
                        }
                        finally
                        {
                            doc.body.removeChild(frame);
                            Cyan.Array.removeElement(window.cometDocs, doc.parentWindow);
                            doc = null;
                        }
                    };
                }
                if (postMessage)
                    url += (url.indexOf("?") >= 0 ? "&" : "?") + "$$postMessage$$=true";
                frame.src = url;
                if (!window.cometDocs)
                    window.cometDocs = [];
                window.cometDocs.push(doc.parentWindow);
                if (Cyan.navigator.version < 9)
                {
                    var interval = setInterval(function ()
                    {
                        if (frame.readyState != "interactive" && frame.readyState != "loading")
                        {
                            try
                            {
                                doc.parentWindow.callback(null, true);
                            }
                            finally
                            {
                                clearInterval(interval);
                                doc.body.removeChild(frame);
                                Cyan.Array.removeElement(window.cometDocs, doc.parentWindow);
                                doc = null;
                            }
                        }
                    }, 1000);
                }
                Cyan.onunload(function ()
                {
                    window.cometDocs = null;
                    frame = null;
                    doc = null;
                });
            }
            else if (Cyan.navigator.isFF())
            {
                this.getXmlHttp().multipart = true;
                this.xmlHttp.onreadystatechange = function ()
                {
                    if (ajax.xmlHttp.readyState == 4 && ajax.xmlHttp.status)
                    {
                        var text = ajax.xmlHttp.responseText;
                        if (text)
                        {
                            var result = eval(text);
                            if (ajax.handleComet && typeof result != "undefined")
                                ajax.handleComet(result, false);
                        }
                        else if (ajax.handleComet)
                            ajax.handleComet(null, true);
                    }
                };
                this.xmlHttp.open("GET", url, true);
                this.xmlHttp.setRequestHeader("Accept", "multipart/x-mixed-replace");
                this.xmlHttp.send(null);
            }
            else
            {
                var last = 0;
                this.getXmlHttp().onreadystatechange = function ()
                {
                    var state = ajax.xmlHttp.readyState;
                    if (state == 4 || state == 3)
                    {
                        //comet
                        try
                        {
                            var text = ajax.xmlHttp.responseText;
                            var index = text.lastIndexOf('\n');
                            if (index >= 0)
                                text = text.substring(0, index + 1);
                            else
                                return;
                            var l = text.length;
                            if (l > last)
                            {
                                if (last)
                                    text = text.substring(last);
                                last = l;
                                text = text.split('\n');
                                for (var i = 0; i < text.length; i++)
                                {
                                    if (text[i])
                                    {
                                        var result = eval(text[i]);
                                        if (typeof result != "undefined" && ajax.handleComet)
                                        {
                                            try
                                            {
                                                ajax.handleComet(result, 0);
                                            }
                                            catch (e)
                                            {
                                                console.error(e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        finally
                        {
                            if (state == 4 && ajax.handleComet)
                            {
                                ajax.handleComet(null, 1);
                                ajax.xmlHttp = null;
                            }
                        }
                    }
                };
                this.xmlHttp.open("GET", Cyan.formatUrl(url), true);
                if (this.headers)
                {
                    for (var name in this.headers)
                        this.xmlHttp.setRequestHeader(name, this.headers[name]);
                    this.headers = null;
                }
                this.xmlHttp.setRequestHeader("Accept", "application/json");
                this.xmlHttp.setRequestHeader("ajax", "true");
                this.xmlHttp.send(null);
            }
        };
    }
};

Cyan.Arachne.get = function (url, parameters, callback, errorHandle)
{
    var ajax = new Cyan.Ajax();
    ajax.handleObject = callback;
    ajax.errorHandle = errorHandle || Cyan.Arachne.errorHandler;
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    ajax.doGet(null, url, null, parameters ? [Cyan.Arachne.encodeParameters(parameters)] : null);
};

Cyan.Arachne.post = function (url, parameters, callback, errorHandle, form)
{
    Cyan.Arachne.formatCsrfTokenToUrl(url, function (url)
    {
        var ajax = new Cyan.Ajax();
        ajax.handleObject = callback;
        ajax.errorHandle = errorHandle || Cyan.Arachne.errorHandler;
        ajax.setRequestHeader("Accept", "application/json");
        ajax.setRequestHeader("ajax", "true");
        ajax.doPost(form, url, null, parameters ? [Cyan.Arachne.encodeParameters(parameters)] : null);
    });
};

Cyan.Arachne.getImagePath = function (element, callback)
{
    var files = element.files;
    if (files)
    {
        if (files.length == 0)
        {
            callback("");
            return;
        }
        else
        {
            var file = files.item(0);
            if (file.getAsDataURL)
            {
                callback(file.getAsDataURL());
                return;
            }
        }
    }
    else if (Cyan.navigator.isIE() && Cyan.navigator.version < 7)
    {
        callback(element.value);
        return;
    }
    else if (element.select instanceof Function && document.selection)
    {
        element.select();
        callback(document.selection.createRange().text);
        return;
    }

    if (!Cyan.Arachne.imageForm)
    {
        Cyan.Arachne.imageForm = document.createElement("FORM");
        document.body.appendChild(Cyan.Arachne.imageForm);
    }

    var parentNode = element.parentNode;
    Cyan.Arachne.imageForm.appendChild(element);

    var url = Cyan.Arachne.getToolsUrl(Cyan.navigator.isIE() ? "uploadImgForPath" : "uploadImgForBase64", false);

    var ajax = new Cyan.Ajax();
    ajax.handleObject = callback;
    ajax.doPost(Cyan.Arachne.imageForm, url, null, [
        {
            $0: element.name
        }
    ]);

    parentNode.appendChild(element);
};

Cyan.Arachne.Script = function (uri)
{
    this.uri = uri;
};

Cyan.Arachne.Script.prototype.call = function (input)
{
    var ajax = new Cyan.Ajax();
    ajax.handleObject = this.callback;
    ajax.errorHandle = this.error;
    ajax.setRequestHeader("Accept", "application/json");
    ajax.setRequestHeader("ajax", "true");
    Cyan.Arachne.formatCsrfTokenToUrl(this.uri, function (uri)
    {
        ajax.doPost(null, uri, null, [
            {
                input: Cyan.Ajax.toJson(input)
            }
        ]);
    });
};

Cyan.Arachne.importComponent = function (component, name, callback)
{
    if (window[name])
    {
        if (callback)
            callback();
    }
    else
    {
        Cyan.Arachne.get(
                Cyan.Arachne.getToolsUrl("getComponentObjectScript", false),
                {
                    $0: component,
                    $1: name
                }, function (s)
                {
                    eval(s);
                    if (callback)
                        callback();
                }, null);
    }
};

Cyan.Arachne.createComponent = function (component, name, el, attributes, model, callback)
{
    if (window[name])
    {
        if (callback)
            callback();
    }
    else
    {
        var tagName;
        if (el)
        {
            el = Cyan.$(el);
        }
        else
        {
            el = document.createElement("div");
            el.style.display = "none";
            document.body.appendChild(el);
        }

        tagName = el.nodeName;

        Cyan.Arachne.get(
                Cyan.Arachne.getToolsUrl("getComponentObjectHtml", false),
                {
                    $0: component,
                    $1: name,
                    $2: tagName,
                    $3: attributes,
                    $4: model
                }, function (result)
                {
                    var f = function ()
                    {
                        if (result.cssFiles)
                        {
                            Cyan.each(result.cssFiles, function ()
                            {
                                Cyan.importCss(this);
                            });
                        }
                        Cyan.Elements.setHTMLWithEvalScript(el, result.html);
                        if (callback)
                            callback();
                    };

                    if (result.jsFiles && result.jsFiles.length)
                    {
                        var index = 0;
                        var f1 = function ()
                        {
                            if (index < result.jsFiles.length)
                                Cyan.importJs(result.jsFiles[index++], f1);
                            else
                                f();
                        };
                        f1();
                    }
                    else
                    {
                        f();
                    }

                }, null);
    }
};

Cyan.Arachne.wrap = function (name, options)
{
    Cyan.onload(function ()
    {
        var f = window[name];
        var n = Cyan.getFunctionParameters(f).length;
        window[name] = function ()
        {
            var args = new Array(length);
            for (var i = 0; i < n; i++)
                args[i] = arguments[i];

            args[n] = options;

            f.apply(window, args);
        }
    });
};
