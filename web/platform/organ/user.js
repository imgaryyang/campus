Cyan.onload(function ()
{
    Cyan.Class.overwrite(window, "freeze", function (userId, freezed)
    {
        this.inherited(userId, freezed, function ()
        {
            reload(userId);
        });
    });

    if (window.availableRoles)
    {
        window.availableRoles.bindSearch("word");
    }

    if (Cyan.$("entity.workno") && Arachne.form.new$)
    {
        Cyan.attach("entity.workno", "focus", function ()
        {
            if (Cyan.$("entity.userName").value && !Cyan.$("entity.workno").value)
            {
                generateWorkno(Arachne.form.deptId, {
                    callback: function (workno)
                    {
                        Cyan.$("entity.workno").value = workno;
                    },
                    obj: {}
                });
            }
        });
    }
});

function onUserNameChange()
{
    var userName = Cyan.$("entity.userName").value;
    if (userName)
    {
        var loginName = Cyan.$("entity.loginName");
        if (loginName && !loginName.value)
        {
            genLoginName(userName, {
                form: false,
                callback: function (result)
                {
                    if (result)
                    {
                        var loginName = Cyan.$("entity.loginName");
                        if (!loginName.value)
                            loginName.value = result;
                    }
                }
            });
        }
    }
}

function role_display()
{
    var roleIds = Cyan.$$$("roleIds").selectedValues();
    if (!roleIds.length)
    {
        Cyan.message("请至少选择一个角色");
        return;
    }
    var url = "/RoleDisplay?deptId=" + Cyan.Arachne.form.deptId;
    for (var i = 0; i < roleIds.length; i++)
        url += "&roleIds=" + roleIds[i];

    System.showModal(url);
}

function setCertId()
{
    var certTypeInput = Cyan.$("entity.certType");
    var certType;
    if (certTypeInput)
        certType = certTypeInput.value;
    else
        certType = Cyan.Arachne.form.entity.certType;

    var api;
    if (certType)
    {
        if (certType)
            api = System.Cert.getApi(certType);
        if (!api || !api.detect())
        {
            api = System.Cert.getApi();
            if (api && certTypeInput)
            {
                Cyan.$$(certTypeInput).setValue(api.type);
            }
        }
    }
    else
    {
        api = System.Cert.getApi();
    }

    if (api)
    {
        if (api.getSignCertId)
        {
            Cyan.$("entity.certId").value = api.getSignCertId();
        }
        else if (api.getSignCert)
        {
            api.getSignCert(function (cert)
            {
                getCertId(cert, api.type, {
                    callback: function (result)
                    {
                        Cyan.$("entity.certId").value = result;
                    },
                    obj: {},
                    check: false,
                    form: false
                });
            });
        }
    }
}

function onDeptChange()
{
    refreshRoles(refreshStations);
}

function refreshRoles(callback)
{
    Cyan.Arachne.form.deptId = Cyan.$("deptId").value;

    rolesSelector.from.reload();

    getRoles(function (roles)
    {
        rolesSelector.removeAll();
        for (var i = 0; i < roles.length; i++)
        {
            rolesSelector.addItem(roles[i].roleId, roles[i].roleName);
        }

        if (callback)
            callback();
    });
}

function refreshStations(callback)
{
    Cyan.Arachne.form.deptId = Cyan.$("deptId").value;
    getAvailableStations(function (availableStations)
    {
        getStations(function (stations)
        {
            stationsSelector.removeAll();
            stationsSelector.from.clearAll();
            var i;
            for (i = 0; i < availableStations.length; i++)
            {
                stationsSelector.from.addItem(availableStations[i].stationId, availableStations[i].stationName);
            }

            for (i = 0; i < stations.length; i++)
            {
                stationsSelector.addItem(stations[i].stationId, stations[i].stationName);
            }

            if (callback)
                callback();
        });
    });
}

function devolve()
{
    System.showModal("/platform/devolve?fromUserId=" + Cyan.Arachne.form.entity.userId);
}

function onIdCardNo()
{
    if (Cyan.$("entity.idCardType").value == "IDENTIFICATION")
    {
        var idCardNo = Cyan.$("entity.idCardNo").value;
        if (idCardNo.length == 18)
        {
            Cyan.$$$("entity.birthday").setValue(idCardNo.substring(6, 14));
        }
    }
}