Cyan.importJs("/platform/opinion/opinion.js");

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "showBaks", function (name, id)
    {
        if (window.event$)
            window.event$.stop();

        this.inherited(name, id, {
            target: "_page"
        });
    });
});

function edit(name, id, component)
{
    component = Cyan.$$$(component);
    var textComponent = component.$("#text")[0];
    var text = textComponent.value;

    System.Opinion.edit(text, function (ret)
    {
        if (ret != null)
        {
            textComponent.value = ret;
            component.$(".content").html(Cyan.escapeHtml(ret));

            saveOpinion(name, id, ret);
        }
    });
}