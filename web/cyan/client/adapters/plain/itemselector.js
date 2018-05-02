Cyan.importJs("adapters/plain/form.js");

Cyan.Class.overwrite(Cyan.ItemSelector, "createListComponent", function (name, data, div, dblclick, click)
{
    var select = this.inherited(name, data, div, dblclick, click).first;
    if (Cyan.navigator.isIE() && Cyan.navigator.version < 9)
    {
        Cyan.onload(function ()
        {
            Cyan.Plain.Form.renderField(select);
        });
    }
    else
    {
        Cyan.Plain.Form.renderField(select);
    }
    return select;
});