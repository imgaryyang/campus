Cyan.importJs("combox.js");

var optionsMenu;
var requireMessage;

Cyan.onload(function ()
{
    Cyan.Arachne.form.forward = "edit";

    var opinion = Cyan.Window.getOpener().System.Opinion;

    if (opinion.text)
        Cyan.$$("#text").setValue(opinion.text);

    var title = opinion.title;
    requireMessage = opinion.requireMessage;
    if (title)
    {
        window.setTimeout(function ()
        {
            Cyan.Window.getWindow().setTitle(title);
        }, 100);
    }


    if (opinion.remark)
        Cyan.$("remark").innerHTML = opinion.remark + "：";

    setTimeout(function ()
    {
        try
        {
            Cyan.$("text").focus();
        }
        catch (e)
        {
        }
    }, 500);

    Cyan.$$("#autoAdd").onclick(function ()
    {
        if(!this.checked) Cyan.$$("#split").check(false);

        modifyAutoAdd(this.checked, {form: ""});
    });

    Cyan.$$("#split").onclick(function ()
   {
       if(this.checked) Cyan.$$("#autoAdd").check(true);

       modifySplit(this.checked, {form: ""});
   });

    setTimeout(reload, 100);
});

function ok()
{
    var content = Cyan.$$("#text").getValue();

    if (!content && requireMessage)
    {
        Cyan.message(requireMessage);
        return;
    }

    var autoAdd = Cyan.$("autoAdd");
    if (autoAdd && autoAdd.checked)
    {
        var split = Cyan.$("split");
        addContent(content, split && split.checked);
    }
    Cyan.Window.closeWindow(content);
}

function reload()
{
    var opinionList = Cyan.$$("#keys:field");
    if (opinionList.length)
    {
        window.loadPage(0, function (result)
        {
            opinionList.clearAll();

            var opinions = result.records;
            window.opinions = opinions;
            for (var i = 0; i < opinions.length; i++)
            {
                var opinion = opinions[i];
                opinionList.addItem(opinion.opinionId, opinion.title);
            }
        });
    }
}

function selectOpinions(component)
{
    optionsMenu.showWith(component);
}

function addOpinion()
{
    System.showModal("/opinion/crud.new", function (ret)
    {
        if (ret)
            reload();
    });
}

function editOpinion()
{
    var selecteds = Cyan.$$("#keys:field").selectedItems();
    if (!selecteds.length)
    {
        Cyan.message("请选择要修改的常用意见");
        return;
    }
    else if (selecteds.length > 1)
    {
        Cyan.message("只能选择一个意见");
        return;
    }

    System.showModal("/opinion/crud/" + selecteds[0].value, function (ret)
    {
        if (ret)
            reload();
    });
}

function deleteOpinion()
{
    var keys = Cyan.$$("#keys:field").selectedValues();
    if (!keys.length)
    {
        Cyan.message("请选择要删除的常用意见");
        return;
    }

    removeAll(keys, reload);
}

function upOpinion()
{
    var select = Cyan.$$("#keys:field");
    select.up();
    sort(select.allValues(), {
        form: ""
    });
}

function downOpinion()
{
    var select = Cyan.$$("#keys:field");
    select.down();
    sort(select.allValues(), {
        form: ""
    });
}

function selectOpinion()
{
    var value = Cyan.$$("#keys:field").selectedValues()[0];
    if (value)
    {
        for (var i = 0; i < window.opinions.length; i++)
        {
            var opinion = window.opinions[i];
            if (opinion.opinionId == value)
            {
                Cyan.$("text").value = Cyan.$("text").value + opinion.content;
                break;
            }
        }
    }
    else
    {
        Cyan.message("请选择常用意见");
    }
}

function selectCommonOpinion()
{
    System.showModal("/opinion/common/select", function (ret)
    {
        if (ret)
            reload();
    }, {width: 600, height: 300});
}