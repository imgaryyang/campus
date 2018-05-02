$.onload(function()
{
    var wrap = function(method)
    {
        Cyan.Class.overwrite(window, method, function(consignationId)
        {
            this.inherited(consignationId, function()
            {
                Cyan.message("操作成功", function()
                {
                    reload(consignationId);
                });
            });
        });
    };

    wrap("stop");
    wrap("restore");
    wrap("accept");
    wrap("reject");
});

function showItems(consignationId)
{
    System.openPage("/ods/flow/worksheet?consignationId=" + consignationId);
}