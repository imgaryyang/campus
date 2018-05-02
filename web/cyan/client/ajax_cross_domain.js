Ajax_Cross_Domain = {};
Ajax_Cross_Domain.call = function (url, callback)
{
    var script = document.createElement("SCRIPT");
    script.src = url;
    if (callback)
    {
        var loaded = false;
        var f = function ()
        {
            if (!loaded)
            {
                loaded = true;
                callback(window.$$result$$);
            }
        };
        script.onload = function ()
        {
            f();
        };
        script.onreadystatechange = function ()
        {
            if (script.readyState == "complete" || script.readyState == "loaded")
                f();
        };
    }
    document.body.appendChild(script);
};