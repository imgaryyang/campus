Cyan.Arachne.exportTable = function (table, type, title)
{
    if (Cyan.isString(table))
    {
        table = Cyan.$(table);
    }

    if (table.nodeName == "TABLE")
    {
        var trs = [];
        var f = function (tr, head)
        {
            var tds = [];

            Cyan.$$(tr).$(head ? "th" : "td").each(function ()
            {
                var td = Cyan.$$(this);
                var align = td.css("text-align");
                if (align != "left" && align != "right")
                    align = "center";

                var vAlign = td.css("vertical-align");
                if (vAlign != "bottom" && vAlign != "top")
                    vAlign = "middle";

                var size = td.getSize()[0];

                tds.push({value: this.innerHTML, rowSpan: this.rowSpan, colSpan: this.colSpan,
                    align: align, vAlign: vAlign, width: size.width, height: size.height});
            });

            trs.push({tds: tds, head: head});
        };
        Cyan.$$(table).$("thead tr").each(function ()
        {
            f(this, true);
        });
        Cyan.$$(table).$("tbody tr").each(function ()
        {
            f(this, false);
        });

        table = {trs: trs};
    }

    if (title)
        table.title = title;

    var url = Cyan.Arachne.getToolsUrl("exportTable", false);

    if (!Cyan.Arachne.exportTableForm)
    {
        Cyan.Arachne.exportTableForm = document.createElement("FORM");
        Cyan.Arachne.exportTableForm.style.display = "none";
        document.body.appendChild(Cyan.Arachne.exportTableForm);

        var input = document.createElement("input");
        input.name = "$0";
        Cyan.Arachne.exportTableForm.appendChild(input);
        Cyan.Arachne.exportTableInput = input;
    }

    Cyan.Arachne.exportTableInput.value = Cyan.Ajax.toJson(table);

    var ajax = new Cyan.Ajax();
    ajax.doPost(Cyan.Arachne.exportTableForm, url, "_blank", [
        {
            $1: type
        }
    ]);
};