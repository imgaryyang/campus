Cyan.importJs("/platform/group/member.js");

Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "addScopes", function ()
    {
        var addScopes = this.inherited;

        var types;
        if (Cyan.Arachne.form.scopeType == "DEPT")
            types = ["dept"];
        else
            types = ["dept", "user"];

        System.selectMembers({
            app:"vote_scope",
            deptId:Cyan.Arachne.form.voteDeptId,
            types:types,
            callback:function (members)
            {
                if (members)
                {
                    var n = members.length;
                    if (n > 0)
                    {
                        var voteScopes = new Array(n);
                        for (var i = 0; i < n; i++)
                        {
                            var member = members[i];
                            voteScopes[i] = {type:member.type.toUpperCase(), objectId:member.id};
                        }

                        addScopes(voteScopes, function (result)
                        {
                            if (result)
                                reload();
                        });
                    }
                }
            }
        });
    });
});