Cyan.onload(function ()
{
    System.getMainWindow = function ()
    {
        return Cyan.$("mainFrame").contentWindow;
    };

    Cyan.attach(window, "resize", function ()
    {
        var mainFrame = Cyan.$("mainFrame");
        if (mainFrame != null)
        {
            setTimeout(function ()
            {
                var height = window.document.documentElement.clientHeight;
                mainFrame.style.height = height + "px";
            }, 50);
        }
    });
});

Cyan.onunload(function ()
{
    Cyan.Arachne.get("/login/exit", null, function ()
    {
    });
});