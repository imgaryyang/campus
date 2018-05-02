window.Cyan = {};

Cyan.void$ = function ()
{
};

Cyan.initLanguage = function ()
{
    if (!window.languageForCyan$)
        window.languageForCyan$ = navigator.userLanguage || navigator.language || "en";
    var index = window.languageForCyan$.indexOf("-");
    if (index > 0)
        window.languageForCyan$ = window.languageForCyan$.substring(0, index + 1) +
                window.languageForCyan$.substring(index + 1).toLowerCase();
};
Cyan.initLanguage();

Cyan.valueOf = function (obj)
{
    if (obj == null)
        return null;

    try
    {
        if (window.Option && obj instanceof Option)
            return obj.value;
    }
    catch (e)
    {
    }

    if (Cyan.isBaseType(obj))
        return obj;

    if (obj.valueOf == Object.prototype.valueOf || !obj.valueOf)
        return obj.key == null ? obj.value : obj.key;

    return obj.valueOf();
};

Cyan.toString = function (obj)
{
    if (obj == null)
        return "";

    if (obj instanceof Date)
        return Cyan.Date.format(obj);

    try
    {
        if (window.Option && obj instanceof Option)
            return obj.text;
    }
    catch (e)
    {
    }

    if (obj.toString == Object.prototype.toString || !obj.toString)
        return obj.text == null ? obj.value : obj.text;

    return obj.toString();
};

Cyan.isFormData = function (form)
{
    return form && window.FormData && form instanceof FormData;
};

Cyan.customEvents = {};
Cyan.addCustomEvent = function (event, config)
{
    if (!Cyan.customEvents[event])
        Cyan.customEvents[event] = config || {};
};

Cyan.elementInitializers = [];

Cyan.titles =
{
    message: "message",
    error: "error",
    confirm: "confirm",
    prompt: "prompt",
    submiting: "submiting",
    waiting: "waiting",
    waitForSubmit: "wait for submit",
    loading: "loading"
};

Cyan.initNavigator = function ()
{
    Cyan.navigator = {};
    Cyan.navigator.name = "";
    var index;
    if ((index = navigator.userAgent.indexOf("MSIE")) >= 0)
    {
        Cyan.navigator.version = parseInt(navigator.userAgent.substring(index + 5));
        Cyan.navigator.name = "IE" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Firefox")) >= 0)
    {
        Cyan.navigator.version = parseInt(navigator.userAgent.substring(index + 8));
        Cyan.navigator.name = "FF" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Edge")) >= 0)
    {
        Cyan.navigator.version = parseInt(navigator.userAgent.substring(index + 5));
        Cyan.navigator.name = "Edge" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Opera")) >= 0)
    {
        Cyan.navigator.version = parseFloat(navigator.userAgent.substring(index + 6));
        Cyan.navigator.name = "Opera" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Chrome")) >= 0)
    {
        Cyan.navigator.version = parseFloat(navigator.userAgent.substring(index + 7));
        Cyan.navigator.name = "Chrome" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Safari")) >= 0)
    {
        Cyan.navigator.version =
                navigator.userAgent.substring(navigator.userAgent.indexOf("Version/") + 8, index - 1);
        Cyan.navigator.name = "Safari" + Cyan.navigator.version;
    }
    else if ((index = navigator.userAgent.indexOf("Trident/")) >= 0)
    {
        index = navigator.userAgent.indexOf("rv:") + 3;
        Cyan.navigator.version = parseInt(navigator.userAgent.substring(index, index + 2));
        Cyan.navigator.name = "IE" + Cyan.navigator.version;
    }
    Cyan.navigator.isIE = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "IE");
    };
    Cyan.navigator.isEdge = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "Edge");
    };
    Cyan.navigator.isFF = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "FF");
    };
    Cyan.navigator.isOpera = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "Opera");
    };
    Cyan.navigator.isChrome = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "Chrome");
    };
    Cyan.navigator.isSafari = function ()
    {
        return Cyan.startsWith(Cyan.navigator.name, "Safari");
    };
};
Cyan.initNavigator();
if (!document.readyState)
{
    window.onload = function ()
    {
        try
        {
            document.readyState = "complete";
        }
        catch (e)
        {
        }
    };
}

Cyan.isBoolean = function (obj)
{
    return typeof obj == "boolean" || obj instanceof Boolean;
};

Cyan.isNumber = function (obj)
{
    return typeof obj == "number" || obj instanceof Number;
};

Cyan.isString = function (obj)
{
    return typeof obj == "string" || obj instanceof String;
};

Cyan.isDate = function (obj)
{
    return obj instanceof Date;
};

Cyan.isBaseType = function (obj)
{
    return obj != null && (Cyan.isString(obj) || Cyan.isNumber(obj) || Cyan.isBoolean(obj) || Cyan.isDate(obj));
};

Cyan.isArray = function (obj)
{
    return obj != null && obj != window &&
            ( obj instanceof Array || (Cyan.isNumber(obj.length) && !Cyan.isString(obj)));
};

Cyan.requireObj = function (name)
{
    name = name.split(".");
    var obj = window, n = name.length;
    for (var i = 0; i < n; i++)
    {
        if (!obj[name[i]])
            obj[name[i]] = {};
        obj = obj[name[i]];
    }
    return obj;
};

Cyan.setValue = function (obj, name, value)
{
    name = name.split(".");
    var n = name.length - 1;
    for (var i = 0; i < n; i++)
    {
        if (!obj[name[i]])
            obj[name[i]] = {};
        obj = obj[name[i]];
    }
    obj[name[n]] = value;
};

(function ()
{
    var functionParameters = {};
    Cyan.getFunctionParameters = function (f)
    {
        var s = f.toString();
        var parameters = functionParameters[s];
        if (parameters == null)
        {
            var index1 = s.indexOf("(");
            var index2 = s.indexOf(")");
            if (index1 >= 0 && index2 > index1)
            {
                s = Cyan.trim(s.substring(index1 + 1, index2));
                if (s)
                {
                    parameters = s.split(",");
                    for (var i = 0; i < parameters.length; i++)
                        parameters[i] = Cyan.trim(parameters[i]);
                }
            }
            if (parameters == null)
                parameters = [];
            functionParameters[s] = parameters;
        }
        return parameters;
    };

    var isIdPart = function (c)
    {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$';
    };

    var containsWord = function (s, word)
    {
        s = Cyan.trim(s);
        var n = s.length, inString = false, backslashBefore = false, quotation = null;
        var m = word.length, start = 0, idPartBefore = false;
        var word2 = "function", closureLevel = 0, braceCount = 0, braceCounts = [], functionFirst = false;
        var m2 = word2.length, start2 = 0, idPartBefore2 = false, functionBefore = false;
        for (var i = 0; i < n; i++)
        {
            var c = s.charAt(i);

            if (inString)
            {
                if (backslashBefore)
                {
                    backslashBefore = false;
                }
                else if (c == "\\")
                {
                    backslashBefore = true;
                }
                else if ((c == "\"" || c == "'") && quotation == c)
                {
                    inString = false;
                }
            }
            else if (c == "\"" || c == "'")
            {
                inString = true;
                quotation = c;
                start = 0;
                start2 = 0;
                idPartBefore = false;
                idPartBefore2 = false;
                backslashBefore = false;
            }
            else if (isIdPart(c))
            {
                if (functionFirst && closureLevel <= 1 || closureLevel == 0)
                {
                    if (!idPartBefore)
                    {
                        if (c == word.charAt(start))
                        {
                            start++;
                            if (start == m)
                            {
                                if (i == n - 1 || !isIdPart(s.charAt(i + 1)))
                                    return true;

                                start = 0;
                                idPartBefore = true;
                            }
                        }
                        else
                        {
                            idPartBefore = true;
                        }
                    }
                }

                if (!idPartBefore2)
                {
                    if (c == word2.charAt(start2))
                    {
                        start2++;
                        if (start2 == m2)
                        {
                            if (i == n - 1 || !isIdPart(s.charAt(i + 1)))
                            {
                                functionBefore = true;
                                if (i == m2 - 1)
                                    functionFirst = true;
                            }
                            else
                            {
                                start2 = 0;
                                idPartBefore2 = true;
                            }
                        }
                    }
                    else
                    {
                        idPartBefore2 = true;
                    }
                }
            }
            else
            {
                idPartBefore = false;
                idPartBefore2 = false;
                start = 0;
                start2 = 0;

                if (c == '{')
                {
                    if (functionBefore)
                    {
                        closureLevel++;
                        functionBefore = false;
                        braceCounts.push(braceCount);
                        braceCount = 0;
                    }
                    else
                    {
                        braceCount++;
                    }
                }
                else if (c == '}')
                {
                    if (braceCount > 0)
                    {
                        braceCount--;
                    }
                    else if (closureLevel > 0)
                    {
                        closureLevel--;
                        braceCount = braceCounts.pop();
                    }
                }
            }
        }

        return false;
    };

    var containsThis = {};
    Cyan.isFunctionContainsThis = function (f)
    {
        var s = f.toString();

        var b = containsThis[s];
        if (b == null)
            containsThis[s] = b = containsWord(s, "this");

        return b;
    };

    Cyan.containsReturn = function (s)
    {
        return containsWord(s, "return");
    }
})();

Cyan.toFunction = function (callable, variableList)
{
    return Cyan.isString(callable) ? new Function(variableList ? variableList : "", callable) : callable;
};

Cyan.innerFunction = function (callable, v)
{
    if (Cyan.isString(callable))
    {
        if (!Cyan.containsReturn(callable))
            callable = "return " + callable;
        if (!v)
            v = "this";
        callable = new Function(v != "this" && /[a-zA-Z_$][a-zA-Z0-9_$]*/.exec(v) ? v : "",
                "with(" + v + "){\n" + callable + "\n}");
    }
    return callable;
};

Cyan.each = function (obj, callable, thisArgs)
{
    if (!obj)
        return;

    var containsThis = true;
    if (Cyan.isString(callable))
        callable = new Function("key", callable);
    else if (thisArgs == null)
        containsThis = Cyan.isFunctionContainsThis(callable);
    else
        containsThis = false;

    var r, value;
    if (Cyan.isArray(obj))
    {
        var n = obj.length;
        for (var i = 0; i < n; i++)
        {
            try
            {
                value = obj[i];
            }
            catch (e)
            {
                continue;
            }
            if (containsThis)
                r = callable.apply(value, [i]);
            else if (thisArgs)
                r = callable.apply(thisArgs, [value, i]);
            else
                r = callable(value, i);

            if (typeof r != "undefined")
                return r;
        }
        return null;
    }

    for (var key in obj)
    {
        try
        {
            value = obj[key];
        }
        catch (e)
        {
            continue;
        }

        if (containsThis)
            r = callable.apply(value, [key]);
        else if (thisArgs)
            r = callable.apply(thisArgs, [value, i]);
        else
            r = callable(value, key);

        if (typeof r != "undefined")
            return r;
    }

    return null;
};

Cyan.lazyEach = function (obj, callable, finish)
{
    if (!obj)
        return;

    var containsThis = true;
    if (Cyan.isString(callable))
        callable = new Function("key", callable);
    else
        containsThis = Cyan.isFunctionContainsThis(callable);

    var r = null, f, n, i = -1;
    if (Cyan.isArray(obj))
    {
        n = obj.length;
        f = function ()
        {
            if (++i == n)
            {
                if (finish)
                    r = finish();
                return;
            }

            var value = obj[i];
            if (containsThis)
                r = callable.apply(value, [f, i]);
            else
                r = callable(value, f, i);
        };
    }
    else
    {
        var keys = [];
        for (var key in obj)
        {
            keys.push(key);

        }
        n = keys.length;
        f = function ()
        {
            if (++i == n)
            {
                if (finish)
                    r = finish();
                return;
            }

            var key = keys[i];
            var value = obj[key];
            if (containsThis)
                r = callable.apply(value, [f, key]);
            else
                r = callable(value, f, key);
        };
    }

    f();
    return r;
};

Cyan.through = function (obj, callable, iterator, context)
{
    if (!context)
        context = {stoped: false, containsThis: Cyan.isFunctionContainsThis(callable)};

    var r;
    if (context.containsThis)
        r = callable.apply(obj);
    else
        r = callable(obj);

    if (typeof r != "undefined")
    {
        context.stoped = true;
        return r;
    }

    return Cyan.each(iterator ? iterator.apply(obj) : obj, function (key)
    {
        var r = Cyan.through(this, callable, iterator, context);
        if (context.stoped)
            return r;
    });
};

Cyan.lazyThrough = function (obj, callable, iterator, finish)
{
    if (!obj)
        return;

    var sequence = [obj], index = 0, value;
    var f = function ()
    {
        if (index >= sequence.length)
        {
            if (finish)
                finish();
        }
        else
        {
            value = sequence[index++];
            f2();
        }
    };

    var f2 = function ()
    {
        callable(value, f3);
    };

    var f3 = function ()
    {
        iterator(value, f4);
    };

    var f4 = function (objs)
    {
        if (objs)
            Cyan.Array.pushAll(sequence, objs);
        f();
    };

    f();
};

Cyan.any = function (obj, callable)
{
    callable = Cyan.innerFunction(callable);
    return !!Cyan.each(obj, Cyan.isFunctionContainsThis(callable) ? function ()
    {
        if (callable.apply(this, arguments))
            return true;
    } : function (obj, key)
    {
        if (callable(obj, key))
            return true;
    });
};

Cyan.all = function (obj, callable)
{
    callable = Cyan.innerFunction(callable);
    return !Cyan.each(obj, Cyan.isFunctionContainsThis(callable) ? function ()
    {
        if (!callable.apply(this, arguments))
            return true;
    } : function (obj, key)
    {
        if (!callable(obj, key))
            return true;
    });
};

Cyan.search = function (obj, callable)
{
    callable = Cyan.innerFunction(callable);
    var result = [];
    Cyan.each(obj, Cyan.isFunctionContainsThis(callable) ? function ()
    {
        if (callable.apply(this, arguments))
            result.push(this);
    } : function (obj, key)
    {
        if (callable(obj, key))
            result.push(obj);
    });
    return result;
};

Cyan.searchFirst = function (obj, callable)
{
    callable = Cyan.innerFunction(callable);
    return Cyan.each(obj, Cyan.isFunctionContainsThis(callable) ? function ()
    {
        if (callable.apply(this))
            return this;
    } : function (obj, key)
    {
        if (callable(obj, key))
            return obj;
    });
};

Cyan.get = function (obj, getter)
{
    getter = Cyan.innerFunction(getter);
    var result = [];
    Cyan.each(obj, Cyan.isFunctionContainsThis(getter) ? function ()
    {
        result.push(getter.apply(this, arguments));
    } : function (obj, key)
    {
        result.push(getter(obj, key));
    });
    return result;
};

Cyan.map = Cyan.get;

Cyan.clone = function (source, target)
{
    if (!target)
        target = Cyan.isArray(source) ? new Array(source.length) : {};
    Cyan.each(source, function (key)
    {
        target[key] = this;
    });
    return target;
};

Cyan.max = function (obj, getter)
{
    var containsThis;
    if (getter)
    {
        getter = Cyan.innerFunction(getter);
        containsThis = Cyan.isFunctionContainsThis(getter);
    }
    var max = null;

    Cyan.each(obj, function (key)
    {
        var value = getter ? (containsThis ? getter.apply(this, [key]) : getter(this, key)) : this;
        if (max == null || value > max)
            max = value;
    });
    return max;
};

Cyan.min = function (obj, getter)
{
    var containsThis;
    if (getter)
    {
        getter = Cyan.innerFunction(getter);
        containsThis = Cyan.isFunctionContainsThis(getter);
    }
    var min = null;
    Cyan.each(obj, function (key)
    {
        var value = getter ? (containsThis ? getter.apply(this, [key]) : getter(this, key)) : this;
        if (min == null || value < min)
            min = value;
    });
    return min;
};

Cyan.sort = function (obj, comparator, exchanger)
{
    var containsThis;
    if (exchanger)
        containsThis = Cyan.isFunctionContainsThis(exchanger);
    var n = obj.length;
    for (var i = 0; i < n; i++)
    {
        var x = obj[i];
        for (var j = 0; j < i; j++)
        {
            var y = obj[j], b;
            if (comparator == "asc" || !comparator)
                b = y > x;
            else if (comparator == "desc")
                b = y < x;
            else
                b = comparator(x, y) < 0;
            if (b)
            {
                var k;
                if (exchanger)
                {
                    for (k = i; k > j; k--)
                    {
                        if (containsThis)
                            exchanger.apply(obj, [k, k - 1]);
                        else
                            exchanger(obj, k, k - 1);
                    }
                }
                else
                {
                    for (k = i; k > j; k--)
                        obj[k] = obj[k - 1];
                    obj[j] = x;
                }
                break;
            }
        }
    }
    return obj;
};

Cyan.Class = {prototype: {}};

Cyan.Class.extend = function (f, parent)
{
    if (parent instanceof Function)
    {
        var temp = function ()
        {
        };
        temp.prototype = parent.prototype;
        f.prototype = new temp();
        f.prototype.constructor = this;
        f.parent = parent;
        if (f.prototype.inherited)
        {
            var inherited = f.prototype.inherited;
            f.prototype.inherited = function ()
            {
                var oldInherited = this.inherited;
                this.inherited = inherited;
                parent.apply(this, arguments);
                this.inherited = oldInherited;
            };
        }
        else
        {
            f.prototype.inherited = parent;
        }
    }
    return f;
};

Cyan.Class.isParentOf = function (parent, f)
{
    while (f)
    {
        if (f == parent)
            return true;
        f = f.parent;
    }
    return false;
};

Cyan.Class.mix = function (c, f)
{
    if (f instanceof Function)
    {
        for (var name in f.prototype)
        {
            if (typeof c.prototype[name] == "undefined")
                c.prototype[name] = f.prototype[name];
        }
    }
};

Cyan.Class.delegate = function (c, f, property)
{
    if (f instanceof Function)
    {
        for (var name in f.prototype)
        {
            var v = f.prototype[name];
            if (v instanceof Function && typeof c.prototype[name] == "undefined")
                c.prototype[name] = Cyan.delegate(v, property);
        }
    }
};
Cyan.delegate = function (f, property)
{
    return function ()
    {
        return f.apply(this[property], arguments);
    };
};

Cyan.Class.overwrite = function (c, name, f)
{
    if (c.prototype)
    {
        Cyan.Class.overwrite(c.prototype, name, f);
    }
    else
    {
        if (c[name])
        {
            var old = c[name];
            var oldInherited;
            var inherited = function ()
            {
                this.inherited = oldInherited;
                var result;
                if (old.apply)
                {
                    result = old.apply(this, arguments);
                }
                else
                {
                    var n = arguments.length;
                    var s = "old(";
                    for (var i = 0; i < n; i++)
                    {
                        if (i > 0)
                            s += ",";
                        s += "args[" + i + "]";
                    }
                    s += ")";
                    result = eval(s);
                }
                this.inherited = inherited;
                return result;
            };
            c[name] = function ()
            {
                oldInherited = this.inherited;
                this.inherited = inherited;
                var result = f.apply(this, arguments);
                this.inherited = oldInherited;
                return result;
            };
        }
        else
        {
            c[name] = f;
        }
    }
};

Cyan.Class.prototype.extend = function (parent)
{
    Cyan.Class.extend(this, parent);
};
Cyan.Class.prototype.overwrite = function (name, f)
{
    Cyan.Class.overwrite(this, name, f);
};
Cyan.Class.prototype.delegate = function (f, property)
{
    Cyan.Class.delegate(this, f, property);
};
Cyan.Class.prototype.mix = function (f)
{
    Cyan.Class.mix(this, f);
};
Cyan.Class.prototype.isParentOf = function (f)
{
    Cyan.Class.isParentOf(this, f);
};

Cyan.Class.declare = function (name, parent, prototype)
{
    var constructor = prototype.constructor;
    var f = constructor ? function ()
    {
        constructor.apply(this, arguments);
    } : Cyan.void$;
    f.className = name;
    f.extend = Cyan.Class.prototype.extend;
    f.overwrite = Cyan.Class.prototype.overwrite;
    f.delegate = Cyan.Class.prototype.delegate;
    f.mix = Cyan.Class.prototype.mix;
    f.isParentOf = Cyan.Class.prototype.isParentOf;

    name = name.split(".");
    var obj = window, n = name.length - 1;
    for (var i = 0; i < n; i++)
    {
        if (!obj[name[i]])
            obj[name[i]] = {};
        obj = obj[name[i]];
    }
    obj[name[n]] = f;
    if (parent)
    {
        if (Cyan.isArray(parent) && parent.length > 0)
        {
            Cyan.Class.extend(f, parent[0]);
            for (i = 1; i < parent.length; i++)
                Cyan.Class.mix(f, parent[i]);
        }
        else
        {
            Cyan.Class.extend(f, parent);
        }
    }
    Cyan.each(prototype, function (name)
    {
        if (name != "constructor")
            if (this instanceof Function)
                Cyan.Class.overwrite(f, name, this);
            else
                f.prototype[name] = this;
    });
    return f;
};

Cyan.overwrite = Cyan.Class.overwrite;

Cyan.trim = function (s)
{
    return s.replace(/(^\s*)|(\s*$)/g, "");
};
Cyan.leftTrim = function (s)
{
    return s.replace(/^\s*/, "");
};
Cyan.rightTrim = function (s)
{
    return s.replace(/\s*$/, "");
};
Cyan.startsWith = function (s1, s2)
{
    if (s1 == null || s2 == null)
        return false;
    return s1.length < s2.length ? false : s1.substring(0, s2.length) == s2;
};
Cyan.endsWith = function (s1, s2)
{
    if (s1 == null || s2 == null)
        return false;
    return s1.length < s2.length ? false : s1.substring(s1.length - s2.length, s1.length) == s2;
};
Cyan.replaceAll = function (s, s1, s2)
{
    return s ? s.split(s1).join(s2) : s;
};

Cyan.escapeHtml = function (s)
{
    if (!s)
        return "";

    var result = null;

    var n = s.length;
    var rBefore = false;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i);

        if (c == '\r')
        {
            rBefore = true;
        }
        else if (rBefore)
        {
            rBefore = false;
            if (c == '\n')
                continue;
        }

        switch (c)
        {
            case '&':
                if (result == null)
                    result = s.substring(0, i);
                result += "&amp;";
                break;
            case '\"':
                if (result == null)
                    result = s.substring(0, i);
                result += "&quot;";
                break;
            case '\'':
                if (result == null)
                    result = s.substring(0, i);
                result += "&#39;";
                break;
            case '<':
                if (result == null)
                    result = s.substring(0, i);
                result += "&lt;";
                break;
            case '>':
                if (result == null)
                    result = s.substring(0, i);
                result += "&gt;";
                break;
            case ' ':
                if (result == null)
                    result = s.substring(0, i);
                result += "&nbsp;";
                break;
            case '\r':
            case '\n':
                if (result == null)
                    result = s.substring(0, i);
                result += "<br>";
                break;
            default:
                if (result != null)
                    result += c;
        }
    }

    return result == null ? s : result;
};

Cyan.escapeAttribute = function (s)
{
    if (!s)
        return "";

    var result = null;

    var n = s.length;
    var rBefore = false;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i);

        if (c == '\r')
        {
            rBefore = true;
        }
        else if (rBefore)
        {
            rBefore = false;
            if (c == '\n')
                continue;
        }

        switch (c)
        {
            case '&':
                if (result == null)
                    result = s.substring(0, i);
                result += "&amp;";
                break;
            case '\"':
                if (result == null)
                    result = s.substring(0, i);
                result += "&quot;";
                break;
            case '\'':
                if (result == null)
                    result = s.substring(0, i);
                result += "&#39;";
                break;
            case '<':
                if (result == null)
                    result = s.substring(0, i);
                result += "&lt;";
                break;
            case '>':
                if (result == null)
                    result = s.substring(0, i);
                result += "&gt;";
                break;
            default:
                if (result != null)
                    result += c;
        }
    }

    return result == null ? s : result;
};

Cyan.toInt = function (s)
{
    if (Cyan.isNumber(s))
        return s;

    return (s ? parseInt(s, 10) : 0) || 0;
};
Cyan.escape = function (s)
{
    var s2 = null, n = s.length;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i);
        switch (c)
        {
            case '\n':
                if (s2 == null)
                    s2 = s.substring(0, i);
                s2 += "\\n";
                break;
            case '\r':
                if (s2 == null)
                    s2 = s.substring(0, i);
                s2 += "\\r";
                break;
            case '\t':
                if (s2 == null)
                    s2 = s.substring(0, i);
                s2 += "\\t";
                break;
            case '\\':
            case '\"':
            case '\'':
                if (s2 == null)
                    s2 = s.substring(0, i);
                s2 += "\\" + c;
                break;
            default:
                if (s2)
                    s2 += c;
        }
    }
    return s2 ? s2 : s;
};

Cyan.unescape = function (s)
{
    var backslashBefore = false;
    var buffer = "";

    var n = s.length, x, c1;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i);

        if (backslashBefore)
        {
            if (c == 'n')
                c = '\n';
            else if (c == 'r')
                c = '\r';
            else if (c == 't')
                c = '\t';
            else if (c == 'b')
                c = '\b';
            else if (c == 'f')
                c = '\f';
            else if (c == 'u')
            {
                if (n <= i + 4)
                    throw new Error("illegal escape character " + s.substring(i));

                x = 0;
                var y = 16 * 16 * 16;
                for (var j = 0; j < 4; j++)
                {
                    c1 = s.charAt(++i);

                    if (!Cyan.isHex(c1))
                        throw new Error("illegal escape character " + s.substring(i, i + 4));

                    x += Cyan.hexToNum(c1) * y;
                    y /= 16;
                }

                c = String.fromCharCode(x);
            }
            else if (Cyan.isOct(c))
            {
                x = Cyan.octToNum(c);

                if (n > i + 1)
                {
                    c1 = s.charAt(i + 1);
                    if (Cyan.isOct(c1))
                    {
                        i++;
                        x *= 8;
                        x += Cyan.octToNum(c1);

                        if (c < '4' && n > i + 1)
                        {
                            var c2 = s.charAt(i + 1);
                            if (Cyan.isOct(c2))
                            {
                                i++;
                                x *= 8;
                                x += Cyan.octToNum(c2);
                            }
                        }
                    }
                }

                c = String.fromCharCode(x);
            }
            else if (c != '\'' && c != '"' && c != '\\')
            {
                throw new Error("illegal escape character " + c);
            }
            backslashBefore = false;
        }
        else if (c == '\\')
        {
            backslashBefore = true;
            continue;
        }

        buffer += c;
    }

    return buffer;
};

Cyan.isHex = function (c)
{
    return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
};

Cyan.isOct = function (c)
{
    return c >= '0' && c <= '7';
};

Cyan.hexToNum = function (c)
{
    if (c >= '0' && c <= '9')
        return c.charCodeAt(0) - 48;

    if (c >= 'A' && c <= 'F')
        return c.charCodeAt(0) - 55;

    if (c >= 'a' && c <= 'f')
        return c.charCodeAt(0) - 87;

    throw new Error("illegal hex charater" + c);
};

Cyan.octToNum = function (c)
{
    if (c >= '0' && c <= '7')
        return c.charCodeAt(0) - 48;

    throw new Error("illegal hex charater" + c);
};

Cyan.toNumber = function (s, defaultValue, min, max)
{
    if (!Cyan.isNumber(s))
        s = s ? ((s = parseFloat(s)).toString() == "NaN" ? defaultValue : s) : defaultValue;
    return s == null ? s : min != null && s < min ? min : max != null && s > max ? max : s;
};

Cyan.toInteger = function (s, defaultValue, min, max)
{
    if (!Cyan.isNumber(s))
        s = s ? ((s = parseFloat(s)).toString() == "NaN" ? defaultValue : s) : defaultValue;
    return s == null ? s : min != null && s < min ? min : max != null && s > max ? max : Math.round(s);
};

(function ()
{
    var K = 1024;
    var M = K * 1024;
    var G = M * 1024;
    var T = G * 1024;
    var P = T * 1024;
    var format = function (size)
    {
        return (Math.round(size * 100) / 100).toString();
    };
    Cyan.toBytesSize = function (size)
    {
        if (size >= P)
            return format(size / P) + "P";
        else if (size >= T)
            return format(size / T) + "T";
        else if (size >= G)
            return format(size / G) + "G";
        else if (size >= M)
            return format(size / M) + "M";
        else if (size >= K)
            return format(size / K) + "K";
        else
            return size + "B";
    };
})();


Cyan.Array = {};

Cyan.Array.through = function (array, callable)
{
    var containsThis = Cyan.isFunctionContainsThis(callable);
    return Cyan.through(array, function ()
    {
        if (!Cyan.isArray(this))
        {
            if (containsThis)
                callable.apply(this, arguments);
            else
                callable(this);
        }
    }, function ()
    {
        return Cyan.isArray(this) ? this : null;
    });
};
Cyan.Array.flatten = function (array)
{
    var result = [];
    Cyan.Array.through(array, function ()
    {
        result.push(this);
    });
    return result;
};

Cyan.Array.forEach = function (array, callable, thisArgs)
{
    var n = array.length;
    for (var i = 0; i < n; i++)
    {
        var e = array[i];
        if (thisArgs != null)
            callable.apply(thisArgs, [e, i]);
        else
            callable(e, i);
    }
};
Cyan.Array.map = function (array, callable, thisArgs)
{
    var n = array.length;
    var result = new Array(n);
    for (var i = 0; i < n; i++)
    {
        var e = array[i];
        if (thisArgs != null)
            result[i] = callable.apply(thisArgs, [e, i]);
        else
            result[i] = callable(e, i);
    }

    return result;
};
Cyan.Array.some = function (array, callable, thisArgs)
{
    var n = array.length;
    for (var i = 0; i < n; i++)
    {
        var e = array[i];
        var result;
        if (thisArgs != null)
            result = callable.apply(thisArgs, [e, i]);
        else
            result = callable(e, i);

        if (result)
            return true;
    }

    return false;
};
Cyan.Array.every = function (array, callable, thisArgs)
{
    var n = array.length;
    for (var i = 0; i < n; i++)
    {
        var e = array[i];
        var result;
        if (thisArgs != null)
            result = callable.apply(thisArgs, [e, i]);
        else
            result = callable(e, i);

        if (!result)
            return false;
    }

    return true;
};
Cyan.Array.filter = function (array, callable, thisArgs)
{
    var n = array.length;
    var result = [];
    for (var i = 0; i < n; i++)
    {
        var e = array[i];
        var b;
        if (thisArgs != null)
            b = callable.apply(thisArgs, [e, i]);
        else
            b = callable(e, i);

        if (b)
            result.push(e);
    }

    return result;
};
Cyan.Array.reduce = function (array, callable, initialValue)
{
    var n = array.length;
    var result = initialValue;
    for (var i = 0; i < n; i++)
    {
        result = callable(result, array[i]);
    }

    return result;
};
Cyan.Array.reduceRight = function (array, callable, initialValue)
{
    var n = array.length;
    var result = initialValue;
    for (var i = n - 1; i >= 0; i--)
    {
        result = callable(result, array[i]);
    }

    return result;
};
Cyan.Array.indexOf0 = function (array, obj, fromIndex)
{
    var n = array.length;
    for (var i = fromIndex || 0; i < n; i++)
    {
        if (array[i] === obj)
        {
            return i;
        }
    }
    return -1;
};
Cyan.Array.lastIndexOf0 = function (array, obj, fromIndex)
{
    var n = array.length;
    if (fromIndex == null)
        fromIndex = n - 1;
    for (var i = fromIndex; i >= 0; i--)
    {
        if (array[i] === obj)
        {
            return i;
        }
    }
    return -1;
};
Cyan.Array.insert = function (array, index, obj)
{
    if (array.length > index)
    {
        for (var i = array.length; i > index; i--)
            array[i] = array[i - 1];
    }
    array[index] = obj;
    return array;
};
Cyan.Array.remove = function (array, index)
{
    var i, n = array.length, result;
    if (Cyan.isArray(index))
    {
        var empty = {};
        var m = index.length;
        result = new Array(m);
        var minIndex = -1;
        for (i = 0; i < m; i++)
        {
            var indexValue = index[i];
            if (indexValue >= 0 && indexValue < n)
            {
                if (minIndex == -1 || indexValue < minIndex)
                    minIndex = indexValue;
                result[i] = array[indexValue];
                array[indexValue] = empty;
            }
        }
        if (minIndex >= 0)
        {
            i = minIndex;
            for (var offset = 1, j; (j = i + offset) < n; i++)
            {
                for (; j < n; j++)
                {
                    if (array[j] == empty)
                    {
                        offset++;
                    }
                    else
                    {
                        array[i] = array[j];
                        array[j] = empty;
                        break;
                    }
                }
            }
            array.length = n - offset;
        }
    }
    else
    {
        if (index >= n)
            return null;
        result = array[index];
        n--;
        for (i = index; i <= n; i++)
            array[i] = array[i + 1];

        array.length = n;
    }
    return result;
};
Cyan.Array.removeElement0 = function (array, obj)
{
    var n = array.length, b = false;
    for (var i = 0; i < n; i++)
    {
        if (array[i] == obj)
        {
            Cyan.Array.remove(array, i);
            n--;
            i--;
            b = true;
        }
    }
    return b;
};
Cyan.Array.removeElement = function (array, obj)
{
    Cyan.Array.removeElement0(array, obj);
    return array;
};
Cyan.Array.pushAll = function (array1, array2)
{
    var n = array2.length;
    for (var i = 0; i < n; i++)
        array1.push(array2[i]);
    return array1;
};
Cyan.Array.removeAll = function (array1, array2)
{
    var n = array2.length;
    for (var i = 0; i < n; i++)
        array1.removeElement(array2[i]);
    return array1;
};
Cyan.Array.removeCase = function (array, callable)
{
    callable = Cyan.innerFunction(callable);
    var containsThis = Cyan.isFunctionContainsThis(callable);
    var n = array.length, count = 0;
    for (var i = 0; i < n; i++)
    {
        var obj = array[i];
        if (containsThis ? callable.apply(obj) : callable(obj))
            count++;
        else if (count > 0)
            array[i - count] = obj;
    }
    array.length -= count;
    return array;
};
Cyan.Array.indexOf = function (array, obj, fromIndex)
{
    var containsThis;
    if (obj instanceof Function)
        containsThis = Cyan.isFunctionContainsThis(obj);
    var n = array.length;
    for (var i = fromIndex || 0; i < n; i++)
    {
        if (obj instanceof Function)
        {
            if (containsThis ? obj.apply(array[i]) : obj(array[i]))
                return i;
        }
        else if (array[i] == obj)
        {
            return i;
        }
    }
    return -1;
};
Cyan.Array.lastIndexOf = function (array, obj, fromIndex)
{
    var containsThis;
    if (obj instanceof Function)
        containsThis = Cyan.isFunctionContainsThis(obj);
    var n = array.length;
    if (fromIndex == null)
        fromIndex = n - 1;
    for (var i = fromIndex; i >= 0; i--)
    {
        if (obj instanceof Function)
        {
            if (containsThis ? obj.apply(array[i]) : obj(array[i]))
                return i;
        }
        else if (array[i] == obj)
        {
            return i;
        }
    }
    return -1;
};
Cyan.Array.contains = function (array, obj)
{
    return Cyan.Array.indexOf(array, obj) != -1;
};
Cyan.Array.containsAll = function (array, obj)
{
    return !Cyan.searchFirst(obj, function ()
    {
        return !Cyan.Array.contains(array, this);
    });
};
Cyan.Array.add = function (array, obj)
{
    if (Cyan.Array.contains(array, obj))
        return false;
    array.push(obj);
    return true;
};
Cyan.Array.addAll = function (array1, array2)
{
    var n = array2.length;
    for (var i = 0; i < n; i++)
        Cyan.Array.add(array1, array2[i]);
    return array1;
};
Cyan.Array.mod = function (array, m, x)
{
    return Cyan.search(array, function (obj, index)
    {
        return index % m == x;
    });
};
Cyan.Array.odds = function (array)
{
    return Cyan.Array.mod(array, 2, 1);
};
Cyan.Array.evens = function (array)
{
    return this.mod(array, 2, 0);
};

Cyan.Date = {};

Cyan.Date.second = "sec";
Cyan.Date.minute = "min";
Cyan.Date.hour = "hour";
Cyan.Date.day = "day";
Cyan.Date.month = "month";
Cyan.Date.year = "year";

Cyan.Date.defaultFormat = "yyyy-MM-dd HH:mm:ss";
Cyan.Date.dateFormats = ["yyyy-MM-dd", "MM/dd/yyyy", "yyyy-M-d", "M/d/yyyy"];
Cyan.Date.timeFormats = ["HH:mm:ss", "HH:mm", "H:m:s", "H:m"];
Cyan.Date.dateTimeFormats = ["yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss", "yyyy-MM-dd HH:mm",
    "MM/dd/yyyy HH:mm", "yyyy-M-d H:m:s", "M/d/yyyy H:m:s", "yyyy-M-d H:m", "M/d/yyyy H:m", "yyyy-MM-ddTHH:mm",
    "yyyy-MM-ddTHH:mm:ss"];
Cyan.Date.yearMonthFormats = ["yyyy-MM", "yyyy/MM", "yyyy.MM"];
Cyan.Date.monthDayFormats = ["MM-dd", "MM/dd", "MM.dd"];

Cyan.Date.toString = Cyan.Date.format;
Cyan.Date.addDateFormat = function (format)
{
    Cyan.Array.add(Cyan.Date.dateFormats, format);
};
Cyan.Date.addTimeFormat = function (format)
{
    Cyan.Array.add(Cyan.Date.timeFormats, format);
};
Cyan.Date.addDateTimeFormat = function (format)
{
    Cyan.Array.add(Cyan.Date.dateTimeFormats, format);
};
Cyan.Date.addYearMonthFormat = function (format)
{
    Cyan.Array.add(Cyan.Date.yearMonthFormats, format);
};
Cyan.Date.addMonthDayFormat = function (format)
{
    Cyan.Array.add(Cyan.Date.monthDayFormats, format);
};
Cyan.Date.format = function (date, format)
{
    if (!format)
        format = Cyan.Date.defaultFormat;
    var year = "" + date.getFullYear(), year2 = "" + year.substring(2), month = "" + (date.getMonth() + 1);
    var day = "" + date.getDate(), dayInWeek = "" + date.getDay(), HH = "" + date.getHours();
    var minutes = "" + date.getMinutes(), seconds = "" + date.getSeconds(), mon = Cyan.Date.getMonthString(month);
    var month2 = month;
    if (month.length == 1)
        month = "0" + month;
    var day2 = day;
    if (day.length == 1)
        day = "0" + day;
    dayInWeek = Cyan.Date.getDayString(dayInWeek);
    var hh = HH;
    if (hh > 12)
        hh = hh - 12;
    var HH2 = HH;
    if (HH.length == 1)
        HH = "0" + HH;
    var hh2 = hh;
    if (hh.length == 1)
        hh = "0" + hh;
    var minutes2 = minutes;
    if (minutes.length == 1)
        minutes = "0" + minutes;
    var seconds2 = seconds;
    if (seconds.length == 1)
        seconds = "0" + seconds;
    var s = format;
    s = Cyan.replaceAll(s, "yyyy", year);
    s = Cyan.replaceAll(s, "yyy", year2);
    s = Cyan.replaceAll(s, "yy", year2);
    s = Cyan.replaceAll(s, "y", year.substring(2));
    s = Cyan.replaceAll(s, "MM", month);
    s = Cyan.replaceAll(s, "M", month2);
    s = Cyan.replaceAll(s, "dd", day);
    s = Cyan.replaceAll(s, "d", day2);
    s = Cyan.replaceAll(s, "HH", HH);
    s = Cyan.replaceAll(s, "H", HH2);
    s = Cyan.replaceAll(s, "hh", hh);
    s = Cyan.replaceAll(s, "h", hh2);
    s = Cyan.replaceAll(s, "mon", mon);
    s = Cyan.replaceAll(s, "mm", minutes);
    s = Cyan.replaceAll(s, "m", minutes2);
    s = Cyan.replaceAll(s, "ss", seconds);
    s = Cyan.replaceAll(s, "s", seconds2);
    s = Cyan.replaceAll(s, "E", dayInWeek);
    return s;
};

Cyan.Date.parse = function (s, format)
{
    if (s == "")
        return null;
    if (!format)
        format = this.matchFormat(s);
    if (!format)
        return null;
    var year = format.indexOf("yyyy"), month = format.indexOf("MM"), month2 = -1;
    if (month < 0)
        month2 = format.indexOf("M");
    var day = format.indexOf("dd"), day2 = -1;
    if (day < 0)
        day2 = format.indexOf("d");
    var hours = format.indexOf("HH"), hours2 = -1;
    if (hours < 0)
        hours2 = format.indexOf("H");
    var minutes = format.indexOf("mm"), minutes2 = -1;
    if (minutes < 0)
        minutes2 = format.indexOf("m");
    var seconds = format.indexOf("ss"), seconds2 = -1;
    if (seconds < 0)
        seconds2 = format.indexOf("s");
    if (month2 >= 0 || day2 >= 0 || hours2 >= 0 || minutes2 >= 0 || seconds2 >= 0)
    {
        var array = [];
        if (month2 >= 0)
            array[array.length] = month2;
        if (day2 >= 0)
            array[array.length] = day2;
        if (hours2 >= 0)
            array[array.length] = hours2;
        if (minutes2 >= 0)
            array[array.length] = minutes2;
        if (seconds2 >= 0)
            array[array.length] = seconds2;
        for (var i = 0; i < array.length; i++)
        {
            var min = 10000;
            var minJ = i;
            for (j = i; j < array.length; j++)
                if (array[j] < min)
                {
                    min = array[j];
                    minJ = j;
                }
            var temp = array[i];
            array[i] = array[minJ];
            array[minJ] = temp;
        }
        var monthIndex = 0, dayIndex = 0, hoursIndex = 0, minutesIndex = 0, secondsIndex = 0;
        if (month2 >= 0)
            for (i = 0; i < array.length; i++)
                if (array[i] == month2)
                    monthIndex = i;
        if (day2 >= 0)
            for (i = 0; i < array.length; i++)
                if (array[i] == day2)
                    dayIndex = i;
        if (hours2 >= 0)
            for (i = 0; i < array.length; i++)
                if (array[i] == hours2)
                    hoursIndex = i;
        if (minutes2 >= 0)
            for (i = 0; i < array.length; i++)
                if (array[i] == minutes2)
                    minutesIndex = i;
        if (seconds2 >= 0)
            for (i = 0; i < array.length; i++)
                if (array[i] == seconds2)
                    secondsIndex = i;
        var values = [];
        for (i = 0; i < array.length; i++)
        {
            var x = array[i], text = s.substring(x, x + 2), value = Cyan.toInt(text), b = false;
            if (value < 10)
                b = "0" + value == text;
            else
                b = value == text;
            if (b)
            {
                values[i] = value;
                for (var j = i + 1; j < array.length; j++)
                    array[j]++;
                if (year > x)
                    year++;
                if (month > x)
                    month++;
                if (day > x)
                    day++;
                if (hours > x)
                    hours++;
                if (minutes > x)
                    minutes++;
                if (seconds > x)
                    seconds++;
            }
            else
                values[i] = Cyan.toInt(s.substring(x, x + 1));
        }
        if (month2 >= 0)
            month = values[monthIndex];
        if (day2 >= 0)
            day = values[dayIndex];
        if (hours2 >= 0)
            hours = values[hoursIndex];
        if (minutes2 >= 0)
            minutes = values[minutesIndex];
        if (seconds2 >= 0)
            seconds = values[secondsIndex];
    }
    var yearS = null;
    if (year >= 0)
        yearS = s.substring(year, year + 4);
    if (yearS)
        year = Cyan.toInt(yearS);
    else
        year = 2000;
    if (month2 < 0)
    {
        var monthS = month >= 0 ? s.substring(month, month + 2) : null;
        month = monthS ? Cyan.toInt(monthS) : 1;
    }
    if (day2 < 0)
    {
        var dayS = day >= 0 ? s.substring(day, day + 2) : null;
        day = dayS ? Cyan.toInt(dayS) : 1;
    }
    if (hours2 < 0)
    {
        var hoursS = hours >= 0 ? s.substring(hours, hours + 2) : null;
        hours = hoursS ? Cyan.toInt(hoursS) : 0;
    }
    if (minutes2 < 0)
    {
        var minutesS = minutes >= 0 ? s.substring(minutes, minutes + 2) : null;
        minutes = minutesS ? Cyan.toInt(minutesS) : 0;
    }
    if (seconds2 < 0)
    {
        var secondsS = seconds >= 0 ? s.substring(seconds, seconds + 2) : null;
        seconds = secondsS ? Cyan.toInt(secondsS) : 0;
    }
    month--;
    if (year == 0 && month == 0 && day == 1 && hours == 0 && minutes == 0 && seconds == 0)
        return null;
    return new Date(year, month, day, hours, minutes, seconds, 0);
};

Cyan.Date.match = function (s, format)
{
    if (!format)
        return false;
    var value = s;
    s = format;
    s = s.replace("\\", "\\\\");
    if (s.indexOf("dd") >= 0)
        s = s.replace("dd", "(([0-2]\\d)|(3[0-1]))");
    else
        s = s.replace("d", "(([0-2]?\\d)|(3[0-1]))");
    s = s.replace("yyyy", "(\\d{4})");
    s = s.replace("yyy", "(\\d{2})");
    s = s.replace("yy", "(\\d{2})");
    s = s.replace("y", "(\\d{2})");
    s = s.replace("MM", "((0[1-9])|(1[0-2]))");
    s = s.replace("M", "((0?[1-9])|(1[0-2]))");
    s = s.replace("HH", "(([0-1]\\d)|(2[0-3]))");
    s = s.replace("H", "(([0-1]?\\d)|(2[0-3]))");
    s = s.replace("hh", "((0\\d)|(1[0-2]))");
    s = s.replace("h", "((0?\\d)|(1[0-2]))");
    s = Cyan.replaceAll(s, "mm", "([0-5]\\d)");
    s = Cyan.replaceAll(s, "m", "([0-5]?\\d)");
    s = Cyan.replaceAll(s, "ss", "([0-5]\\d)");
    s = Cyan.replaceAll(s, "s", "([0-5]?\\d)");
    try
    {
        var pattern = new RegExp("^" + s + "$");
        return pattern.test(value);
    }
    catch (e)
    {
        return false;
    }
};

Cyan.Date.matchFormats = function (s, formats)
{
    return Cyan.searchFirst(formats, function ()
    {
        if (Cyan.Date.match(s, this))
            return this;
    });
};
Cyan.Date.matchDateFormat = function (s)
{
    return Cyan.Date.matchFormats(s, Cyan.Date.dateFormats);
};
Cyan.Date.matchTimeFormat = function (s)
{
    return Cyan.Date.matchFormats(s, Cyan.Date.timeFormats);
};
Cyan.Date.matchDateTimeFormat = function (s)
{
    return Cyan.Date.matchFormats(s, Cyan.Date.dateTimeFormats);
};
Cyan.Date.matchYearMonthFormat = function (s)
{
    return Cyan.Date.matchFormats(s, Cyan.Date.yearMonthFormats);
};
Cyan.Date.matchMonthDayFormat = function (s)
{
    return Cyan.Date.matchFormats(s, Cyan.Date.monthDayFormats);
};
Cyan.Date.matchFormat = function (s)
{
    return Cyan.Date.matchDateTimeFormat(s) || Cyan.Date.matchDateFormat(s) || Cyan.Date.matchTimeFormat(s) ||
            Cyan.Date.matchYearMonthFormat(s) || Cyan.Date.matchMonthDayFormat(s);
};
Cyan.Date.getDayString = function (day)
{
    return ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"][day];
};
Cyan.Date.getSimpleDayString = function (day)
{
    return ["Sun", "Mon", "Tur", "Wed", "Thu", "Fri", "Sat"][day];
};
Cyan.Date.getMonthString = function (month)
{
    return ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"][month - 1];
};

Cyan.Date.isToday = function (date)
{
    var today = new Date();
    return date.getFullYear() == today.getFullYear() && date.getMonth() == today.getMonth() &&
            date.getDate() == today.getDate();
};

Cyan.Date.getMaxDay = function (month, year)
{
    var maxDay = 31;
    if (month == 3 || month == 5 || month == 8 || month == 10)
    {
        maxDay = 30;
    }
    else if (month == 1)
    {
        if (!year || (year % 4 == 0 && year % 100 != 0))
            maxDay = 29;
        else
            maxDay = 28;
    }
    return maxDay;
};

Cyan.Date.initDays = function (daysComponent, monthsComponent, yearsComponent)
{
    daysComponent = Cyan.$(daysComponent);

    var items;
    if (daysComponent.allItems)
        items = daysComponent.allItems();
    else
        items = daysComponent.options;

    var nullable = items.length && items[0].value == "";
    var nullText;
    if (nullable)
        nullText = items[0].text;

    var month = Cyan.$(monthsComponent).value;
    var year;
    if (yearsComponent)
        year = Cyan.$(yearsComponent).value;

    var day = daysComponent.value;
    if (daysComponent.clearAll)
        daysComponent.clearAll();
    else
        daysComponent.options.length = 0;

    if (nullable)
    {
        if (daysComponent.addItem)
            daysComponent.addItem("", nullText);
        else
            daysComponent.options[daysComponent.options.length] = new Option(nullText, "");
    }

    if (month != "")
    {
        month = parseInt(month);
        if (year)
            year = parseInt(year);

        var maxDay = Cyan.Date.getMaxDay(month, year);
        for (var i = 1; i <= maxDay; i++)
        {
            var day1 = i.toString();
            if (daysComponent.addItem)
                daysComponent.addItem(day1, day1);
            else
                daysComponent.options[daysComponent.options.length] = new Option(day1, day1);
        }

        if (day)
        {
            if (day > maxDay)
                day = maxDay;
        }
        Cyan.$$(daysComponent).setValue(day);
    }
};

Cyan.Date.initMonthDay = function (daysComponent, monthsComponent)
{
    daysComponent = Cyan.$(daysComponent);
    monthsComponent = Cyan.$(monthsComponent);

    Cyan.attach(daysComponent, "change", function ()
    {
        Cyan.Date.initDays(daysComponent, monthsComponent);
    });
};

Cyan.attach = function (component, event, callable, useCapture)
{
    if (!event || !callable)
        return;

    component = Cyan.$(component);
    if (component)
    {
        if (component.addEventListener)
            component.addEventListener(event, callable, useCapture || false);
        else
            component.attachEvent("on" + event, callable);
    }
};
Cyan.detach = function (component, event, callable, useCapture)
{
    if (!event || !callable)
        return;

    component = Cyan.$(component);
    if (!component)
        return;

    if (component.removeEventListener)
        component.removeEventListener(event, callable, useCapture || false);
    else
        component.detachEvent("on" + event, callable);
};
Cyan.fireEvent = function (component, event, args, bubbles)
{
    if (!event)
        return;
    component = Cyan.$(component);
    if (!component)
        return;

    var eventObject;
    if (document.createEvent)
    {
        eventObject = document.createEvent('HTMLEvents');
        eventObject.initEvent(event, bubbles, true);
    }
    else
    {
        eventObject = document.createEventObject();
    }
    if (args)
    {
        Cyan.clone(args, eventObject);
    }
    if (component.dispatchEvent)
        return component.dispatchEvent(eventObject);
    else
        return component.fireEvent("on" + event, eventObject);
};
Cyan.dispatchEvent = Cyan.fireEvent;

Cyan.isLoaded = function ()
{
    return document.readyState == "complete" || document.readyState == "loaded";
};

Cyan.onload = function (callable)
{
    if (Cyan.isLoaded())
        callable();
    else
        Cyan.attach(window, "load", callable);
};

Cyan.onunload = function (callable)
{
    Cyan.attach(window, "unload", callable);
};

Cyan.run = function (condition, callable)
{
    condition = Cyan.innerFunction(condition);
    if (condition())
    {
        callable();
    }
    else
    {
        var intervalId = window.setInterval(function ()
        {
            if (condition())
            {
                window.clearInterval(intervalId);
                callable();
            }
        }, 10);
    }
};

Cyan.Style = function (cssText)
{
    if (cssText)
    {
        var style = this;
        Cyan.each(cssText.split(";"), function (index)
        {
            var s = this.split(":");
            style[Cyan.trim(s[0]).toUpperCase()] = s[1] ? Cyan.trim(s[1]) : "";
        });
    }
};
Cyan.Style.prototype.toString = function ()
{
    var cssText = "";
    Cyan.each(this, function (key)
    {
        if (key && !(this instanceof Function))
        {
            if (cssText.length > 0)
                cssText += ";";
            cssText += key + ":" + this;
        }
    });
    return cssText;
};
Cyan.Style.prototype.add = function (obj)
{
    if (obj.nodeName && obj.style)
        obj = obj.style;
    if (obj.cssText)
        obj = obj.cssText;
    if (Cyan.isString(obj))
        obj = new Cyan.Style(obj);
    Cyan.clone(obj, this);
    return this;
};
Cyan.Style.prototype.set = function (obj)
{
    if (obj.nodeName && obj.style)
        obj = obj.style;
    obj.cssText = new Cyan.Style(obj.cssText).add(this).toString();
};

Cyan.isField = function (element)
{
    return element.nodeName == "INPUT" && element.type != "submit" && element.type != "reset" &&
            element.type != "button" && element.type != "image" || element.nodeName == "SELECT" ||
            element.nodeName == "TEXTAREA";
};

Cyan.$ = function (element, index, field)
{
    if (!element)
        return element;

    if (Cyan.isFormData(element))
        return element;

    var elements;
    if (Cyan.isString(element))
    {
        if (!index)
            elements = document.getElementById(element);
        if (elements)
            return elements;
        elements = document.getElementsByName(element);
    }
    else if (Cyan.isNumber(element.length) && !element.nodeName && !element.parent &&
            !(element instanceof Cyan.Elements))
    {
        elements = element;
    }
    else
    {
        return element;
    }

    if (!(index >= 0) && field)
    {
        var n = elements.length;
        for (var i = 0; i < n; i++)
        {
            element = elements[i];
            if (Cyan.isField(element))
                return element;
        }
    }

    return elements[index ? index : 0];
};

Cyan.Elements = function (elements)
{
    if (elements)
    {
        if (!Cyan.isNumber(elements.length) || elements.nodeName || elements.parent)
        {
            this[0] = elements;
            this.length = 1;
        }
        else
        {
            Cyan.clone(elements, this);
            this.length = elements.length;
        }
    }
    else
    {
        //没有元素
        this.length = 0;
    }
    this.first = this.length > 0 ? this[0] : null;
};
Cyan.Elements.prototype.each = function (callable)
{
    return Cyan.each(this, callable);
};
Cyan.Elements.prototype.all = function (callable)
{
    if (callable)
        return Cyan.all(this, callable);
    if (!this.allElements)
    {
        var result = [];
        this.eachElement(function ()
        {
            result.push(this);
        });
        this.allElements = new Cyan.Elements(result);
    }
    return this.allElements;
};
Cyan.Elements.prototype.any = function (callable)
{
    return Cyan.any(this, callable);
};
Cyan.Elements.prototype.search = function (callable)
{
    return new Cyan.Elements(Cyan.search(this, callable));
};
Cyan.Elements.prototype.searchFirst = function (callable)
{
    return Cyan.searchFirst(this, callable);
};
Cyan.Elements.prototype.get = function (getter)
{
    return Cyan.get(this, getter);
};
Cyan.Elements.prototype.map = Cyan.Elements.prototype.get;
Cyan.Elements.getChildNodes = function (element)
{
    return (element || this).childNodes;
};
Cyan.Elements.prototype.element = function (index)
{
    return new Cyan.Elements([this[index]]);
};
Cyan.Elements.eachElement = function (element, callable)
{
    return Cyan.through(element || document, callable, Cyan.Elements.getChildNodes);
};
Cyan.Elements.prototype.eachElement = function (callable)
{
    return this.each(function ()
    {
        return Cyan.Elements.eachElement(this, callable);
    });
};
Cyan.Elements.prototype.childElements = function ()
{
    return this.first ? new Cyan.Elements(this.first.childNodes) : null;
};
Cyan.Elements.prototype.set = function (name, value)
{
    this.each(function ()
    {
        this[name] = value;
    });
    return value;
};
Cyan.Elements.prototype.values = function ()
{
    var values = [];
    this.each(function ()
    {
        if (this.nodeName == 'SELECT')
        {
            Cyan.each(this.options, function ()
            {
                if (this.selected)
                    values.push(this.value);
            });
        }
        else
            values.push(this.value);
    });
    return values;
};

Cyan.Elements.getText = function (button)
{
    if (button.getText instanceof Function)
        return button.getText();

    if (button.nodeName == "INPUT")
        return button.value;
    else if (button.nodeName == "BUTTON")
        return button.innerHTML;
};

Cyan.Elements.setText = function (element, text)
{
    if (element.setText instanceof Function)
    {
        element.setText(text);
    }
    else if (element.nodeName == "INPUT")
    {
        element.value = text;
    }
    else if (element.nodeName == "BUTTON")
    {
        if (element.childNodes.length)
            element.childNodes[0].nodeValue = text;
        else
            element.appendChild(document.createTextNode(text));
    }
    else
    {
        element.innerHTML = "";
        var ss = text.split("\n");

        for (var i = 0; i < ss.length; i++)
        {
            if (i > 0)
                element.appendChild(document.createElement("br"));

            element.appendChild(document.createTextNode(ss[i]));
        }
    }
};

Cyan.Elements.createButton = function (text)
{
    var button = document.createElement("BUTTON");
    try
    {
        button.type = "button";
    }
    catch (e)
    {
    }
    button.setAttribute("type", "button");
    Cyan.Elements.setText(button, text);

    return button;
};

Cyan.Elements.prototype.getText = function ()
{
    return this.first ? Cyan.Elements.getText(this.first) : null;
};

Cyan.Elements.prototype.setText = function (text)
{
    this.each(function ()
    {
        Cyan.Elements.setText(this, text);
    });
};

Cyan.Elements.prototype.getValue = function ()
{
    var first = this.first;
    if (first)
    {
        if (first.getValue)
            return first.getValue();

        if (first.nodeName == "SELECT" && first.multiple)
        {
            return Cyan.get(Cyan.search(first.options, "selected"), "value");
        }

        return first.value;
    }

    return null;
};

Cyan.Elements.prototype.setValue = function (value, name)
{
    if (value == null)
        value = "";
    var valueSetter = function ()
    {
        if (this.nodeName == "FORM")
        {
            var form = this;
            if (Cyan.isBaseType(value) || Cyan.isArray(value))
            {
                Cyan.$$(Cyan.search(form, function ()
                {
                    return this.name == name;
                })).setValue(value);
            }
            else
            {
                form = Cyan.$$(form);
                Cyan.each(value, function (value, key)
                {
                    form.setValue(value, name ? name + "." + key : key);
                });
            }
        }
        else if (this.nodeName == "OPTION")
        {
            this.selected =
                    Cyan.isArray(value) ? Cyan.Array.contains(value, this.value) : this.value == value.toString();
        }
        else if (this.nodeName == "SELECT")
        {
            Cyan.each(this.options, valueSetter);
            if (this.onvaluechange)
                this.onvaluechange();
        }
        else if (this.type == "checkbox" || this.type == "radio")
        {
            this.checked =
                    Cyan.isArray(value) ? Cyan.Array.contains(value, this.value) :
                    this.value == value.toString();
        }
        else if (this.setValue instanceof Function)
        {
            this.setValue(value);
        }
        else if (this.value || this.value == "")
        {
            this.value = value.toString();
            if (this.onvaluechange)
                this.onvaluechange();
        }
    };
    this.each(valueSetter);
    return this;
};

Cyan.Elements.prototype.val = function (value)
{
    if (value != null)
        return this.setValue(value);
    else
        return this.getValue();
};

Cyan.Elements.prototype.getValues = function (name)
{
    var values = [];
    this.each(function ()
    {
        if (Cyan.isField(this) && this.name == name && (this.value || this.value == "") && this.type != "file")
        {
            if (this.nodeName == "SELECT")
                Cyan.each(this.options, function ()
                {
                    if (this.selected)
                        values.push(this.value);
                });
            else if ((this.type != "checkbox" && this.type != "radio") || this.checked)
                values.push(this.value);
        }
    });
    return values;
};
Cyan.Elements.prototype.getValuesMap = function ()
{
    var result = {};
    Cyan.each(this.first.nodeName == "FORM" ? this.first : Cyan.$$(this.first).$(":field"), function ()
    {
        if (Cyan.isField(this) && (this.value || this.value == "") && this.type != "file")
        {
            var name = this.name;
            var values = result[name];
            if (values == null)
                result[name] = values = [];
            if (this.nodeName == "SELECT")
                Cyan.each(this.options, function ()
                {
                    if (this.selected)
                        values.push(this.value);
                });
            else if ((this.type != "checkbox" && this.type != "radio") || this.checked)
                values.push(this.value);
        }
    });
    return result;
};
Cyan.Elements.prototype.getParent = function ()
{
    return new Cyan.Elements(this.get("parentNode"));
};
Cyan.Elements.prototype.remove = function ()
{
    this.each(Cyan.Elements.remove);
    return this;
};
Cyan.Elements.remove = function ()
{
    this.parentNode.removeChild(this);
};
Cyan.Elements.prototype.goToPrevious = function ()
{
    this.each(Cyan.Elements.goToPrevious);
    return this;
};
Cyan.Elements.goToPrevious = function ()
{
    var previous = this.previousSibling;
    if (previous)
        this.parentNode.insertBefore(this, previous);
};
Cyan.Elements.prototype.goToNext = function ()
{
    this.each(Cyan.Elements.goToNext);
    return this;
};
Cyan.Elements.goToNext = function ()
{
    var next = this.nextSibling;
    if (next)
        this.parentNode.insertBefore(next, this);
};
Cyan.Elements.prototype.attribute = function (name, value)
{
    if (value || value == "")
    {
        this.each(function ()
        {
            if (this.setAttribute)
                this.setAttribute(name, value);
            else
                this[name] = value;
        })
    }
    else
    {
        return this.get(function ()
        {
            return this[name] || (this.getAttribute ? this.getAttribute(name) : null);
        });
    }
};

Cyan.Elements.prototype.attr = Cyan.Elements.prototype.attribute;

Cyan.Elements.hasClass = function (element, className)
{
    return Cyan.Array.contains(element.className.split(" "), className);
};

Cyan.Elements.addClass = function (element, className)
{
    if (!Cyan.Array.contains(element.className.split(" "), className))
        element.className += " " + className;
};

Cyan.Elements.removeClass = function (element, className)
{
    var classes = element.className.split(" ");
    if (Cyan.Array.removeElement0(classes, className))
        element.className = classes.join(" ");
};

Cyan.Elements.prototype.addClass = function (className)
{
    this.each(function ()
    {
        Cyan.Elements.addClass(this, className);
    });
    return this;
};

Cyan.Elements.prototype.removeClass = function (className)
{
    this.each(function ()
    {
        Cyan.Elements.removeClass(this, className);
    });
    return this;
};

Cyan.Elements.isHidden = function (element)
{
    if (element.nodeName == "INPUT" && element.type == "hidden")
        return true;

    if (Cyan.Elements.getCss(element, "display") == "none")
        return true;

    if (Cyan.Elements.getCss(element, "visibility") == "hidden")
        return true;
};

Cyan.Elements.isVisible = function (element)
{
    return !Cyan.Elements.isHidden(element);
};

Cyan.Elements.getCss = function (element, name, styleName)
{
    if (element.nodeType != 1)
        return null;

    if (!styleName)
    {
        if (name.charAt(0) == "-")
        {
            styleName = name;
        }
        else
        {
            var ss = name.split("-");
            styleName = ss[0];
            for (var i = 1; i < ss.length; i++)
            {
                var s = ss[i];
                styleName += s.substring(0, 1).toUpperCase() + s.substring(1);
            }
        }
    }
    if (element.style[styleName])
        return element.style[styleName];

    if (element.currentStyle)
        return element.currentStyle[styleName];

    if (window.getComputedStyle)
        return window.getComputedStyle(element, null).getPropertyValue(name);

    if (document.defaultView && document.defaultView.getComputedStyle)
    {
        return document.defaultView.getComputedStyle(element, null).getPropertyValue(name);
    }

    return null;
};

Cyan.Elements.prototype.css = function (name, value)
{
    var styleName;
    if (name.charAt(0) == "-")
    {
        styleName = name;
    }
    else
    {
        var ss = name.split("-");
        styleName = ss[0];
        for (var i = 1; i < ss.length; i++)
        {
            var s = ss[i];
            styleName += s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }
    if (value || value == "")
    {
        this.each(function ()
        {
            if (this.style)
                this.style[styleName] = value;
        });
        return this;
    }
    else
    {
        var el = this.first;

        if (!el)
            return null;

        return Cyan.Elements.getCss(el, name, styleName);
    }
};
Cyan.Elements.prototype.cssText = function (cssText)
{
    if (cssText || cssText == "")
    {
        var style = new Cyan.Style(cssText);
        this.each(function ()
        {
            style.set(this);
        });
        return this;
    }
    else
    {
        return this.first ? this.first.style.cssText : null;
    }
};
Cyan.Elements.prototype.html = function (html)
{
    if (html || html == "")
        return this.set("innerHTML", html);
    else
        return this.first ? this.first.innerHTML : null;
};
Cyan.Elements.prototype.display = function (show)
{
    this.css("display", (typeof show == "undefined" ? this.any("style.display=='none'") : show) ? "" : "none");
    return this;
};
Cyan.Elements.prototype.show = function ()
{
    this.display(true);
    return this;
};
Cyan.Elements.prototype.hide = function ()
{
    this.display(false);
    return this;
};
Cyan.Elements.prototype.visible = function (visible)
{
    this.css("visibility", (typeof visible == "undefined" ?
            this.any("style.visibility=='hidden'") : visible) ? "visible" : "hidden");
    return this;
};
Cyan.Elements.prototype.disable = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
            Cyan.each(this, arguments.callee);
        else if (this.ondisable)
            this.ondisable();
        else
            this.disabled = true;
    });
    return this;
};
Cyan.Elements.prototype.enable = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
            Cyan.each(this, arguments.callee);
        else if (this.onenable)
            this.onenable();
        else
            this.disabled = false;
    });
    return this;
};
Cyan.Elements.prototype.checkeds = function ()
{
    return this.search("checked");
};
Cyan.Elements.prototype.checkedValues = function ()
{
    return this.search("checked").values();
};
Cyan.Elements.prototype.check = function (checked)
{
    this.set("checked", typeof checked == "undefined" ? this.any("!checked") : !!checked);
    return this;
};
Cyan.Elements.prototype.attach = function (event, callable, useCapture)
{
    this.each(function ()
    {
        Cyan.attach(this, event, callable, useCapture);
    });
    return this;
};
Cyan.Elements.prototype.detach = function (event, callable, useCapture)
{
    this.each(function ()
    {
        Cyan.detach(this, event, callable, useCapture);
    });
    return this;
};
Cyan.Elements.prototype.fireEvent = function (event, args, bubbles)
{
    var result = null;
    this.each(function ()
    {
        var r = Cyan.fireEvent(this, event, args, bubbles);
        if (result == null)
            result = r;
    });
    return result;
};
Cyan.Elements.prototype.dispatchEvent = Cyan.Elements.prototype.fireEvent;
Cyan.Elements.prototype.submit = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
        {
            if (Cyan.fireEvent(this, "submit"))
                this.submit();
        }
    });
    return this;
};
Cyan.Elements.prototype.reset = function ()
{
    this.each(function ()
    {
        if (this.nodeName == "FORM")
            this.reset();
    });
    return this;
};
Cyan.Elements.disableSelection = function (el)
{
    if (Cyan.navigator.isIE())
    {
        el.onselectstart = function ()
        {
            return false;
        };
    }
    el.style["-ms-user-select"] = "none";
    el.style["-moz-user-select"] = "none";
    el.style["-webkit-user-select"] = "none";
};
Cyan.Elements.prototype.disableSelection = function ()
{
    if (Cyan.navigator.isIE())
    {
        this.set("onselectstart", function ()
        {
            return false;
        });
        this.css("-ms-user-select", "none");
    }
    else if (Cyan.navigator.isFF())
    {
        this.css("-moz-user-select", "none");
    }
    else
    {
        this.css("-webkit-user-select", "ignore");
    }
    return this;
};
Cyan.Elements.prototype.getPosition = function ()
{
    if (!this.position)
        this.position = this.get(Cyan.Elements.getPosition);
    return this.position;
};
Cyan.Elements.getPosition = function (element)
{
    if (!element || !element.nodeName)
        element = this;
    return Cyan.Elements.getOffset(element, document.body);
};
Cyan.Elements.getOffset = function (element, container)
{
    var result = {x: element.offsetLeft, y: element.offsetTop}, element0 = element;
    try
    {
        while ((element = element.offsetParent) != container)
        {
            result.x += element.offsetLeft;
            result.y += element.offsetTop;
        }
    }
    catch (e)
    {
    }
    element = element0;
    while ((element = element.parentNode) != container)
    {
        if (!element)
            break;
        result.x -= element.scrollLeft;
        result.y -= element.scrollTop;
    }
    return result;
};

Cyan.Elements.setPosition = function (element, x, y)
{
    var element0 = element;
    while (element = element.offsetParent)
    {
        x += element.scrollLeft - element.offsetLeft;
        y += element.scrollTop - element.offsetTop;
    }
    element0.style.left = x + "px";
    element0.style.top = y + "px";
};

Cyan.Elements.getPositionInFrame = function (element, win)
{
    var position = Cyan.Elements.getPosition.apply(element);
    if (win != window && window.parent != window)
    {
        var iframe = window.frameElement;
        if (iframe)
        {
            var position1 = parent.Cyan.Elements.getPositionInFrame(iframe, win);
            position.x += position1.x - document.body.scrollLeft || document.documentElement.scrollLeft;
            position.y += position1.y - (document.body.scrollTop || document.documentElement.scrollTop) + 1;
        }
    }
    return position;
};
Cyan.Elements.getOffsetParent = function (element)
{
    var parent = element.offsetParent;
    return parent ? parent : document.body;
};
Cyan.Elements.scrollYTo = function (container, element)
{
    var offset = Cyan.Elements.getOffset(element, container);
    var size = Cyan.Elements.getComponentSize(container);
    var size1 = Cyan.Elements.getComponentSize(element);
    var y = offset.y;
    var y1 = y + size1.height;
    var height = size.height;
    var scrollTop = container.scrollTop;

    var min = scrollTop;
    var max = scrollTop + height;

    if (y < min)
    {
        scrollTop = y - 10;
        if (scrollTop < 0)
            scrollTop = 0;
        container.scrollTop = scrollTop;
    }
    else if (y1 > max)
    {
        scrollTop = y1 - height + 10;
        container.scrollTop = scrollTop;
    }
};
Cyan.Elements.scrollXTo = function (container, element)
{
    var offset = Cyan.Elements.getOffset(element, container);
    var size = Cyan.Elements.getComponentSize(container);
    var size1 = Cyan.Elements.getComponentSize(element);
    var x = offset.x;
    var x1 = x + size1.width;
    var width = size.width;
    var scrollLeft = container.scrollLeft;

    var min = scrollLeft;
    var max = scrollLeft + width;

    if (x < min)
    {
        scrollLeft = x - 10;
        if (scrollLeft < 0)
            scrollLeft = 0;
        container.scrollLeft = scrollLeft;
    }
    else if (x1 > max)
    {
        scrollLeft = x1 - width + 10;
        container.scrollLeft = scrollLeft;
    }
};
Cyan.Elements.prototype.center = function ()
{
    this.each(Cyan.Elements.center);
    return this;
};
Cyan.Elements.center = function (element)
{
    if (!element)
        element = this;
    var parent = Cyan.Elements.getOffsetParent(element);
    try
    {
        var elementSize = Cyan.Elements.getComponentSize(element);
        var parentSize;
        if (parent.nodeName == "BODY")
        {
            parentSize = {
                width: window.innerWidth || document.body.clientWidth,
                height: window.innerHeight || document.body.clientHeight
            };
        }
        else
        {
            parentSize = Cyan.Elements.getComponentSize(parent);
        }

        element.style.left = ((parentSize.width - elementSize.width) / 2 + parent.scrollLeft) + "px";
        element.style.top = ((parentSize.height - elementSize.height) / 2 + parent.scrollTop) + "px";
    }
    catch (e)
    {
    }
};
Cyan.Elements.prototype.getSize = function ()
{
    if (!this.size)
        this.size = this.get(Cyan.Elements.getComponentSize);

    return this.size;
};
Cyan.Elements.getComponentSize = function (element)
{
    if ((!element || !element.nodeName) && this.nodeName)
        element = this;

    var width = element.offsetWidth || element.clientWidth;
    var height = element.offsetHeight || element.clientHeight;

    if (!width)
        width = Cyan.toInt(Cyan.Elements.getCss(element, "width"));

    if (!height)
        height = Cyan.toInt(Cyan.Elements.getCss(element, "height"));

    return {width: width, height: height};
};

Cyan.Elements.isElementBefore = function (element)
{
    var before = element.previousSibling;
    return before &&
            (before.nodeType != 3 || Cyan.trim(before.nodeValue).length > 0 ||
            Cyan.Elements.isElementBefore(before));
};
Cyan.Elements.prototype.$ = function (selector)
{
    if (Cyan.isString(selector))
        selector = Cyan.Selector.parse(selector);
    else if (!(Cyan.isArray(selector)))
        selector = [selector];
    var elements = this;
    var result = [];
    Cyan.each(selector, function ()
    {
        Cyan.Array.addAll(result, this.select(elements));
    });
    return new Cyan.Elements(result);
};

Cyan.Elements.setHTMLWithEvalScript = function (element, html)
{
    html = Cyan.replaceAll(html, "<script", "&nbsp;<script");
    element.innerHTML = html;
    Cyan.Elements.evalScript(element);
};

Cyan.Elements.evalScript = function (element)
{
    if ((!element || !element.nodeName) && this.nodeName)
        element = this;

    if (element.nodeName == "SCRIPT")
    {
        eval(element.innerHTML);
    }

    var elements = element.childNodes;
    if (elements)
    {
        for (var i = 0; i < elements.length; i++)
            Cyan.Elements.evalScript(elements[i]);
    }
};

Cyan.Elements.prototype.evalScript = function ()
{
    this.each(Cyan.Elements.evalScript);
};

Cyan.Elements.prototype.setHTMLWithEvalScript = function (html)
{
    this.each(function ()
    {
        Cyan.Elements.setHTMLWithEvalScript(this, html);
    });
};

Cyan.Selector = function ()
{
    this.itemLists = [];
};

Cyan.Selector.selectors = {};

Cyan.Selector.split = function (s, split)
{
    var result = [], n = s.length, backslashBefore = false, quotation = null,
            inSquareBrackets = false, s1 = "", bracketsCount = 0;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i), b = false;
        if (backslashBefore)
        {
            backslashBefore = false;
        }
        else if (c == "\\")
        {
            backslashBefore = true;
        }
        else if (quotation)
        {
            if (quotation == c)
                quotation = null;
        }
        else if (c == "\"" || c == "'")
        {
            quotation = c;
        }
        else if (inSquareBrackets)
        {
            if (c == "]")
                inSquareBrackets = false;
        }
        else if (c == "[")
        {
            inSquareBrackets = true;
        }
        else if (bracketsCount)
        {
            if (c == ")")
                bracketsCount--;
            else if (c == "(")
                bracketsCount++;
        }
        else if (c == "(")
        {
            bracketsCount = 1;
        }
        else if (c == split)
        {
            b = true;
        }

        if (b)
        {
            result.push(s1);
            s1 = "";
        }
        else
        {
            s1 += c;
        }
    }

    result.push(s1);
    return result;
};

Cyan.Selector.unescape = function (s)
{
    var backslashBefore = false;
    var buffer = "";

    var n = s.length, x, c1;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i);

        if (backslashBefore)
        {
            backslashBefore = false;
        }
        else if (c == '\\')
        {
            backslashBefore = true;
            continue;
        }

        buffer += c;
    }

    return buffer;
};

Cyan.Selector.parse = function (s)
{
    var result = this.selectors[s];
    if (!result)
    {
        var ss = Cyan.Selector.split(s, ",");
        result = new Array(ss.length);
        for (var i = 0; i < ss.length; i++)
            result[i] = this.parseOne(ss[i]);
        this.selectors[s] = result;
    }
    return result;
};

Cyan.Selector.parseOne = function (s)
{
    var n = s.length, backslashBefore = false, quotation = null, inSquareBrackets = false,
            bracketsCount = 0, s1 = "", selector = new Cyan.Selector(), type = " ";
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i), b = false;
        if (backslashBefore)
        {
            backslashBefore = false;
        }
        else if (c == "\\")
        {
            backslashBefore = true;
        }
        else if (quotation)
        {
            if (quotation == c)
                quotation = null;
        }
        else if (c == "\"" || c == "'")
        {
            quotation = c;
        }
        else if (inSquareBrackets)
        {
            if (c == "]")
                inSquareBrackets = false;
        }
        else if (c == "[")
        {
            inSquareBrackets = true;
        }
        else if (bracketsCount)
        {
            if (c == ")")
                bracketsCount--;
            else if (c == "(")
                bracketsCount++;
        }
        else if (c == "(")
        {
            bracketsCount = 1;
        }
        else if (c == " " || c == ">" || c == "+" || c == "~")
        {
            b = true;
            if (s1)
            {
                selector.addItemList(Cyan.Selector.parseItem(s1), type);
                type = c;
                s1 = "";
            }
        }

        if (!b)
        {
            s1 += c;
        }
    }

    if (s1)
    {
        selector.addItemList(Cyan.Selector.parseItem(s1), type);
    }

    return selector;
};

Cyan.Selector.prototype.addItemList = function (itemList, type)
{
    itemList.type = type;
    itemList.select = Cyan.Selector.itemListSelect;
    itemList.check = Cyan.Selector.itemListCheck;
    this.itemLists.push(itemList);
};

Cyan.Selector.prototype.select = function (element)
{
    return Cyan.Array.reduce(this.itemLists, function (elements, itemList)
    {
        return Cyan.Array.reduce(elements, function (result, element)
        {
            Cyan.Array.pushAll(result, itemList.select(element));
            return result;
        }, []);
    }, element.nodeName ? [element] : element);
};

Cyan.Selector.prototype.check = function (element, index, length, i)
{
    if (i == null)
        i = this.itemLists.length - 1;

    var itemList = this.itemLists[i];
    if (!itemList.check(element, index, length))
        return false;

    if (i == 0)
        return true;

    if (itemList.type == ">")
    {
        return this.check(element.parentNode, 0, 1, i - 1);
    }
    else if (itemList.type == "+")
    {
        do {
            element = element.previousSibling;
        } while (element && element.nodeType != 1);
        return element && this.check(element, 0, 1, i - 1);
    }
    else if (itemList.type == "~")
    {
        while (element = element.previousSibling)
        {
            if (element.nodeType = 1)
            {
                if (this.check(element, 0, 1, i - 1))
                    return true;
            }
        }
    }
    else
    {
        while (element = element.parentNode)
        {
            if (this.check(element, 0, 1, i - 1))
                return true;

            if (element.nodeName == "BODY")
                break;
        }
    }

    return false;
};

Cyan.Selector.itemListSelect = function (element)
{
    if (this.length == 0 || this[0] == null)
        return [];

    var result;
    if (this.type == ">")
    {
        result = this[0].selectElements(Cyan.Array.filter(element.childNodes, Cyan.Selector.nodeType));
    }
    else if (this.type == "+")
    {
        do {
            element = element.nextSibling;
        } while (element && element.nodeType != 1);
        if (element && this[0].check(element, 0, 1))
            result = [element];
        else
            result = [];
    }
    else if (this.type == "~")
    {
        var elements = [];
        var index = 0;
        while (element = element.nextSibling)
        {
            if (element.nodeType = 1)
                elements.push(element);
        }
        result = this[0].selectElements(elements);
    }
    else
    {
        result = this[0].eachElements(element);
    }

    for (var i = 1; i < this.length; i++)
    {
        if (this[i] == null)
            return [];
        result = this[i].selectElements(result);
    }

    return result;
};

Cyan.Selector.itemListCheck = function (element, index, length)
{
    var n = this.length;
    if (n == 0)
        return false;

    for (var i = 0; i < n; i++)
    {
        if (!this[i].check(element, index, length))
            return false;
    }

    return true;
};

Cyan.Selector.nodeType = function (element)
{
    return element.nodeType == 1;
};

Cyan.Selector.Item = function ()
{
};

Cyan.Selector.Item.prototype.eachElements = function (element)
{
    var item = this, index = 0;
    var result = [];
    Cyan.Elements.eachElement(element, function ()
    {
        if (this != element && this.nodeType == 1 && item.check(this, index++))
        {
            result.push(this);
        }
    });
    return result;
};

Cyan.Selector.Item.prototype.selectElements = function (elements)
{
    var item = this, length = elements.length;
    return Cyan.Array.filter(elements, function (element, index)
    {
        return item.check(element, index, length);
    });
};

Cyan.Selector.All = {
    eachElements: function (element)
    {
        if (element == document)
            return document.getElementsByTagName("*");
        var result = [];
        Cyan.Elements.eachElement(element, function ()
        {
            if (this != element && element.nodeType == 1)
                result.push(this);
        });
    },
    selectElements: function (elements)
    {
        return Cyan.Array.filter(elements, Cyan.Selector.nodeType);
    },
    check: function (element)
    {
        return true;
    }
};

Cyan.Selector.Index = function (index)
{
    this.index = Cyan.Selector.Index.toIndex(index);
};
Cyan.Selector.Index.toIndex = function (index)
{
    return Cyan.isNumber(index) ? index : index == "first" ? 0 : index == "last" ? -1 : Cyan.toInt(index);
};
Cyan.Selector.Index.prototype.selectElements = function (elements)
{
    var n = elements.length, index = 0, element, i;
    if (this.index >= 0)
    {
        for (i = 0; i < n; i++)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                if (index++ == this.index)
                    return [element];
            }
        }
    }
    else
    {
        for (i = n - 1; i >= 0; i--)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                if (index++ == -this.index - 1)
                    return [element];
            }
        }
    }
    return [];
};
Cyan.Selector.Index.prototype.eachElements = function (element)
{
    var index0 = this.index, index = 0, all = [];
    var result = Cyan.Elements.eachElement(element, function ()
    {
        if (this != element && this.nodeType == 1)
        {
            if (index0 < 0)
                all.push(this);
            else if (index++ == index0)
                return [this];
        }
    });
    return result || ( index0 > 0 ? [] : all.length >= -index0 ? [all[all.length + index0]] : []);
};
Cyan.Selector.Index.prototype.check = function (element, index, length)
{
    if (this.index >= 0)
        return index == this.index;
    else if (length)
        return index == this.index + length;
    else
        return false;
};

Cyan.Selector.Between = function (start, end)
{
    this.start = Cyan.Selector.Index.toIndex(start);
    this.end = Cyan.Selector.Index.toIndex(end);
};
Cyan.Selector.Between.prototype.selectElements = function (elements)
{
    var start = this.start, end = this.end, result = [], n = elements.length, i, element, index = 0;
    if (start > 0 && end > 0)
    {
        for (i = 0; i < n; i++)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                if (index >= start && index <= end)
                {
                    result.push(element);
                }
                index++;
                if (index > end)
                    break;
            }
        }
    }
    else
    {
        for (i = 0; i < n; i++)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                result.push(element);
            }
        }

        if (start < 0)
            start += result.length;

        if (end < 0)
            end += result.length;

        result = result.slice(start, end + 1);
    }

    return result;
};
Cyan.Selector.Between.prototype.eachElements = function (element)
{
    var start = this.start, end = this.end, index = 0, all = [], result = [];
    Cyan.Elements.eachElement(element, function ()
    {
        if (this != element && this.nodeType == 1)
        {
            if (start < 0 || end < 0)
            {
                all.push(this);
            }
            else
            {
                if (index >= start && index <= end)
                    result.push(this);
                index++;
                if (index > end)
                    return true;
            }
        }
    });
    if (start < 0 || end < 0)
    {
        if (start < 0)
            start = all.length + start;
        if (end < 0)
            end = all.length + end;
        return all.slice(start, end + 1);
    }
    else
    {
        return result;
    }
};
Cyan.Selector.Between.prototype.check = function (element, index, length)
{
    if ((this.start < 0 || this.end < 0) && !length)
        return false;

    var start = this.start;
    var end = this.end;

    if (start < 0)
        start += length;

    if (end < 0)
        end += length;

    return index >= start && index <= end;
};

Cyan.Selector.Child = Cyan.Class.extend(function (index)
{
    this.index = Cyan.Selector.Index.toIndex(index);
}, Cyan.Selector.Item);
Cyan.Selector.Child.getIndex = function (element)
{
    var childNodes = element.parentNode.childNodes, n = childNodes.length, index = 0;
    for (var i = 0; i < n; i++)
    {
        var node = childNodes[i];
        if (node.nodeType == 1)
        {
            if (node == element)
                return index;
            index++;
        }
    }
};
Cyan.Selector.Child.getLastIndex = function (element)
{
    var childNodes = element.parentNode.childNodes, n = childNodes.length, index = 0;
    for (var i = n - 1; i >= 0; i--)
    {
        var node = childNodes[i];
        if (node.nodeType == 1)
        {
            if (node == element)
                return index;
            index++;
        }
    }
};
Cyan.Selector.Child.prototype.check = function (element, index)
{
    if (this.index >= 0)
        return Cyan.Selector.Child.getIndex(element) == this.index;
    else
        return Cyan.Selector.Child.getLastIndex(element) == -this.index - 1;
};

Cyan.Selector.Mod = Cyan.Class.extend(function (mod, value)
{
    this.mod = Cyan.isNumber(mod) ? mod : mod ? Cyan.toInt(mod) : 3;
    this.value = Cyan.isNumber(value) ? value : value ? Cyan.toInt(value) : 0;
}, Cyan.Selector.Item);
Cyan.Selector.Mod.prototype.check = function (element, index)
{
    return (index + 1) % this.mod == this.value;
};

Cyan.Selector.ChildMod = Cyan.Class.extend(function (mod, value)
{
    this.mod = Cyan.isNumber(mod) ? mod : mod ? Cyan.toInt(mod) : 3;
    this.value = Cyan.isNumber(value) ? value : value ? Cyan.toInt(value) : 0;
}, Cyan.Selector.Item);
Cyan.Selector.ChildMod.prototype.check = function (element, index)
{
    return (Cyan.Selector.Child.getIndex(element) + 1) % this.mod == this.value;
};

Cyan.Selector.TagName = Cyan.Class.extend(function (tagName)
{
    this.tagName = tagName.toUpperCase();
}, Cyan.Selector.Item);
Cyan.Selector.TagName.prototype.check = function (element)
{
    return this.tagName == element.nodeName.toUpperCase();
};
Cyan.Class.overwrite(Cyan.Selector.TagName, "eachElements", function (element)
{
    return element == document ?
            ( this.tagName == "BODY" ? [document.body] : document.getElementsByTagName(this.tagName)) :
            this.inherited(element);
});

Cyan.Selector.Name = Cyan.Class.extend(function (name)
{
    this.name = name;
}, Cyan.Selector.Item);
Cyan.Selector.Name.prototype.check = function (element)
{
    return this.name == element.id || this.name == element.name;
};
Cyan.Class.overwrite(Cyan.Selector.Name, "eachElements", function (element)
{
    if (element == document)
    {
        element = document.getElementsByName(this.name);
        if (element.length)
            return element;
        var e = document.getElementById(this.name);
        return e ? [e] : [];
    }
    return this.inherited(element);
});

Cyan.Selector.Class = Cyan.Class.extend(function (className)
{
    this.className = className;
}, Cyan.Selector.Item);
Cyan.Selector.Class.prototype.check = function (element)
{
    return element.className &&
            (this.className == element.className || Cyan.Array.contains(element.className.split(" "), this.className));
};

Cyan.Selector.Property = Cyan.Class.extend(function (name, value)
{
    var c = name.charAt(name.length - 1);
    this.type = value == null ? 0 : c == '!' ? 1 : c == '^' ? 2 : c == '$' ? 3 : c == '*' ? 4 : 0;

    if (value)
    {
        value = Cyan.trim(value.toString());
        if (Cyan.startsWith(value, "'") && Cyan.endsWith(value, "'") ||
                Cyan.startsWith(value, "\"") && Cyan.endsWith(value, "\""))
        {
            value = value.substring(1, value.length - 1);
        }
    }

    this.name = this.type ? Cyan.trim(name.substring(0, name.length - 1)) : Cyan.trim(name);
    this.value = value;
}, Cyan.Selector.Item);
Cyan.Selector.Property.prototype.check = function (element)
{
    var value = element[this.name] || (element.getAttribute ? element.getAttribute(this.name) : null);
    value = value == null ? null : value.toString();
    return this.value == null ? value != null : this.type == 0 ? this.value == value :
            this.type == 1 ? value && this.value != value :
                    this.type == 2 ? value && Cyan.startsWith(value, this.value) :
                            this.type == 3 ? value && Cyan.endsWith(value, this.value) :
                            value && value.indexOf(this.value) >= 0;
};

Cyan.Selector.Type = Cyan.Class.extend(function (type)
{
    this.type = type.toLowerCase();
}, Cyan.Selector.Item);
Cyan.Selector.Type.prototype.check = function (element)
{
    return element.nodeName == "INPUT" && element.type == this.type;
};

Cyan.Selector.Index = function (index)
{
    this.index = Cyan.Selector.Index.toIndex(index);
};
Cyan.Selector.Index.toIndex = function (index)
{
    return Cyan.isNumber(index) ? index : index == "first" ? 0 : index == "last" ? -1 : Cyan.toInt(index);
};
Cyan.Selector.Index.prototype.selectElements = function (elements)
{
    var n = elements.length, index = 0, element, i;
    if (this.index >= 0)
    {
        for (i = 0; i < n; i++)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                if (index++ == this.index)
                    return [element];
            }
        }
    }
    else
    {
        for (i = n - 1; i >= 0; i--)
        {
            element = elements[i];
            if (element.nodeType == 1)
            {
                if (index++ == -this.index - 1)
                    return [element];
            }
        }
    }
    return [];
};
Cyan.Selector.Index.prototype.eachElements = function (element)
{
    var index0 = this.index, index = 0, all = [];
    var result = Cyan.Elements.eachElement(element, function ()
    {
        if (this != element && this.nodeType == 1)
        {
            if (index0 < 0)
                all.push(this);
            else if (index++ == index0)
                return [this];
        }
    });
    return result || ( index0 > 0 ? [] : all.length >= -index0 ? [all[all.length + index0]] : []);
};
Cyan.Selector.Index.prototype.check = function (element, index, length)
{
    if (this.index >= 0)
        return index == this.index;
    else if (length)
        return index == this.index + length;
    else
        return false;
};

Cyan.Selector.Not = Cyan.Class.extend(function (selectors)
{
    this.selectors = selectors;
}, Cyan.Selector.Item);
Cyan.Selector.Not.prototype.eachElements = function (element)
{
    var result = [];
    Cyan.Elements.eachElement(element, function ()
    {
        if (this != element && this.nodeType == 1)
        {
            result.push(this);
        }
    });
    var length = result.length, item = this;
    return Cyan.Array.filter(result, function (element, index)
    {
        return item.check(element, index, length);
    });
};
Cyan.Selector.Not.prototype.check = function (element, index, length)
{
    return !Cyan.Array.some(this.selectors, function (selector)
    {
        return selector.check(element, index, length);
    });
};

Cyan.Selector.Has = Cyan.Class.extend(function (selectors)
{
    this.selectors = selectors;
}, Cyan.Selector.Item);
Cyan.Selector.Has.prototype.check = function (element, index, length)
{
    var selectors = this.selectors;
    return !!Cyan.Elements.eachElement(element, function ()
    {
        var e = this;
        if (e != element && e.nodeType == 1 && Cyan.Array.some(selectors, function (selector)
                {
                    return selector.check(e, index, length);
                }))
        {
            return true;
        }
    });
};

Cyan.Selector.hidden = new Cyan.Selector.Item();
Cyan.Selector.hidden.check = function (element)
{
    return Cyan.Elements.isHidden(element);
};

Cyan.Selector.visible = new Cyan.Selector.Item();
Cyan.Selector.visible.check = function (element)
{
    return Cyan.Elements.isVisible(element);
};

Cyan.Selector.field = new Cyan.Selector.Item();
Cyan.Selector.field.check = function (element)
{
    return Cyan.isField(element);
};

Cyan.Selector.button = new Cyan.Selector.Item();
Cyan.Selector.button.check = function (element)
{
    return element.nodeName == "INPUT" &&
            (element.type == "button" || element.type == "submit" || element.type == "reset") ||
            element.nodeName == "BUTTON";
};

Cyan.Selector.image = new Cyan.Selector.Item();
Cyan.Selector.image.check = function (element)
{
    return element.nodeName == "IMAGE" || element.nodeName == "IMG" ||
            element.nodeName == "INPUT" && element.type == "image";
};

Cyan.Selector.header = new Cyan.Selector.Item();
Cyan.Selector.header.check = function (element)
{
    var nodeName = element.nodeName;
    if (Cyan.startsWith(nodeName, "H") && nodeName.length == 2)
    {
        var c = nodeName.charAt(1);
        if (c >= '1' && c <= '9')
            return true;
    }
    return false;
};

Cyan.Selector.checked = new Cyan.Selector.Item();
Cyan.Selector.checked.check = function (element)
{
    return element.nodeName == "INPUT" && (element.type == "checkbox" || element.type == "radio") && element.checked;
};

Cyan.Selector.selected = new Cyan.Selector.Item();
Cyan.Selector.selected.check = function (element)
{
    return element.nodeName == "OPTION" && element.selected;
};

Cyan.Selector.enabled = new Cyan.Selector.Item();
Cyan.Selector.enabled.check = function (element)
{
    return !element.disabled;
};

Cyan.Selector.disabled = new Cyan.Selector.Item();
Cyan.Selector.disabled.check = function (element)
{
    return element.disabled;
};

Cyan.Selector.empty = new Cyan.Selector.Item();
Cyan.Selector.empty.check = function (element)
{
    return !element.childNodes.length;
};

Cyan.Selector.parent = new Cyan.Selector.Item();
Cyan.Selector.parent.check = function (element)
{
    return element.childNodes.length;
};

Cyan.Selector.Contains = Cyan.Class.extend(function (text)
{
    this.text = text;
}, Cyan.Selector.Item);
Cyan.Selector.Contains.prototype.check = function (element, index)
{
    if (element.nodeType == 3)
    {
        if (element.nodeValue.indexOf(this.text) >= 0)
            return true;
    }

    var childNodes = element.childNodes, n = childNodes.length;
    for (var i = 0; i < n; i++)
    {
        if (this.check(childNodes[i]))
            return true;
    }

    return false;
};

Cyan.Selector.parsers = {};
Cyan.Selector.addParser = function (c, parser)
{
    this.parsers[c] = parser;
};
Cyan.Selector.addParser("#", {
    parse: function (s)
    {
        return new Cyan.Selector.Name(s);
    }
});
Cyan.Selector.addParser(".", {
    parse: function (s)
    {
        return new Cyan.Selector.Class(s);
    }
});
Cyan.Selector.addParser("@", Cyan.Selector.propertyParser = {
    parse: function (s)
    {
        var index = s.indexOf("="), name, value;
        if (index > 0)
        {
            name = s.substring(0, index);
            value = s.substring(index + 1);
        }
        else
        {
            name = s;
        }

        return new Cyan.Selector.Property(name, value);
    }
});
Cyan.Selector.addParser(":", {
    parse: function (s)
    {
        s = Cyan.trim(s);
        var v = this.nameds[s];
        if (v)
            return v;

        var index;
        if (s.endsWith(")"))
        {
            index = s.indexOf("(");
            if (index > 0)
            {
                var functionName = s.substring(0, index);
                v = this.functions[functionName];
                if (v)
                    return v(Cyan.trim(s.substring(index + 1, s.length - 1)));
            }
        }

        index = s.indexOf("%");
        if (index >= 0)
            return new Cyan.Selector.Mod(Cyan.trim(s.substring(index + 1)), Cyan.trim(s.substring(0, index)));
        return new Cyan.Selector.Index(s);
    }, nameds: {
        hidden: Cyan.Selector.hidden,
        visible: Cyan.Selector.visible,
        checkbox: new Cyan.Selector.Type("checkbox"),
        radio: new Cyan.Selector.Type("radio"),
        submit: new Cyan.Selector.Type("submit"),
        reset: new Cyan.Selector.Type("reset"),
        image: Cyan.Selector.image,
        button: Cyan.Selector.button,
        header: Cyan.Selector.header,
        text: new Cyan.Selector.Type("text"),
        file: new Cyan.Selector.Type("file"),
        password: new Cyan.Selector.Type("password"),
        odd: new Cyan.Selector.Mod(2, 1),
        even: new Cyan.Selector.Mod(2, 0),
        field: Cyan.Selector.field,
        checked: Cyan.Selector.checked,
        selected: Cyan.Selector.selected,
        enabled: Cyan.Selector.enabled,
        disabled: Cyan.Selector.disabled,
        empty: Cyan.Selector.empty,
        parent: Cyan.Selector.parent,
        "first-child": new Cyan.Selector.Child(0),
        "last-child": new Cyan.Selector.Child(-1)
    }, functions: {
        eq: function (s)
        {
            return new Cyan.Selector.Index(s);
        },
        between: function (s)
        {
            var ss = s.split(",");
            return new Cyan.Selector.Between(ss[0], ss[1]);
        },
        gt: function (s)
        {
            var index = Cyan.Selector.Index.toIndex(s);
            return index == -1 ? null : new Cyan.Selector.Between(index + 1, -1);
        },
        lt: function (s)
        {
            var index = Cyan.Selector.Index.toIndex(s);
            return index ? new Cyan.Selector.Between(0, index - 1) : null;
        },
        contains: function (s)
        {
            if (Cyan.startsWith(s, "'") && Cyan.endsWith(s, "'") ||
                    Cyan.startsWith(s, "\"") && Cyan.endsWith(s, "\""))
                s = s.substring(1, s.length - 1);
            return new Cyan.Selector.Contains(Cyan.Selector.unescape(s));
        },
        "nth-child": function (s)
        {
            s = Cyan.trim(s);
            if (s == "odd")
                return new Cyan.Selector.ChildMod(2, 1);

            if (s == "even")
                return new Cyan.Selector.ChildMod(2, 0);

            var index = s.indexOf("%");
            if (index >= 0)
                return new Cyan.Selector.ChildMod(Cyan.trim(s.substring(index + 1)), Cyan.trim(s.substring(0, index)));

            if (s != "first" && s != "last")
                s = Cyan.toInt(s) - 1;

            return new Cyan.Selector.Child(s);
        },
        not: function (s)
        {
            return new Cyan.Selector.Not(Cyan.Selector.parse(s));
        },
        has: function (s)
        {
            return new Cyan.Selector.Has(Cyan.Selector.parse(s));
        }
    }
});

Cyan.Selector.parseItem = function (s)
{
    var temp = "", parser = null, result = [], n = s.length, backslashBefore = false, quotation = null,
            inSquareBrackets = false, bracketsCount = 0;
    for (var i = 0; i < n; i++)
    {
        var c = s.charAt(i), parser1 = null;

        if (backslashBefore)
        {
            backslashBefore = false;
            temp += c;
        }
        else if (c == "\\")
        {
            backslashBefore = true;
            if (bracketsCount)
                temp += c;
        }
        else if (quotation)
        {
            if (quotation == c)
                quotation = null;
            temp += c;
        }
        else if (c == "\"" || c == "'")
        {
            quotation = c;
            temp += c;
        }
        else if (inSquareBrackets)
        {
            if (bracketsCount)
            {
                temp += c;
            }
            else
            {
                if (c == "]")
                {
                    result.push(parser.parse(temp));
                    temp = "";
                    inSquareBrackets = false;
                }
                else
                {
                    temp += c;
                }
            }
        }
        else if (c == "[")
        {
            if (bracketsCount)
            {
                temp += c;
            }
            else
            {
                inSquareBrackets = true;
                parser1 = Cyan.Selector.propertyParser;
            }
        }
        else if (bracketsCount)
        {
            if (c == ")")
                bracketsCount--;
            else if (c == "(")
                bracketsCount++;
            temp += c;
        }
        else if (c == "(")
        {
            bracketsCount = 1;
            temp += c;
        }
        else
        {
            parser1 = this.parsers[c];
            if (!parser1)
                temp += c;
        }
        if ((parser1 || i == n - 1) && temp)
        {
            result.push(parser ? parser.parse(temp) :
                    temp == "*" ? Cyan.Selector.All : new Cyan.Selector.TagName(temp));
        }
        if (parser1)
        {
            parser = parser1;
            temp = "";
        }
    }
    return result;
};

Cyan.$$ = function ()
{
    var result;
    if (arguments.length == 0)
    {
        result = new Cyan.Elements([document]);
    }
    else if (arguments.length == 1 && arguments[0] && arguments[0] instanceof Cyan.Elements)
    {
        return arguments[0];
    }
    else
    {
        result = [];
        for (var i = 0; i < arguments.length; i++)
        {
            var element = arguments[i];
            if (element)
            {
                if (element instanceof Cyan.Elements)
                {
                    Cyan.Array.addAll(result, element);
                }
                else if (Cyan.isString(element))
                {
                    Cyan.each(Cyan.Selector.parse(element), function ()
                    {
                        Cyan.Array.addAll(result, this.select(document));
                    });
                }
                else if (element instanceof Cyan.Selector)
                {
                    Cyan.Array.addAll(result, element.select(document));
                }
                else if (Cyan.isNumber(element.length) && !element.nodeName && !element.parent)
                {
                    Cyan.Array.addAll(result, element);
                }
                else
                {
                    Cyan.Array.add(result, element);
                }
            }
        }
    }
    return new Cyan.Elements(result);
};
Cyan.$$$ = function (element, index)
{
    if (index == null && Cyan.isString(element))
    {
        var e = document.getElementById(element);
        if (e)
            return Cyan.$$(e);
        else
            return Cyan.$$(document.getElementsByName(element));
    }
    else
    {
        return Cyan.$$(Cyan.$(element, index));
    }
};

Cyan.focus = function (component)
{
    if (component)
    {
        component = Cyan.$(component);
        if (component)
        {
            var component1 = component;
            while (component1 && component1.nodeName != "BODY")
            {
                if (component1.active instanceof Function)
                {
                    try
                    {
                        component1.active();
                    }
                    catch (e)
                    {
                    }
                }
                component1 = component1.parentNode;
            }
            if (component.focus)
            {
                try
                {
                    component.focus();
                }
                catch (e)
                {
                }
            }
            if (component.select instanceof Function)
            {
                try
                {
                    component.select();
                }
                catch (e)
                {
                }
            }
        }
    }
};

Cyan.message = function (message, callback)
{
    alert(message);
    if (callback)
        callback();
};

Cyan.error = function (message, callback)
{
    alert(message);
    if (callback)
        callback();
};

Cyan.confirm = function (message, callback, type)
{
    if (!type)
    {
        var ret = confirm(message) ? "ok" : "cancel";
        if (callback)
            callback(ret);
        return ret;
    }
    else
        alert("please set the adapter!");
};

Cyan.prompt = function (message, defaultValue, callback)
{
    var ret = prompt(message, defaultValue);
    if (callback)
        callback(ret);
    return ret;
};

Cyan.initJspath = function ()
{
    if (!window.cyanJspath$)
    {
        var script = Cyan.$$("script").searchFirst(function ()
        {
            return Cyan.endsWith(this.src, 'cyan/client/base.js');
        });
        if (script)
        {
            var path = script.src;
            if (Cyan.startsWith(path, "http://"))
            {
                path = path.substring(7);
                path = path.substring(path.indexOf("/"));
            }
            else if (Cyan.startsWith(path, "https://"))
            {
                path = path.substring(8);
                path = path.substring(path.indexOf("/"));
            }
            window.cyanJspath$ = path.substring(0, path.length - 7);
        }
    }
};
Cyan.initJspath();

Cyan.getRealPath = function (path)
{
    return Cyan.startsWith(path, "/") || path.indexOf(":") > 0 ? path : window.cyanJspath$ + path;
};

Cyan.imports = [];
Cyan.jsImportCallbacks = {};

Cyan.importJs = function (path, callback)
{
    path = Cyan.getRealPath(path);
    var path2;
    if (path.indexOf(":") < 0)
    {
        path2 = location.protocol + "//" + location.host;
        path2 += path;
    }
    else
    {
        path2 = path;
    }
    if (!Cyan.Array.contains(Cyan.imports, path))
    {
        Cyan.imports.push(path);
        if (!Cyan.$$("script").any(function ()
                {
                    return this.src == path || this.src == path2;
                }))
        {
            if (Cyan.isLoaded())
            {
                Cyan.jsImportCallbacks[path] = [];

                var script = document.createElement("SCRIPT");
                script.src = path;
                if (!callback)
                {
                    var callback0 = window.scriptCallback;
                    if (callback0)
                    {
                        callback = function ()
                        {
                            callback0();
                        };
                    }
                }
                if (callback)
                    Cyan.jsImportCallbacks[path].push(callback);

                callback = function ()
                {
                    var callbacks = Cyan.jsImportCallbacks[path];
                    if (callbacks)
                    {
                        for (var i = 0; i < callbacks.length; i++)
                        {
                            callbacks[i]();
                        }
                        Cyan.jsImportCallbacks[path] = null;
                    }
                };

                window.scriptCallback = callback;

                var f = function ()
                {
                    if (window.scriptCallback == callback)
                    {
                        callback();
                        window.scriptCallback = null;
                    }
                };

                if (Cyan.navigator.isIE() && Cyan.navigator.version < 9)
                {
                    script.onreadystatechange = function ()
                    {
                        if (script.readyState == "complete" || script.readyState == "loaded")
                            f();

                    };
                }
                else
                {
                    script.onload = f;
                }

                document.body.appendChild(script);
            }
            else
            {
                document.writeln("<script src=" + path + "></script>");
                if (callback)
                    Cyan.onload(callback);
            }
            return true;
        }
        else
        {
            if (callback)
            {
                if (Cyan.jsImportCallbacks[path])
                {
                    Cyan.jsImportCallbacks[path].push(callback);
                    return true;
                }
            }
        }
    }
    else
    {
        if (callback)
        {
            if (Cyan.jsImportCallbacks[path])
            {
                Cyan.jsImportCallbacks[path].push(callback);
                return true;
            }
        }
    }
    if (callback)
        callback();
    return false;
};

Cyan.importCss = function (path)
{
    path = Cyan.getRealPath(path);
    if (!Cyan.Array.contains(Cyan.imports, path))
    {
        if (!Cyan.$$("link").any(function ()
                {
                    return this.href == path;
                }))
        {
            if (Cyan.isLoaded())
            {
                var link = document.createElement("link");
                link.href = path;
                link.rel = "stylesheet";
                link.type = "text/css";
                document.body.appendChild(link);
            }
            else
            {
                document.writeln("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + path + "\">");
            }
            return true;
        }
        Cyan.imports.push(path);
    }
    return false;
};

Cyan.importLanguage = function (name)
{
    Cyan.importJs("locale/" + window.languageForCyan$ + "/" + name + ".js");
};

Cyan.adapterFiles = [];


Cyan.importAdapter = function (name)
{
    if (Cyan.adapterPath)
        Cyan.importJs(Cyan.adapterPath + name + ".js");
    Cyan.Array.add(Cyan.adapterFiles, name);
};

Cyan.importAdapterCss = function (name)
{
    if (Cyan.adapterPath)
        Cyan.importCss(Cyan.adapterPath + name + ".css");
};

Cyan.setAdapter = function (adapter)
{
    var s = "adapters/" + adapter + "/";
    if (Cyan.adapterPath != s)
    {
        Cyan.adapterPath = s;
        Cyan.each(Cyan.adapterFiles, function ()
        {
            Cyan.importJs(Cyan.adapterPath + this + ".js");
        });
    }
};

Cyan.importLanguage("base");
Cyan.importAdapter("base");

(function ()
{
    var cookies;

    var parseCookies = function ()
    {
        cookies = {};
        var s = document.cookie;
        var length = s.length, stringSymbol, s1 = "", backslashBefore = false;
        for (var i = 0; i < length; i++)
        {
            var c = s.charAt(i);

            if (!stringSymbol && c == ";")
            {
                parseCookie(s1);
                s1 = "";
                continue;
            }

            if (stringSymbol)
            {
                if (backslashBefore)
                    backslashBefore = false;
                else if (c == '\\')
                    backslashBefore = true;
                else if (c == stringSymbol)
                    stringSymbol = null;
            }
            else if (c == "\"" || c == "\'")
            {
                stringSymbol = c
            }

            s1 += c;
        }

        if (s1)
            parseCookie(s1);
    };

    var parseCookie = function (s)
    {
        var index = s.indexOf("=");
        if (index > 0)
        {
            var name = Cyan.trim(s.substring(0, index));
            var value = Cyan.trim(s.substring(index + 1));

            if (Cyan.startsWith(value, "\"") && Cyan.endsWith(value, "\"") ||
                    Cyan.startsWith(value, "\'") && Cyan.endsWith(value, "\'"))
            {
                value = Cyan.unescape(value.substring(1, value.length - 1));
            }

            try
            {
                value = decodeURIComponent(value);
            }
            catch (e)
            {
            }

            cookies[name] = value;
        }
    };

    Cyan.getCookie = function (name)
    {
        if (!cookies)
            parseCookies();

        return cookies[name] || null;
    };

    Cyan.setCookie = function (name, value, expires)
    {
        value = encodeURIComponent(value);
        if (value.indexOf("%") > 0)
            value = "\"" + value + "\"";
        var s = name + "=" + value + ";path=/";
        if (!expires)
            expires = 60 * 60 * 24 * 3650;

        if (expires > 0)
        {
            var expiresDate = new Date();
            expiresDate.setTime(expiresDate.getTime() + expires);

            s += ";expires=" + expiresDate.toGMTString();
        }
        document.cookie = s;
    };
})();

Cyan.getUrlParameters = function (name)
{
    if (name)
    {
        var values = Cyan.getUrlParameters()[name];
        return values ? values : [];
    }

    if (!Cyan.urlParameters)
    {
        Cyan.urlParameters = {};
        if (location.search)
        {
            Cyan.each(location.search.substring(1).split("&"), function ()
            {
                var index = this.indexOf("=");
                if (index > 0)
                {
                    var name = this.substring(0, index);
                    var value = this.substring(index + 1);
                    try
                    {
                        value = decodeURIComponent(value);
                    }
                    catch (e)
                    {
                    }
                    var values = Cyan.urlParameters[name];
                    if (values)
                        values.push(value);
                    else
                        Cyan.urlParameters[name] = [value];
                }
            });
        }
    }
    return Cyan.urlParameters;
};
Cyan.getUrlParameter = function (name)
{
    var values = Cyan.getUrlParameters()[name];
    return values ? values[0] : null;
};
Cyan.addStyle = function (name, cssText)
{
    cssText = name + "{" + cssText + "}";
    if (document.createStyleSheet)
    {
        document.createStyleSheet().cssText = cssText;
    }
    else
    {
        var style = document.createElement("style");
        style.type = "text/css";
        document.getElementsByTagName("HEAD")[0].appendChild(style);
        if (Cyan.navigator.isFF())
            style.innerHTML = cssText;
        else
            style.appendChild(document.createTextNode(cssText));
    }
};

Cyan.formatUrl = function (url)
{
    if (!url)
        return url;
    return url + ( url.indexOf("?") >= 0 ? "&" : "?") + "$$time$$=" +
            Cyan.Date.format(new Date(), "yyyyMMddHHmmss");
};
Cyan.reloadImg = function (img)
{
    img = Cyan.$(img);
    img.src = Cyan.formatUrl(img.src);
};

Cyan.getBodySize = function (win)
{
    if (!win)
        win = window;
    return {width: Cyan.getBodyWidth(win), height: Cyan.getBodyHeight(win)};
};

Cyan.getBodyWidth = function (win)
{
    if (!win)
        win = window;

    var compatMode;
    try
    {
        compatMode = win.document.compatMode;
    }
    catch (e)
    {
        return 400;
    }

    if (compatMode == "BackCompat")
    {
        if (Cyan.navigator.isIE())
            return win.document.body.scrollWidth;
        else if (Cyan.navigator.isFF() || Cyan.navigator.isOpera())
            return win.document.body.scrollWidth;
        else
            return win.document.body.scrollWidth;
    }
    else
    {
        if (Cyan.navigator.isOpera())
            return win.document.body.scrollWidth;
        else
            return win.document.body.scrollWidth;
    }
};

Cyan.getBodyHeight = function (win)
{
    if (!win)
        win = window;

    var compatMode;
    try
    {
        compatMode = win.document.compatMode;
    }
    catch (e)
    {
        return 300;
    }
    if (compatMode == "BackCompat")
    {
        if (Cyan.navigator.isIE())
            return win.document.body.scrollHeight;
        else if (Cyan.navigator.isFF() || Cyan.navigator.isOpera())
            return win.document.documentElement.clientHeight;
        else
            return win.document.body.scrollHeight;
    }
    else
    {
        if (Cyan.navigator.isOpera())
            return win.document.documentElement.clientHeight;
        else if (Cyan.navigator.isFF())
            return win.document.documentElement.offsetHeight;
        else
            return win.document.documentElement.scrollHeight;
    }
};

Cyan.getFileName = function (path, ext)
{
    var pos = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
    if (pos >= 0)
        path = path.substring(pos + 1);
    if (!ext)
    {
        var index = path.lastIndexOf(".");
        if (index > 0)
            path = path.substring(0, index);
    }
    return path;
};

Cyan.getExtName = function (fileName)
{
    if (!fileName)
        return null;

    var s = fileName.toLowerCase();
    if (Cyan.endsWith(s, ".tar.gz"))
        return "tar.gz";

    if (Cyan.endsWith(s, ".tar.bz2"))
        return "tar.bz2";

    var index = fileName.lastIndexOf('.');
    if (index >= 0)
        return fileName.substring(index + 1);
    else
        return null;
};


Cyan.generateId = function (prefix)
{
    var id;
    var index = 0;
    while (Cyan.$(id = "" + prefix + (index++)));
    return id;
};

Cyan.Widget = function ()
{
};
Cyan.Widget.prototype.autoRender = true;
Cyan.Widget.prototype.dragGroup = "default";
Cyan.Widget.prototype.dropGroup = "default";
Cyan.Widget.prototype.ddGroup = "default";
Cyan.Widget.DragContext = {};
Cyan.onload(function ()
{
});

(function ()
{
    var inited = false;
    var f = function ()
    {
        if (inited)
            return;
        inited = true;
        if (!String.prototype.trim)
        {
            String.prototype.trim = function ()
            {
                return Cyan.trim(this);
            };
        }
        if (!Array.prototype.forEach)
        {
            Array.prototype.forEach = function (callback, thisArg)
            {
                Cyan.Array.forEach(this, callback, thisArg);
            };
        }
        if (!Array.prototype.map)
        {
            Array.prototype.map = function (callback, thisArg)
            {
                Cyan.Array.map(this, callback, thisArg);
            };
        }
        if (!Array.prototype.every)
        {
            Array.prototype.every = function (callback, thisArg)
            {
                Cyan.Array.every(this, callback, thisArg);
            };
        }
        if (!Array.prototype.some)
        {
            Array.prototype.some = function (callback, thisArg)
            {
                Cyan.Array.some(this, callback, thisArg);
            };
        }
        if (!Array.prototype.filter)
        {
            Array.prototype.filter = function (callback, thisArg)
            {
                Cyan.Array.filter(this, callback, thisArg);
            };
        }
        if (!Array.prototype.reduce)
        {
            Array.prototype.reduce = function (callback, initialValue)
            {
                Cyan.Array.reduce(this, callback, initialValue);
            };
        }
        if (!Array.prototype.reduceRight)
        {
            Array.prototype.reduceRight = function (callback, initialValue)
            {
                Cyan.Array.reduceRight(this, callback, initialValue);
            };
        }
        if (!Array.prototype.indexOf)
        {
            Array.prototype.indexOf = function (searchElement, fromIndex)
            {
                Cyan.Array.indexOf0(this, searchElement, fromIndex);
            };
        }
        if (!Array.prototype.lastIndexOf)
        {
            Array.prototype.lastIndexOf = function (searchElement, fromIndex)
            {
                Cyan.Array.lastIndexOf0(this, searchElement, fromIndex);
            };
        }
    };

    Cyan.initBasePrototpe = f;
    Cyan.onload(f);
})();