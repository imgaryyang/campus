Cyan.onload(function ()
{
    var labels = Cyan.Arachne.form.typeList;
    if (labels && labels.length)
    {
        var labelsDiv = document.createElement("div");
        labelsDiv.id = "labels";

        for (var i = 0; i < labels.length; i++)
        {
            var label = labels[i];

            var span = document.createElement("span");
            span.innerHTML = label.text;
            span.typeName = label.type;
            span.onclick = selectLabel;

            if (label.type == Cyan.Arachne.form.type)
                span.className = "label_selected";
            else
                span.className = "label";

            labelsDiv.appendChild(span);
        }

        Cyan.$("top").appendChild(labelsDiv);
    }
});

function selectLabel()
{
    if (this.className == "label_selected")
        return;

    var labels = Cyan.$$("#labels span");

    for (var i = 0; i < labels.length; i++)
    {
        var label = labels[i];

        if (label == this)
            label.className = "label_selected";
        else
            label.className = "label";
    }

    Cyan.Arachne.form.type = this.typeName;
    loadList(1);
}


function openRecent(url, target)
{
    if (target == "BLANK")
    {
        window.open(url);
    }
    else
    {
        System.openPage(url);
    }
}