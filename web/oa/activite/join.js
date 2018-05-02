$.onload(function()
{
    Cyan.Class.overwrite(window, "participate", function(memberId)
    {
        this.inherited(memberId, function()
        {
            refresh();
        });
    });

    Cyan.Class.overwrite(window, "noParticipate", function(memberId)
    {
        this.inherited(memberId, function()
        {
            refresh();
        });
    });
});

function showActivite(activiteId)
{
    System.openPage("/oa/activite/query/" + activiteId);
}

function participate(memberId, state)
{
    $.confirm("确定参加此活动", function(ret)
    {
        if (ret == "ok")
        {
            participate(memberId, state);
            refresh();
        }
    });
}

function noParticipate(memberId, state)
{
    $.confirm("确定不参加此活动", function(ret)
    {
        if (ret == "ok")
        {
            noParticipate(memberId, state);
            refresh();
        }
    });
}