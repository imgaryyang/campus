$.onload(function()
{
    var file = $("entity.icon");
    if (file)
    {
        $$(file).attach("change", function()
        {
            Cyan.Arachne.getImagePath(this, function(path)
            {
                $("icon").src = path;
            });
        });
    }
});