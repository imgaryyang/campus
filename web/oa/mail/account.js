$.onload(function()
{
    var address = $("entity.address");
    if (address)
    {
        $$(address).attach("change", function()
        {
            if (Cyan.Validator.isEmail(this.value))
            {
                var value = this.value;
                var index = value.indexOf("@");
                var user = value.substring(0, index);
                var server = value.substring(index + 1);

                var smtpServer = $("entity.smtpServer");
                if (!smtpServer.value)
                    smtpServer.value = "smtp." + server;

                var pop3Server = $("entity.pop3Server");
                if (pop3Server)
                {
                    if (!pop3Server.value)
                        pop3Server.value = "pop3." + server;
                }

                var userName = $("entity.userName");
                if (!userName.value)
                    userName.value = user;
            }
        });
    }
});