$.onload(function()
{
    $$("#appIds").onclick(function()
    {
        if (this.checked)
        {
            var desktop = Cyan.Window.getOpener().System.Desktop;
            desktop.addModule(this.value);
            desktop.save();
        }
        else
        {
            Cyan.Window.getOpener().System.Desktop.removeModule(this.value);
        }
    });
});