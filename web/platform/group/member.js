System.Member = {};

System.selectDepts = function (selected, selectable, appId, callback)
{
    var url = "/deptselect";

    var b = false, i;

    if (selected && selected.length > 0)
    {
        System.Member.selected = selected;
    }

    if (selectable && selectable.length > 0)
    {
        for (i = 0; i < selectable.length; i++)
        {
            if (b)
            {
                url += "&";
            }
            else
            {
                url += "?";
                b = true;
            }
            url += "selectable=" + selectable[i];
        }
    }

    if (appId)
    {
        if (b)
        {
            url += "&";
        }
        else
        {
            url += "?";
        }

        url += "appId=" + appId;
    }

    System.showModal(url, function (result)
    {
        System.Member.selected = null;
        if (callback)
            callback(result);
    });
};

System.selectUsers = function (selected, appId, callback)
{
    var url = "/userselect";

    var b = false, i;

    if (selected && selected.length > 0)
    {
        System.Member.selected = selected;
    }

    if (appId)
    {
        if (b)
        {
            url += "&";
        }
        else
        {
            url += "?";
        }

        url += "appId=" + appId;
    }

    System.showModal(url, function (result)
    {
        System.Member.selected = null;
        if (callback)
            callback(result);
    });
};

System.selectMembers = function (options)
{
    var url = "/member/select";
    if (options.deptId)
        url += "?deptId=" + options.deptId;
    if (options.scopeId)
    {
        url += options.deptId ? "&" : "?";
        url += "scopeId=" + options.scopeId;
    }
    if (options.scopeName)
    {
        url += options.deptId || options.scopeId ? "&" : "?";
        url += "scopeName=" + encodeURIComponent(options.scopeName);
    }
    if (options.app)
    {
        url += options.deptId || options.scopeId || options.scopeName ? "&" : "?";
        url += "app=" + options.app;
    }

    var types = options.types;
    if (types)
    {
        if (Cyan.isString(types))
            types = [types];

        for (var i = 0; i < types.length; i++)
        {
            if (i || options.deptId || options.scopeId || options.scopeName || options.app)
                url += "&";
            else
                url += "?";
            url += "type=" + types[i];
        }
    }

    var callback = options.callback;
    if (options.selected && options.selected.length > 0)
    {
        System.Member.selected = options.selected;

        var f = callback;
        callback = function (result)
        {
            System.Member.selected = null;
            if (f)
                f(result);
        }
    }

    System.showModal(url, callback);
};