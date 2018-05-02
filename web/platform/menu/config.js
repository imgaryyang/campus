$.onload(function()
{
    var saveConfig = window.saveConfig;
    window.saveConfig = function()
    {
        var menuIds = $$('#menuIds:field').allValues();
        saveConfig(menuIds, function()
        {
            Cyan.Window.setReturnValue(menuIds);
            $.message("操作成功", function()
            {
                closeWindow();
            });
        });
    };
});

function up()
{
    $$('#menuIds:field').up();
}

function down()
{
    $$('#menuIds:field').down();
}