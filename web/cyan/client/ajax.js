Cyan.Ajax = function ()
{
};

Cyan.Ajax.callbacks = {};

Cyan.Ajax.postFrameName = null;

if (typeof window.XMLHttpRequest == "undefined")
{
    var XMLHttpRequest = function ()
    {
        var msxmls = ["MSXML3", "MSXML2", "Microsoft"];
        for (var i = 0; i < msxmls.length; i++)
        {
            try
            {
                return new ActiveXObject(msxmls[i] + ".XMLHTTP");
            }
            catch (e)
            {
            }
        }
        throw new Error("No XML component install!");
    };

    Cyan.Ajax.postFrameName = "postframe_" + Math.random().toString().substring(2);
    document.writeln("<IFRAME id='" + Cyan.Ajax.postFrameName + "' name='" + Cyan.Ajax.postFrameName +
    "' style='display:none'></IFRAME>");
}

Cyan.Ajax.createHref = function ()
{
    if (!Cyan.Ajax.href)
    {
        Cyan.Ajax.href = document.createElement("A");
        document.body.appendChild(Cyan.Ajax.href);
    }
    return Cyan.Ajax.href;
};

Cyan.Ajax.eval = function (text)
{
    if (text && !Cyan.endsWith(Cyan.trim(text), ";"))
        text = "(" + text + ")";
    return eval(text);
};

Cyan.Ajax.open = function (url)
{
    if (Cyan.navigator.isIE())
    {
        var href = Cyan.Ajax.createHref();
        href.href = url;
        href.target = "_blank";
        href.click();
    }
    else
    {
        var win = window;
        win.open(url);
    }
};

Cyan.Ajax.redirect = function (url)
{
    if (Cyan.navigator.isIE())
    {
        var href = Cyan.Ajax.createHref();
        href.href = url;
        href.target = "_self";
        href.click();
    }
    else
    {
        window.location.href = url;
    }
};

Cyan.Ajax.customTarget = {};

Cyan.Ajax.prototype.getXmlHttp = function ()
{
    if (!this.xmlHttp)
        this.xmlHttp = new XMLHttpRequest();
    return this.xmlHttp;
};

Cyan.Ajax.prototype.setRequestHeader = function (name, value)
{
    if (!this.headers)
        this.headers = {};
    this.headers[name] = value;
};

Cyan.Ajax.prototype.call = function (url, content)
{
    var ajax = this;
    this.getXmlHttp().onreadystatechange = function ()
    {
        var state = ajax.xmlHttp.readyState;
        if (state == 4)
        {
            try
            {
                var status = ajax.xmlHttp.status;
                if (status == 200)
                {
                    if (ajax.handleText)
                    {
                        try
                        {
                            ajax.handleText(ajax.xmlHttp.responseText);
                        }
                        catch (e)
                        {
                            if (ajax.errorHandle)
                                ajax.errorHandle(e);
                            else
                                throw e;
                        }
                    }
                    else if (ajax.handleXml)
                    {
                        try
                        {
                            ajax.handleXml(ajax.xmlHttp.responseXML);
                        }
                        catch (e)
                        {
                            if (ajax.errorHandle)
                                ajax.errorHandle(e);
                            else
                                throw e;
                        }
                    }
                    else
                    {
                        //将返回结果作为json
                        var text = ajax.xmlHttp.responseText;
                        var result;
                        try
                        {
                            result = Cyan.Ajax.eval(text);
                        }
                        catch (e)
                        {
                            if (ajax.errorHandle)
                            {
                                ajax.errorHandle(e);
                                return;
                            }
                            else
                            {
                                throw e;
                            }
                        }
                        if (result instanceof Error && ajax.errorHandle)
                            ajax.errorHandle(result);
                        else if (ajax.handleObject)
                            ajax.handleObject(result);
                    }
                }
                else if (status == 400 || status == 404 || status == 500)
                {
                    var s = ajax.xmlHttp.responseText;
                    if (s)
                        s = Cyan.trim(s);

                    if (!s)
                        s = "" + status;

                    ajax.errorHandle(new Error(s));
                }
            }
            finally
            {
                ajax = null;
            }
        }
    };

    var method = this.method;
    if (!method)
        method = content == null ? "GET" : "POST";

    url = Cyan.formatUrl(url);

    this.xmlHttp.open(method, url, true);
    if (this.headers)
    {
        for (var name in this.headers)
            this.xmlHttp.setRequestHeader(name, this.headers[name]);
        this.headers = null;
    }
    if (content != null && !Cyan.isFormData(content))
    {
        if (this.contentType)
            this.xmlHttp.setRequestHeader("CONTENT-TYPE", this.contentType);
        else
            this.xmlHttp.setRequestHeader("CONTENT-TYPE", "application/x-www-form-urlencoded; charset=UTF-8");
    }

    this.xmlHttp.send(content);
};

Cyan.Ajax.onsubmit = function (form)
{
    if (!form)
        return true;

    if (form.nodeName != "FORM")
        return true;

    if (form.cyanOnSubmit)
    {
        return form.cyanOnSubmit() != false;
    }
    else if (Cyan.navigator.isFF())
    {
        return true;
    }
    else
    {
        return Cyan.fireEvent(form, "submit");
    }
};

Cyan.Ajax.prototype.doGet = function (form, url, target, args)
{
    form = Cyan.$(form);

    if (!Cyan.Ajax.onsubmit(form))
        return false;

    if (!url)
        url = form.action;

    if (args)
    {
        var parameters;
        if (Cyan.isArray(args))
        {
            for (var i = 0; i < args.length; i++)
            {
                parameters = this.toParameters(args[i]);
                Cyan.Array.removeCase(parameters, Cyan.Ajax.isLongContent);
                url = parameters.removes(form).toUrl(url, !form && i == args.length - 1);
            }
        }
        else
        {
            parameters = this.toParameters(args);
            Cyan.Array.removeCase(parameters, Cyan.Ajax.isLongContent);
            url = parameters.removes(form).toUrl(url, !form);
        }
    }

    if (form)
    {
        url = this.toParameters(form).toUrl(url ? url : form.action, true);
    }

    if (url.length > 3000)
    {
        var index = url.indexOf("?");
        if (index > 0)
        {
            var path = url.substring(0, index + 1);
            var queryString = url.substring(index + 1);
            var ss = queryString.split("&");
            var k = 0;
            for (var j = 0; j < ss.length; j++)
            {
                var s = ss[j];
                if (s.length < 100)
                {
                    if (k++ > 0)
                        path += "&";

                    path += s;

                    if (k > 100)
                        break;
                }
            }
        }
    }

    if (!target)
    {
        this.method = "GET";
        this.call(url, null);
    }
    else if (Cyan.isString(target))
    {
        if (target == "_self")
        {
            Cyan.Ajax.redirect(Cyan.formatUrl(url));
        }
        else if (target == "_blank")
        {
            Cyan.Ajax.open(Cyan.formatUrl(url));
        }
        else if (target == "_parent")
        {
            if (window.parent.Cyan && window.parent.Cyan.Ajax)
                window.parent.Cyan.Ajax.redirect(Cyan.formatUrl(url));
            else
                window.parent.location.href = Cyan.formatUrl(url);
        }
        else if (target == "_dialog")
        {
            if (Cyan.Window)
                Cyan.Window.showDialog(url);
        }
        else if (target == "_window")
        {
            if (Cyan.Window)
                Cyan.Window.openWindow(url);
        }
        else if (target == "_modal")
        {
            if (Cyan.Window)
                Cyan.Window.showModal(Cyan.formatUrl(url), this.handleObject);
        }
        else if (Cyan.Ajax.customTarget[target])
        {
            var targetHandler = Cyan.Ajax.customTarget[target];
            if (targetHandler.open)
                targetHandler.open(Cyan.formatUrl(url));
        }
        else
        {
            target = Cyan.$(target);
            if (target)
            {
                var nodeName = target.nodeName;
                if (nodeName == "OBJECT")
                {
                    target.data = Cyan.formatUrl(url);
                }
                else if (nodeName == "IFRAME" || nodeName == "IMG" || nodeName == "IMAGE" ||
                        nodeName == "BGSOUND" || nodeName == "INPUT" || nodeName == "FRAME" ||
                        nodeName == "EMBED")
                {
                    target.src = Cyan.formatUrl(url);
                }
                else
                {
                    //将返回的文档作为html插入到目标控件中，并初始化新产生的html元素
                    this.handleText = function (text)
                    {
                        Cyan.Elements.setHTMLWithEvalScript(target, text);
                        for (var i = 0; i < Cyan.elementInitializers.length; i++)
                            Cyan.elementInitializers[i](target);

                        if (this.callback)
                            this.callback();
                    };
                    if (this.loadingHTML)
                        target.innerHTML = this.loadingHTML;

                    this.call(url, null);
                }
            }
        }
    }
    else if (target.doGet)
    {
        target.doGet(url, this);
    }

    return true;
};

Cyan.Ajax.prototype.doPost = function (form, url, target, args)
{
    var formData;
    if (Cyan.isFormData(form))
        formData = form;

    if (!formData)
    {
        form = Cyan.$(form);

        if (!Cyan.Ajax.onsubmit(form))
            return false;

        if (form)
            form = Cyan.$$(form);
    }
    var targetObject, ajax = this;

    var formPost, containsFile = form && !formData && form.containsFile();

    if (target && Cyan.isString(target))
    {
        if (target.charAt(0) == '_')
        {
            formPost = true;
        }
        else
        {
            targetObject = Cyan.$(target);
            if (targetObject)
            {
                var nodeName = targetObject.nodeName;
                formPost = nodeName == "IFRAME" || nodeName == "FRAME";
            }
        }
    }
    else if (!target)
    {
        formPost = !window.FormData && (containsFile || !formData && (form && form[0].length > 700));
    }

    if (formPost)
        form = form.firstForm();

    var i, n, realForm;
    if (form)
        realForm = form.firstForm ? form.firstForm() : form;

    if (url)
    {
        if (containsFile)
        {
            var fileNames = [];
            n = realForm.length;
            for (i = 0; i < n; i++)
            {
                component = realForm[i];
                if (component.nodeName == "INPUT" && component.type == "file" && component.value != "" &&
                        component.getAttribute("nameField"))
                {
                    var value = component.value;
                    if (value)
                    {
                        var split = value.lastIndexOf("\\");
                        if (split >= 0)
                            value = value.substring(split + 1);
                    }
                    fileNames.push(new Cyan.Ajax.Parmater(component.getAttribute("nameField"), value, this));
                }
            }
            if (fileNames.length > 0)
            {
                fileNames.makeUrl = Cyan.Ajax.makeUrl;
                fileNames.toUrl = Cyan.Ajax.toUrl;
                fileNames.toQueryString = Cyan.Ajax.toQueryString;
                fileNames.ajax = this;
                url = fileNames.toUrl(url, false);
            }
        }

        if (form && !formData)
        {
            if (realForm)
            {
                var index, start = 0;
                while ((index = url.indexOf("${", start)) >= 0)
                {
                    var index2 = url.indexOf("}", index + 2);
                    if (index2 < 0)
                        break;
                    var v = url.substring(index + 2, index2);
                    var component = realForm[v];
                    if (component)
                        url = url.substring(0, index) + encodeURIComponent(component.value) + url.substring(index2 + 1);
                    else
                        start = index2;
                }
            }
            //url = decodeURI(url);
        }
    }

    var parameters;

    if (formPost)
    {
        if (!url)
            url = form.action;
        if (args && target != "_self")
        {
            for (i = 0; i < args.length; i++)
            {
                parameters = this.toParameters(args[i]).removes(form);
                Cyan.Array.removeCase(parameters, Cyan.Ajax.isLongContent);
                url = parameters.toUrl(url);
            }
        }

        if (!target)
        {
            if (!Cyan.Ajax.postFrameName)
            {
                Cyan.Ajax.postFrameName = "postframe_" + Math.random().toString().substring(2);
                var frame = document.createElement("IFRAME");
                frame.name = Cyan.Ajax.postFrameName;
                frame.id = Cyan.Ajax.postFrameName;
                frame.style.display = "none";
                document.body.appendChild(frame);
            }

            target = Cyan.Ajax.postFrameName;

            if (this.handleObject)
            {
                var callback;
                while (Cyan.Ajax.callbacks[callback = Math.random().toString().substring(2)]);
                Cyan.Ajax.callbacks[callback] = {handleObject: this.handleObject, errorHandle: this.errorHandle};

                url += (url.indexOf("?") >= 0 ? "&" : "?") + "$$callback$$=" + callback;
            }
        }
        else if (target == "_self")
        {
            if (args)
            {
                var names = [];
                n = form.length;
                for (i = 0; i < n; i++)
                    names.push(form[i].name);
                if (Cyan.isArray(args))
                {
                    for (i = 0; i < args.length; i++)
                        Cyan.Ajax.appendParameterToForm(form, args[i], names);
                }
                else
                {
                    Cyan.Ajax.appendParameterToForm(form, args, names);
                }
            }
        }
        else if (target == "_dialog" || target == "_window" || target == "_modal")
        {
            if (Cyan.Window)
            {
                var win = Cyan.Window.createFrameWindow(target == "_modal" ? null : url);
                if (target == "_dialog")
                {
                    win.setDialog();
                }
                else if (target == "_window")
                {
                    win.setWindow();
                }
                else
                {
                    win.setModal(true);
                    win.callback = this.handleObject;
                }

                win.create();
                target = win.frame;
                Cyan.Window.getTopWindow().Cyan.$(win.frame).src = Cyan.getRealPath("empty2.html");
            }
        }
        else if (Cyan.Ajax.customTarget[target])
        {
            var targetHandler = Cyan.Ajax.customTarget[target];
            if (targetHandler.createPostFrame)
                target = targetHandler.createPostFrame(url);
        }

        form.action = url;
        form.method = "POST";
        form.encoding = "multipart/form-data";
        form.target = target;
        form.submit();
    }
    else
    {
        if (target && Cyan.isString(target))
        {
            this.handleText = function (text)
            {
                targetObject.innerHTML = text;
                for (var i = 0; i < Cyan.elementInitializers.length; i++)
                    Cyan.elementInitializers[i](target);
            };

            if (this.loadingHTML)
                targetObject.innerHTML = this.loadingHTML;
        }

        var content, f;

        if (!formData && (containsFile || form && form[0].length > 700))
            formData = new window.FormData(form.firstForm());

        if (formData)
        {
            content = formData;
            if (args)
            {
                f = function (arg)
                {
                    parameters = ajax.toParameters(arg, null, null, true).removes(realForm);
                    for (var i = 0; i < parameters.length; i++)
                    {
                        var parameter = parameters[i];
                        formData.append(parameter.name, parameter.value);
                    }
                    url = parameters.makeUrl(url);
                };
                if (Cyan.isArray(args))
                {
                    for (i = 0; i < args.length; i++)
                        f(args[i]);
                }
                else
                {
                    f(args);
                }
            }
        }
        else
        {
            content = "";
            if (args)
            {
                f = function (arg)
                {
                    parameters = ajax.toParameters(arg, null, null, true);
                    if (form)
                        parameters = parameters.removes(form.firstForm());
                    url = parameters.makeUrl(url);
                    content = parameters.toQueryString(content);
                };
                if (Cyan.isArray(args))
                {
                    for (i = 0; i < args.length; i++)
                        f(args[i]);
                }
                else
                {
                    f(args);
                }
            }

            if (form && form.firstForm())
            {
                content = this.toParameters(form.firstForm(), null, null, true).toQueryString(content);
            }
        }

        this.method = "POST";
        if (!url)
            url = form.firstForm().action;
        if (target && target.doPost)
            target.doPost(url, content, this);
        else
            this.call(url, content);
    }

    return true;
};

Cyan.Ajax.prototype.doPut = function (form, url, args)
{
    form = Cyan.$(form);

    if (!Cyan.Ajax.onsubmit(form))
        return false;

    if (!url)
        url = form.action;

    if (args)
    {
        if (Cyan.isArray(args))
        {
            for (var i = 0; i < args.length; i++)
                url = this.toParameters(args[i]).removes(form).toUrl(url, !form && i == args.length - 1);
        }
        else
        {
            url = this.toParameters(args).removes(form).toUrl(url, !form);
        }
    }

    if (form)
        url = this.toParameters(form).toUrl(url, true);

    this.method = "PUT";
    this.call(url, null);

    return true;
};

Cyan.Ajax.prototype.doDelete = function (form, url, args)
{
    form = Cyan.$(form);

    if (!Cyan.Ajax.onsubmit(form))
        return false;

    if (!url)
        url = form.action;

    if (args)
    {
        if (args)
        {
            if (Cyan.isArray(args))
            {
                for (var i = 0; i < args.length; i++)
                    url = this.toParameters(args[i]).removes(form).toUrl(url, !form && i == args.length - 1);
            }
            else
            {
                url = this.toParameters(args).removes(form).toUrl(url, !form);
            }
        }
    }

    if (form)
        url = this.toParameters(form).toUrl(url, true);

    this.method = "DELETE";
    this.call(url, null);

    return true;
};

Cyan.Ajax.isLongContent = function ()
{
    return this.value.length > 1000;
};

Cyan.callback = function (obj, callback, remove)
{
    var call = Cyan.Ajax.callbacks[callback];
    if (call)
    {
        if (obj instanceof Error)
            call.errorHandle(obj);
        else
            call.handleObject(obj);
        if (remove && window.Cyan)
            Cyan.Ajax.callbacks[callback] = null;
    }
};

Cyan.Ajax.toJson = function (obj)
{
    if (obj == null || obj == window)
        return "null";
    if (Cyan.isString(obj))
        return "\"" + Cyan.escape(obj) + "\"";
    if (Cyan.isNumber(obj))
        return obj.toString();
    if (Cyan.isBoolean(obj))
        return obj.toString();
    if (obj instanceof Date)
        return "new Date(" + obj.getFullYear() + "," + obj.getMonth() + "," + obj.getDate() + "," + obj.getHours() +
                "," +
                obj.getMinutes() + "," + obj.getSeconds() + "," + obj.getMilliseconds() + ")";

    var s;
    if (Cyan.isArray(obj))
    {
        s = "[";
        var n = obj.length;
        for (var i = 0; i < n; i++)
        {
            if (i > 0)
                s += ",";
            s += Cyan.Ajax.toJson(obj[i]);
        }
        s += "]";
        return s;
    }

    s = "{";
    var first = true;
    for (var name in obj)
    {
        var value = obj[name];
        if (value == null || !(value instanceof Function))
        {
            if (first)
                first = false;
            else
                s += ",";
            s += "\"" + Cyan.escape(name) + "\":" + Cyan.Ajax.toJson(value);
        }
    }

    s += "}";
    return s;
};

Cyan.Ajax.Parmater = function (name, value, aliasName, ajax)
{
    this.name = name;
    this.value = value;
    this.aliasName = aliasName;
    this.ajax = ajax;
};
Cyan.Ajax.Parmater.prototype.toString = function ()
{
    var value = this.value;
    if (!Cyan.startsWith(value, "${") || !Cyan.endsWith(value, "}"))
    {
        if (this.ajax && this.ajax.encrypt)
        {
            value = this.ajax.encrypt(this.name, value);
        }
        value = encodeURIComponent(value);
    }
    return encodeURIComponent(this.name) + "=" + value;
};

Cyan.Ajax.removes = function (parameters)
{
    if (parameters)
    {
        Cyan.Array.removeCase(this, function ()
        {
            var parameter = this;
            return !!Cyan.searchFirst(parameters, function ()
            {
                return this.name == parameter.name || Cyan.startsWith(parameter.name, this.name + ".");
            });
        });
    }
    return this;
};

Cyan.Ajax.makeUrl = function (url, all)
{
    var parameters = this;
    var index, start = 0;
    while ((index = url.indexOf("${", start)) >= 0)
    {
        var index2 = url.indexOf("}", index + 2);
        if (index2 < 0)
            break;
        var v = url.substring(index + 2, index2);
        for (var i = 0; i < parameters.length; i++)
        {
            var parameter = parameters[i];
            if (parameter.name == v || (parameter.aliasName && Cyan.Array.contains(parameter.aliasName, v)))
            {
                v = null;
                Cyan.Array.remove(parameters, i);
                url = url.substring(0, index) + encodeURIComponent(parameter.value) + url.substring(index2 + 1);
                break;
            }
        }
        if (v != null)
        {
            if (all)
                url = url.substring(0, index) + url.substring(index2 + 1);
            else
                start = index2 + 1;
        }
    }

    if (all)
        url = url.replace("/?", "?").replace(/\/*$/, "");

    return url;
};

Cyan.Ajax.toUrl = function (url, all)
{
    var parameters = this, parameters2 = null;
    if (url)
    {
        url = this.makeUrl(url, all);
        var index = url.indexOf("?");
        if (index >= 0)
        {
            parameters2 = this.ajax.toParameters(url.substring(index + 1));
            url = url.substring(0, index);
        }
        if (parameters2)
            parameters = Cyan.Array.addAll(parameters2.removes(parameters), parameters);
    }
    var s = parameters.join("&");
    return url ? (s.length > 0 ? url + "?" + s : url) : s;
};

Cyan.Ajax.toQueryString = function (queryString)
{
    var parameters = this;
    if (queryString)
    {
        var parameters2 = this.ajax.toParameters(queryString);
        parameters = Cyan.Array.addAll(parameters2.removes(parameters), parameters);
    }

    return parameters.join("&");
};

Cyan.Ajax.prototype.toParameters = function (obj, prefix, parameters, post, b)
{
    if (obj == null || obj == window)
        return parameters;

    var ajax = this;
    var nodeName;
    try
    {
        nodeName = obj.nodeName;
    }
    catch (e)
    {
        return;
    }

    if (!parameters)
        parameters = [];

    if (obj instanceof Cyan.Elements)
    {
        obj = obj.firstForm();

        if (obj == null)
            return parameters
    }

    var aliasName;
    if (obj.$$aliasName$$)
    {
        aliasName = obj.$$aliasName$$;
        obj = obj.value;

        if (obj == null)
            return parameters
    }

    var i;

    if (window.Blob && obj instanceof Blob)
    {
        Cyan.Array.add(parameters, new Cyan.Ajax.Parmater(prefix, obj, aliasName, this));
    }
    else if (Cyan.isString(obj) && !prefix)
    {
        var ss = obj.split("&");
        for (i = 0; i < ss.length; i++)
        {
            if (ss[i])
            {
                var s = ss[i].split("=");
                var name = s[0];
                var value = "";
                if (s.length > 0)
                    value = s[1];
                try
                {
                    name = decodeURIComponent(name);
                }
                catch (e)
                {
                }
                try
                {
                    value = decodeURIComponent(value);
                }
                catch (e)
                {
                }
                Cyan.Array.add(parameters, new Cyan.Ajax.Parmater(name, value, aliasName, this));
            }
        }
    }
    else if (nodeName == "FORM")
    {
        Cyan.each(obj, function ()
        {
            var method = this.getAttribute("method");
            if (method && "post" == method.toLowerCase() && !post)
                return;

            var name;
            if (this.nodeName == "SELECT")
            {
                name = this.name;
                if (obj[name])
                {
                    if (!post && obj[name].length > 100)
                        return;
                    if (prefix)
                        name = prefix + "." + name;
                    Cyan.each(this.options, function ()
                    {
                        if (this.selected)
                            Cyan.Array.add(parameters, new Cyan.Ajax.Parmater(name, this.value, aliasName, ajax));
                    });
                }
            }
            else if (this.name && (this.value || this.value == ""))
            {
                if (this.type == "file" || this.type == "submit" || this.type == "button" ||
                        (this.type == "checkbox" || this.type == "radio") && !this.checked)
                    return;

                name = this.name;
                var value = this.value;
                if (!post && (value.length > 100 || this.nodeName == "TEXTAREA" ||
                        (this.type != "radio" && obj[name] && obj[name].length > 100)))
                    return;
                Cyan.Array.add(parameters,
                        new Cyan.Ajax.Parmater(prefix ? prefix + "." + name : name, value, aliasName, ajax));
            }
        });
    }
    else if (obj.callee)
    {
        var parameterNames = Cyan.getFunctionParameters(obj.callee);
        var n = Math.min(parameterNames.length, obj.length);
        for (i = 0; i < n; i++)
            this.toParameters(obj[i], parameterNames[i], parameters, true);
    }
    else if (Cyan.isBaseType(obj))
    {
        var content = Cyan.toString(obj);
        if (post || content.length < 300 || content.indexOf("{") >= 0)
            Cyan.Array.add(parameters, new Cyan.Ajax.Parmater(prefix, content, aliasName, this));
    }
    else if (Cyan.isArray(obj))
    {
        if (post || obj.length < 25)
        {
            Cyan.each(obj, function ()
            {
                ajax.toParameters(this, prefix, parameters, post);
            });
        }
    }
    else if (obj && !(obj instanceof Function))
    {
        if (post || b)
        {
            Cyan.each(obj, function (key)
            {
                ajax.toParameters(this, prefix ? prefix + "." + key : key, parameters, post);
            });
        }
        else
        {
            if (!b)
            {
                var parameters2 = this.toParameters(obj, prefix, null, post, true);
                if (parameters2.length < 100)
                {
                    Cyan.Array.addAll(parameters, parameters2);
                }
            }
        }
    }

    parameters.toUrl = Cyan.Ajax.toUrl;
    parameters.makeUrl = Cyan.Ajax.makeUrl;
    parameters.toQueryString = Cyan.Ajax.toQueryString;
    parameters.removes = Cyan.Ajax.removes;
    parameters.ajax = this;
    return parameters;
};

Cyan.Ajax.appendParameterToForm = function (form, obj, excludes, prefix)
{
    if (obj == null || obj == window)
        return;

    if (obj.callee)
    {
        var parameterNames = Cyan.getFunctionParameters(obj.callee);
        var n = Math.min(parameterNames.length, obj.length);
        for (var i = 0; i < n; i++)
            Cyan.Ajax.appendParameterToForm(form, obj[i], excludes, parameterNames[i]);
    }
    else if (Cyan.isBaseType(obj))
    {
        if (!excludes || !Cyan.Array.contains(excludes, prefix))
        {
            var input = document.createElement("INPUT");
            input.type = "hidden";
            input.name = prefix;
            input.value = Cyan.toString(obj);
            form.appendChild(input);
        }
    }
    else if (Cyan.isArray(obj))
    {
        Cyan.each(obj, function ()
        {
            Cyan.Ajax.appendParameterToForm(form, this, excludes, prefix);
        });
    }
    else if (obj && !(obj instanceof Function))
    {
        Cyan.each(obj, function (key)
        {
            Cyan.Ajax.appendParameterToForm(form, this, excludes, prefix ? prefix + "." + key : key);
        });
    }
};

Cyan.Ajax.formatCsrfToken = function (url)
{
    if (url)
    {
        var csrf_token = Cyan.getCookie("csrf_token");
        if (csrf_token)
        {
            url += ( url.indexOf("?") >= 0 ? "&" : "?") + "csrf_token=" + csrf_token;
        }
    }

    return url;
};

Cyan.Elements.prototype.firstForm = function ()
{
    if (!this.form && this.length)
    {
        this.form = this.first.nodeName == "FORM" ? this.first : this.searchFirst("nodeName=='FORM'");
        if (!this.form)
        {
            this.form = this.each(function ()
            {
                var element = this;
                while ((element = element.parentNode))
                {
                    if (element.nodeName == "FORM")
                        return element;
                    else if (element.nodeName == "BODY")
                        break;
                }
            });
        }
    }
    return this.form;
};
Cyan.Elements.prototype.containsFile = function ()
{
    return this.firstForm() && Cyan.any(this.firstForm(), "nodeName=='INPUT'&&type=='file'&&value");
};
Cyan.Elements.prototype.appendParameter = function (obj, excludes, prefix)
{
    Cyan.Ajax.appendParameterToForm(this.firstForm(), obj, excludes, prefix);
};

Cyan.onload(function ()
{
    if (window.$ && $.withCyan)
    {
        $.ajax = function ()
        {
            return new Cyan.Ajax();
        };
    }
});