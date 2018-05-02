Cyan.importJs("event.js");

Cyan.Portlet = Cyan.Class.extend(function (name, columns)
{
    this.name = name;
    this.columns = columns;

    if (this.columns.length <= 2)
    {
        this.spacing = 30;
        this.defaultWidth = .48;
        this.defaultHeight = 230;
    }
    else
    {
        this.spacing = 10;
        this.defaultWidth = parseInt(99 / this.columns.length) / 100;
        this.defaultHeight = parseInt(150 * 3 / this.columns.length);
    }
}, Cyan.Widget);

Cyan.importAdapter("portlet");