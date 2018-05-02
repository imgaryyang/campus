$.onload(function ()
{
    var shares = Cyan.Arachne.form.shares;
    if (shares)
    {
        setTimeout(function ()
        {
            var n = shares.length;
            for (var i = 0; i < n; i++)
            {
                var share = shares[i];
                shareSelector.getSelector().addSelected({
                    type:share.type.toLowerCase(), id:share.type == "DEPT" ? share.deptId :
                            share.userId, name:share.name
                });
            }

            var word = Cyan.$("word");
            if (word)
            {
                shareSelector.getSelector().bindQuery(word);
            }
        }, 100);
    }
});

function beforeSave()
{
    var members = shareSelector.getSelector().selected;
    var n = members.length;
    var shares = Cyan.Arachne.form.shares = new Array(n);
    for (var i = 0; i < n; i++)
    {
        var member = members[i];
        shares[i] = member.type == "dept" ? {type:"dept", deptId:member.id, userId:0} :
        {type:"user", userId:member.id, deptId:0};
    }

    return true;
}