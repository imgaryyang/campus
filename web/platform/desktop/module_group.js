var selected = false;

Cyan.onload(function ()
{
    var bodyHeight = document.documentElement.clientHeight;
    var topHeight = Cyan.Elements.getComponentSize(Cyan.$("top")).height;
    Cyan.$("frames").style.height = (bodyHeight - topHeight - 9) + "px";
});

function setCount(frame, count)
{
    if (!Cyan.Arachne.form.count)
        return;

    var index = Cyan.Array.indexOf(Cyan.$$("#frames iframe"), frame);
    var label = Cyan.$$("#labels span:" + index);
    var font = label.$("font")[0];

    if (!font)
        return;

    if (count)
        font.innerHTML = "(" + count + ")";
    else
        font.innerHTML = "";

    if (Cyan.Arachne.form.hideEmpty)
    {
        label.count = count;
        label = label[0];
        if (count)
        {
            label.style.display = "block";
        }
        else
        {
            label.className = "label";
            label.style.display = "none";
        }

        selectLabel();
    }
}

function selectLabel(label, selected)
{
    if (selected)
        window.selected = true;

    var frames = Cyan.$$("#frames iframe");
    var labels = Cyan.$$("#labels span");
    if (label)
    {
        labels.each(function (label1, index)
        {
            if (label1 == label)
            {
                label1.className = "label_selected";
                frames[index].style.display = "";
            }
            else
            {
                label1.className = "label";
                frames[index].style.display = "none";
            }
        });
    }
    else if (!window.selected || !labels.searchFirst(function (label)
            {
                return label.className == "label_selected";
            }))
    {
        var firstDisplayLabel = labels.searchFirst(function (label)
        {
            return label.style.display != "none";
        });
        if (firstDisplayLabel)
        {
            Cyan.$("labels").style.display = "block";
            selectLabel(firstDisplayLabel);
        }
        else
        {
            frames.css("display", "none");
            Cyan.$("labels").style.display = "none";
        }
    }
}

System.reload = function (callback)
{
    Cyan.$$("iframe").each(function (frame)
    {
        var win = frame.contentWindow;
        if (win.System && win.System.reload)
        {
            win.System.reload();
        }
    });
};