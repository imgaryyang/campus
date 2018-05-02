Cyan.onload(function ()
{
    window.office = new System.Office("content");
    office.render(Cyan.$("word"), function ()
    {
        office.setUserName(Cyan.Arachne.form.userName);
        if (Cyan.Arachne.form.newFile)
        {
            office.setEditType(0);
            office.newFile("");
        }
        else
        {
            office.setEditType(1);
            office.openFile("/weboffice/test/load");
        }
    });

    var save0 = window.save;
    window.save = function ()
    {
        save0({
            target: office,
            callback: function (ret)
            {
                alert(ret);
            }
        });
    };
});

function printWord()
{
    office.print();
}

function openFile()
{
    office.openLocalFile();
}

function saveAs()
{
    office.saveAs();
}

function hitchedByTemplate()
{
    office.hitchedByTemplate("/platform/weboffice/template.doc", "正文");
    office.setText("主题词", "这里很好");
    office.clearBookmarks();
}

function showFileType()
{
    alert(office.getFileType());
}