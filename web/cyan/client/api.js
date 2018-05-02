navigator.version = Cyan.navigator.version;
navigator.name = Cyan.navigator.name;
navigator.isIE = Cyan.navigator.isIE;
navigator.isFF = Cyan.navigator.isFF;
navigator.isOpera = Cyan.navigator.isOpera;
navigator.isSafari = Cyan.navigator.isSafari;

var each$ = Cyan.each;
var through$ = Cyan.through;
var any$ = Cyan.any;
var all$ = Cyan.all;
var search$ = Cyan.search;
var searchFirst$ = Cyan.searchFirst;
var get$ = Cyan.get;
var clone$ = Cyan.clone;
var max$ = Cyan.max;
var min$ = Cyan.min;
var sort$ = Cyan.sort;

Function.prototype.getParameters = function ()
{
    return Cyan.getFunctionParameters(this);
};
Function.prototype.onload = function ()
{
    Cyan.onload(this);
};
Function.prototype.extend = Cyan.Class.prototype.extend;
Function.prototype.isParentOf = Cyan.Class.prototype.isParentOf;
Function.prototype.mix = Cyan.Class.prototype.mix;
Function.prototype.delegate = Cyan.Class.prototype.delegate;
Function.prototype.overwrite = Cyan.Class.prototype.overwrite;
Function.prototype.advice = Cyan.Class.prototype.advice;
Function.declare = Cyan.Class.declare;
String.prototype.trim = function ()
{
    return Cyan.trim(this);
};
String.prototype.leftTrim = function ()
{
    return Cyan.leftTrim(this);
};
String.prototype.rightTrim = function ()
{
    return Cyan.rightTrim(this);
};
String.prototype.startsWith = function (s)
{
    return Cyan.startsWith(this, s);
};
String.prototype.endsWith = function (s)
{
    return Cyan.endsWith(this, s);
};
String.prototype.replaceAll = function (s1, s2)
{
    return Cyan.replaceAll(this, s1, s2);
};
String.prototype.toInt = function ()
{
    return Cyan.toInt(this);
};
Number.toNumber = Cyan.toNumber;
Number.toInteger = Cyan.toInteger;
Array.prototype.first = function ()
{
    return this[0];
};
Array.prototype.last = function ()
{
    return this[this.length - 1];
};
Array.prototype.clear = function ()
{
    this.length = 0;
};
Array.prototype.each = function (callable)
{
    return Cyan.each(this, callable);
};
Array.prototype.through = function (callable)
{
    return Cyan.Array.through(this, callable);
};
Array.prototype.all = function (callable)
{
    return Cyan.all(this, callable);
};
Array.prototype.any = function (callable)
{
    return Cyan.any(this, callable);
};
Array.prototype.search = function (callable)
{
    return Cyan.search(this, callable);
};
Array.prototype.searchFirst = function (callable)
{
    return Cyan.searchFirst(this, callable);
};
Array.prototype.gets = function (getter)
{
    return Cyan.get(this, getter);
};
if (!Array.prototype.map)
{
    Array.prototype.map = function (f, thisArg)
    {
        this.gets(function (e, index)
        {
            f(e, index);
        });
    };
}
Array.prototype.clone = function (target)
{
    return Cyan.clone(this, target);
};
Array.prototype.max = function (getter)
{
    return Cyan.max(this, getter);
};
Array.prototype.min = function (getter)
{
    return Cyan.min(this, getter);
};
Array.prototype.flatten = function ()
{
    return Cyan.Array.flatten(this);
};
Array.prototype.insert = function (index, obj)
{
    return Cyan.Array.insert(this, index, obj);
};
Array.prototype.removeByIndex = function (index)
{
    return Cyan.Array.remove(this, index);
};
Array.prototype.removeElement = function (obj)
{
    return Cyan.Array.removeElement(obj);
};
Array.prototype.pushAll = function (array)
{
    return Cyan.Array.pushAll(this, array);
};
Array.prototype.removeAll = function (array)
{
    return Cyan.Array.removeAll(this, array);
};
Array.prototype.removeCase = function (callable)
{
    return Cyan.Array.removeCase(this, callable);
};
Array.prototype.contains = function (obj)
{
    return Cyan.Array.contains(this, obj);
};
Array.prototype.containsAll = function (obj)
{
    return Cyan.Array.containsAll(this, obj);
};
Array.prototype.add = function (obj)
{
    return Cyan.Array.add(this, obj);
};
Array.prototype.addAll = function (array)
{
    return Cyan.Array.addAll(this, array);
};
Array.prototype.mod = function (m, x)
{
    return Cyan.Array.mod(this, m, x);
};
Array.prototype.odds = function ()
{
    return Cyan.Array.odds(this);
};
Array.prototype.evens = function ()
{
    return Cyan.Array.evens(this);
};
Date.prototype.formatDate = function (format)
{
    return Cyan.Date.format(this, format);
};
Date.prototype.isToday = function ()
{
    return Cyan.Date.isToday(this);
};
String.prototype.toDate = function (format)
{
    return Cyan.Date.parse(this, format);
};
String.prototype.escapeHtml = function ()
{
    return Cyan.escapeHtml(this);
};
Date.format = Cyan.Date.format;
Date.parse = Cyan.Date.parse;
Date.match = Cyan.Date.match;
Date.matchFormats = Cyan.Date.matchFormats;
Date.matchDateFormat = Cyan.Date.matchDateFormat;
Date.matchTimeFormat = Cyan.Date.matchTimeFormat;
Date.matchDateTimeFormat = Cyan.Date.matchDateTimeFormat;
Date.matchFormat = Cyan.Date.matchFormat;
function $()
{
    return Cyan.$.apply(window, arguments);
}
$.withCyan = true;
var $$ = Cyan.$$;
var $$$ = Cyan.$$$;
$.declare = Cyan.Class.declare;
$.attach = function ()
{
    return Cyan.attach.apply(window, arguments);
};
$.detach = function ()
{
    Cyan.detach.apply(window, arguments);
};
$.onload = Cyan.onload;
$.focus = Cyan.focus;
$.importJs = Cyan.importJs;
$.importCss = Cyan.importCss;
$.setAdapter = Cyan.setAdapter;
location.getParameters = Cyan.getUrlParameters;
location.getParameter = Cyan.getUrlParameter;
$.onload(function ()
{
    $.message = Cyan.message;
    $.error = Cyan.error;
    $.confirm = Cyan.confirm;
    $.prompt = Cyan.prompt;
    $.customConfirm = Cyan.customConfirm;
    $.tip = Cyan.tip;
});