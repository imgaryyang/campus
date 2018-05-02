Cyan.onload(function ()
{
    if (!Cyan.$("placard"))
        return;

    Cyan.$("placard").innerHTML =
            "<marquee id='placard_marquee' scrollAmount='3'><div id='placard_content'></div></di>";
    var textNode = document.createTextNode("");
    Cyan.$("placard_content").appendChild(textNode);
    var marquee = Cyan.$("placard_marquee");

    Cyan.$$("#placard").onmouseover(function ()
    {
        try
        {
            marquee.stop();
        }
        catch (e)
        {
        }
    }).onmouseout(function ()
    {
        try
        {
            marquee.start();
        }
        catch (e)
        {
        }
    });
    var index = -1;
    var f = function ()
    {
        if (System.placards && System.placards.length)
        {
            index = (index + 1) % System.placards.length;
            textNode.nodeValue = System.placards[index];
        }
    };

    var updateCallback = function (result)
    {
        if (result && Cyan.isArray(result))
        {
            if (!System.placards)
            {
                if (result.length)
                {
                    textNode.nodeValue = result[0];
                    index = 0;

                    setTimeout(function ()
                    {
                        if (Cyan.navigator.isIE())
                        {
                            marquee.onstart = f;
                        }
                        else
                        {
                            setInterval(f, 10000)
                        }
                    }, 10);
                    System.placards = result;
                }
            }
            else
            {
                System.placards = result;
                if (!result.length)
                    textNode.nodeValue = "";
            }
        }
    };

    var update = function ()
    {
        Cyan.Arachne.get("/placards", null, updateCallback);
    };

    update();

    System.getComet().setHandler("com.gzzm.platform.desktop.PlacardUpdate", update);

    Cyan.Class.overwrite(window, "stopFlash", function ()
    {
        this.inherited();
        if (marquee.stop)
            marquee.stop();
    });

    Cyan.Class.overwrite(window, "startFlash", function ()
    {
        this.inherited();
        if (marquee.start)
            marquee.start();
    });
});