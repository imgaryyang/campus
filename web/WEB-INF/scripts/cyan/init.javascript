var isNashorn = __$$engine$$__.indexOf("Nashorn") > 0;

(function ()
{
    var escapeString = function (s)
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

    var isIdPart = function (c)
    {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$';
    };

    var isIdStart = function (c)
    {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$';
    };

    var isId = function (s)
    {
        if (!s)
            return false;

        if (!isIdStart(s[0]))
            return false;

        var n = s.length;
        for (var i = 1; i < n; i++)
        {
            if (!isIdPart(s[i]))
                return false;
        }

        return true;
    };

    var containsWord = function (s, word)
    {
        s.replace(/(^\s*)|(\s*$)/g, "");
        var n = s.length, inString = false, backslashBefore = false, quotation = null;
        var m = word.length, start = 0, idPartBefore = false;
        for (var i = 0; i < n; i++)
        {
            var c = s[i];

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
                idPartBefore = false;
                backslashBefore = false;
            }
            else if (isIdPart(c))
            {
                if (!idPartBefore)
                {
                    if (c == word[start])
                    {
                        start++;
                        if (start == m)
                        {
                            if (i == n - 1 || !isIdPart(s[i + 1]))
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
            else
            {
                idPartBefore = false;
                start = 0;
            }
        }

        return false;
    };

    var arrayIndexOf = function (array, e)
    {
        var n = array.length;
        for (var i = 0; i < n; i++)
        {
            if (array[i] == e)
                return i;
        }

        return -1;
    };

    var arrayContains = function (array, e)
    {
        return arrayIndexOf(array, e) >= 0;
    };

    var arrayRemove = function (array, e)
    {
        var index = arrayIndexOf(array, e);
        if (index >= 0)
            array.splice(index, 1);
    };

    var functionToString = function (f)
    {
        var s = f.toString();

        s = s.replace(/(^\s*)|(\s*$)/g, "");

        if (s.indexOf("function") == 0)
        {
            var s1 = s.substring(8);
            s1 = s1.replace(/(^\s*)|(\s*$)/g, "");

            var index = s1.indexOf("(");
            if (index > 0)
            {
                return "function " + s1.substring(index);
            }
        }

        return s;
    };

    var JsonSerializer;
    if (isNashorn)
        JsonSerializer = Java.type("net.cyan.commons.util.json.JsonSerializer");
    else
        JsonSerializer = Packages.net.cyan.commons.util.json.JsonSerializer;

    var jsonSerializer = new JsonSerializer();

    var JavaString, JavaNumber, JavaBoolean, JavaDate;
    if (isNashorn)
    {
        JavaString = Java.type("java.lang.String");
        JavaNumber = Java.type("java.lang.Number");
        JavaBoolean = Java.type("java.lang.Boolean");
        JavaDate = Java.type("java.util.Date");
    }
    else
    {
        JavaString = Packages.java.lang.String;
        JavaNumber = Packages.java.lang.Number;
        JavaBoolean = Packages.java.lang.Boolean;
        JavaDate = Packages.java.util.Date;
    }

    var toJson = function (obj, toLoadedVariables, loadedVariables)
    {
        var s, name;

        if (obj == null)
        {
            return "null";
        }
        else if (typeof obj == "string" || obj instanceof String || obj instanceof JavaString)
        {
            return "\"" + escapeString(obj) + "\"";
        }
        else if (typeof obj == "number" || obj instanceof Number || obj instanceof JavaNumber)
        {
            return obj.toString();
        }
        else if (typeof obj == "boolean" || obj instanceof Boolean || obj instanceof JavaBoolean)
        {
            return obj.toString();
        }
        else if (obj instanceof Date || obj instanceof JavaDate)
        {
            return "new Date(" + obj.getTime() + ")";
        }
        else if (obj instanceof java.util.Calendar)
        {
            return "new Date(" + obj.getTimeInMillis() + ")";
        }
        else if (obj instanceof Function || typeof obj == "function")
        {
            if (!toLoadedVariables)
                return null;

            s = functionToString(obj);

            for (name in this)
            {
                var x = this[name];
                if (!arrayContains(toLoadedVariables, name) && !arrayContains(loadedVariables, name) &&
                        containsWord(s, name))
                {
                    toLoadedVariables.push(name);
                }
            }

            return s;
        }
        else if (obj instanceof Array)
        {
            s = "[";
            var n = obj.length;
            for (var i = 0; i < n; i++)
            {
                if (i > 0)
                    s += ",";
                s += toJson.apply(this, [obj[i], toLoadedVariables, loadedVariables]);
            }
            s += "]";
            return s;
        }
        else if (obj instanceof Object)
        {
            s = "{";
            var first = true;
            for (name in obj)
            {
                var value = obj[name];

                var s1 = toJson.apply(this, [value, toLoadedVariables, loadedVariables]);
                if (s1 != null)
                {
                    if (first)
                        first = false;
                    else
                        s += ",";
                    s += "\"" + escapeString(name) + "\":" + s1;
                }
            }

            s += "}";
            return s;
        }
        else if (obj instanceof java.lang.Object)
        {
            jsonSerializer.clear();
            jsonSerializer.serialize(obj);
            return jsonSerializer.toString();
        }

        return null;
    };

    var executeS = function (f)
    {
        f = functionToString(f);

        var loadedVariables = ["println", "print", "__$$script$$context", "context"];
        var toLoadedVariables = [];

        var s = "", name, json;
        for (name in this)
        {
            if (isId(name) && containsWord(f, name))
            {
                loadedVariables.push(name);
                arrayRemove(toLoadedVariables, name);
                json = toJson.apply(this, [this[name], toLoadedVariables, loadedVariables]);
                if (json != null)
                {
                    s += "var " + name + "=" + json + ";\r\n";
                }
            }
        }

        while (toLoadedVariables.length)
        {
            name = toLoadedVariables.pop();
            loadedVariables.push(name);

            json = toJson.apply(this, [this[name], toLoadedVariables, loadedVariables]);
            if (json != null)
            {
                s += "var " + name + "=" + json + ";\r\n";
            }
        }


        return "(function(){\r\n" + s + "(" + f + ")();" + "\r\n})();";
    };

    var execute = function (f)
    {
        var s = executeS.apply(this, [f]);

        var HtmlPage;
        if (isNashorn)
            HtmlPage = Java.type("net.cyan.arachne.HtmlPage");
        else
            HtmlPage = Packages.net.cyan.arachne.HtmlPage;

        var htmlPage = HtmlPage.getPage();
        if (htmlPage != null)
        {
            htmlPage.execute(s);
        }
        else
        {
            var BaseScript;
            if (isNashorn)
                BaseScript = Java.type("net.cyan.commons.util.json.JsonSerializer.BaseScript");
            else
                BaseScript = Packages.net.cyan.commons.util.json.JsonSerializer.BaseScript;

            if (this.__$$result$$__ instanceof BaseScript)
                s = this.__$$result$$__.toString() + "\r\n" + s;

            this.__$$result$$__ = new BaseScript(s);
        }
    };

    this.toJson = toJson;
    this.executeS = executeS;
    this.execute = execute;
})();

function query(sql, database)
{
    var SimpleDao;
    if (isNashorn)
        SimpleDao = Java.type("com.gzzm.platform.commons.SimpleDao");
    else
        SimpleDao = Packages.com.gzzm.platform.commons.SimpleDao;

    return SimpleDao.getInstance(database || "").sqlQuery(sql, __$$script$$context);
}

function queryForType(sql, c, database)
{
    var SimpleDao;
    if (isNashorn)
        SimpleDao = Java.type("com.gzzm.platform.commons.SimpleDao");
    else
        SimpleDao = Packages.com.gzzm.platform.commons.SimpleDao;

    return SimpleDao.getInstance(database || "").sqlQuery(sql, c, __$$script$$context);
}

function queryFirst(sql, database)
{
    var SimpleDao;
    if (isNashorn)
        SimpleDao = Java.type("com.gzzm.platform.commons.SimpleDao");
    else
        SimpleDao = Packages.com.gzzm.platform.commons.SimpleDao;

    return SimpleDao.getInstance(database || "").sqlQueryFirst(sql, __$$script$$context);
}

function queryFirstForType(sql, c, database)
{
    var SimpleDao;
    if (isNashorn)
        SimpleDao = Java.type("com.gzzm.platform.commons.SimpleDao");
    else
        SimpleDao = Packages.com.gzzm.platform.commons.SimpleDao;

    return SimpleDao.getInstance(database || "").sqlQueryFirst(sql, c, __$$script$$context);
}

function getBean(c)
{
    var Tools;
    if (isNashorn)
        Tools = Java.type("com.gzzm.platform.commons.Tools");
    else
        Tools = Packages.com.gzzm.platform.commons.Tools;

    return Tools.getBean(c);
}