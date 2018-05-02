Cyan.importJs("adapters/extjs/form.js");

Cyan.Class.overwrite(Cyan.ItemSelector, "createListComponent", function (name, data, div, dblclick, click)
{
    var select = this.inherited(name, data, div, dblclick, click).first;
    Cyan.Extjs.Form.renderField(select);
    return Cyan.$$(Cyan.$$(div).$("input:hidden").searchFirst(function ()
    {
        return this.name == name;
    }));
});