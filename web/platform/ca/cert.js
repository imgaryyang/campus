if (!window.System)
{
    System = {};
}

System.Cert = {
    apis: [],
    getApi: function (type)
    {
        var apis = System.Cert.apis;
        for (var i = 0; i < apis.length; i++)
        {
            var api = apis[i];
            if (type)
            {
                if (api.type == type)
                    return api;
            }
            else if (api.detect())
            {
                return api;
            }
        }
    },
    addApi: function (api)
    {
        System.Cert.apis.push(api);
    }
};

Cyan.importJs("/platform/ca/imp.js");