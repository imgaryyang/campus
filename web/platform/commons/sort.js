Cyan.onload(function ()
{
    var sort = window.sort;
    window.sort = function ()
    {
        var keys = Cyan.$$('#keys:field').allValues();
        sort(keys, {
            callback: function ()
            {
                Cyan.Window.setReturnValue(keys);
                Cyan.message("操作成功", function ()
                {
                    closeWindow();
                });
            },
            form: ""
        });
    };
});

function up()
{
    Cyan.$$('#keys:field').up();
}

function down()
{
    Cyan.$$('#keys:field').down();
}