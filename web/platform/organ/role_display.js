var rolesMap = {};
var scopesMap = {};

function menuselect()
{
    var node = menuTree.getSelectedNode();
    if (node)
    {
        var id = node.getValue().value.menuId;
        if (id)
        {
            var roles = rolesMap[id];
            if (roles)
            {
                updateRoles(roles, id);
            }
            else
            {
                getRoles(id, function (roles)
                {
                    rolesMap[id] = roles;
                    var node1 = menuTree.getSelectedNode();
                    if (node1 == node)
                        updateRoles(roles, id);
                });
            }
        }
    }
}

function updateRoles(roles, appId)
{
    var roleString = "", groupString = "";
    var roleIds = [];
    for (var i = 0; i < roles.length; i++)
    {
        var role = roles[i];
        if (role.type == "group")
        {
            if (groupString)
                groupString += ",";
            groupString += role.roleName;
        }
        else
        {
            if (roleString)
                roleString += ",";
            roleString += role.roleName;
            roleIds.push(role.roleId);
        }
    }

    $("roles").innerHTML = roleString;
    $("roleGroups").innerHTML = groupString;

    var scopes = scopesMap[appId];
    if (scopes)
    {
        updateScopes(scopes);
    }
    else
    {
        if (roleIds && roleIds.length)
        {
            (Cyan.Arachne.form.deptId ? getDepts : getScopes)(roleIds, appId, function (scopes)
            {
                scopesMap[appId] = scopes;
                var node = menuTree.getSelectedNode();
                if (node && node.getValue().value.menuId == appId)
                    updateScopes(scopes);
            });
        }
        else
        {
            scopes = [];
            scopesMap[appId] = scopes;
            updateScopes(scopes);
        }
    }
}

function updateScopes(scopes)
{
    var s = "";
    if (scopes)
    {
        for (var i = 0; i < scopes.length; i++)
        {
            var scope = scopes[i];
            if (s)
                s += "<BR>";
            s += scope.toString();
        }
    }
    else
    {
        s = "所有部门";
    }

    $("scopes").innerHTML = s;
}