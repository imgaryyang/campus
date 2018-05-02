Cyan.onload(function ()
{
    var div = document.createElement("DIV");
    div.innerHTML = "<object style='left:0;top:0;height:0;width:0' id='capicom' " +
    "classid='clsid:A996E48C-D3DC-4244-89F7-AFA33EC60679' codebase='/capicom.cab#version=2,0,0,3'></object>";
    document.body.appendChild(div);
});

(function ()
{
    var clearBlank = function (s)
    {
        return s.replace(/[\r\n]/g, "");
    };

    var getSignCerts = function ()
    {
        var store = new ActiveXObject("CAPICOM.Store");
        store.Open(2, "My", 0);
        return store.Certificates.Find(12, 0x00000080).Find(9);
    };

    var getSignCert = function ()
    {
        var certs = getSignCerts(), cert;
        var count = certs.Count;
        try
        {
            if (count > 1)
                cert = certs.Select("选择签名证书", "请选中你的签名证书，按确定").Item(1);
            else if (count == 1)
                cert = certs.Item(1);
        }
        catch (e)
        {
            if (e.number != -2138568446)
            {
                Cyan.message("选择证书错误");
            }
        }

        return cert;
    };

    var sign = function (data, callback)
    {
        var cert = getSignCert();

        if (cert)
        {
            var signedData = new ActiveXObject("CAPICOM.SignedData");
            var signer = new ActiveXObject("CAPICOM.Signer");
            signedData.Content = data;
            signer.Certificate = cert;
            signer.Options = 2;

            callback(clearBlank(cert.Export(0)), clearBlank(signedData.Sign(signer, false, 0)));
        }
    };

    window.baseCaApi = function (type)
    {
        return {
            type: type || "base",
            detect: function ()
            {
                try
                {
                    var certs = getSignCerts();
                    return certs.Count >= 1;
                }
                catch (e)
                {
                    return false;
                }
            },
            getSignCert: function (callback)
            {
                var cert = getSignCert();
                if (cert)
                    callback(clearBlank(cert.Export(0)));
            },
            sign: sign
        };
    }
})();