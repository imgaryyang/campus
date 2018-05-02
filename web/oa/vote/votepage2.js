Cyan.onload(function()
{
    window.setTimeout(resize, 100);
});

function resize()
{
    var bodyHeight = document.documentElement.clientHeight;
    var topHeight = Cyan.Elements.getComponentSize(Cyan.$("top")).height;
    Cyan.$("container").style.height = (bodyHeight - topHeight - 26) + "px";
}

