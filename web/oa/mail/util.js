function parseEmail(email)
{
    if (!Cyan.isString(email))
        email = this;

    var name;
    var address;
    var account;
    var server;

    var index = email.indexOf("<");
    if (index >= 0 && email.endsWith(">"))
    {
        address = email.substring(index + 1, email.length - 1);
        name = email.substring(0, index).trim();

        if (name)
        {
            if (name.startsWith("'") || name.startsWith("\""))
                name = name.substring(1);
            if (name.endsWith("'") || name.endsWith("\""))
                name = name.substring(0, name.length - 1);
        }

        if (!name)
        {
            name = address;
        }
    }
    else
    {
        name = email;
        address = email;
    }


    index = address.indexOf("@");
    if (index > 0)
    {
        account = address.substring(0, index);
        server = address.substring(index + 1);
    }

    return {name:name, address:address, email:email, account:account, server:server};
}

function parseEmailList(emails)
{
    return emails.replaceAll(";", ",").split(",").gets(parseEmail);
}